% ===================================================================
% File 'utilities.pl'
% Purpose: Shared utilities for interfacing to OpenCyc from SWI-Prolog
% Maintainer: Douglas Miles
% Contact: $Author$@users.sourceforge.net ;
% Version: 'utilities.pl' 1.0.0
% Revision:  $Revision$
% Revised At:   $Date$
% ===================================================================
:- style_check(-singleton).
:- style_check(-discontiguous).
:- style_check(-atom).
:- style_check(-string).

% ===================================================================
% ===================================================================
% ===================================================================

isSlot(Var):-var(Var).
isSlot('$VAR'(Var)):-number(Var).


% ===================================================================
% CycL Term Reader
% ===================================================================
:-dynamic reading_in_comment/0.
:-dynamic reading_in_string/0.
:-dynamic read_in_atom/0.

readCycL(CHARS):-readCycL(user_input,CHARS).

readCycL(Stream,[])  :-at_end_of_stream(Stream).     
readCycL(Stream,Trim)  :-
		flag('bracket_depth',_,0),
		retractall(reading_in_comment),
		retractall(reading_in_string),!,
		readCycLChars_p0(Stream,CHARS),!,trim(CHARS,Trim).

readCycLChars_p0(Stream,[]):-at_end_of_stream(Stream),!.
readCycLChars_p0(Stream,[Char|Chars]):-
        get_code(Stream,C),
	cyclReadStateChange(C),
	readCycLChars_p1(C,Char,Stream,Chars),!.
	
readCycLChars_p1(C,Char,Stream,[]):- at_end_of_stream(Stream),!.
readCycLChars_p1(C,Char,Stream,[]):-isCycLTerminationStateChar(C,Char),!.
readCycLChars_p1(C,Char,Stream,Chars):-cyclAsciiRemap(C,Char),
      flag(prev_char,_,Char),
      readCycLChars_p0(Stream,Chars),!.

isCycLTerminationStateChar(10,32):-reading_in_comment,!.
isCycLTerminationStateChar(13,32):-reading_in_comment,!.
isCycLTerminationStateChar(41,41):-flag('bracket_depth',X,X),(X<1),!.

cyclReadStateChange(_):- reading_in_comment,!.
cyclReadStateChange(34):-flag(prev_char,Char,Char),   % char 92 is "\" and will escape a quote mark
      (Char=92 -> true;(retract(reading_in_string) ; assert(reading_in_string))),!.
cyclReadStateChange(_):- reading_in_string,!.
cyclReadStateChange(59):- assert(reading_in_comment),!.
cyclReadStateChange(40):-!,flag('bracket_depth',N,N + 1).
cyclReadStateChange(41):-!,flag('bracket_depth',N,N - 1).
cyclReadStateChange(_).

cyclAsciiRemap(X,32):- (not(number(X));X>128;X<32),!.
cyclAsciiRemap(X,X):-!.


% ===================================================================
% CycL Term Parser
% ===================================================================
/*===================================================================
% getSurfaceFromChars/3 does less consistancy checking then conv_to_sterm

Always a S-Expression: 'WFFOut' placing variables in 'VARSOut'

|?-getSurfaceFromChars("(isa a b)",Clause,Vars).
Clause = [isa,a,b]
Vars = _h70

| ?- getSurfaceFromChars("(isa a (b))",Clause,Vars).
Clause = [isa,a,[b]]
Vars = _h70

|?-getSurfaceFromChars("(list a b )",Clause,Vars)
Clause = [list,a,b]
Vars = _h70

| ?- getSurfaceFromChars("(genlMt A ?B)",Clause,Vars).
Clause = [genlMt,'A',_h998]
Vars = [=('B',_h998)|_h1101]

| ?- getSurfaceFromChars("(goals Iran  (not   (exists   (?CITIZEN)   (and    (citizens Iran ?CITIZEN)    (relationExistsInstance maleficiary ViolentAction ?CITIZEN
)))))",Clause,Vars).

Clause = [goals,Iran,[not,[exists,[_h2866],[and,[citizens,Iran,_h2866],[relationExistsInstance,maleficiary,ViolentAction,_h2866]]]]]
Vars = [=(CITIZEN,_h2866)|_h3347]

====================================================================*/

