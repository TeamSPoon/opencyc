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
	 cycInit/0,
	 getCycConnection/3,
	 finishCycConnection/3,
	 invokeSubL/1,
	 invokeSubL/2,
	 invokeSubLRaw/2,
	 cycInfo/0,
	 printSubL/2,
	 formatCyc/3,
	 toCycApiExpression/2,
	 toCycApiExpression/3,
	 cycQuery/1,
	 cycQuery/2,
	 toMarkUp/4,
	 cycAssert/1,
	 cycAssert/2,
	 cycRetract/1,
	 balanceBinding/2,
	 cycRetract/2,
	 cycRetractAll/1,
	 cycRetractAll/2,
	 isDebug/0,
	 makeConstant/1,
	 ensureMt/1,
	 readCycL/2,
	 cyclify/2,
	 getCycLTokens/2,
	 cyclifyNew/2,
	 unnumbervars/2,
	 defaultAssertMt/1,
	 mtAssertPred/2,
	 isRegisterCycPred/3,
	 registerCycPred/1,
	 setCycOption/2,
	 isCycOption/2,
	 registerCycPred/2,
	 registerCycPred/3,
         getSurfaceFromChars/3,
	 assertThrough/1,
	 assertThrough/2,
	 retractAllThrough/1,
	 retractAllThrough/2,
	 atom_junct/2,
	 list_to_term/2,
	 testOpenCyc/0]).

:- style_check(-singleton).
:- style_check(-discontiguous).
:- style_check(-atom).
:- style_check(-string).

% ===================================================================
% Cyc initialization - call cycInit. once and this fiule will be loaded if not already
% ===================================================================
cycInit.

% ===================================================================
% Cyc Option Switches
%
%  setCycOption(Var,Value) - sets an option first removing the prevoius value
%
%  isCycOption(Var,Value). - tests for option
%
% ===================================================================
:-dynamic(isCycOption/2).
setCycOption(Var,Value):-
      nonvar(Var),
      retractall(isCycOption(Var,_)),
      asserta(isCycOption(Var,Value)).


% ===================================================================
% Connecter to Cyc TCP Server
% ===================================================================
:-setCycOption(cycServer,'127.0.0.1':3601).

:-dynamic(cycConnection/3).
:-dynamic(cycConnectionUsed/3).
:-dynamic(cycMutex/2).
:-dynamic(cycChatMode/1).

getCycConnection(SocketId,OutStream,InStream):-
      retract(opencyc:cycConnection(SocketId,OutStream,InStream)),
      assertz(opencyc:cycConnectionUsed(SocketId,OutStream,InStream)),!.

getCycConnection(SocketId,OutStream,InStream):-
      tcp_socket(SocketId),
      isCycOption(cycServer,Server),
      tcp_connect(SocketId,Server),
      tcp_open_socket(SocketId, InStream, OutStream),!,
      isDebug((format(user_error,'Connected to Cyc TCP Server {~w,~w}\n',[InStream,OutStream]),flush_output(user_error))),
      assertz(opencyc:cycConnectionUsed(SocketId,OutStream,InStream)),!.

finishCycConnection(SocketId,OutStream,InStream):-
      ignore(system:retractall(opencyc:cycConnectionUsed(SocketId,OutStream,InStream))),
      asserta(opencyc:cycConnection(SocketId,OutStream,InStream)),!.
      
% ===================================================================
% cycInfo. - Prints Cyc Usage info to current output 
% ===================================================================
cycInfo:- % will add more 
   listing(cycConnection),
   listing(cycConnectionUsed),
   listing(cycConstantMade),
   listing(isCycOption).


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
      printSubL(InStream,OutStream,Send),
      readSubL(InStream,Get),!,
      finishCycConnection(SocketId,OutStream,InStream),!,
      checkSubLError(Send,Get,Receive),!.

checkSubLError(Send,[53,48,48,_|Info],Info):-!, %Error "500 "
      atom_codes(ErrorMsg,Info),
      throw(cyc_error(ErrorMsg,Send)).
