% ===================================================================
% File 'interface.pl'
% Purpose: Lower-level connection based utilities for interfacing to OpenCyc from SWI-Prolog
% Maintainer: Douglas Miles
% Contact: $Author$@users.sourceforge.net ;
% Version: 'interface.pl' 1.0.0
% Revision:  $Revision$
% Revised At:   $Date$

% ===================================================================
:- style_check(-singleton).
:- style_check(-discontiguous).
:- style_check(-atom).
:- style_check(-string).

% ===================================================================
% Connecter to Cyc TCP Server
% ===================================================================
:-dynamic(cycConnection/3).
:-dynamic(cycConnectionUsed/3).
:-dynamic(cycMutex/2).
:-dynamic(cycChatMode/1).

getCycConnection(SocketId,OutStream,InStream):-
      retract(cycConnection(SocketId,OutStream,InStream)),
      assertz(cycConnectionUsed(SocketId,OutStream,InStream)),!.

getCycConnection(SocketId,OutStream,InStream):-
      tcp_socket(SocketId),
      tcp_connect(SocketId,'127.0.0.1':3601),
      tcp_open_socket(SocketId, InStream, OutStream),!,
      format(user_error,'Connected to Cyc TCP Server {~w,~w}\n',[InStream,OutStream]),
      assertz(cycConnectionUsed(SocketId,OutStream,InStream)),!,
      flush_output(user_error).

finishCycConnection(SocketId,OutStream,InStream):-
      (at_end_of_stream(InStream);read_line_with_nl(InStream, Receive)),
      retractall(cycConnectionUsed(SocketId,OutStream,InStream)),
      asserta(cycConnection(SocketId,OutStream,InStream)),!.
      
      

cc:-
   listing(cycConnection),
   listing(cycConnectionUsed).


% ===================================================================
% Invoke SubL
% invokeSubLRaw(-Send[,+Receive]).
% 
% ?- invokeSubLRaw('(find-constant "Dog")').
% #$Dog
%
% ===================================================================

invokeSubL(Send):-
      invokeSubLRaw(Send,Receive),
      format('~s',[Receive]).

invokeSubL(Send,Receive):-
      invokeSubLRaw(Send,ReceiveCodes),
      receiveCodes(ReceiveCodes,Receive).
      

invokeSubLRaw(Send,Receive):-
      getCycConnection(SocketId,OutStream,InStream),
      printSubL(OutStream,Send),
      trace,
      readSubL(InStream,[A,B,C,D|Receive]),!,
      finishCycConnection(SocketId,OutStream,InStream),!,
      checkSubLError(Send,[A,B,C,D|Receive]).

checkSubLError(Send,[53,48,48,32|Info]):- %Error "500 "
      atom_codes(ErrorMsg,Info),
      throw(cyc_error(ErrorMsg,Send)).
checkSubLError(_,_).

printSubL(OutStream,Send):-
      var(Send) ->
	 throw(cyc_error('Unbound SubL message',Send));
         is_list(Send) ->
	    formatCyc(OutStream,'~s~n',[Send]);
	       atom(Send) -> formatCyc(OutStream,'~w~n',[Send]);
	       compound(Send) ->
      	       (toCycApiExpression(Send,[],STerm),formatCyc(OutStream,'~w~n',[STerm]));
%	       throw(cyc_error('SubL message type not supported',Send)),
	       	       formatCyc(OutStream,'~w~n',[Send]).


formatCyc(OutStream,Format,Args):-
      format(OutStream,Format,Args),
      isDebug(format(user_error,Format,Args)),
      flush_output(OutStream),!.

readSubL(InStream,Receive):-
	 read_line_with_nl(InStream, Receive),!.


:-dynamic(isDebug).
isDebug(Call):- isDebug -> Call ; true.

% read_line_with_nl(-Stream,+Codes).
read_line_with_nl(SocketId, Receive):- read_line_with_nl(SocketId, Receive, []).
read_line_with_nl(Fd, Codes, Tail) :-
        get_code(Fd, C0),
        read_line_with_nl(C0, Fd, Codes, Tail).
read_line_with_nl(end_of_file, _, Tail, Tail) :- !.
read_line_with_nl(-1, _, Tail, Tail) :- !.
read_line_with_nl(10, _, [10|Tail], Tail) :- !.
read_line_with_nl(13, _, [10|Tail], Tail) :- !.
read_line_with_nl(C, Fd, [C|T], Tail) :-
        get_code(Fd, C2),
        read_line_with_nl(C2, Fd, T, Tail).

      
is_string([A,B|_]):-integer(A),integer(B).

receiveCodes(ReceiveCodes,Receive):-atom_codes(Receive,ReceiveCodes).