getSurfaceFromChars([],[end_of_file],_):-!.
getSurfaceFromChars([41],[end_of_file],_):-!.

getSurfaceFromChars([CH|ARSIn],TERM,VARS):-
         trim([CH|ARSIn],CHARS),CHARS=[FC|REST],!,
	 (CHARS=[] -> % String came empty
	   TERM=nil; 
            FC=59 ->  % ";" Comment Char found in Line
	       (atom_codes(Atom,REST),TERM=[file_comment,Atom]) ;   
	       FC=40 -> %Use vanila CycL parser
                  getSurfaceFromCharBalanced(CHARS,TERM,VARS);  
		  %All above methods of parsing failed.. Convert to comment
		  (atom_codes(Atom,[FC|REST]),TERM=[file_comment,Atom])),!. 
	    
getSurfaceFromChars(C,TERM,VARS):-string_to_list(C,List),!,getSurfaceFromChars(List,TERM,VARS),!.


getSurfaceFromCharBalanced(Chars,WFFOut,VARSOut):- 
    retractall(var_counter(_)),retractall(numbered_var(_,_,_)),asserta(var_counter(0)), 
               getCycLTokens(Chars,Tokens), 
               clean_sexpression(Tokens,WFFClean),!,
               phrase(cycL(WFF),WFFClean),
               collect_temp_vars(VARS),!,
               ((VARS=[],VARSOut=_,WFFOut=WFF);
                    (unnumbervars(VARS,LIST),
                     cyclVarNums(LIST,WFF,WFFOut,VARSOut2) ,
                     list_to_set(VARSOut2,VARSOut1),
                     open_list(VARSOut1,VARSOut))),!.

/*===================================================================
% clean_sexpression(Tokens,CleanTokens)

Removes out STANDARD tokens

====================================================================*/

clean_sexpression([],[]).
clean_sexpression(['#$'|WFF],WFFClean):-clean_sexpression(WFF,WFFClean).
clean_sexpression(['#'|WFF],WFFClean):-clean_sexpression(WFF,WFFClean).
clean_sexpression(['$'|WFF],WFFClean):-clean_sexpression(WFF,WFFClean).
clean_sexpression([E|WFF],[E|WFFClean]):-clean_sexpression(WFF,WFFClean).

/*===================================================================
% Safe Entry Call Into ISO-Prolog tokenize_chars/2
====================================================================*/

getCycLTokens(X,Z):-is_list(X),!,  tokenize_chars(X,Y),convert_the_atoms(Y,Z).
getCycLTokens(X,[X]). 

convert_the_atoms([],[]):-!.
convert_the_atoms([H|T],[HH|TT]):-!,  
                convert_the_atom(H,HH),
                convert_the_atoms(T,TT).

%convert_the_atom(H,HH):-atom_codes(H,[34|Rest]),reverse(Rest,[_|AtomCharsR]),reverse(AtomCharsR,AtomChars),atom_codes(HH,AtomChars).
%convert_the_atom(H,HH):-atom_codes(H,[39|Rest]),reverse(Rest,[_|AtomCharsR]),reverse(AtomCharsR,AtomChars),atom_codes(HH,AtomChars).
convert_the_atom(H,H):-!.



%===================================================================
% Removes Leading and Trailing whitespaces and non ANSI charsets.
%====================================================================
trim(X,Y):-ltrim(X,R),reverse(R,Rv),ltrim(Rv,RY),reverse(RY,Y).