checkSubLError(_,[_,_,_,_|Info],Info):-!.
checkSubLError(Send,Info,Info).

% ===================================================================
% Lowlevel printng
% ===================================================================

printSubL(InStream,OutStream,Send):-
      popRead(InStream),
      printSubL(OutStream,Send).

printSubL(OutStream,Send):-     
      (var(Send) ->
	 throw(cyc_error('Unbound SubL message',Send));
         is_list(Send) ->
	    formatCyc(OutStream,'~s~n',[Send]);
	       atom(Send) -> formatCyc(OutStream,'~w~n',[Send]);
	       compound(Send) ->
      	       (toCycApiExpression(Send,[],STerm),formatCyc(OutStream,'~w~n',[STerm]));
%	       throw(cyc_error('SubL message type not supported',Send)),
	       	       formatCyc(OutStream,'~w~n',[Send])),!.


formatCyc(OutStream,Format,Args):-
      format(OutStream,Format,Args),
      isDebug(format(user_error,Format,Args)),
      flush_output(OutStream),!.

readSubL(InStream,[G,E,T,Space|Response]):-
      get_code(InStream,G),
      get_code(InStream,E),
      get_code(InStream,T),
      get_code(InStream,Space),
      readCycLTermChars(InStream,Response),!.

% ===================================================================
% Lowlevel readCycLTermChars
% ===================================================================
readCycLTermChars(InStream,Response):-
   notrace(readCycLTermChars(InStream,Response,_)).
   

readCycLTermChars(InStream,[Start|Response],Type):-
   peek_code(InStream,Start),
   readCycLTermCharsUntil(Start,InStream,Response,Type),
   isDebug(format('cyc>~s (~w)~n',[Response,Type])).

readCycLTermCharsUntil(34,InStream,Response,string):-!,
   get_code(InStream,_),
   readUntil(34,InStream,Response),
   popRead(InStream).

readCycLTermCharsUntil(35,InStream,[35|Response],term):-!,
   get_code(InStream,_),
   readUntil(10,InStream,Response),
   popRead(InStream).

readCycLTermCharsUntil(84,InStream,"T",true):-!,
   popRead(InStream).

readCycLTermCharsUntil(78,InStream,"N",nil):-!,
   popRead(InStream).

readCycLTermCharsUntil(40,InStream,Trim,cons):-!,
   readCycL(InStream,Trim),
   popRead(InStream).

% needs better solution!  .01 seconds works but .001 seconds don't :(  meaning even .01 might in some circumstances be unreliable
popRead(InStream) :- once(wait_for_input([InStream], Inputs,0.01)),Inputs=[],!.
popRead(InStream) :-get_code(InStream, _),popRead(InStream).

readUntil(Char,InStream,Response):-
      get_code(InStream,C),
      readUntil(Char,C,InStream,Response).
      
readUntil(Char,Char,InStream,[]):-!.
readUntil(Char,C,InStream,[C|Out]):-get_code(InStream,Next),
   readUntil(Char,Next,InStream,Out).


      
% ===================================================================
%  conversion toCycApiExpression
% ===================================================================
toMarkUp(_,Term,Vars,Out):-
   toCycApiExpression(Term,Vars,Out),!.


toCycApiExpression(Prolog,CycLStr):-toCycApiExpression(Prolog,[],CycLStr).

toCycApiExpression(Prolog,Vars,Chars):-var(Prolog),!,toCycVar(Prolog,Vars,Chars).
toCycApiExpression(Prolog,Vars,Prolog):-(atom(Prolog);number(Prolog)),!.
toCycApiExpression(Prolog,Vars,Chars):-is_string(Prolog),!,sformat(Chars,'"~s"',[Prolog]).
toCycApiExpression(Prolog,Vars,Chars):-string(Prolog),!,sformat(Chars,'"~s"',[Prolog]).
toCycApiExpression([P|List],Vars,Chars):-
			toCycApiExpression_l([P|List],Vars,Term),
			sformat(Chars,'\'(~w)',[Term]).
