% ===================================================================
% File 'opencyc.pl'
% Purpose: Lower-level connection based utilities for interfacing to OpenCyc from SWI-Prolog
% Maintainer: Douglas Miles
% Contact: $Author$@users.sourceforge.net ;
% Version: 'opencyc.pl' 1.0.0
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
	 cycQuery/6,
	 cycQuery/8,
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
	 makeConstant/1,
	 readCycL/2,
	 cyclify/2,
	 idGen/1,
	 subst/4,
	 isCycOption/2,
	 cyclifyNew/2,
	 unnumbervars/2,
	 defaultAssertMt/1,
	 mtAssertPred/2,
	 isRegisterCycPred/3,
	 registerCycPred/1,
	 setCycOption/2,
	 getCycLTokens/2,
	 registerCycPred/2,
	 registerCycPred/3,
         getSurfaceFromChars/3,
	 assertThrough/1,
	 assertThrough/2,
	 assertThrough/2,
	 assertThrough/2,
	 writel/1,
	 writel/2,
	 atomSplit/2,
	 list_to_term/2,
	 testOpenCyc/0]).

:- style_check(-singleton).
:- style_check(-discontiguous).
:- style_check(-atom).
:- style_check(-string).

%:- set_prolog_flag(optimise,true).
%:- set_prolog_flag(file_name_variables,false).
%:- set_prolog_flag(agc_margin,0).
%:- set_prolog_flag(trace_gc,false).
%:-set_prolog_flag(character_escapes,true).
%:-set_prolog_flag(double_quotes,codes).
%:-set_prolog_flag(report_error,true).
%:-set_prolog_flag(verbose,normal).
:-dynamic(cycConnection/3).
:-dynamic(cycConnectionUsed/3).
:-dynamic(cycMutex/2).
:-dynamic(cycChatMode/1).


cycBaseJavaClass('logiccyc.SwiCyc').

:-use_module(library(system)).
:-use_module(library(shlib)).
:-use_module(library(listing)).
:-use_module(library(sgml)).
:-use_module(library(rdf)).
:- use_module(library(socket)).
:- use_module(library(readutil)).

%Load the TCP Library
%:- use_module(library(unix)).
:- use_module(library(shell)).
:- use_module(library(shlib)).
:- use_module(library(url)).
:- use_module(library(quintus)).
:- use_module(library(qsave)).

%:- use_module((javart)).


% ===================================================================
% Cyc Option Switches
%
%  setCycOption(Var,Value) - sets an option first removing the prevoius value
%
%  isCycOption(Var,Value). - tests for option
%
% ===================================================================

setCycOption([]):-!.
setCycOption([H|T]):-!,
      setCycOption(H),!,
      setCycOption(T),!.
setCycOption(Var=_):-var(Var),!.
setCycOption(_=Var):-var(Var),!.
setCycOption((N=V)):-nonvar(N),!,setCycOption_thread(N,V),!.
setCycOption(N):-atomic(N),!,setCycOption_thread(N,true).
	
setCycOption(Name,Value):-setCycOption_thread(Name,Value).
setCycOption_thread(Name,Value):-
	((thread_self(Process),
	retractall('$CycOption'(Process,Name,_)),
	asserta('$CycOption'(Process,Name,Value)),!)).


unsetCycOption(Name=Value):-nonvar(Name),
	unsetCycOption_thread(Name,Value).
unsetCycOption(Name):-nonvar(Name),
	unsetCycOption_thread(Name,_).
unsetCycOption(Name):-(retractall('$CycOption'(_Process,Name,_Value))).


unsetCycOption_thread(Name):-
	unsetCycOption_thread(Name,_Value).

unsetCycOption_thread(Name,Value):-
	thread_self(Process),
	retractall('$CycOption'(Process,Name,Value)).
	
getCycOption_nearest_thread(Name,Value):-
	getCycOption_thread(Name,Value),!.
getCycOption_nearest_thread(Name,Value):-
	'$CycOption'(_,Name,Value),!.
getCycOption_nearest_thread(_Name,_Value):-!.




isCycOption(Name):-!,isCycOption(Name,true).
isCycOption(Name=Value):-!,isCycOption(Name,Value).

isCycOption(Name,Value):-getCycOption_thread(Name,Value).


getCycOption_thread(Name,Value):-
	((thread_self(Process),
	'$CycOption'(Process,Name,Value))),!.


getCycOption(Name=Value):-nonvar(Name),!,ensureCycOption(Name,_,Value).
getCycOption(Name=Default,Value):-nonvar(Name),!,ensureCycOption(Name,Default,Value).
getCycOption(Name,Value):-nonvar(Name),!,ensureCycOption(Name,_,Value).


ensureCycOption(Name=Default,Value):-
	ensureCycOption(Name,Default,Value),!.
	
ensureCycOption(Name,_Default,Value):-
	getCycOption_thread(Name,Value),!.

ensureCycOption(Name,Default,Default):-
	setCycOption_thread(Name,Default),!.

ensureCycOption(Name,_Default,Value):-nonvar(Name),!,   
	setCycOption_thread(Name,Value),!.

ensureCycOption(_Name,Default,Default).

setCycOption(Name,Value):-setCycOption_thread(Name,Value).



setCycOptionDefaults:-
             (unsetCycOption(_)),
             setCycOption(opt_callback='sendNote'),
             setCycOption(cb_consultation='off'),
             setCycOption(opt_debug='off'),
             setCycOption(cb_error='off'),
             setCycOption(cb_result_each='off'),

% User Agent Defaults for Blank Variables
             setCycOption(opt_cxt_request='#$BaseKB'),
             setCycOption(opt_ctx_assert='#$BaseKB'),
             setCycOption(opt_tracking_number='generate'),
             setCycOption(opt_agent='ua_parse'),
             setCycOption(opt_precompiled='off'),
             getCycOption(opt_theory,Context),setCycOption(opt_theory=Context),
             setCycOption(opt_notation='cycl'),
             setCycOption(opt_timeout=2),
             setCycOption(opt_readonly='off'),
             setCycOption(opt_debug='off'),
             setCycOption(opt_compiler='Byrd'),
             setCycOption(opt_language = 'pnx_nf'),

%Request Limits
             setCycOption(opt_answers_min=1),
             setCycOption(opt_answers_max=999), %TODO Default
             setCycOption(opt_backchains_max=5),
             setCycOption(opt_deductions_max=100),
             setCycOption(opt_backchains_max_neg=5),
             setCycOption(opt_deductions_max_neg=20),
             setCycOption(opt_forwardchains_max=1000),
             setCycOption(opt_max_breath=1000), %TODO Default

%Request Contexts
             setCycOption(opt_explore_related_contexts='off'),
             setCycOption(opt_save_justifications='off'),
             setCycOption(opt_deductions_assert='on'),
             setCycOption(opt_truth_maintence='on'),
             setCycOption(opt_forward_assertions='on'),
             setCycOption(opt_deduce_domains='on'),
             setCycOption(opt_notice_not_say=off),


%Request Pobibility
             setCycOption(opt_certainty_max=1),
             setCycOption(opt_certainty_min=1),
             setCycOption(opt_certainty=1),
             setCycOption(opt_resource_commit='on').

% ===================================================================
% Cyc initialization - call cycInit. once and this fiule will be loaded if not already
% ===================================================================
cycInit.

:-setCycOption(cycServer,'127.0.0.1':3601).
:-setCycOption(query(backchains),1).
:-setCycOption(query(number),nil).
:-setCycOption(query(time),nil). %max ten seconds maybe?
:-setCycOption(query(depth),nil).

:-setCycOption(defaultAssertOptions,[':DIRECTION', ':FORWARD', ':STRENGTH', ':MONOTONIC']).
:-setCycOption(':DIRECTION', ':FORWARD').
:-setCycOption(':STRENGTH', ':MONOTONIC').
:-setCycOption(hookCycPredicates,true).


writel(Lisp):-
   toCycApiExpression(Lisp,Chars),!,
   format('~w',[Chars]).

writel(Stream,Lisp):-
   toCycApiExpression(Lisp,Chars),!,
   format(Stream,'~w',[Chars]).
% ===================================================================
% Connecter to Cyc TCP Server
% ===================================================================

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
toCycApiExpression('$VAR'(VAR),Vars,Chars):-!,sformat(Chars,'?~w',['$VAR'(VAR)]).
toCycApiExpression(Prolog,Vars,Prolog):-(atom(Prolog);number(Prolog)),!.
toCycApiExpression(Prolog,Vars,Chars):-is_string(Prolog),!,sformat(Chars,'"~s"',[Prolog]).
toCycApiExpression(Prolog,Vars,Chars):-string(Prolog),!,sformat(Chars,'"~s"',[Prolog]).
toCycApiExpression([P|List],Vars,Chars):-
			toCycApiExpression_l([P|List],Vars,Term),
			sformat(Chars,'\'(~w)',[Term]).