ltrim([],[]):-!.
ltrim([32,32,32,32,32,32,32|String],Out) :-trim(String,Out),!.
ltrim([32,32,32,32,32|String],Out) :-trim(String,Out),!.
ltrim([32,32,32|String],Out) :- trim(String,Out),!.
ltrim([32,32|String],Out) :- trim(String,Out),!.
ltrim([P|X],Y):- (not(number(P));P<33;P>128),trim(X,Y),!.
ltrim(T,T).

/*===================================================================
%  CycL String to DCG Converter
% Converts up to 13 forms
%     13 Terms long
%  
% =169 Parens Pairs at the First 2 levels  
% 
====================================================================*/


cycL([A]) --> expr(A).
cycL([and,A|L]) --> expr(A) , cycL(L).

   %%expr(RF) --> reifiableFN(RF),!.
expr([]) -->  ['(',')'],!.
expr([Head]) -->  ['('],opr(Head),[')'],!.
expr([Head|LIST]) -->  ['('],opr(Head),many_slots(LIST),[')'].

many_slots([A]) --> slot(A).
many_slots([A|L]) --> slot(A) , many_slots(L).

opr(Head) --> simple(Head) .
opr(Head) --> expr(Head).

%slot(Name) --> simple(Name),['AssignmentFn'], { nonvar(Name), ! }.
slot(SKFName) --> ['SKF'],simple(Name), { nonvar(Name), ! , skf_name(Name,SKFName) }.
slot(WFF) -->  simple(WFF), { nonvar(WFF), ! }.
%slot(['AssignmentFn',Name,List]) -->  reifiableFN(['AssignmentFn',Name,List]).
slot(WFF) -->  expr(WFF), { nonvar(WFF), ! }.


expr(WFF) -->  variable(WFF), { nonvar(WFF) ,!}.
%expr(WFF) -->  reifiableFN(WFF), { nonvar(WFF),! }.   %slot(WFF) -->  literal_list(WFF), { nonvar(WFF) }.


variables_list([list,A]) --> qual_var(A).
variables_list([list,A]) -->  ['('],qual_var(A),[')'],!.
variables_list([list,A,B]) -->  ['('],qual_var(A),qual_var(B),[')'],! .
variables_list([list,A|QV]) -->  ['('],qual_var(A),many_qual_var(QV),[')'],!.
many_qual_var([A]) -->  qual_var(A).
many_qual_var([A|T]) -->  qual_var(A),many_qual_var(T).

% Var/Quality pairs that Sowa's ACE examples use

qual_var(VN) --> ['('],variable(VN),[')'].
qual_var(VN) --> variable(VN).
qual_var(VN) --> ['('],variable(VN),qual(_Quality),[')'].

qual(Q) --> constant(Q), { nonvar(Q) }. % , 'surface-instance'(_,Q,_) }.

number(Number) -->  [Number] , {  nonvar(Number), number(Number),! } .

quantity(Number) --> number(Number).

simple(WFF) -->  quantity(WFF), { nonvar(WFF), ! }.
simple(WFF) -->  variable(WFF), { nonvar(WFF), ! }.
simple(WFF) -->  constant(WFF), { nonvar(WFF), ! }.
%simple(['AssignmentFn',Name,[]]) --> ['SKF'],constant(Name).
%simple(['AssignmentFn',Name,[]]) --> ['SKF'],simple(Name),{ nonvar(Name) , nonvar(List), ! } .
%simple(['AssignmentFn',Name,[]]) --> ['AssignmentFn'],simple(Name), { nonvar(Name) , nonvar(List), ! } .

