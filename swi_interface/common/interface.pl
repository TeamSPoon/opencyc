% ===================================================================
% File 'interface.pl'
% Purpose: Lower-level connection based utilities for interfacing to OpenCyc from SWI-Prolog
% Maintainer: Douglas Miles
% Contact: $Author$@users.sourceforge.net ;
% Version: 'interface.pl' 1.0.0
% Revision:  $Revision$
% Revised At:   $Date$
% ===================================================================

:-module(opencyc,[
	 getCycConnection/3,
	 finishCycConnection/3,
	 invokeSubL/1,
	 invokeSubL/2,
	 invokeSubLRaw/2,
	 listCycStats/0,
	 printSubL/2,
	 formatCyc/3,
	 toCycApiExpression/2,
	 toCycApiExpression/3,
	 cycQuery/1,
	 cycQuery/2,
	 cycAssert/1,
	 cycAssert/2,
	 cycRetract/1,
	 cycRetract/2,
	 cycRetractAll/1,
	 cycRetractAll/2,
	 isDebug/0,
	 makeConstant/1,
	 ensureMt/1,
	 cyclify/2,
	 cyclifyNew/2,
	 defaultMt/1,
	 mtForPred/2,
	 isRegisterCycPred/3,
	 registerCycPred/1,
	 registerCycPred/2,
	 registerCycPred/3,
	 assertThrough/1,
	 assertThrough/2,
	 retractAllThrough/1,
	 retractAllThrough/2,
	 testOpenCyc/0]).

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
      isDebug((format(user_error,'Connected to Cyc TCP Server {~w,~w}\n',[InStream,OutStream]),flush_output(user_error))),
      assertz(cycConnectionUsed(SocketId,OutStream,InStream)),!.

finishCycConnection(SocketId,OutStream,InStream):-
      (at_end_of_stream(InStream);readLineNl(InStream, Receive)),
      retractall(cycConnectionUsed(SocketId,OutStream,InStream)),
      asserta(cycConnection(SocketId,OutStream,InStream)),!.
      
listCycStats:- % will add more 
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
      isDebug(format('~s',[Receive])).

invokeSubL(Send,Receive):-
      invokeSubLRaw(Send,ReceiveCodes),
      atom_codes(Receive,ReceiveCodes).

invokeSubLRaw(Send,Receive):-
      getCycConnection(SocketId,OutStream,InStream),
      printSubL(OutStream,Send),
      readSubL(InStream,[A,B,C,D|Receive]),!,
      finishCycConnection(SocketId,OutStream,InStream),!,
      checkSubLError(Send,[A,B,C,D|Receive]).

checkSubLError(Send,[53,48,48,32|Info]):- %Error "500 "
      atom_codes(ErrorMsg,Info),
      throw(cyc_error(ErrorMsg,Send)).
checkSubLError(_,_).

% ===================================================================
% Lowlevel printng
% ===================================================================

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
	 readLineNl(InStream, Receive),!.

% ===================================================================
% Lowlevel readline
% ===================================================================

% readLineNl(-Stream,+Codes).
readLineNl(SocketId, Receive):- readLineNl(SocketId, Receive, []).
readLineNl(Fd, Codes, Tail) :-
        get_code(Fd, C0),
        readLineNl(C0, Fd, Codes, Tail).
readLineNl(end_of_file, _, Tail, Tail) :- !.
readLineNl(-1, _, Tail, Tail) :- !.
readLineNl(10, _, [10|Tail], Tail) :- !.
readLineNl(13, _, [10|Tail], Tail) :- !.
readLineNl(C, Fd, [C|T], Tail) :-
        get_code(Fd, C2),
        readLineNl(C2, Fd, T, Tail).

% ===================================================================
%  conversion toCycApiExpression
% ===================================================================

toCycApiExpression(Prolog,CycLStr):-toCycApiExpression(Prolog,[],CycLStr).

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

is_string([A,B|_]):-integer(A),integer(B).

% ===================================================================
%  Cyc Assert
% ===================================================================

cycAssert(Mt:CycL):-!,
   cycAssert(CycL,Mt).
cycAssert(CycL):-
   mtForPred(CycL,Mt),
   cycAssert(CycL,Mt).

cycAssert(CycL,Mt):-
      retractall(cached_query(_,_)),
      cyclifyNew(CycL,CycLGood),
      cyclify(Mt,MtGood),
      invokeSubL('CYC-ASSERT'(quote(CycLGood),MtGood)).

      
% ===================================================================
%  Cyc Unassert/Retract
% ===================================================================
cycRetract(CycL,Mt):-cycRetractAll(CycL,Mt).
cycRetract(CycL):-cycRetractAll(CycL).

cycRetractAll(CycL):-
      mtForPred(CycL,Mt),
      cycUnassert(CycL,Mt).

cycRetractAll(CycL,Mt):-cycUnassert(CycL,Mt).
cycUnassert(CycL,Mt):-
      retractall(cached_query(_,_)),
      cyclifyNew(CycL,CycLGood),
      cyclify(Mt,MtGood),
      invokeSubL('CYC-UNASSERT'(quote(CycLGood),MtGood)).

% ===================================================================
%  Debugging Cyc 
% ===================================================================
     
:-dynamic(isDebug).

% Uncomment this next line to see Cyc debug messages

% isDebug.

isDebug(Call):- isDebug -> Call ; true.


% ===================================================================
%  Cyc Query Cache Control
% ===================================================================


:-dynamic(cachable_query/1).
:-dynamic(cached_query/2).

cachable_query(isa(_,_)).


% ===================================================================
%  Cyc Query
% ===================================================================

cycQuery(CycL):-cycQuery(CycL,'#$EverythingPSC',Result).
cycQuery(CycL,Mt):-cycQuery(CycL,Mt,Result).