toCycApiExpression(Prolog,Vars,Chars):-var(Prolog),!,toCycVar(Prolog,Vars,Chars).
toCycApiExpression(Prolog,Vars,Prolog):-(atom(Prolog);number(Prolog)),!.
toCycApiExpression(Prolog,Vars,Chars):-is_string(Prolog),!,sformat(Chars,'"~s"',[Prolog]).
toCycApiExpression([P|List],Vars,Chars):-
			toCycApiExpression_l([P|List],Vars,Term),
			sformat(Chars,'\(~w)',[Term]).
toCycApiExpression(quote(List),Vars,Chars):-
			toCycApiExpression(List,Vars,Term),
			sformat(Chars,'\'~w',[Term]).
toCycApiExpression(Prolog,Vars,Chars):-compound(Prolog),!,
			Prolog=..[P|List],
			toCycApiExpression_l(List,Vars,Term),
			(P = holds ->
			   sformat(Chars,'(~w)',[Term]);
			   sformat(Chars,'(~w ~w)',[P,Term])).

toCycApiExpression_l([],Vars,''):-!.
toCycApiExpression_l([A],Vars,Chars):-toCycApiExpression(A,Vars,Chars),!.
toCycApiExpression_l([A|Rest],Vars,Chars):-
      toCycApiExpression(A,Vars,Chars1),
      toCycApiExpression_l(Rest,Vars,Chars2),
      sformat(Chars,'~w ~w',[Chars1,Chars2]),!.

toCycVar(Var,[VV|_],NameQ):-nonvar(VV),VV=..[_,Name,VarRef],
   Var==VarRef,!,sformat(NameQ,'?~w',[Name]).
toCycVar(Var,[_|Rest],Name):-nonvar(Rest),toCycVar(Var,Rest,Name).
toCycVar(VAR,_,VarName):-
      term_to_atom(VAR,AVAR),
      atom_codes(AVAR,[95|CODES]),!,
      catch(sformat(VarName,'?HYP-~s',[CODES]),_,VarName='?HYP-VAR').


cycReset:-discontinueConnection.
   

% ===================================================================
%  Cyc Assert
% ===================================================================

cycAssert(CycL,Mt):-
      retractall(cached_query(_,_)),
      cyclifyNew(CycL,CycLGood),
      cyclify(Mt,MtGood),
      invokeSubL('CYC-ASSERT'(quote(CycLGood),MtGood)).

% ===================================================================
%  Cyc Unassert/Retract
% ===================================================================

cycRetract(CycL,Mt):-cycUnassert(CycL,Mt).
cycUnassert(CycL,Mt):-
      retractall(cached_query(_,_)),
      cyclifyNew(CycL,CycLGood),
      cyclify(Mt,MtGood),
      invokeSubL('CYC-UNASSERT'(quote(CycLGood),MtGood)).

% ===================================================================
%  Cyc Query
% ===================================================================
     
isDebug.

cycQuery(CycL):-cycQuery(CycL,'#$EverythingPSC',Result).
cycQuery(CycL,Mt):-cycQuery(CycL,Mt,Result).

cycQuery(CycL,Mt,Result):-
      copy_term(CycL,Copy),
      numbervars(Copy,'$VAR',0,_),!,
      cycQuery(Copy,CycL,Mt,Result).

:-dynamic(cachable_query/1).
:-dynamic(cached_query/2).

cachable_query(isa(_,_)).

cycQuery(Copy,CycL,Mt,Result):-cached_query(Copy,Results),!,
      member(CycL,Results).
cycQuery(Copy,CycL,Mt,Result):-cachable_query(Copy),!,
      findall(CycL,cycQueryReal(CycL,Mt,Result),Save),
      asserta(cached_query(CycL,Save)),!,
      member(CycL,Save).
cycQuery(Copy,CycL,Mt,Result):-
      cycQueryReal(CycL,Mt,Result).

cycQueryReal(CycL,Mt,Result):-
      getCycConnection(SocketId,OutStream,InStream),
      cyclify(CycL,CycLGood),
      cyclify(Mt,MtGood),
      printSubL(OutStream,'CYC-QUERY'(quote(CycLGood),MtGood)),
      get_code(InStream,A),
      get_code(InStream,B),
      get_code(InStream,C),
      get_code(InStream,D),
      free_variables(CycLGood,Vars),
      get_code(InStream,E),!,% Takes the first paren
      repeat,
      (peek_code(InStream,PCode), 
      isDebug(format('PCODE (~q)~n',[PCode])),
      ((member(PCode,[35,73]),finishCycConnection(SocketId,OutStream,InStream),!,fail);true), % 35 is No
      ((PCode=78,finishCycConnection(SocketId,OutStream,InStream),!);(    % 78 is Yes
      readCycL(InStream,Trim),
      peek_code(InStream,Code), 
      isDebug(format('"~s" (~q)~n',[Trim,Code])),
      ((Code\=32,!,finishCycConnection(SocketId,OutStream,InStream));(true)),
      getSurfaceFromChars(Trim,IResult,_),
      IResult=[Result],
      syncCycLVars(Result,Vars)))).