toCycApiExpression(nv(List),Vars,Chars):-toCycApiExpression_l(List,Vars,Chars),!.
toCycApiExpression([nv|List],Vars,Chars):-toCycApiExpression_l(List,Vars,Chars),!.

toCycApiExpression(varslist(List),Vars,Chars):-!,toCycApiExpression_vars(List,Vars,Chars).
toCycApiExpression(varslist(List,Vars),_,Chars):-!,toCycApiExpression_vars(List,Vars,Chars).

toCycApiExpression(string(List),Vars,Chars):-
			toCycApiExpression_l(List,Vars,Term),
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


toCycApiExpression_vars(List,Vars,''):-
	       var(List),!.
toCycApiExpression_vars([Var],Vars,Chars):-!,
		  toCycApiExpression_var(Var,Vars,Chars).
toCycApiExpression_vars([Var|Rest],Vars,Chars):-
		  toCycApiExpression_var(Var,Vars,C1),
	       toCycApiExpression_vars(Rest,Vars,C2),
	       sformat(Chars,'~w , ~w',[C1,C2]).

toCycApiExpression_var(Var,Vars,Chars):-
	    Var=..[_,Name,Value],
            %toCycVar(Name,Vars,C1),	 
	    toCycApiExpression(Value,Vars,C2),!,
	    sformat(Chars,'?~w = ~w',[Name,C2]).
toCycApiExpression_var(Value,Vars,Chars):-
	       toCycApiExpression(Value,Vars,Chars).

	       


toCycApiExpression_l([],Vars,''):-!.
toCycApiExpression_l([A],Vars,Chars):-toCycApiExpression(A,Vars,Chars),!.
toCycApiExpression_l([A|Rest],Vars,Chars):-
      toCycApiExpression(A,Vars,Chars1),
      toCycApiExpression_l(Rest,Vars,Chars2),
      sformat(Chars,'~w ~w',[Chars1,Chars2]),!.

toCycVar(Var,[VV|_],NameQ):-nonvar(VV),
   VV=..[_,Name,VarRef],
   Var==VarRef,!,sformat(NameQ,'?~w',[Name]).
toCycVar(Var,[_|Rest],Name):-
   nonvar(Rest),toCycVar(Var,Rest,Name).
toCycVar(VAR,_,VarName):-
      term_to_atom(VAR,AVAR),
      atom_codes(AVAR,[95|CODES]),!,
      catch(sformat(VarName,'?HYP-~s',[CODES]),_,VarName='?HYP-VAR').

is_string([A,B|_]):-integer(A),A>12,A<129,integer(B),B>12,B<129.


% ===================================================================
%  Debugging Cyc 
% ===================================================================
     
:-dynamic(isDebug).

% Uncomment this next line to see Cyc debug messages

%isDebug.

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
cycQuery(CycL):-cycQuery(CycL,'#$EverythingPSC').
cycQuery(CycL,Mt):-
	 queryParams(Backchain,Number,Time,Depth),
	 cycQuery(CycL,Mt,Backchain,Number,Time,Depth).

cycQuery(CycL,Mt,Backchain,Number,Time,Depth):-
      copy_term(CycL,Copy),
      numbervars(Copy,'$VAR',0,_),!,
      cycQuery(Copy,CycL,Mt,Result,Backchain,Number,Time,Depth).

cycQuery(Copy,CycL,Mt,Result,Backchain,Number,Time,Depth):-
      cached_query(Copy,Results),!,
      member(CycL,Results).

cycQuery(Copy,CycL,Mt,Result,Backchain,Number,Time,Depth):-cachable_query(Copy),!,
      findall(CycL,cycQueryReal(CycL,Mt,Result,Backchain,Number,Time,Depth),Save),
      (Save=[] -> true ; asserta(cached_query(CycL,Save))),!,
      member(CycL,Save).
cycQuery(Copy,CycL,Mt,Result,Backchain,Number,Time,Depth):-
      /*notrace*/(cycQueryReal(CycL,Mt,Result,Backchain,Number,Time,Depth)).

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

cycQueryReal(CycL,Mt,Result,Backchain,Number,Time,Depth):-
      getCycConnection(SocketId,OutStream,InStream),
      popRead(InStream),
      cyclify(CycL,CycLified),
      cyclify(Mt,Mtified),
      free_variables(CycLified,Vars),
      %  backchain number time depth
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
balanceBinding(string(B),B):-!.
balanceBinding(string(Woslds),List):-atomSplit(List,B),!.
balanceBinding(['\'',[B]],BO):-!,balanceBindingS(B,BO).
balanceBinding(['QUOTE',B],BO):-!,balanceBindingS(B,BO).
balanceBinding([A|L],Binding):-balanceBindingCons(A,L,Binding).
balanceBinding(Binding,Binding):-!.
 
balanceBindingCons(A,L,[A|L]):- (var(A);A=string(_);number(A)),!.
balanceBindingCons('and-also',L,Binding):-balanceBindingS(L,LO), list_to_term(LO,Binding),!.
balanceBindingCons('eval',L,Binding):-balanceBindingS(L,LO), list_to_term(LO,Binding),!.
balanceBindingCons('#$and-also',L,Binding):-balanceBindingS(L,LO), list_to_term(LO,Binding),!.

balanceBindingCons(A,L,Binding):-
	 balanceBinding(A,AO),
	 balanceBindingS(L,LO),
	 Binding=..[AO|LO],!.

balanceBindingS(Binding,Binding):- (var(Binding);atom(Binding);number(Binding)),!.
balanceBindingS([],[]).
balanceBindingS([V,[L]|M],[LL|ML]):-V=='\'',balanceBindingS(L,LL),balanceBindingS(M,ML).
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

noncyclified(Same):- (var(Same);number(Same);string(Same)),!.
noncyclified('$VAR'(_)).
noncyclified(string(_)).
noncyclified([]).

cyclify(Same,Same):- noncyclified(Same),!.
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


cyclifyNew(Same,Same):- noncyclified(Same),!.
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
   atom_concat('#$',New,Const),!,
   makeConstant(New).

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


%?- getSurfaceFromChars("'(ls dfg)",S,V).
%S = ['QUOTE', [[ls, dfg]]]

expr(['QUOTE',WFF]) --> ['\''],['('],many_slots(WFF),[')'],{!}.
expr(['QUOTE',WFF]) --> ['\''],expr2([WFF]).
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

qual(Q) --> constant(Q), { nonvar(Q) }.

number(Number) -->  [Number] , { nonvar(Number), number(Number),! } .

quantity(Number) --> number(Number).

simple(WFF) -->  quantity(WFF), { nonvar(WFF), ! }.
simple(WFF) -->  variable(WFF), { nonvar(WFF), ! }.
simple(WFF) -->  constant(WFF), { nonvar(WFF), ! }.

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

%======================================================================
% CLSID Generation
% idGen(+Var)
% Creates a new unique number   TODO
%
% Example:
% | ?- idGen(X).
% X = 2234
%======================================================================
idGen(X):-flag(idGen,X,X+1).

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
digit(C)   :- member(C,"-_.01234567890+eE").

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



atomSplit(Atom,Words):-var(Atom),!,
   concat_atom(Words,' ',Atom).
   

atomSplit(Atom,Words):-
   concat_atom(Words1,' ',Atom),
   atomSplit2(Words1,Words),!.

atomSplit2([],[]).
atomSplit2([W|S],[A,Mark|Words]):- member(Mark,['.',',','?']),atom_concat(A,Mark,W),not(A=''),!,atomSplit2(S,Words).
atomSplit2([W|S],[Mark,A|Words]):- member(Mark,['.',',','?']),atom_concat(Mark,A,W),not(A=''),!,atomSplit2(S,Words).
atomSplit2([W|S],[W|Words]):-atomSplit2(S,Words).


% ===================================================================
% Substitution based on ==
% ===================================================================

% Usage: subst(+Fml,+X,+Sk,?FmlSk)

subst(A,B,C,D):-(nd_subst(A,B,C,D)),!.

nd_subst(  Var, VarS,SUB,SUB ) :- Var==VarS,!.
nd_subst(        P, X,Sk,        P1 ) :- functor(P,_,N),nd_subst1( X, Sk, P, N, P1 ).

nd_subst1( _,  _, P, 0, P  ).

nd_subst1( X, Sk, P, N, P1 ) :- N > 0, P =.. [F|Args], nd_subst2( X, Sk, Args, ArgS ),
            nd_subst2( X, Sk, [F], [FS] ),
            P1 =.. [FS|ArgS].