toCycApiExpression(nv(List),Vars,Chars):-toCycApiExpression_l(List,Vars,Chars),!.
toCycApiExpression([nv|List],Vars,Chars):-toCycApiExpression_l(List,Vars,Chars),!.
toCycApiExpression(string(List),Vars,Chars):-
			toCycApiExpression(List,Vars,Term),
			sformat(Chars,'"~w"',[Term]).
toCycApiExpression(quote(List),Vars,Chars):-
			toCycApiExpression(List,Vars,Term),
			sformat(Chars,'\'~w',[Term]).
toCycApiExpression(Prolog,Vars,Chars):-compound(Prolog),!,
		  Prolog=..[P|List],
		  toCycApiExpression(P,List,Vars,Chars).

toCycApiExpression((holds),List,Vars,Chars):-
	       toCycApiExpression_l(List,Vars,Term),
	       sformat(Chars,'(~w)',[Term]).
toCycApiExpression((';'),List,Vars,Chars):-
	       toCycApiExpression_l(List,Vars,Term),
	       sformat(Chars,'(#$or ~w)',[Term]).
toCycApiExpression((','),List,Vars,Chars):-
	       toCycApiExpression_l(List,Vars,Term),
	       sformat(Chars,'(#$and ~w)',[Term]).
toCycApiExpression((':-'),[A,B],Vars,Chars):-
	       toCycApiExpression(A,Vars,TA),
	       toCycApiExpression(B,Vars,TB),
	       sformat(Chars,'(#$enables-ThingProp ~w ~w)',[TB,TA]). % ? enables-Generic ?
toCycApiExpression(('=>'),[A,B],Vars,Chars):-
	       toCycApiExpression(A,Vars,TA),
	       toCycApiExpression(B,Vars,TB),
	       sformat(Chars,'(#$implies ~w ~w)',[TA,TB]). 
toCycApiExpression(('<=>'),[A,B],Vars,Chars):-
	       toCycApiExpression(A,Vars,TA),
	       toCycApiExpression(B,Vars,TB),
	       sformat(Chars,'(#$equiv ~w ~w)',[TA,TB]). 
toCycApiExpression(P,List,Vars,Chars):-
	       toCycApiExpression_l(List,Vars,Term),
	       sformat(Chars,'(~w ~w)',[P,Term]).

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
%  Debugging Cyc 
% ===================================================================
     
:-dynamic(isDebug).

% Uncomment this next line to see Cyc debug messages

isDebug.

isDebug(Call):- isDebug -> Call ; true.


% ===================================================================
%  Cyc Query Cache Control
% ===================================================================


:-dynamic(cachable_query/1).
:-dynamic(cached_query/2).

cachable_query(isa(_,_)).

% ===================================================================
%  Cyc Assert
% ===================================================================

cycAssert(Mt:CycL):-!,
   cycAssert(CycL,Mt).
cycAssert(CycL):-
   mtAssertPred(CycL,Mt),
   cycAssert(CycL,Mt).

cycAssert(CycL,Mt):-
      system:retractall(opencyc:cached_query(_,_)),
      cyclifyNew(CycL,CycLified),
      cyclify(Mt,Mtified),
      defaultAssertOptions(DefaultOptions), 
      toCycApiExpression('CYC-ASSERT'(quote(CycLified),Mtified,(DefaultOptions)),API),
      invokeSubL(API),!.

defaultAssertOptions(Opts):-isCycOption(defaultAssertOptions,Opts).


:-setCycOption(defaultAssertOptions,[':DIRECTION', ':FORWARD', ':STRENGTH', ':MONOTONIC']).
:-setCycOption(':DIRECTION', ':FORWARD').
:-setCycOption(':STRENGTH', ':MONOTONIC').

      
% ===================================================================
%  Cyc Unassert/Retract
% ===================================================================
cycRetract(CycL,Mt):-cycRetractAll(CycL,Mt).
cycRetract(CycL):-cycRetractAll(CycL).