%reifiableFN(['AssignmentFn',SKFName,[]]) --> ['(','SKF'],simple(Name),[')'], { nonvar(Name) ,! , skf_name(Name,SKFName),sendNote('(skf)') } .
%reifiableFN(['AssignmentFn',SKFName,List]) --> ['(','SKF'],simple(Name),arbitrary(List),[')'], { nonvar(Name) , nonvar(List), ! , skf_name(Name,SKFName),sendNote('(skf)') } .
%reifiableFN(['AssignmentFn',Name,List]) --> ['(','AssignmentFn'],simple(Name),arbitrary(List),[')'], { nonvar(Name) , nonvar(List), ! } .
%%reifiableFN(['AssignmentFn',Name,_]) --> ['SKF'],simple(Name).
%reifiableFN(['AssignmentFn',Name,List]) --> ['('],simple(Name),arbitrary(List),[')'], { nonvar(Name) , nonvar(List),'surface-instance'(Name,'Function',_) ,! } .

skf_name(Num,SKFName):-!,number(Num), number_codes(Num,Codes),atom_codes(SKFName,[115,107|Codes]).

% Construct arbitrary list of args
          
arbitrary([]) -->  [].
arbitrary(VN)-->  ['?',A], { var_number(A,VN)   } . 
arbitrary([Head]) -->  slot(Head).
arbitrary([A|L]) --> slot(A) , many_slots(L).


variable(VN)-->  ['?',A], { var_number(A,VN)   } . 
variable(VN)-->  ['??'], { var_gen(A),var_number(A,VN)   } .     %Anonymous
variable(VN)-->  ['?'], { var_gen(A),var_number(A,VN)   } . 

% Makes up sequencial Variable names for anonymous cycl getPrologVars
var_gen(Atom):-idGen(Number),number_codes(Number,Codes),atom_codes(Atom,[86,65,82|Codes]). % "VAR"

constant(Number) --> number(Number) .
   
constant(Unquoted) -->  [Unquoted] , {  nonvar(Unquoted), not((Unquoted='?';Unquoted='(';Unquoted=')')),! } .
     
var_number(A,'$VAR'(VN)):-numbered_var(A,'$VAR'(VN),_),!.
var_number(A,'$VAR'(VN)):-get_next_num(VN),assert(numbered_var(A,'$VAR'(VN),_)),!.

:-dynamic(numbered_var/3).

:-assert(var_counter(0)).

% This creates ISO Prolog getPrologVars w/in a CycL/STANDARD expression to be reconstrated as after parsing is complete 

get_next_num(VN):-!,retract(var_counter(VN)),NVN is VN +1,asserta(var_counter(NVN)).

cyclVarNums(LIST,'$VAR'(NUM),VAR,[=(SYM,VAR)]):-numbered_var(SYM,'$VAR'(NUM),_VAR),
               member(=(SYM,VAR),LIST).

cyclVarNums(_,Atom,Atom,[]):-atomic(Atom).
cyclVarNums(LIST,Term,NewTerm,VARLIST):-Term=..[F|ARGS],cyclVarNums_list(LIST,ARGS,VARARGS,VARLIST),NewTerm=..[F|VARARGS].

cyclVarNums_list(_LIST,[],[],[]).
cyclVarNums_list(LIST,[A|RGS],[V|ARARGS],VARLIST):-
            cyclVarNums(LIST,A,V,VARS1),
            cyclVarNums_list(LIST,RGS,ARARGS,VARS2),
            append(VARS1,VARS2,VARLIST).


unnumbervars(X,Y):-term_to_atom(X,A),atom_to_term(A,Y,_).

open_list(V,V):-var(V).
open_list(A,B):-append(A,_,B).

unnumbervars_nil(X,Y):-!,unnumbervars(X,Y).

collect_temp_vars(VARS):-!,(setof(=(Name,Number),numbered_var(Name,Number,_),VARS);VARS=[]).

%================================================================
% STRING TOKENIZATION                            
%================================================================
:-assert(show_this_hide(tokenize,2)).

%tokenize_chars(M,['(',surf,')']):-nonvar(M),member(34,M),!.
tokenize_chars(X,Y):-once( tokenize3(X,Y) ).