nd_subst2( _,  _, [], [] ).
nd_subst2( X, Sk, [A|As], [Sk|AS] ) :- X == A, !, nd_subst2( X, Sk, As, AS).
nd_subst2( X, Sk, [A|As], [A|AS]  ) :- var(A), !, nd_subst2( X, Sk, As, AS).
nd_subst2( X, Sk, [A|As], [Ap|AS] ) :- nd_subst( A,X,Sk,Ap ),nd_subst2( X, Sk, As, AS).
nd_subst2( X, Sk, L, L ).

weak_nd_subst(  Var, VarS,SUB,SUB ) :- Var=VarS,!.
weak_nd_subst(        P, X,Sk,        P1 ) :- functor(P,_,N),weak_nd_subst1( X, Sk, P, N, P1 ).

weak_nd_subst1( _,  _, P, 0, P  ).

weak_nd_subst1( X, Sk, P, N, P1 ) :- N > 0, P =.. [F|Args], weak_nd_subst2( X, Sk, Args, ArgS ),
            weak_nd_subst2( X, Sk, [F], [FS] ),
            P1 =.. [FS|ArgS].

weak_nd_subst2( _,  _, [], [] ).
weak_nd_subst2( X, Sk, [A|As], [Sk|AS] ) :- X = A, !, weak_nd_subst2( X, Sk, As, AS).
weak_nd_subst2( X, Sk, [A|As], [A|AS]  ) :- var(A), !, weak_nd_subst2( X, Sk, As, AS).
weak_nd_subst2( X, Sk, [A|As], [Ap|AS] ) :- weak_nd_subst( A,X,Sk,Ap ),weak_nd_subst2( X, Sk, As, AS).
weak_nd_subst2( X, Sk, L, L ).

% ===================================================================
% PURPOSE
% This File is the bootstrap SWI-Prolog listener to hanndle OpenCyc API requests
% So first is loads the proper files and then starts up the system
% ===================================================================


% ===================================================================
% Prolog Dependant Code
% ===================================================================

    
/*
:-module(system_dependant,
      [getCputime/1,
      numbervars/1,
      unnumbervars/2,
      writeSTDERR/1,
      writeSTDERR/2,
      writeFmt/1,
      writeFmt/2,
      writeFmt/3,
      fmtString/2,
      fmtString/3,
      writeFmtFlushed/1,
      writeFmtFlushed/2,
      writeFmtFlushed/3,
      saveUserInput/0,
      writeSavedPrompt/0,
      if_prolog/2,
      callIfPlatformWin32/1,
      callIfPlatformUnix/1,
      prologAtInitalization/1,
      prolog_thread_create/3,
      prolog_current_thread/2,
      prolog_thread_exit/1,
      prolog_thread_self/1,
      prolog_thread_at_exit/1,
      prolog_thread_signal/2,
      prolog_thread_join/2,
      prolog_notrace/1,
      prolog_statistics/0,
      main/1]).
      
*/      



% ========================================================================================
% Using getCputime/1 (in Cyc code) since Eclipse prolog (another port for Cyc)  chokes on getCputime/1
% ========================================================================================
getCputime(Start):-statistics(cputime,Start).
prolog_statistics:-statistics.
prolog_notrace(G):-notrace(G).

% ========================================================================================
% Threads 
% ========================================================================================
prolog_thread_create(Goal,Id,Options):-thread_create(Goal,Id,Options).
prolog_current_thread(Id,Status):-current_thread(Id,Status).
prolog_thread_exit(Goal):-thread_exit(Goal).
prolog_thread_self(Id):-thread_self(Id).
prolog_thread_at_exit(Goal):-thread_at_exit(Goal).
prolog_thread_signal(ID,Goal):-thread_signal(ID,Goal).
prolog_thread_join(Id,X):-thread_join(Id,X).

% ========================================================================================
% Some prologs have a printf() type predicate.. so I made up fmtString/writeFmt in the Cyc code that calls the per-prolog mechaism
% in SWI it's formzat/N and sformat/N
% ========================================================================================
:-dynamic(isConsoleOverwritten/0).

writeFmtFlushed(X,Y,Z):-catch((format(X,Y,Z),flush_output(X)),_,true).
writeFmtFlushed(X,Y):-catch((format(X,Y),flush_output),_,true).
writeFmtFlushed(X):-catch((format(X,[]),flush_output),_,true).

writeFmt(X,Y,Z):-catch(format(X,Y,Z),_,true).
writeFmt(X,Y):-format(X,Y).
writeFmt(X):-format(X,[]).

fmtString(X,Y,Z):-sformat(X,Y,Z).
fmtString(Y,Z):-sformat(Y,Z).

saveUserInput:-retractall(isConsoleOverwritten),flush_output.
writeSavedPrompt:-not(isConsoleOverwritten),!.
writeSavedPrompt:-flush_output.
writeOverwritten:-isConsoleOverwritten,!.
writeOverwritten:-assert(isConsoleOverwritten).


writeSTDERR(F):-writeSTDERR('~q',[F]).
writeSTDERR(F,A):-((
        format(user_error,F,A),
        nl(user_error),
        flush_output(user_error))).

writeErrMsg(Out,E):-!. %message_to_string(E,S),writeFmtFlushed(Out,'<prolog:error>~s</prolog:error>\n',[S]),!.
writeErrMsg(Out,E,Goal):-!. %message_to_string(E,S),writeFmtFlushed(Out,'<prolog:error>goal "~q" ~s</prolog:error>\n',[Goal,S]),!.
writeFileToStream(Dest,Filename):-
        catch((
        open(Filename,'r',Input),
        repeat,
                get_code(Input,Char),
                put(Dest,Char),
        at_end_of_stream(Input),
        close(Input)),E,
        writeFmtFlushed('<prolog:error goal="~q">~w</prolog:error>\n',[writeFileToStream(Dest,Filename),E])).