cycRetractAll(CycL):-
      mtAssertPred(CycL,Mt),
      cycUnassert(CycL,Mt).

cycRetractAll(CycL,Mt):-cycUnassert(CycL,Mt).
cycUnassert(CycL,Mt):-
      system:retractall(opencyc:cached_query(_,_)),
      cyclifyNew(CycL,CycLified),
      cyclify(Mt,Mtified),
      invokeSubL('CYC-UNASSERT'(quote(CycLified),Mtified)).


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
      (Save=[] -> true ; asserta(cached_query(CycL,Save))),!,
      member(CycL,Save).
cycQuery(Copy,CycL,Mt,Result):-
      /*notrace*/(cycQueryReal(CycL,Mt,Result)).

/*
	  (clet ((*cache-inference-results* t)
	    (*allow-forward-skolemization*t)  
	    (*compute-inference-results* nil)  
	    (*unique-inference-result-bindings* t) 
	    (*generate-readable-fi-results* t))
	    (without-wff-semantics
	       (ask-template '(?SEL1 ?SEL2)  '?Formula #$BaseKB 0 nil nil nll )) )
	       
*/
%queryParams(Backchain,Number,Time,Depth).
%queryParams(0,	nil,	nil,	nil). % default
%queryParams(1,	nil,	nil,	nil). % used here

queryParams(Backchain,Number,Time,Depth):-
   isCycOption(query(backchains),Backchain),
   isCycOption(query(number),Number),
   isCycOption(query(time),Time),
   isCycOption(query(depth),Depth),!.

:-setCycOption(query(backchains),1).
:-setCycOption(query(number),nil).
:-setCycOption(query(time),nil). %max ten seconds
:-setCycOption(query(depth),nil).

cycQueryReal(CycL,Mt,Result):-
      getCycConnection(SocketId,OutStream,InStream),
      popRead(InStream),
      cyclify(CycL,CycLified),
      cyclify(Mt,Mtified),
      free_variables(CycLified,Vars),
      %  backchain number time depth
      queryParams(Backchain,Number,Time,Depth),
      printSubL(OutStream,
	       clet('((*cache-inference-results* t)(*compute-inference-results* nil)(*unique-inference-result-bindings* t)(*generate-readable-fi-results* t))',
		     'without-wff-semantics'('ask-template'(Vars,quote(CycLified),Mtified,Backchain,Number,Time,Depth)))),
      get_code(InStream,G),
      get_code(InStream,E),
      get_code(InStream,T),
      get_code(InStream,Space),
      get_code(InStream,FirstParen),!,% Takes the first paren
      call_cleanup(cycQueryResults(SocketId,InStream,OutStream,CycL,Mt,CycLified,Mtified,Vars,Result),
	    Exit,queryCleanup(Exit,SocketId,OutStream,InStream)).


queryCleanup(Exit,SocketId,OutStream,InStream):-
      finishCycConnection(SocketId,OutStream,InStream),!.

cycQueryResults(SocketId,InStream,OutStream,CycL,Mt,CycLified,Mtified,Vars,Result):-
      repeat,
      peek_code(InStream,PCode),%  isDebug(format('PCODE (~q)~n',[PCode])),
      cycQueryEachResult(PCode,InStream,Vars,Result,Cut),
      ((Cut==cut,!);(Cut==fail,!,fail);true).

cycQueryEachResult(35,InStream,Vars,Result,fail). % No solutions at all
cycQueryEachResult(73,InStream,Vars,Result,fail). % Depth limit exceeded
cycQueryEachResult(41,InStream,Vars,Result,fail).  % The previous solution was the last
cycQueryEachResult(78,InStream,Vars,Result,cut). % True
cycQueryEachResult(__,InStream,Vars,Result,more):-
      readCycL(InStream,Trim), %isDebug(format('"~s"~n',[Trim])),
      getSurfaceFromChars(Trim,[Result],_),%isDebug(format('~q.~n',[Result])),
      syncCycLVars(Result,Vars),!.