tokenize3([],[]).
tokenize3([32|T],O):-!,tokenize3(T,O),!.
tokenize3(CharList,[Token|TList])  :- 
  append(_,[C|List],CharList), C \= 32,!,
  get_token([C|List],Token,Rest),!,
  tokenize3(Rest,TList),!.

get_token(List,Token,Rest)  :- 
  get_chars_type(List,Lchars,Rest,Type),!,
  type_codes(Type,Lchars,Token),!.

type_codes(num,CODES,Num):-catch(number_codes(Num,CODES),_,fail),!.
type_codes(_,[34|Lchars],string(S)):-!,atom_codes(S,[34|Lchars]).
type_codes(_,Lchars,Token):-!,atom_codes(Token,Lchars).

get_chars_type(L,S,L1,sep)  :-  separator(L,S,L1),!.
get_chars_type([C|L],[C|Lc],L1,S)  :- 
  check_start(S,C),
  get_word_chars(S,L,Lc,L1).

get_word_chars(S,L,Lc,L1)  :- 
  check_end(S,L,Lc,L1).
get_word_chars(S,[C|L],[C|Lc],L1)  :- 
  legal_char(S,C),
  get_word_chars(S,L,Lc,L1).

legal_char(num,C)    :-  digit(C).
legal_char(quote,C)  :-  not(bracket(_,C,_)).
legal_char(symb,C)   :-  valid_char(C).

check_start(Name,S):-bracket(Name,S,_E).
check_start(num, C)   :-  start_digit(C).
check_start(symb,C)   :- valid_char(C). %, 'not'(digit(C)).

check_end(_,[],[],[])  :-  !.
check_end(num, [C|L],[],[C|L])  :-  'not'(digit(C)),!.
check_end(Name,[E|L],[E],L)  :-  bracket(Name,S,E),!.
%check_end(symb,[C1,C2|L],[],[C1,C2|L])  :-  member([C1,C2],["Fn"]),!.
check_end(symb,[C|L],[],[C|L])  :-  'not'(valid_char(C)).

separator([C,D,E,F|L],[C,D,E],L)  :-member([C,D,E,F],["SKF-"]),!.
separator([C,D,E|L],[C,D,E],L)  :-member([C,D,E],["<=>","=:=","=\=","\==","@=<","@>=","=..","-->","SKF"]),!.
separator([C,D|L],[C,D],L)  :-member([C,D],["=>",":-","\+","->","\=","==","@<","@>","=<",">=","#$","//","??"]),!. %,"Fn"
separator([C|L],[C],L)  :- member(C,"*,():[];= < >^{}?%$#/"),!.

valid_char(C)  :-  letter(C); digit(C); C = 95 ; C=45 ; C=39.
letter(C)  :-   C=45 ; (97 =< C, C =< 122) ; (65 =< C, C =< 90) ; C = 95 .
start_digit(C)   :- member(C,"-01234567890").
digit(C)   :- member(C,"-.01234567890+eE").

%get_word([C|T],C,T)  :-  member(C,":,.?&%"),!. % ( : , . ?)
get_word([C|T],[C],T)  :- member(C,"=&"),!. % (=)
get_word([C,C1|T],[C,C1],T)  :- member([C,C1],["??"]),!. %"Fn",
get_word([C|T],[C|W],T2)  :-  bracket(_,C,C1),!,get_chars(0,C1,T,W,T2).
get_word([C|T],[C|W],T2)  :-  valid_start(C),!, get_chars(1,32,T,W,T2).

get_chars(K,C1,[C|T],[C|W],T2)  :-  valid_char(K,C,C1),!,get_chars(K,C1,T,W,T2).
get_chars(0,C,[C|T],[],T)  :- bracket(_,C,_), !.
get_chars(0,C,[C|T],[C],T)  :-  (C = 41; C = 93),!. % ) or ]
get_chars(1,_C1,[C|T],[],[C|T])  :-  member(C, [10,13|"=:,?"]).
%get_chars(2,_C1,[C,C2|T],[],[C,C2|T])  :-  member([C,C2], ["Fn"]).