% ========================================================================================
% numbervars/1 (just simpler numbervars.. will use a rand9ome start point so if a partially numbered getPrologVars wont get dup getPrologVars
% Each prolog has a specific way it could unnumber the result of a numbervars
% ========================================================================================

numbervars(X):-get_time(T),convert_time(T,A,B,C,D,E,F,G),!,numbervars(X,'$VAR',G,_).
%unnumbervars(X,Y):-term_to_atom(X,A),atom_to_term(A,Y,_).


% ========================================================================================
% Ensure a Module is loaded
% ========================================================================================
moduleEnsureLoaded(X):-
        catch(ensure_loaded(X),_,(catch((atom_concat('mod/',X,Y),
        ensure_loaded(Y)),_,format(user_error,';; file find error ~q ~q',[X,E])))).

% ========================================================================================
% Platform specifics
% ========================================================================================
callIfPlatformWin32(G):-prolog_flag(windows,true),!,ignore(G).
callIfPlatformWin32(G):-!.

callIfPlatformUnix(G):-not(prolog_flag(windows,true)),!,ignore(G).
callIfPlatformUnix(G):-!.

/*
:- callIfPlatformWin32(set_prolog_flag(debug_on_error,true)).
:- callIfPlatformUnix(set_prolog_flag(debug_on_error,false)).
:- callIfPlatformUnix(set_prolog_flag(debug_on_error,true)).
*/

% ========================================================================================
% Prolog specific code choices
% ========================================================================================
if_prolog(swi,G):-call(G).  % Run B-Prolog Specifics
if_prolog(_,_):-!.  % Dont run SWI Specificd or others

% used like if_prolog(bp,do_bp_stuff),if_prolog(swi,do_swi_stuff) inline in Cyc code


prologAtInitalization(V):-at_initialization(V),!,logOnFailureIgnore(V).


% ===================================================================
% Semi-Prolog Dependant Code
% ===================================================================
sigma_ua(X):-processRequest(X).

processRequest(X):-writeq(processRequest(X)),nl.


% ===========================================================
% SOCKET SERVER - Looks at first charicater of request and decides between:
%  Http, Native or Soap and replies accordingly
% ===========================================================
/*
:-module(cyc_httpd,[
   createPrologServerThread/1,
   xmlPrologServer/1,
   read_line_with_nl/3,
   decodeRequest/2,
   invokePrologCommandRDF/6,
   serviceAcceptedClientSocketAtThread/1]).
*/

% :-include(cyc_header).



% :-use_module(cyc_threads).
%% :-ensure_loaded(system_dependant).

:-dynamic(isKeepAlive/1).

createPrologServerThread(Port) :-
        servantProcessCreate(nokill,'OpenCyc CycL/XML/SOAP Server Socket',xmlPrologServer(Port),_,[]).

xmlPrologServer(Port):-
        tcp_socket(ServerSocket),
        catch(ignore(tcp_setopt(ServerSocket, reuseaddr)),_,true),
        at_halt(tcp_close_socket(ServerSocket)),
        attemptServerBind(ServerSocket, Port),
        tcp_listen(ServerSocket, 655),
        repeat,
	       acceptClientsAtServerSocket(ServerSocket),
        fail.


attemptServerBind(ServerSocket, Port):-
        catch((tcp_bind(ServerSocket, Port),
        flush_output,
        writeSTDERR('% OpenCyc Prolog API server started on port ~w. \n',[Port]),flush_output),
        error(E,_),
        writeSTDERR('\nnOpenCyc Prolog API server not started becasue: "~w"\n',[Port,E])).

acceptClientsAtServerSocket(ServerSocket):-
		tcp_open_socket(ServerSocket, AcceptFd, _),
                cleanOldProcesses,!,
		tcp_accept(AcceptFd, ClientSocket, ip(A4,A3,A2,A1)),!,
                getPrettyDateTime(DateTime),
                sformat(Name,'Dispatcher for ~w.~w.~w.~w  started ~w ',[A4,A3,A2,A1,DateTime]),
                servantProcessCreate(killable,Name,serviceAcceptedClientSocketAtThread(ClientSocket),_,[detatch(true)]),!.

serviceAcceptedClientSocketAtThread(ClientSocket):-
	tcp_open_socket(ClientSocket, In, Out),!,
        setCycOption('$socket_in',In),
        setCycOption('$socket_out',Out),!,
        serviceIO(In,Out),
        flush_output,
	catch(tcp_close_socket(ClientSocket),_,true),
	prolog_thread_exit(complete).



getPrettyDateTime(String):-get_time(Time),convert_time(Time, String).

serviceIO(In,Out):-
        peek_char(In,Char),!,
	%writeSTDERR(serviceIOBasedOnChar(Char,In,Out)),
	serviceIOBasedOnChar(Char,In,Out),!.


serviceIOBasedOnChar('G',In,Out):-!,  
         serviceHttpRequest(In,Out).
serviceIOBasedOnChar('P',In,Out):-!,
         serviceHttpRequest(In,Out).

serviceIOBasedOnChar('(',In,Out):-!,  
         serviceCycApiRequest(In,Out).

serviceIOBasedOnChar('<',In,Out):-!,
         serviceSoapRequest(In,Out).  % see cyc_soap.pl

serviceIOBasedOnChar('+',In,Out):-!,  
         serviceJavaApiRequest(In,Out).

serviceIOBasedOnChar(C,In,Out):-
        serviceNativeRequestAsRDF(C,In,Out).


% ===========================================================
% PROLOGD for Java SERVICE
% ===========================================================
serviceJavaApiRequest(In,Out):-
      get0(In,Plus),
        thread_self(Session),
        retractall(isKeepAlive(Session)),
        xmlClearTags,
        repeat,
                catch(
                        read_term(In,PrologGoal,[variable_names(ToplevelVars),character_escapes(true),syntax_errors(error)]),
                        E,
                        writeErrMsg(Out,E)),
                invokePrologCommand(Session,In,Out,PrologGoal,ToplevelVars,Returns),
                notKeepAlive(Out,Session),!.

invokePrologCommand(Session,In,Out,PrologGoal,ToplevelVars,Returns):-var(PrologGoal),!.

invokePrologCommand(Session,In,Out,PrologGoal,ToplevelVars,Returns):-
      %%  writeFmt(Out,'<prolog:solutions goal="~q">\n',[PrologGoal]),
        set_output(Out),set_input(In),!,
	ignore(catch(PrologGoal,_,true)),
        xmlExitTags,!.

% ===========================================================
% PROLOGD for OpenCyc SERVICE
% ===========================================================

serviceCycApiRequest(In,Out):-
       readCycL(In,Trim), 
       isDebug(format('"~s"~n',[Trim])),
       serviceCycApiRequestSubP(In,Trim,Out).
   
serviceCycApiRequestSubP(In,Trim,Out):-
       getSurfaceFromChars(Trim,[Result],ToplevelVars),!,
       balanceBinding(Result,PrologGoal),
        thread_self(Session),
        retractall(isKeepAlive(Session)),
        xmlClearTags,
       invokePrologCommand(Session,In,Out,PrologGoal,ToplevelVars,Returns),
       writel(varslist(ToplevelVars,ToplevelVars)).

serviceCycApiRequestSubP(Trim):-
       getSurfaceFromChars(Trim,[Result],ToplevelVars),!,
       balanceBinding(Result,PrologGoal),!,
	 (catch(PrologGoal,_,true)),
       once((writel(varslist(ToplevelVars,ToplevelVars)),write('<br>\n'))),fail.



% ===========================================================
% HTTPD SERVICE
% ===========================================================

serviceHttpRequest(In,Out):-
        readHTTP(In,Options),
        writeFmtFlushed(Out,'HTTP/1.1 200 OK\nServer: Cyc-HTTPD\nContent-Type: text/html\n\n',[]),
        setCycOption(client,html),
        tell(Out),
        writeSTDERR('REQUEST: "~q" \n',[Options]), 
	processRequest(Options),
	flush_output.


readHTTP(In,Request):-
        read_line_with_nl(In, Codes, []),
        append("GET /",Stuff,Codes), %true,
        append(RequestCodes,[72,84,84,80|_],Stuff),
        atom_codes(RequestEncoded,RequestCodes),
        decodeRequest(RequestEncoded,Request).

readHTTP(In,Request):-
        read_line_with_nl(In, Codes, []),
        append("POST /",Stuff,Codes), %true,
        append(RequestCodes,[72,84,84,80|_],Stuff),
        atom_codes(RequestEncoded,RequestCodes),
        decodeRequest(RequestEncoded,Request).


read_line_with_nl(Fd, Codes, Tail) :-
        get_code(Fd, C0),
        read_line_with_nl(C0, Fd, Codes, Tail).
read_line_with_nl(end_of_file, _, Tail, Tail) :- !.
read_line_with_nl(-1, _, Tail, Tail) :- !.
read_line_with_nl(10, _, [10|Tail], Tail) :- !.
read_line_with_nl(C, Fd, [C|T], Tail) :-
        get_code(Fd, C2),
        read_line_with_nl(C2, Fd, T, Tail).



decodeRequest(RequestEncoded,[file=Request]):-
      concat_atom([X],'?',RequestEncoded),
      www_form_encode(Request,X),!.
decodeRequest(RequestEncoded,[file=Request|ENCARGS]):-
      concat_atom([X,ARGS],'?',RequestEncoded),
      www_form_encode(Request,X),
      concat_atom(ArgList,'&',ARGS),
      decodeRequestArguments(ArgList,ENCARGS).

decodeRequestArguments([],[]):-!.
decodeRequestArguments([ctx=Value|List],[ctx=CValue,theory=KValue|ARGS]):-
          concat_atom([KValue,CValue],':',Value),!,
          decodeRequestArguments(List,ARGS).
decodeRequestArguments([Arg|List],[DName=DValue|ARGS]):-
          split_nv(Arg,Name,Value),
          www_form_encode(AName,Name),
          www_form_encode(AValue,Value),!,
          decodeRequestAtom(AName,DName),
          decodeRequestAtom(AValue,DValue),
          decodeRequestArguments(List,ARGS).

%ctx=PrologMOO%3ASTRUCTURAL-ONTOLOGY&

split_nv(Arg,Name,Value):-concat_atom([Name,Value],'=',Arg),!.
split_nv(Arg,Arg,Arg).

decodeRequestAtom(A,A):-var(A),!.
decodeRequestAtom(tn,tn):-!.
decodeRequestAtom(N,N):-number(N),!.
decodeRequestAtom(A=B,AA=BB):-
                decodeRequestAtom(A,AA),
                decodeRequestAtom(B,BB),!.
decodeRequestAtom(A,T):-catch(atom_to_term(A,T,_),_,fail),number(T),!.
decodeRequestAtom(A,T):-catch(atom_to_term(A,T,_),_,fail),not(var(T)),not(compound(T)),!.
decodeRequestAtom(A,T):-atom(A),catch(atom_codes(A,[95|_]),_,fail),catch(atom_to_term(A,T,_),_,fail),!.
decodeRequestAtom(A,A):-!.

% ===========================================================
% NATIVE SERVICE
% ===========================================================

serviceNativeRequestAsRDF(_,In,Out):-
        writeFmt(Out,'<?xml version="1.0" encoding="ISO-8859-1"?>\n',[]),
        thread_self(Session),
        retractall(isKeepAlive(Session)),
        xmlClearTags,
        repeat,
                catch(
                        read_term(In,PrologGoal,[variable_names(ToplevelVars),character_escapes(true),syntax_errors(error)]),
                        E,
                        writeErrMsg(Out,E)),
                %writeSTDERR(PrologGoal:ToplevelVars),
                invokePrologCommandRDF(Session,In,Out,PrologGoal,ToplevelVars,Returns),
                notKeepAlive(Out,Session),!.

notKeepAlive(Out,Session):-isKeepAlive(Session),
        write(Out,
                'complete.\n'
                %'<prolog:keepalive/>\n'
                                ),catch(flush_output(Out),_,true),!,fail.
notKeepAlive(Out,Session):-catch(flush_output(Out),_,true).


keep_alive:-thread_self(Me),retractall(isKeepAlive(Me)),assert(isKeepAlive(Me)),writeFmtFlushed('<keepalive/>\n',[]).
goodbye:-thread_self(Me),retractall(isKeepAlive(Me)),writeFmt('<bye/>/n',[]).


invokePrologCommandRDF(Session,In,Out,PrologGoal,ToplevelVars,Returns):-var(PrologGoal),!.

invokePrologCommandRDF(Session,In,Out,PrologGoal,ToplevelVars,Returns):-
        term_to_atom(Session,Atom),concat_atom(['$answers_for_session',Atom],AnswersFlag),
        writeFmt(Out,'<prolog:solutions goal="~q">\n',[PrologGoal]),
        flag(AnswersFlag,_,0),
        set_output(Out),set_input(In),!,
        getCputime(Start),
        callNondeterministicPrologCommandRDF(Session,AnswersFlag,In,Out,PrologGoal,ToplevelVars),
        xmlExitTags,
        getCputime(End),
        flag(AnswersFlag,Returns,Returns),
%       (Returns > 0 ->
%               writeFmt(Out,'<prolog:yes/>\n',[]) ;
%               writeFmt(Out,'<prolog:no/>\n',[])),!,
        Elapsed is End -Start,
        writeFmt(Out,'</prolog:solutions answers="~w" cputime="~g">\n',[Returns,Elapsed]),!.

callNondeterministicPrologCommandRDF(Session,AnswersFlag,In,Out,PrologGoal,ToplevelVars):-
        ground(PrologGoal),!,
        catch(
                (PrologGoal,
                 flag(AnswersFlag,Answers,Answers+1),
                 writePrologToplevelVarsXML(Out,PrologGoal,AnswersFlag,ToplevelVars)
                 ),
           Err,writeErrMsg(Out,Err,PrologGoal)),!.

callNondeterministicPrologCommandRDF(Session,AnswersFlag,In,Out,PrologGoal,ToplevelVars):-
        catch(
                (PrologGoal,
                 flag(AnswersFlag,Answers,Answers+1),
                 writePrologToplevelVarsXML(Out,PrologGoal,AnswersFlag,ToplevelVars),
                 fail),
           Err,writeErrMsg(Out,Err,PrologGoal)),!.
callNondeterministicPrologCommandRDF(Session,AnswersFlag,In,Out,PrologGoal,ToplevelVars):-!.


writePrologToplevelVarsXML(Out,PrologGoal,AnswersFlag,ToplevelVars):-
         flag(AnswersFlag,Answers,Answers),
        writeFmt(Out,'<prolog:result solution="~w">\n',[Answers]),
        writePrologToplevelVarsXML2(Out,ToplevelVars),
        writeFmt(Out,'</prolog:result>\n',[]),!.

writePrologToplevelVarsXML2(Out,[]):-!.
writePrologToplevelVarsXML2(Out,[Term|REST]):-!,Term=..[_,N,V],
         writeFmtFlushed(Out,'       <prolog:p>~w = ~q</prolog:p>\n',[N,V]),
         writePrologToplevelVarsXML2(Out,REST),!.


writeFmt(A,B,C):-!.
writeFmt(A,B):-!.

writeFmt(A,B,C):-
        writeFmtFlushed(A,B,C).
writeFmt(A,B):-
        writeFmtFlushed(A,B).


throwCyc(Module,Type,Details):-
        current_prolog_flag(debug_on_error, DebugOnError),
        set_prolog_flag(debug_on_error, false),!,
        throw(cycException(Module,Type,Details,DebugOnError)),
        ifInteractive(writeDebug('Post throwCyc')),!.




% ===========================================================
% NATIVE SOAPD SERVER FOR SWI-PROLOG
% ===========================================================

			    
%:-module(cyc_soap,[]).

% :-include('cyc_header.pl').

:-dynamic(xmlCurrentOpenTags/2).

serviceSoapRequest(In,Out):-
      writeSTDERR('SOAP Request'),
        catch(read_do_soap(stream(In),Out),E,
        writeFmt(Out,'<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n<error>~w</error>\n',[E])),
        catch(flush_output(Out),_,true).


read_do_soap(Source):-
        open(Source,read,Stream),
        read_do_soap(Stream,user_output).

read_do_soap(Source,Out):-
       thread_self(Self),
        write(Out,'<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n'),
       % writeFmt(Out,'<?xml version="1.0" encoding="ISO-8859-1"?>\n<answer thread="~w">\n',[Self]),
        catch(flush_output(Out),_,true),
        load_structure(Source,RDF,[]),
        structure_to_options(RDF,Options),
%       writeFmt(user_error,'structure="~q"\noptions="~q"\n',[RDF,Options]),
        catch(flush_output(user_error),_,true),
        processRequest([client=soap|Options]).
        %writeFmt(Out,'</answer>\n',[]).


% request
structure_to_options([element(request, Options, [Atom])],[submit=ask,sf=Atom|Options]):-!.

% assert
structure_to_options([element(assert, Options, [Atom])],[submit=assert,sf=Atom|Options]):-!.
structure_to_options([element(asssertion, Options, [Atom])],[submit=assert,sf=Atom|Options]):-!.
structure_to_options([element(assertion, Options, [Atom])],[submit=assert,sf=Atom|Options]):-!.

% get inner
structure_to_options([element(Ptag, ['xmlns:cyc'=Server], Inner)],[opt_server=Server,opt_outter=Ptag|Out]):-!,
        structure_to_options(Inner,Out).



xmlOpenTag(Name):-thread_self(Self),asserta(xmlCurrentOpenTags(Self,A)),writeFmtServer('<~w>',[Name]),!.
xmlOpenTagW(Out,Name,Text):-thread_self(Self),asserta(xmlCurrentOpenTags(Self,A)),writeFmtServer(Out,'~w',[Text]),!.

xmlCloseTag(Name):-thread_self(Self),ignore(retract(xmlCurrentOpenTags(Self,A))),writeFmtServer('</~w>',[Name]),!.
xmlCloseTagW(Name,Text):-thread_self(Self),ignore(retract(xmlCurrentOpenTags(Self,A))),writeFmtServer('~w',[Text]),!.
xmlCloseTagW(Out,Name,Text):-thread_self(Self),ignore(retract(xmlCurrentOpenTags(Self,A))),writeFmtServer(Out,'~w',[Text]),!.

xmlClearTags:-thread_self(Self),retractall(xmlCurrentOpenTags(Self,A)).

xmlExitTags:-thread_self(Self),retract(xmlCurrentOpenTags(Self,A)),writeFmtServer('</~w>',[Name]),fail.
xmlExitTags.


% ===========================================================
% Insert
% ===========================================================
parse_cyc_soap(Options):-memberchk(submit=assert,Options),!,
        getCycOption(opt_ctx_assert='#$BaseKB',Ctx),
        getCycOption(opt_theory='#$PrologDataMt',Context),
        getCycOption(sf=surf,Assertion),
        atom_codes(Assertion,Assertion_Chars),
        getCycOption(user='Web',User),
        getCycOption(interp='cycl',Interp),
        logOnFailure(getCycOption(tn=_,EXTID)),
        %sendNote(user,'Assert',formula(NEWFORM),'Ok.'). %,logOnFailure(saveCycCache)
        logOnFailure(getCleanCharsWhitespaceProper(Assertion_Chars,Show)),!,
        xml_assert(Show,Ctx,Context,User).

xml_assert(Show,Ctx,Context,User):-
        getSurfaceFromChars(Show,STERM,Vars),
        getCycTermFromSurface(STERM,NEWFORM),
        xml_assert(Show,NEWFORM,Vars,Ctx,Context,User).

xml_assert(Show,Ctx,Context,User):-!,
        writeFmt('<assertionResponse accepted="false">\nUnable to parse: "~s"\n</assertionResponse>\n',[Show]).

xml_assert(Show,NEWFORM,Vars,Ctx,Context,User):-
        logOnFailure(getTruthCheckResults(tell,[untrusted],surface,NEWFORM,Ctx,STN,Context,Vars,Maintainer,Result)),
        (Result=accept(_) ->
                        (
                        once(invokeInsert([trusted,canonicalize,to_mem],surface,NEWFORM,Ctx,EXTID,Context,Vars,User)),
                        write('<assertionResponse accepted="true">\nOk.\n</assertionResponse>\n')
                        )
                        ;
                        (
                        Result=notice(FormatStr,Args),
                        write('<assertionResponse accepted="false">\n'),
                        writeFmt(FormatStr,Args),
                        write('\n</assertionResponse>\n')
                        )
        ),!.

xml_assert(Show,NEWFORM,Vars,Ctx,Context,User):-!.


% ===========================================================
% Ask a Request
% ===========================================================
parse_cyc_soap(Options):-memberchk(submit=ask,Options),!,make,
        %write('<!DOCTYPE cyc:ask SYSTEM "/opt/tomcat-4.0/webapps/cyc-1.4b1/dtd/java_prolog.dtd">\n'),
        write('<cyc:ask xmlns:cyc="http://localhost">\n'),
        getCycOption(opt_ctx_request='#$BaseKB',Ctx),
        getCycOption(opt_theory='#$PrologDataMt',Context),
        getCycOption(sf=surf,Askion),
        atom_codes(Askion,Askion_Chars),
        getCycOption(user='Web',User),
        getCycOption(interp='cycl',Interp),
         logOnFailure(getCleanCharsWhitespaceProper(Askion_Chars,Show)),!,
         logOnFailure(getSurfaceFromChars(Show,STERM,Vars)),!,
         logOnFailure(getCycTermFromSurface(STERM,NEWFORM)),!,
              logOnFailure(once(( NEWFORM=comment(_) ->
                     (writeFmt('<error>Syntax Error: Unmatched parentheses in "~s"</error>\n',[Show]),!,FORM=_) ;(!,
                     logOnFailure(invokeRequest_xml(NEWFORM,ChaseVars,Ctx,TrackingAtom,Context,User,Vars,CPU))
                     )))),
        write('</cyc:ask>\n').

invokeRequest_xml(NEWFORM,ChaseVars,Ctx,TrackingAtom,Context,User,Vars,CPU):-
        invokeRequestToBuffer(NEWFORM,ChaseVars,Ctx,TrackingAtom,Context,User,Vars,CPU),
        final_answer(Logic:How),
        invoke_final_answer(Logic,How,CPU).

invoke_final_answer(possible,How,CPU):-!,
        writeFmt('<requestResponse yesno="~w" numBindings="0" seconds="~w"/>\n',[How,CPU]).

invoke_final_answer(Logic,How,CPU):-
        writeFmt('<requestResponse yesno="~w" numBindings="~w" seconds="~w">\n<bindings>\n',[Logic,How,CPU]),
        cite_xml_buffered_answers,
        write('</bindings>\n</requestResponse>\n').


cite_xml_buffered_answers:-
        retract(requestBuffer_db(UResultsSoFar,Result,Explaination,Status)),
        once(inform_xml_agent(UResultsSoFar,Result,Explaination,Status)),fail.

% Call to write Summary
/*
cite_xml_buffered_answers:-
        final_answer(Logic:How),
        writeDebug(final_answer(Logic:How)),
        inform_xml_agent(How, ['Summary'=Logic|_G14093],final_answer(Logic:How),final_answer(Logic:How) ).
*/
cite_xml_buffered_answers:-!.

% ===========================================================
% Send to debugger
% ===========================================================
inform_xml_agent(UResultsSoFar,Result,InExplaination,Status):-
        writeDebug(inform_xml_agent(UResultsSoFar,Result,InExplaination,Status)),fail.

% ===========================================================
% Hide certain returns
% ===========================================================
inform_xml_agent(-1,Result,Explaination,Status).

inform_xml_agent(0, ['Result'=none|A], 'Unproven', done(possible:searchfailed)).
inform_xml_agent(_, ['Result'=true|A], found(_), done(true:_)).
inform_xml_agent(_, ['Summary'=_|_G5892], _, _).

% ===========================================================
% Write Answers
% ===========================================================
inform_xml_agent(UResultsSoFar,Result,InExplaination,Status):-
        writeFmt('<binding>\n',[]),
        inform_xml_vars(Result,Vars),
        length_explaination(InExplaination,InLength),
        findall(Length-Explaination,
                (retract(inform_xml_agent_buffer_db(_,Result,Explaination,_)),
                 length_explaination(Explaination,Length)
                 ),KeyList),

        keysort([(InLength-InExplaination)|KeyList],[(_-ChoiceExplaination)|_]),
        inform_xml_explaination(InLength,ChoiceExplaination,Result),
        writeFmt('</binding>\n',[]).

inform_xml_vars(Result,Vars):-
        length_var(Result,NumVar),
        writeFmt('<variables numVars="~w">\n',[NumVar]),
        inform_each_variable(Result,Vars),
        writeFmt('</variables>\n',[]).

length_var([],0).
length_var([A|'$VAR'(_)],1).
length_var([A|L],N):-
          length_var(L,NN),
          N is NN +1.

inform_each_variable([],Vars).
inform_each_variable('$VAR'(_),Vars).
inform_each_variable([NV|Rest],Vars):-
        inform_nv(NV,Vars),
        inform_each_variable(Rest,Vars).


inform_nv('$VAR'(_),Vars).
inform_nv(Name=Value,Vars):-
        toMarkUp(cycl,Name,Vars,OName),
        toMarkUp(cycl,Value,Vars,OValue),
        writeFmt('<var varName="~w" value="~w"/>\n',[OName,OValue]).


inform_xml_explaination(InLength,ChoiceExplaination,Result):-
        writeFmt('<explaination numSteps="~w">',[InLength]),
        flag(explaination_linenumber,_,0),
        writeObject_explaination(ChoiceExplaination,Result),
        writeFmt('</explaination>\n').

writeObject_explaination(deduced,_).
writeObject_explaination('$VAR'(_),_).
writeObject_explaination(explaination(Choice1) ,Result):-!,
        writeObject_explaination(Choice1,Result),!.
writeObject_explaination(Choice1 * Choice2 ,Result):-!,
        writeObject_explaination(Choice1,Result), !,
        writeObject_explaination(Choice2,Result),!.
writeObject_explaination(Choice1,Result):-!,
             write('<explainationStep isRule="true">\n<originalRule>\n'),
             toMarkUp(html,Choice1,Result,Out),!,
             ignore(write_escaped(Out)),
             write('\n</originalRule>\n</explainationStep>\n').

write_escaped([O|T]):-!,
        write_e_codes([O|T]),!.
write_escaped(Out):-atom(Out),!,
        atom_codes(Out,Codes),!,write_escaped(Codes),!.
write_escaped(String):- !,
        string_to_atom(String,Atom),
         atom_codes(Atom,Codes),!,
        write_e_codes(Codes),!.

write_e_codes([]):-!.
write_e_codes([E|Cs]):-!,
        write_e(E),!,
        write_e_codes(Cs),!.
write_e(34):-write('&qt;'),!.
write_e(60):-write('&lt;'),!.
write_e(62):-write('&gt;'),!.
write_e(C):-put_code(C),!.


% ===================================================================
% writeIfOption(class(input),message(input),respoinse(output))
% generic call interface that was hooked into the belief engine with "ua_set_agent_callback(console_post)"
%This is not a predicate the useragent calls, but one that is called by the belief module to communicate  a question to the useragent or inform it of something.  
% The useragent decides if it can answer the a question and if not itself may ask a human user that is using it.
% There is three arguments to the my_callback predicate: Class, Message and Response
%
% Whenever the belief engine calls 'my_callback' only the first two arguments (Class,Message) are bound to supply information relevant to a Server invoked request.
%
% Class is a programmer defined message catagory  
% The Class is inteded to contain user defined message names that are sent as a callback function that is sent to the user's module consultation 
% Is is the type of Message catagory for the user agent.. A list of these are in TABLE 1.1 in <http://127.0.0.1/cyc_interface_advanced.html>
% (Class is always a ground Term)
%
% Message is a prolog term in the writeFmt defined by it's Class
% Each Class has a one known Message writeFmt shown in the table.   
% Message sometimes is ground term. 
%
%
% Response has normally has 2 response single_bindings: continue or abort
% This response is sent back to the belief_engine.
% If the belief_engine didn't receive 'abort', then it moves to the next stage in the command.
% 
% ===================================================================

			  /*      				   
:-module(cyc_generation,
	 [ 
	 writeDebug/1,
	 writeDebug/2,
	 writeDebugFast/1,
	 logOnFailureIgnore/1,
	 setCycOptionExplicitWriteSettings/0,
	 setCycOptionImplicitWriteSettings/0,
	 sendNote/1,
	 sendNote/4,
	 writeFailureLog/2,
	 debugOnFailure/2,
	 writeObject/2,
	 writeObject/3,
	 writeObject_conj/2]).
					 */

% :-include('cyc_header.pl').

% :-use_module(cyc_globalisms).

% ==========================================================
%  Sending Notes
% ==========================================================
writeDebug(T):-!.  writeDebug(C,T):-!.
 
%writeDebug(T):-(isCycOption(opt_debug=off)),!.
%writeDebug(C,T):-(isCycOption(opt_debug=off)),!.

logOnFailureIgnore(X):-ignore(logOnFailure(X)),!.

writeModePush(_Push):-!.
writeModePop(_Pop):-!.

writeDebug(T):-!,
	((
	if_prolog(swi,
		(prolog_current_frame(Frame),
		prolog_frame_attribute(Frame,level,Depth),!,
		Depth2 = (Depth-25))),
	writeFmt(';;',[T]),!,
	indent_e(Depth2),!,
	writeFmt('~q\n',[T]))),!.

indent_e(X):- catch((X < 2),_,true),write(' '),!.
indent_e(X):-XX is X -1,!,write(' '), indent_e(XX).


writeDebug(C,T):-!,
	((
	writeFmt('<font size=+1 color=~w>',[C]),
	writeDebug(T),
        writeFmt('</font>',[]))),!.

dumpstack_argument(T):-isCycOption(opt_debug=off),!.  
	
dumpstack_argument(Frame):-
	write(frame=Frame),write(' '),
	dumpstack_argument(1,Frame).

dumpstack_argument(1,Frame):-!,
	prolog_frame_attribute(Frame,goal,Goal),!,
	write(goal=Goal),write('\n').
	
dumpstack_argument(N,Frame):-
	prolog_frame_attribute(Frame,argument(N),O),!,
	write(N=O),write(' '),
	NN is N +1,
	dumpstack_argument(NN,Frame).
	
dumpstack_argument(N,Frame):-!,write('\n').
	
:-dynamic(mods/1).

write_response_begin:-!.
write_response_end:-!.

sendNote(X):-var(X),!.
sendNote(X):-mods(X),!.
sendNote(X):-!,assert(mods(X)).
sendNote(X).			 

sendNote(To,From,Subj,Message):-sendNote(To,From,Subj,Message,_).

sendNote(To,From,Subj,Message,Vars):-
	not(not((numbervars((To,From,Subj,Message,Vars)),
	%writeDebug(sendNote(To,From,Subj,Message,Vars)),
	catch(sendNote_1(To,From,Subj,Message,Vars),E,
	writeFmt('send note ~w ~w \n <HR>',[E,sendNote(To,From,Subj,Message,Vars)]))))).


sendNote_1(To,From,Subj,surf,Vars):-!.
sendNote_1(To,From,[],surf,Vars):-!.
sendNote_1(To,From,[],end_of_file,Vars):-!.
sendNote_1(doug,From,_,_,Vars):-!.
sendNote_1(extreme_debug,From,_,_,Vars):-!.
sendNote_1(debug,'Belief',_,_,Vars):-!.

%sendNote_1(canonicalizer,From,Subj,Message,Vars):-!.


sendNote_1(canonicalizer,From,Subj,Message,Vars):-
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,nv(Subj),Vars,SS),
            toMarkUp(cycl,nv(Message),Vars,SA),
            writeFmt('<font color=red>canonicalizer</font>: ~w "~w" (from ~w). \n',[SA,SS,SFrom]),!.