syncCycLVars(_,[]).
syncCycLVars([Binding|T],[PBinding|VV]):-
      balanceBinding(Binding,PBinding),syncCycLVars(T,VV),!.

%list_to_term(X,Y):- balanceBinding(X,Y).
list_to_term(X,Y):-nonvar(X),var(Y),!,list_to_terms_lr(X,Y).
list_to_term(X,Y):-list_to_terms_rl(X,Y).
list_to_terms_rl(List,(A,B)):-list_to_terms_rl(A,AL),list_to_terms_rl(B,BL),append(AL,BL,List).
list_to_terms_rl(List,(A;B)):-list_to_terms_rl(A,AL),list_to_terms_rl(B,BL),append(AL,[or|BL],List).
list_to_terms_lr([],true):-!.
list_to_terms_lr([T],T):-!.
list_to_terms_lr([H|T],(H,TT)):-!,list_to_terms_lr(T,TT).
   


balanceBinding(Binding,Binding):- (var(Binding);atom(Binding);number(Binding)),!.
balanceBinding(string(B),string(B)):-!.
balanceBinding([A|L],Binding):-balanceBindingCons(A,L,Binding).
balanceBinding(Binding,Binding):-!.
 
balanceBindingCons(A,L,[A|L]):- (var(A);A=string(_)),!.
balanceBindingCons('and-also',L,Binding):-balanceBindingS(L,LO), list_to_term(LO,Binding),!.
balanceBindingCons('#$and-also',L,Binding):-balanceBindingS(L,LO), list_to_term(LO,Binding),!.

balanceBindingCons(A,L,Binding):-
	 balanceBinding(A,AO),
	 balanceBindingS(L,LO),
	 Binding=..[AO|LO],!.

balanceBindingS(Binding,Binding):- (var(Binding);atom(Binding);number(Binding)),!.
balanceBindingS([],[]).
balanceBindingS([A|L],[AA|LL]):-balanceBinding(A,AA),balanceBindingS(L,LL).
   
% ===================================================================
%  Cyclification
%
%    cyclify(Statement,Cyclified)
%     Makes sure that atoms in Statement are prefixed witbh '#$' when comunicationg with Cyc
%
%    cyclifyNew(Statement,Cyclified)
%     same as cyclify/2 but adds the constant names with (CREATE-CONSTANT "~w")
%
% ===================================================================

cyclify(Same,Same):- (var(Same);number(Same);string(Same)),!.
cyclify([],[]).
cyclify([H|T],Term):-integer(H) -> Term=[H|T]; cyclify_l([H|T],Term).
cyclify(Before,After):-atom(Before),
      sub_atom(Before,0,1,_,F),!,
      cyclify(F,Before,After).
cyclify(Before,After):- Before=..[B|BL], cyclify(B,A), cyclify_l(BL,AL), After=..[A|AL].

cyclify(Char,Before,After):-cyclifyAtom(Char,Before,After).
cyclify(_,Before,After):-atom_concat('#$',Before,After).
      
cyclify_l([B],[A]):-cyclify(B,A),!.
cyclify_l([],[]).
cyclify_l([B|BL],[A|AL]):-cyclify(B,A),cyclify_l(BL,AL).


cyclifyNew(Same,Same):-var(Same);number(Same).
cyclifyNew([],[]).
cyclifyNew([H|T],Term):-integer(H) -> Term=[H|T]; cyclifyNew_l([H|T],Term).
cyclifyNew(Before,After):-atom(Before),
      sub_atom(Before,0,1,_,F),!,
      cyclifyNew(F,Before,After).
cyclifyNew(Before,After):- Before=..[B|BL], cyclifyNew(B,A), cyclifyNew_l(BL,AL), After=..[A|AL].

cyclifyNew(Char,Before,After):-cyclifyAtom(Char,Before,After).
cyclifyNew(_,Before,After):-atom_concat('#$',Before,After),makeConstant(Before).
      