syncCycLVars(_,[]).
syncCycLVars([[_, '.', Binding]|T],[Binding|VV]):-syncCycLVars(T,VV),!.
syncCycLVars([[_|Binding]|T],[Binding|VV]):-syncCycLVars(T,VV),!.

   

      
      
      

cyclify(Same,Same):-var(Same);number(Same).
cyclify([],[]).
cyclify([H|T],Term):-integer(H) -> Term=[H|T]; cyclify_l([H|T],Term).
cyclify(Before,After):-atom(Before),
      sub_atom(Before,0,1,_,F),!,
      cyclify(F,Before,After).
cyclify(Before,After):-
      Before=..[B|BL],
      cyclify(B,A),
      cyclify_l(BL,AL),
      After=..[A|AL].

cyclify('#',Before,Before).
cyclify('?',Before,Before).
cyclify('"',Before,Before).
cyclify(_,Before,After):-atom_concat('#$',Before,After).
      
cyclify_l([B],[A]):-cyclify(B,A),!.
cyclify_l([],[]).
cyclify_l([B|BL],[A|AL]):-
      cyclify(B,A),
      cyclify_l(BL,AL).


cyclifyNew(Same,Same):-var(Same);number(Same).
cyclifyNew([],[]).
cyclifyNew([H|T],Term):-integer(H) -> Term=[H|T]; cyclifyNew_l([H|T],Term).
cyclifyNew(Before,After):-atom(Before),
      sub_atom(Before,0,1,_,F),!,
      cyclifyNew(F,Before,After).
cyclifyNew(Before,After):-
      Before=..[B|BL],
      cyclifyNew(B,A),
      cyclifyNew_l(BL,AL),
      After=..[A|AL].

cyclifyNew('#',Before,Before).
cyclifyNew('?',Before,Before).
cyclifyNew('"',Before,Before).
cyclifyNew(_,Before,After):-atom_concat('#$',Before,After),makeConstant(Before).

makeConstant(Const):-
   sformat(String,'(CREATE-CONSTANT "~w")',[Const]),
   catch( invokeSubL(String),_,true).

      
cyclifyNew_l([B],[A]):-cyclifyNew(B,A),!.
cyclifyNew_l([],[]).
cyclifyNew_l([B|BL],[A|AL]):-
      cyclifyNew(B,A),
      cyclifyNew_l(BL,AL).

      
% ===================================================================
% ===================================================================
sendNote(To,From,Subj,Msg):-
   format('~q ~q ~q ~q ~n',[To,From,Subj,Msg]).
      
% ===================================================================
% ===================================================================
% ===================================================================


isSlot(Var):-var(Var).
isSlot('$VAR'(Var)):-number(Var).


:-dynamic reading_in_comment/0.
:-dynamic reading_in_string/0.
:-dynamic read_in_atom/0.
:-dynamic prev_char/1.

% ===================================================================
% CycL Term Reader
% ===================================================================
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
	%put(user_error,C),flush_output(user_error),
	cyclReadStateChange(C),readCycLChars_p1(C,Char,Stream,Chars),!.
	
readCycLChars_p1(C,Char,Stream,[]):- at_end_of_stream(Stream),!.
readCycLChars_p1(C,Char,Stream,[]):-isCycLTerminationStateChar(C,Char),!.
readCycLChars_p1(C,Char,Stream,Chars):-cyclAsciiRemap(C,Char),readCycLChars_p0(Stream,Chars),!.

isCycLTerminationStateChar(10,32):-reading_in_comment,!.
isCycLTerminationStateChar(13,32):-reading_in_comment,!.
isCycLTerminationStateChar(41,41):-flag('bracket_depth',X,X),(X<1),!.

cyclReadStateChange(_):- reading_in_comment,!.
cyclReadStateChange(34):- (retract(reading_in_string) ; assert(reading_in_string)),!.
cyclReadStateChange(_):- reading_in_string,!.
cyclReadStateChange(59):- assert(reading_in_comment),!.
cyclReadStateChange(40):-!,flag('bracket_depth',N,N + 1).
cyclReadStateChange(41):-!,flag('bracket_depth',N,N - 1).
cyclReadStateChange(_).

skipCycLChar(Stream):- get_char(Stream,_),!.

cyclAsciiRemap(N,32):-not(number(N)),!.
cyclAsciiRemap(X,32):-X<32,!.
cyclAsciiRemap(X,32):-X>128,!.
cyclAsciiRemap(X,X):-!.