/*

sendNote_1(debug,From,Subj,Message,Vars):- %isCycOption(disp_notes_nonuser=on),!,
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,Subj,Vars,SS),
            toMarkUp(cycl,Message,Vars,SA),
            writeFmt('% debug: ~w "~w" (from ~w). \n',[SA,SS,SFrom]).
sendNote_1(debug,From,Subj,Message,Vars):-!.
*/


sendNote_1(To,From,Subj,Message,Vars):- isCycOption(client=consultation),  !, 
            toMarkUp(cycl,To,Vars,STo),
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,nv(Subj),Vars,S),
            toMarkUp(cycl,nv(Message),Vars,A),
            fmtString(Output,'~w (~w from ~w) ',[A,S,SFrom]),
	    sayn(Output),!.

sendNote_1(To,From,'Rejected',Message,Vars):- isCycOption(client=automata),  !.

sendNote_1(To,From,Subj,Message,Vars):- isCycOption(client=automata),  !, 
            toMarkUp(cycl,To,Vars,STo),
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,nv(Subj),Vars,S),
            toMarkUp(cycl,nv(Message),Vars,A),
            writeFmt(user_error,'% ~w (~w from ~w) ',[A,S,SFrom]).

sendNote_1(To,From,Subj,Message,Vars):- isCycOption(client=html),  !, %  In Html
            toMarkUp(cycl,To,Vars,STo),
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,nv(Subj),Vars,S),
            toMarkUp(html,nv(Message),Vars,A),
            writeFmt('<hr><B>To=<font color=green>~w</font> From=<font color=green>~w</font> Subj=<font color=green>~w</font></B><BR>~w\n',[To,From,S,A]),!.