cyclifyNew_l([B],[A]):-cyclifyNew(B,A),!.
cyclifyNew_l([],[]).
cyclifyNew_l([B|BL],[A|AL]):-cyclifyNew(B,A),cyclifyNew_l(BL,AL).

cyclifyAtom('#',Before,Before).
cyclifyAtom('?',Before,Before).
cyclifyAtom(':',Before,Before).
cyclifyAtom('(',Before,Before).
cyclifyAtom('!',Before,After):-atom_concat('!',After,Before).
cyclifyAtom('"',Before,Before).

% ============================================
% Make new CycConstant
% ============================================

:-dynamic(cycConstantMade/1).

makeConstant(Const):-
   (cycConstantMade(Const)->true;
   (sformat(String,'(CREATE-CONSTANT "~w")',[Const]),
   catch(invokeSubL(String),_,true),
   asserta(cycConstantMade(Const)))),!.

% ============================================
% Make new Microtheory
% ============================================

ensureMt(Const):-
   cycAssert('#$BaseKB':'#$isa'(Const,'#$Microtheory')).

% ============================================
% dynamic Default Microtheory
% ============================================

:-dynamic(defaultAssertMt/1).
:-dynamic(everythingMt/1).

defaultAssertMt('PrologDataMt').
everythingMt('#$EverythingPSC').

%:-defaultAssertMt(Mt),!,ensureMt(Mt),cycAssert('#$BaseKB':'#$genlMt'(Mt,'#$InferencePSC')). % Puts the defaultAssertMt/1 into Cyc 
:-defaultAssertMt(Mt),!,
   ensureMt(Mt),
   everythingMt(EverythingPSC),
   cycAssert('#$BaseKB':'#$genlMt'(Mt,EverythingPSC)). % Puts the defaultAssertMt/1 into Cyc 

% ===================================================================
%  Predicates need and Assertion Mt
% ===================================================================

mtAssertPred(CycL,Mt):-nonvar(CycL),
   functor(CycL,Pred,_),
   isRegisterCycPred(Mt,Pred,_),!.

mtAssertPred(CycL,Mt):-defaultAssertMt(Mt).

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
registerCycPred(Term):-
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
      ignore(defaultAssertMt(Mt)),
      asserta(( user:Term :- cycQuery(Term,Mt))),
      %asserta(( Mt:Term :- cycQuery(Term,Mt))),
      assertz(isRegisterCycPred(Mt,Pred,Arity)),!.


% ============================================
% Assert Side Effect Prolog to Cyc Predicate Mapping
%
% ============================================
:-setCycOption(hookCycPredicates,true).

user:exception(undefined_predicate, Pred ,retry):-
      isCycOption(hookCycPredicates,true),cycDefineOrFail(Pred).

cycDefineOrFail(Mod:Pred/Arity):-atom_concat('#$',_,Pred),
      cycDefineOrFail(Mod,Pred,Arity).
cycDefineOrFail(Pred/Arity):-atom_concat('#$',_,Pred),registerCycPred(Mod,Pred,Arity).

cycDefineOrFail(Mod,Pred,Arity):-
      atom_concat('#$',_,Mod),
      registerCycPred(Mod,Pred,Arity).
cycDefineOrFail(_,Pred,Arity):-
      registerCycPred(_,Pred,Arity).