% ===================================================================
% CycL Term Parser
% ===================================================================
/*===================================================================
% getSurfaceFromChars/3 is does less consistantsy checking then conv_to_sterm

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

getSurfaceFromChars([CH|ARSIn],TERM,VARS):-!, 
         %getCleanCharsWhitespaceProper(CHARSIn,NoWhiteCHARS),!,  
         (trim([CH|ARSIn],CHARS)),!,
              CHARS=[FC|REST],!,
          (( 
            (FC=59,TERM=[comment,REST], VARS= _ ) ;   % ";" Comment Char found in Line
            (CHARS=[],TERM=nil,VARS=_,! 	  )    %String came empty
            ;
            (FC=40,getSurfaceFromCharBalanced(CHARS,TERM,VARS) ,! )    %Use vanila CycL parser
            ;
            ( TERM=[comment,[FC|REST]],VARS= _,! )     %All above methods of parsing failed.. Convert to comment
            )).
	    
getSurfaceFromChars(C,TERM,VARS):-string_to_list(C,List),!,getSurfaceFromChars(List,TERM,VARS),!.


getSurfaceFromCharBalanced(Chars,WFFOut,VARSOut):- 
    retractall(var_counter(_)),retractall(numbered_var(_,_,_)),asserta(var_counter(0)), 
               getCycLTokens(Chars,Tokens), 
               clean_sexpression(Tokens,WFFClean),!,
               phrase(cycL(WFF),WFFClean),
               collect_temp_vars(VARS),!,
               ( 
                     (VARS=[],VARSOut=_,WFFOut=WFF)
               ;
                     (
                     unnumbervars(VARS,LIST),
                     cyclVarNums(LIST,WFF,WFFOut,VARSOut2) ,
                     list_to_set(VARSOut2,VARSOut1),
                     open_list(VARSOut1,VARSOut)
                     ) 
               ),!.

/*===================================================================
% clean_sexpression(Tokens,CleanTokens)

Removes out STANDARD tokens

====================================================================*/

clean_sexpression([],[]).
clean_sexpression(['#$'|WFF],WFFClean):-clean_sexpression(WFF,WFFClean).
clean_sexpression(['#'|WFF],WFFClean):-clean_sexpression(WFF,WFFClean).
clean_sexpression(['$'|WFF],WFFClean):-clean_sexpression(WFF,WFFClean).
clean_sexpression([E|WFF],[E|WFFClean]):-clean_sexpression(WFF,WFFClean).


%isCharCodelist([]):-!.
%isCharCodelist([H|T]):-!,integer(H),isCharCodelist(T).

/*===================================================================
% S-Expression Version of ISO-Prolog chars_to_tem/3
====================================================================*/
chars_to_term_s(CHARS,TERM,VARS):-
             once(chars_to_term(CHARS,PTERM,VARS)),
             once(pterm_to_sterm(PTERM,TERM)).


/*===================================================================
% Safe Entry Call Into ISO-Prolog tokenize_chars/2
====================================================================*/

getCycLTokens(X,Z):-is_list(X),!,  tokenize_chars(X,Y),convert_the_atoms(Y,Z).

convert_the_atoms([],[]):-!.
convert_the_atoms([H|T],[HH|TT]):-!,  
                convert_the_atom(H,HH),
                convert_the_atoms(T,TT).

%convert_the_atom(H,HH):-atom_codes(H,[34|Rest]),reverse(Rest,[_|AtomCharsR]),reverse(AtomCharsR,AtomChars),atom_codes(HH,AtomChars).
%convert_the_atom(H,HH):-atom_codes(H,[39|Rest]),reverse(Rest,[_|AtomCharsR]),reverse(AtomCharsR,AtomChars),atom_codes(HH,AtomChars).
convert_the_atom(H,H):-!.


getCycLTokens(X,[X]). 

/*===================================================================
% Removes Leading whitespaces and not ANSI charset
====================================================================*/
trim(X,Y):-ltrim(X,R),reverse(R,Rv),ltrim(Rv,RY),reverse(RY,Y).

ltrim([],[]):-!.
ltrim([32,32,32,32,32,32,32|String],Out) :-!, trim(String,Out),!.
ltrim([32,32,32,32,32|String],Out) :- !,trim(String,Out),!.
ltrim([32,32,32|String],Out) :-!, trim(String,Out),!.
ltrim([32,32|String],Out) :- !,trim(String,Out),!.
ltrim([32,32],[]) :- !.
ltrim([P|X],Y):-P<33,trim(X,Y),!.
ltrim([P|X],Y):-P>128,trim(X,Y),!.
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
% ISO-Prolog STRING TOKENIZATION                            
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
                                                                