sendNote_1(To,From,Subj,Message,Vars):- isCycOption(client=console),!, % In CYC
            toMarkUp(cycl,To,Vars,STo),
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,nv(Subj),Vars,SS),
            toMarkUp(cycl,nv(Message),Vars,SA),
            writeFmt(user_error,'; ~w: ~w "~w" (from ~w). \n',[STo,SA,SS,SFrom]),!.
  
sendNote_1(To,From,Subj,Message,Vars):-  % In CYC
            toMarkUp(cycl,To,Vars,STo),
            toMarkUp(cycl,From,Vars,SFrom),
            toMarkUp(cycl,nv(Subj),Vars,SS),
            toMarkUp(cycl,nv(Message),Vars,SA),
            writeFmt(user_error,'; ~w: ~w "~w" (from ~w). \n',[STo,SA,SS,SFrom]),!.

sendNote(To,From,Subj,Message,Vars):-!.


writeDebugFast(X):-writeq(X),nl.

logOnFailure(assert(X,Y)):- catch(assert(X,Y),_,Y=0),!.
logOnFailure(assert(X)):- catch(assert(X),_,true),!.
logOnFailure(assert(X)):- catch(assert(X),_,true),!.
%logOnFailure(X):-catch(X,E,true),!.
logOnFailure(X):-catch(X,E,(writeFailureLog(E,X),!,catch((true,X),_,fail))),!.
logOnFailure(X):- writeFailureLog('Predicate Failed',X),!.