% ============================================
% Assert Side Effect Prolog to Cyc Predicate Mapping
%
% ?- assert(isa('Fido','Dog')).
% Will assert (#$isa #$Fido #$Dog) into #$BaseKB
%
% ?- assert('DogsMt':isa('Fido','Dog')).
% Will assert (#$isa #$Fido #$Dog) into #$DogsMt
% ============================================
:-redefine_system_predicate(system:assert(_)).
system:assert(Term):-
      nonvar(Term),
      assertThrough(Term).

assertThrough(Mt:CycL):-
      assertThrough(Mt,CycL).

assertThrough(CycL):-
      assertThrough(Mt,CycL).

assertThrough(ToMt,CycL):-
      functor(CycL,Pred,Arity),
      (isRegisterCycPred(Mt,Pred,Arity);atom_concat('#$',_,Pred)),!,
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
:-registerCycPred('#$BaseKB',isa,2).
:-registerCycPred('#$BaseKB',genls,2).
:-registerCycPred('#$BaseKB',genlMt,2).


% ============================================
% Testing 
% ============================================
      
testOpenCyc:-halt.


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
getSurfaceFromChars(Chars,TERM,VARS):-trim(Chars,CharsClean),
      getSurfaceFromCleanChars(Chars,TERM,VARS).
getSurfaceFromChars(C,TERM,VARS):-atom(C),atom_codes(C,Chars),!,
      getSurfaceFromChars(Chars,TERM,VARS).
getSurfaceFromChars(C,TERM,VARS):-
      string_to_list(C,List),!,
      getSurfaceFromChars(List,TERM,VARS),!.

getSurfaceFromCleanChars([],[end_of_file],_):-!.
getSurfaceFromCleanChars([41|_],[end_of_file],_):-!.
getSurfaceFromCleanChars([59|Comment],[file_comment,Atom],VARS):-atom_codes(Atom,Comment),!.
getSurfaceFromCleanChars(Chars,WFFOut,VARSOut):- 
	       retractall(numbered_var(_,_)), 
               getCycLTokens(Chars,WFFClean), 
               phrase(cycL(WFF),WFFClean),
               collect_temp_vars(VARS),!,
               ((VARS=[],VARSOut=_,WFFOut=WFF);
                    (unnumbervars(VARS,LIST),
                     cyclVarNums(LIST,WFF,WFFOut,VARSOut2) ,
                     list_to_set(VARSOut2,VARSOut1),
                     open_list(VARSOut1,VARSOut))),!.

getSurfaceFromCleanChars(Comment,[unk_comment,Atom],VARS):-atom_codes(Atom,Comment),!.

%===================================================================
% Removes Leading and Trailing whitespaces and non ANSI charsets.
%====================================================================
trim(X,Y):-ltrim(X,R),reverse(R,Rv),ltrim(Rv,RY),reverse(RY,Y),!.

ltrim([],[]):-!.
ltrim([32,32,32,32,32,32,32|String],Out) :-trim(String,Out),!.
ltrim([32,32,32,32,32|String],Out) :-trim(String,Out),!.
ltrim([32,32,32|String],Out) :- trim(String,Out),!.
ltrim([32,32|String],Out) :- trim(String,Out),!.
ltrim([P|X],Y):- (not(number(P));P<33;P>128),trim(X,Y),!.
ltrim(T,T).

% ===================================================================
%  CycL String to DCG Converter
% Converts up to 13 forms
%     13 Terms long
%  
% =169 Parens Pairs at the First 2 levels  
% 
% ===================================================================


cycL(A) --> expr(A).
cycL([noparens|A]) --> many_slots(A).

expr(['QUOTE',WFF]) --> ['\''],expr2(WFF).
expr(WFF) --> expr2(WFF).

expr2('NIL') -->  ['(',')'],!.
expr2([LIST]) -->  ['('],many_slots(LIST),[')'].
expr2(WFF) -->  slot(WFF), { nonvar(WFF) ,!}.

many_slots([A]) --> slot(A).
many_slots([A|L]) --> slot(A) , many_slots(L).

slot(WFF) -->  simple(WFF), { nonvar(WFF), ! }.
slot(WFF) -->  expr(WFF), { nonvar(WFF), ! }.

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

number(Number) -->  [Number] , { nonvar(Number), number(Number),! } .

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
%variable(VN)-->  ['?'], { var_gen(A),var_number(A,VN)   } . 

% Makes up sequencial Variable names for anonymous cycl getPrologVars
var_gen(Atom):-idGen(Number),number_codes(Number,Codes),atom_codes(Atom,[86,65,82|Codes]). % "VAR"

idGen(Id):- flag(idGen,Id,Id+1).

constant(Number) --> number(Number) .
   
constant(Constant) -->  ['#$',Unquoted] , { nonvar(Unquoted), not((Unquoted='?';Unquoted='(';Unquoted=')')),!,atom_concat('#$',Unquoted,Constant) } .
constant(Constant) -->  [':',Unquoted] , { nonvar(Unquoted), not((Unquoted='?';Unquoted='(';Unquoted=')')),!,atom_concat(':',Unquoted,Constant) } .
constant(Unquoted) -->  [Unquoted] , {  nonvar(Unquoted), not((Unquoted='?';Unquoted='(';Unquoted=')')),! } .
     
var_number(A,'$VAR'(VN)):-numbered_var(A,'$VAR'(VN)),!.
var_number(A,'$VAR'(VN)):-flag(get_next_num,VN,VN+1),asserta(numbered_var(A,'$VAR'(VN))),!.

:-dynamic(numbered_var/2).

% This creates ISO Prolog getPrologVars w/in a CycL/STANDARD expression to be reconstrated as after parsing is complete 

cyclVarNums([],WFF,WFF,_):-!.

cyclVarNums(LIST,'$VAR'(NUM),VAR,[=(SYM,VAR)]):-numbered_var(SYM,'$VAR'(NUM)),
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

collect_temp_vars(VARS):-!,(setof(=(Name,Number),numbered_var(Name,Number),VARS);VARS=[]).

%================================================================
% STRING TOKENIZATION                            
%================================================================
:-assert(show_this_hide(tokenize,2)).

%getCycLTokens(M,['(',surf,')']):-nonvar(M),member(34,M),!.
getCycLTokens(X,Y):-once( tokenize3(X,Y) ). %,isDebug(format('~q.~n',[Y])).

tokenize3([],[]).
tokenize3([32|T],O):-tokenize3(T,O).
tokenize3(CharList,[Token|TList])  :- 
  append(_,[C|List],CharList), C \= 32,
  get_token(C,List,Token,Rest),
  tokenize3(Rest,TList),!.

%  cyc-> "#$"
get_token(35,[36|List],Token,Rest)  :-not(List=[32|_]),
  get_chars_type(List,Lchars,Rest,Type),!,
  atom_codes(Token,[35, 36|Lchars]).

%  cyc-> ":"
get_token(58,List,Token,Rest)  :-not(List=[32|_]),
  get_chars_type(List,Lchars,Rest,Type),!,
  atom_codes(Token,[58|Lchars]).

get_token(A,List,Token,Rest)  :- 
  get_chars_type([A|List],Lchars,Rest,Type),!,
  type_codes(Type,Lchars,Token),!.

type_codes(num,CODES,Num):-catch(number_codes(Num,CODES),_,fail),!.
type_codes(_,[34|Lchars],string(S)):-!,
      reverse(Lchars,[_|Rchars]),
      reverse(Rchars,LcharsNoQuotes),
      getCycLTokens(LcharsNoQuotes,S).
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
check_start(num, C)   :- start_digit(C).
check_start(symb,C)   :- valid_char(C). %, 'not'(digit(C)).

check_end(_,[],[],[])  :-  !.
check_end(num, [C|L],[],[C|L])  :-  'not'(digit(C)),!.
check_end(Name,[E|L],[E],L)  :-  bracket(Name,S,E),!.
%check_end(symb,[C1,C2|L],[],[C1,C2|L])  :-  member([C1,C2],["Fn"]),!.
check_end(symb,[C|L],[],[C|L])  :-  'not'(valid_char(C)).

separator([C,D,E|L],[C,D,E],L)  :-member([C,D,E],["<=>","=:=","=\\=","\\==","@=<","@>=","=..","-->"]),!.
separator([C,D|L],[C,D],L)  :-member([C,D],["=>",":-","\\+","->","\\=","==","@<","@>","=<",">=","#$","//","??"]),!. %,"Fn"
separator([C|L],[C],L)  :- member(C,"*,.():[];= < >^{}?%$#/"),!.

valid_char(46):-!,fail.
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
valid_start(35).
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