valid_start(C)  :-  valid(C). %; C = 37.  % (%)
valid_char(K,C,C1)  :-  K = 0,!, C \= C1; K = 1, valid(C).

%bracket(quote,39,39).  % single quotes
bracket(quote,34,34).  % double quotes
%bracket(list,91,93).  % square brackets []
%bracket(quote,37,37).  % Literal Percent %%
%bracket(quote,35,35).  % Literal Percent ##

quote_found(0,B,B)  :-  member(B,[34]),!.
quote_found(Q,Q,0).

var_found(0,B,C)  :-  'not'(valid(B)),var_start(C).

var_start(C)  :-  (65 =< C,C =< 90);C = 95;C = 39.
valid(C)  :-   (65 =< C, C =< 90);    % A - Z
             (97 =< C, C =< 122);   % a - z
             (48 =< C, C =< 57);    % 0 - 9
             C = 95; C = 39;C = 45.  % underscore; hyphen



/*===================================================================
Convert S-Expression originating from user to a Prolog Clause representing the surface level

Recursively creates a Prolog term based on the S-Expression to be done after compiler
                                                 
Examples:

| ?- sterm_to_pterm([a,b],Pterm).
Pterm = a(b)

| ?- sterm_to_pterm([a,[b]],Pterm).    %Note:  This is a special Case
Pterm = a(b)

| ?- sterm_to_pterm([holds,X,Y,Z],Pterm).    %This allows Hilog terms to be Converted
Pterm = _h76(_h90,_h104)                    

| ?- sterm_to_pterm([X,Y,Z],Pterm).   %But still works in normal places
Pterm = _h76(_h90,_h104)                    

| ?- sterm_to_pterm(['AssignmentFn',X,[Y,Z]],Pterm).                                
Pterm = 'AssignmentFn'(_h84,[_h102,_h116])
====================================================================*/

sterm_to_pterm(VAR,VAR):-isSlot(VAR),!.
sterm_to_pterm([VAR],VAR):-isSlot(VAR),!.
sterm_to_pterm([X],Y):-!,nonvar(X),sterm_to_pterm(X,Y).

sterm_to_pterm([S|TERM],PTERM):-isSlot(S),
            sterm_to_pterm_list(TERM,PLIST),            
            PTERM=..[holds,S|PLIST].

sterm_to_pterm([S|TERM],PTERM):-number(S),!,
            sterm_to_pterm_list([S|TERM],PTERM).            
	    
sterm_to_pterm([S|TERM],PTERM):-nonvar(S),atomic(S),!,
            sterm_to_pterm_list(TERM,PLIST),            
            PTERM=..[S|PLIST].

sterm_to_pterm([S|TERM],PTERM):-!,  atomic(S),
            sterm_to_pterm_list(TERM,PLIST),            
            PTERM=..[holds,S|PLIST].

sterm_to_pterm(VAR,VAR):-!.

sterm_to_pterm_list(VAR,VAR):-isSlot(VAR),!.
sterm_to_pterm_list([],[]):-!.
sterm_to_pterm_list([S|STERM],[P|PTERM]):-!,
              sterm_to_pterm(S,P),
              sterm_to_pterm_list(STERM,PTERM).
sterm_to_pterm_list(VAR,[VAR]).




atom_junct(Atom,Words):-
   concat_atom(Words1,' ',Atom),
   atom_junct2(Words1,Words),!.

atom_junct2([],[]).
atom_junct2([W|S],[A,Mark|Words]):- member(Mark,['.',',','?']),atom_concat(A,Mark,W),not(A=''),!,atom_junct2(S,Words).
atom_junct2([W|S],[Mark,A|Words]):- member(Mark,['.',',','?']),atom_concat(Mark,A,W),not(A=''),!,atom_junct2(S,Words).
atom_junct2([W|S],[W|Words]):-atom_junct2(S,Words).