writeFailureLog(E,X):-
		writeFmt(user_error,'\n% error:  ~q ~q\n',[E,X]),flush_output(user_error),!,
		%,true.
		writeFmt('\n;; error:  ~q ~q\n',[E,X]),!,flush_output. %,say([E,X]).
		
debugOnFailure(assert(X,Y)):- catch(assert(X,Y),_,Y=0),!.
debugOnFailure(assert(X)):- catch(assert(X),_,true),!.
debugOnFailure(assert(X)):- catch(assert(X),_,true),!.
%logOnFailure(X):-catch(X,E,true),!.
debugOnFailure(X):-catch(X,E,(writeFailureLog(E,X),fail)),!.
debugOnFailure(X):-true,X.

debugOnFailure(arg_domains,CALL):-!,logOnFailure(CALL),!.
debugOnFailure(Module,CALL):-debugOnFailure(CALL),!.


noDebug(CALL):-CALL.
	


%unknown(Old, autoload).

% ================================================================
%   Serialize Objects to XML
% ================================================================


%%writeObject(OBJ,Vars):-!. %,writeq(OBJ),!.
%writeObject(OBJ,Vars):-!,catch(writeq(OBJ),_,true),nl,!.

writeObject(quiet,Term,Vars):-!.

writeObject(Verbose,Term,Vars):-writeObject(Term,Vars).

		
writeObject(OBJ,Vars):- isCycOption(client=html),!,
		((toMarkUp(html,OBJ,Vars,Chars),write(Chars))),!.
		
writeObject(OBJ,Vars):- isCycOption(client=atomata),!,
		((toMarkUp(cycl,OBJ,Vars,Chars),write(Chars))),!.

writeObject(OBJ,Vars):- isCycOption(client=console),!,
		((toMarkUp(cycl,OBJ,Vars,Chars),write(Chars))),!.

writeObject(OBJ,Vars):- isCycOption(client=consultation),!,
		(say(OBJ,Vars)),!.

writeObject(OBJ,Vars):- !,
		((toMarkUp(cycl,OBJ,Vars,Chars),write(Chars))),!.


writeObject_conj(A,Vars):-isSlot(A),!,
	writeObject(A,Vars).

writeObject_conj(and(A,true),Vars):-!,
	writeObject_conj(A,Vars).