cycQuery(CycL,Mt,Result):-
      copy_term(CycL,Copy),
      numbervars(Copy,'$VAR',0,_),!,
      cycQuery(Copy,CycL,Mt,Result).

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

% ===================================================================
%  Cyclification
%
%    cyclify(Before,After)
%     Makes sure that atoms in Statement are prefixed witbh '#$' when comunicationg with Cyc
%
%    cyclifyNew(Before,After)
%     same as cyclify/2 but adds the constant names with (CREATE-CONSTANT "~w")
%
% ===================================================================

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
      
cyclifyNew_l([B],[A]):-cyclifyNew(B,A),!.
cyclifyNew_l([],[]).
cyclifyNew_l([B|BL],[A|AL]):-
      cyclifyNew(B,A),
      cyclifyNew_l(BL,AL).


% ============================================
% Make new CycConstant
% ============================================

makeConstant(Const):-
   sformat(String,'(CREATE-CONSTANT "~w")',[Const]),
   catch( invokeSubL(String),_,true).

% ============================================
% Make new Microtheory
% ============================================

ensureMt(Const):-
   cycAssert(isa(Const,'Microtheory'),'BaseKB').

% ============================================
% dynamic Default Microtheory
% ============================================

:-dynamic(defaultMt/1).

defaultMt('PrologDataMt').

:-defaultMt(Mt),
   ensureMt(Mt). % Puts the defaultMt/1 into Cyc 

% ===================================================================
%  Predicates need and Assertion Mt
% ===================================================================

mtForPred(CycL,Mt):-
   functor(CycL,Pred,_),
   isRegisterCycPred(Mt,Pred,_),!.

mtForPred(CycL,Mt):-defaultMt(Mt).

% ============================================
% Prolog to Cyc Predicate Mapping
%
%  the following will all do the same things:
%
% ?- registerCycPred('BaseKB':isa/2). 
% ?- registerCycPred('BaseKB':isa(_,_)). 
% ?- registerCycPred(isa(_,_),'BaseKB'). 
% ?- registerCycPred('BaseKB',isa,2). 
%
%  Will make calls 
% ?- isa(X,Y)
%  Query into #$BaseKB for (#$isa ?X ?Y) 
%
% ============================================
:-dynamic(isRegisterCycPred/3).

% ?- registerCycPred('BaseKB':isa/2). 
registerCycPred(Mt:Pred/Arity):-!,
   registerCycPred(Mt,Pred,Arity).
% ?- registerCycPred('BaseKB':isa(_,_)). 
registerCycPred(Mt:Term):-
   functor(Term,Pred,Arity),
   registerCycPred(Mt,Pred,Arity).
% ?- registerCycPred(isa(_,_),'BaseKB'). 
registerCycPred(Term,Mt):-
   functor(Term,Pred,Arity),
   registerCycPred(Mt,Pred,Arity).
   
% ?- registerCycPred('BaseKB',isa,2). 
registerCycPred(Mt,Pred,0):-!,registerCycPred(Mt,Pred,2).
registerCycPred(Mt,Pred,Arity):-isRegisterCycPred(Mt,Pred,Arity),!.
registerCycPred(Mt,Pred,Arity):-
      functor(Term,Pred,Arity),
      asserta((Term:-cycQuery(Term,Mt))),
      assertz(isRegisterCycPred(Mt,Pred,Arity)),!.

% ============================================
% Assert Side Effect Prolog to Cyc Predicate Mapping
%
% ?- assert(isa('Fido','Dog')).
% Will assert (#$isa #$Fido #$Dog) into #$BaseKB
%
% ?- assert('DogsMt':isa('Fido','Dog')).
% Will assert (#$isa #$Fido #$Dog) into #$DogsMt
% ============================================
:-redefine_system_predicate(assert(_)).
assert(Term):-assertThrough(Term).

assertThrough(Mt:CycL):-
      assertThrough(Mt,CycL).

assertThrough(CycL):-
      assertThrough(Mt,CycL).

assertThrough(ToMt,CycL):-
      functor(CycL,Pred,Arity),
      isRegisterCycPred(Mt,Pred,Arity),!,
      ignore(ToMt=Mt),
      cycAssert(CycL,ToMt),!.

assertThrough(ToMt,CycL):-
      ignore(ToMt=user),
      assertz(ToMt:CycL),!.

% ============================================
% Retract (All) Side Effect Prolog to Cyc Predicate Mapping
%
% ?- retractall(isa('Fido','Dog')).
% Will retract (#$isa #$Fido #$Dog) from #$BaseKB
%
% ?- retractall('DogsMt':isa('Fido','Dog')).
% Will retract (#$isa #$Fido #$Dog) from #$DogsMt
% ============================================
:-redefine_system_predicate(retractall(_)).
retractall(Term):-retractAllThrough(Term).

retractAllThrough(Mt:CycL):-
      retractAllThrough(Mt,CycL).

retractAllThrough(CycL):-
      retractAllThrough(Mt,CycL).

retractAllThrough(ToMt,CycL):-
      functor(CycL,Pred,Arity),
      isRegisterCycPred(Mt,Pred,Arity),!,
      ignore(ToMt=Mt),
      cycRetract(CycL,ToMt),!.

retractAllThrough(ToMt,CycL):-
      ignore(ToMt=user),
      system:retractall(ToMt:CycL),!.
            
% ============================================
% Register isa/genls (more for testing :)
% ============================================

% examples
:-registerCycPred('BaseKB',isa,2).
:-registerCycPred('BaseKB',genls,2).


% ============================================
% Testing 
% ============================================
      
testOpenCyc:-halt.