writeObject_conj(and(true,A),Vars):-!,
	writeObject_conj(A,Vars).

writeObject_conj(and(A,B),Vars):-!,
	writeObject_conj(A,Vars),
	writeObject_conj('\n\n Also \n\n ',Vars),
	writeObject_conj(B,Vars).

writeObject_conj(Output,Vars):-
	%write(Output),nl.
	writeObject(Output,Vars).


:-dynamic(resolve_skolem/2).


writeIfOption(C,P):-once_ignore(writeCycEvent(C,P,_)).
writeIfOption(C,M,Vars):-once_ignore(writeCycEvent(C,M,Vars)).


write_val(Any,Vars):- isCycOption(client=html)
      -> write_val_xml(Any,Vars) ;
      write_sterm(Any,Vars).
      
write_val_xml(Any,Vars):-
      toMarkUp(leml,Any,Vars,Chars),write(Chars),nl.


         
writeCycEvent(_,_,_):-isCycOption(disp_hide_all=true),!.
writeCycEvent(_,_,_):-telling_file,!.
writeCycEvent(Class,_,_):-isCycOption(Class=false),!.
writeCycEvent(Class,_,_):-isCycOption(disp_explicit=true),not(isCycOption(_Class=true)),!.

writeCycEvent(request_start,Note,Vars):-!,
         (isCycOption(client=html) -> 
          (writeFmt('<Answer>\n'),le_push('Answer'));
          true).

writeCycEvent(request_end,(Result,Normal,Elapsed,Num,Bindings),Vars):-!, 
                  (isCycOption(client=html) -> 
                     ((    
                       (toMarkUp(leml,note('user',logicEngine,Result,(Result,Normal,Elapsed,Num,Bindings)),Vars,Chars),once((var(Chars);write(Chars)))),
                       writeFmt('<Summary result="~w" solutions="~d" bindings="~d" cpu="~f"/>\n</Answer>\n',[Result,Num,Bindings,Elapsed]),
                       le_pull('Answer')
                     ));
                       writeFmt('\n%%  ~w solutions="~d" bindings="~d" cpu="~f"\n',[Result,Num,Bindings,Elapsed])).

writeCycEvent(Class,Message,Vars):-not(isCycOption(client=html)),!, toMarkUp(cycl,[Class,Message],Vars,Chars),write(Chars),nl.
writeCycEvent(Class,Message,Vars):-isCycOption(client=html),!, event_to_chars(leml,Class,_Message,Vars,Chars),write(Chars),nl.
writeCycEvent(cb_consultation, assertion([PredicateI|ConsultTemplate],_Context_atom,_SN), continue):- 
               agentConsultation(_Context_atom,[PredicateI|ConsultTemplate], _ListOfGafsAsserted).
writeCycEvent(_,_,_):-!.


/*
Where the parameters are some string syntax or other straightforward data
structure and we've used I to signify a parameter that is used by the
function and O to signify a parameter that is returned by the
function.  If that were forall it had, we think that is sufficient for
the bulk of interactions.  Everything else is helpful but not strictly
essential.  Because of that, we believe that it is possible to run
our system with just the above commands after startup.

   We have shown a number of features implemented such as

  - explaination trees
  - belief execution time and search controls
  - compilation
  - consultation mode

The expanded API is
*/          
%=================================================================
%  CONSULTATION MANAGEMENT DIRECTIVES
%=================================================================

/*
where the xxxNative versions take the disp_modification WFSform and the other
versions take STANDARD.  Consultation mode has a simple default interface too:
*/



/* ; where the list is of arguments
missing that is requested from the user.  The default is to ask for
any and forall arguments that are missing

%TODO

ua_consultationModeEvery() ; ask the user for as many inputs as he's willing

to give
etc. ; other modes...

A further expansion to handle communication with a user agent external to
Prolog would add a message sent to a socket that process is listening to.
and a message string sent from Prolog to the user agent to request user input

"userInputRequest predicateName<cr>"

Where <cr> indicates a carriage return or some other suitable delimiter.

*/


% User Agent
:-dynamic('$CycOption'/3).
:-dynamic(saved_note/4).
:-dynamic(act_mem/3).


% ===========================================================
% THREAD SERVICE
% ===========================================================

% imports these models from SWI-Prolog
% prolog_thread_create(Goal,Id,Options)
% prolog_current_thread(Id,Status)
% prolog_thread_at_exit(Goal)
% prolog_thread_self(Id)
% prolog_thread_at_exit(Id,Goal)
% prolog_thread_join(Id,_)

/*
:-module(cyc_threads,
      [ thread_self/1,
	 servantProcessCreate/1,
	 servantProcessCreate/3,
	 servantProcessCreate/4,
	 servantProcessCreate/5,
	 isCycProcess/2,
	 isCycProcess/5,
	 createProcessedGoal/1,
	 servantProcessSelfClean/0,
	 showCycStatisticsHTML/0,
	 cleanOldProcesses/0,
	 showCycProcessHTML/0]).
  */
% :-include('cyc_header.pl').

:-dynamic(isCycProcess/5).


createProcessedGoal(Goal):-
      servantProcessCreate((prolog_thread_at_exit((
	 (thread_self(Id),prolog_thread_exit(i_am_done(Id))))),Goal),Id,[]).


servantProcessCreate(Perms,Name,Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(servantProcessSelfClean),Goal),Id,Options),
        asserta(isCycProcess(Perms,Name,Goal,Id,Options)).

servantProcessCreate(Name,Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(servantProcessSelfClean),Goal),Id,Options),
        asserta(isCycProcess(killable,Name,Goal,Id,Options)).

servantProcessCreate(Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(servantProcessSelfClean),Goal),Id,Options),
        asserta(isCycProcess(killable,thread(Id),Goal,Id,Options)).

servantProcessCreate(Goal):-
        servantProcessCreate(Goal,_Id,[detach(true)]).

isCycProcess(ID,Goal):-
        isCycProcess(_,_,Goal,ID,_).

debugProcess(T):-
	prolog_thread_signal(T, (attach_console, true)).


servantProcessSelfClean:-
      trace, 
      thread_self(Id),
      retractall(isCycProcess(_Perms,_Name,_Goal,Id,_Options)).




showCycStatisticsHTML:-
   writeFmt('<pre>'),prolog_statistics,writeFmt('</pre>').

showCycProcessHTML:-
        showCycStatisticsHTML,
        writeFmt('<hr><table border=1 width=80%><th>Id</th><th>Name</th><th>Status</th><th>Actions</th><th>Options</th><th>Goals</th>',[]),
        prolog_current_thread(Id,Status),
        isCycProcess(Perms,Name,Goal,Id,Options),
        writeCycProcessesHTML(Perms,Name,Goal,Id,Options,Status),
        fail.
showCycProcessHTML:-
        writeFmt('</table>',[]).


writeCycProcessesHTML(nokill,Name,Goal,Id,Options,Status):-
        writeFmt('<tr><td>~w</td><td><nobr>~w</td><td>~w</td><td>&nbsp;</a></td><td>~w</td><td>~w</td><tr>\n ',[Id,Name,Status,Options,Goal]),!.

writeCycProcessesHTML(Perms,Name,Goal,Id,Options,Status):-
        writeFmt('<tr><td>~w</td><td><nobr>~w</td><td>~w</td><td><A href="controlpanel.jsp?killable=~w">Kill</a></td><td>~w</td><td>~w</td><tr>\n ',[Id,Name,Status,Id,Options,Goal]),!.

cleanOldProcesses:-
        saveUserInput,
        prolog_current_thread(Id,Status),
        handleProcessStatus(Id,Status),fail.
cleanOldProcesses:-writeSavedPrompt,!.
cleanOldProcesses:-!.

handleProcessStatus(Id,running):-!. %Normal
handleProcessStatus(Id,exited(complete)):-!,prolog_thread_join(Id,_),!.
handleProcessStatus(Id,true):-!, writeSTDERR('% Process ~w complete.\n',[Id]),!,prolog_thread_join(Id,_),!.
handleProcessStatus(Id,exception(Error)):-!, writeSTDERR('% Process ~w exited with exceptions: ~q \n',[Id,Error]),!,prolog_thread_join(Id,_),!.
handleProcessStatus(Id,O):-!, writeSTDERR('% Process ~w exited "~q". \n',[Id,O]),!,prolog_thread_join(Id,_),!.





mutex_call(Goal,Id):-
                        mutex_create(Id),
                        mutex_lock(Id),!,
                        with_mutex(Id,Goal),!,
                        mutex_unlock_all.


startCycAPIServer:-
      flush_output,
      createPrologServerThread(3677).

:- (current_prolog_flag(threads,true) -> startCycAPIServer ; true).

