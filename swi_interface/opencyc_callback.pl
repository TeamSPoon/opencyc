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


mooBaseJavaClass('logicmoo.SwiMoo').

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

:- style_check(-singleton).
:- style_check(-discontiguous).
:- style_check(-atom).
:- style_check(-string).


:- set_prolog_flag(optimise,false).
:- set_prolog_flag(file_name_variables,false).
:- set_prolog_flag(agc_margin,0).
:- set_prolog_flag(trace_gc,false).
:-set_prolog_flag(character_escapes,true).
%:-set_prolog_flag(double_quotes,string).
:-set_prolog_flag(double_quotes,codes).
:-set_prolog_flag(report_error,true).
:-set_prolog_flag(verbose,normal).
:-set_prolog_flag(unknown,error).



:- use_module((opencyc)).
:-cycInit.


% ========================================================================================
% Using getCputime/1 (in Moo code) since Eclipse prolog (another port for Moo)  chokes on getCputime/1
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
% Some prologs have a printf() type predicate.. so I made up fmtString/writeFmt in the Moo code that calls the per-prolog mechaism
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

writeErrMsg(Out,E):-message_to_string(E,S),writeFmtFlushed(Out,'<prolog:error>~s</prolog:error>\n',[S]),!.
writeErrMsg(Out,E,Goal):-message_to_string(E,S),writeFmtFlushed(Out,'<prolog:error>goal "~q" ~s</prolog:error>\n',[Goal,S]),!.
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

% used like if_prolog(bp,do_bp_stuff),if_prolog(swi,do_swi_stuff) inline in Moo code


prologAtInitalization(V):-at_initialization(V),!,logOnFailureIgnore(V).


% ===================================================================
% Semi-Prolog Dependant Code
% ===================================================================
sigma_ua(X):-processRequest(X).

processRequest(X):-writeq(processRequest(X)),nl.


% -------------------------------------------------------------------
% Load the Moo header
% -------------------------------------------------------------------

% :-include('moo_header.pl').

% -------------------------------------------------------------------
% Load the Moo Engine
% -------------------------------------------------------------------
% :-ensure_loaded('moo_bootstrap.pl').

% load files
processBootstrap:-!.
/*
   moduleFile(_,Filename),
   ensure_loaded(Filename),fail.
  */
processBootstrap:-!.

startLogicMoo:-!.
startLogicMoo:-
	 startJava,
	 createJamud,
	 startJamud,
	 loadJamudReferences.


% -------------------------------------------------------------------
% Start the system
% -------------------------------------------------------------------





main(Port):-
   ignore(Port=5001),
   processBootstrap,
   setMooOptionDefaults, %trace,
   startLogicMoo,
   setMooOption(client,html),
   createPrologServer(80),
   createPrologServer(Port),!.
%   callIfPlatformUnix((prologAtInitalization(['mod/mod_nani.pl']),prologAtInitalization(['mod/mod_eliza.pl']),
%         prologAtInitalization(bot),prologAtInitalization(bot2))).
   %throw(wait_now).

% ===========================================================
% SOCKET SERVER - Looks at first charicater of request and decides between:
%  Http, Native or Soap and replies accordingly
% ===========================================================
/*
:-module(moo_httpd,[
   createPrologServer/1,
   xmlPrologServer/1,
   read_line_with_nl/3,
   decodeRequest/2,
   invokePrologCommandRDF/6,
   serviceAcceptedClientSocketAtThread/1]).
*/

% :-include(moo_header).



% :-use_module(moo_threads).
%% :-ensure_loaded(system_dependant).

:-dynamic(isKeepAlive/1).

createPrologServer(Port) :-
        mooProcessCreate(nokill,'Moo XML/SOAP Server Socket',xmlPrologServer(Port),_,[]).

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
        writeSTDERR('cs.\nMoo server started on port ~w. \n\nYes\n?- ',[Port]),flush_output),
        error(E,_),
        (writeSTDERR('\nWaiting for OS to release port ~w. \n(sleeping 4 secs becasue "~w")\n',[Port,E]),
        sleep(4),
        attemptServerBind(ServerSocket, Port))),!.


acceptClientsAtServerSocket(ServerSocket):-
		tcp_open_socket(ServerSocket, AcceptFd, _),
                cleanOldProcesses,!,
		tcp_accept(AcceptFd, ClientSocket, ip(A4,A3,A2,A1)),!,
                getPrettyDateTime(DateTime),
                sformat(Name,'Dispatcher for ~w.~w.~w.~w  started ~w ',[A4,A3,A2,A1,DateTime]),
                mooProcessCreate(killable,Name,serviceAcceptedClientSocketAtThread(ClientSocket),_,[detatch(true)]),!.

serviceAcceptedClientSocketAtThread(ClientSocket):-
	tcp_open_socket(ClientSocket, In, Out),!,
        setMooOption('$socket_in',In),
        setMooOption('$socket_out',Out),!,
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
         serviceSoapRequest(In,Out).  % see moo_soap.pl

serviceIOBasedOnChar('+',In,Out):-!,  
         serviceJavaApiRequest(In,Out).

serviceIOBasedOnChar(C,In,Out):-
        serviceNativeRequestAsRDF(C,In,Out).


% ===========================================================
% PROLOGD for Java SERVICE
% ===========================================================
serviceJavaApiRequest(In,Out):-
      get0(In,Plus),
        getThread(Session),
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
        getThread(Session),
        retractall(isKeepAlive(Session)),
        xmlClearTags,
       invokePrologCommand(Session,In,Out,PrologGoal,ToplevelVars,Returns).

serviceCycApiRequestSubP(Trim):-
       getSurfaceFromChars(Trim,[Result],ToplevelVars),!,
       balanceBinding(Result,PrologGoal),
	 ignore(catch(PrologGoal,_,true)).



% ===========================================================
% HTTPD SERVICE
% ===========================================================

serviceHttpRequest(In,Out):-
        readHTTP(In,Options),
        writeFmtFlushed(Out,'HTTP/1.1 200 OK\nServer: Moo-HTTPD\nContent-Type: text/html\n\n',[]),
        setMooOption(client,html),
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
        getThread(Session),
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


keep_alive:-getThread(Me),retractall(isKeepAlive(Me)),assert(isKeepAlive(Me)),writeFmtFlushed('<keepalive/>\n',[]).
goodbye:-getThread(Me),retractall(isKeepAlive(Me)),writeFmt('<bye/>/n',[]).


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


throwMoo(Module,Type,Details):-
        current_prolog_flag(debug_on_error, DebugOnError),
        set_prolog_flag(debug_on_error, false),!,
        throw(mooException(Module,Type,Details,DebugOnError)),
        ifInteractive(writeDebug('Post throwMoo')),!.




% ===========================================================
% NATIVE SOAPD SERVER FOR SWI-PROLOG
% ===========================================================

			    
%:-module(moo_soap,[]).

% :-include('moo_header.pl').

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
       getThread(Self),
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
structure_to_options([element(Ptag, ['xmlns:moo'=Server], Inner)],[opt_server=Server,opt_outter=Ptag|Out]):-!,
        structure_to_options(Inner,Out).



xmlOpenTag(Name):-getThread(Self),asserta(xmlCurrentOpenTags(Self,A)),writeFmtServer('<~w>',[Name]),!.
xmlOpenTagW(Out,Name,Text):-getThread(Self),asserta(xmlCurrentOpenTags(Self,A)),writeFmtServer(Out,'~w',[Text]),!.

xmlCloseTag(Name):-getThread(Self),ignore(retract(xmlCurrentOpenTags(Self,A))),writeFmtServer('</~w>',[Name]),!.
xmlCloseTagW(Name,Text):-getThread(Self),ignore(retract(xmlCurrentOpenTags(Self,A))),writeFmtServer('~w',[Text]),!.
xmlCloseTagW(Out,Name,Text):-getThread(Self),ignore(retract(xmlCurrentOpenTags(Self,A))),writeFmtServer(Out,'~w',[Text]),!.

xmlClearTags:-getThread(Self),retractall(xmlCurrentOpenTags(Self,A)).

xmlExitTags:-getThread(Self),retract(xmlCurrentOpenTags(Self,A)),writeFmtServer('</~w>',[Name]),fail.
xmlExitTags.


% ===========================================================
% Insert
% ===========================================================
parse_moo_soap(Options):-memberchk(submit=assert,Options),!,
        getMooOption(opt_ctx_assert='GlobalContext',Ctx),
        getMooOption(opt_theory='PrologMOO',Context),
        getMooOption(sf=surf,Assertion),
        atom_codes(Assertion,Assertion_Chars),
        getMooOption(user='Web',User),
        getMooOption(interp='kif',Interp),
        logOnFailure(getMooOption(tn=_,EXTID)),
        %sendNote(user,'Assert',formula(NEWFORM),'Ok.'). %,logOnFailure(saveMooCache)
        logOnFailure(getCleanCharsWhitespaceProper(Assertion_Chars,Show)),!,
        xml_assert(Show,Ctx,Context,User).

xml_assert(Show,Ctx,Context,User):-
        getSurfaceFromChars(Show,STERM,Vars),
        getMooTermFromSurface(STERM,NEWFORM),
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
parse_moo_soap(Options):-memberchk(submit=ask,Options),!,make,
        %write('<!DOCTYPE moo:ask SYSTEM "/opt/tomcat-4.0/webapps/moo-1.4b1/dtd/java_prolog.dtd">\n'),
        write('<moo:ask xmlns:moo="http://localhost">\n'),
        getMooOption(opt_ctx_request='GlobalContext',Ctx),
        getMooOption(opt_theory='PrologMOO',Context),
        getMooOption(sf=surf,Askion),
        atom_codes(Askion,Askion_Chars),
        getMooOption(user='Web',User),
        getMooOption(interp='kif',Interp),
         logOnFailure(getCleanCharsWhitespaceProper(Askion_Chars,Show)),!,
         logOnFailure(getSurfaceFromChars(Show,STERM,Vars)),!,
         logOnFailure(getMooTermFromSurface(STERM,NEWFORM)),!,
              logOnFailure(once(( NEWFORM=comment(_) ->
                     (writeFmt('<error>Syntax Error: Unmatched parentheses in "~s"</error>\n',[Show]),!,FORM=_) ;(!,
                     logOnFailure(invokeRequest_xml(NEWFORM,ChaseVars,Ctx,TrackingAtom,Context,User,Vars,CPU))
                     )))),
        write('</moo:ask>\n').

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
        toMarkUp(kif,Name,Vars,OName),
        toMarkUp(kif,Value,Vars,OValue),
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
:-module(moo_threads,
      [ getThread/1,
	 mooProcessCreate/1,
	 mooProcessCreate/3,
	 mooProcessCreate/4,
	 mooProcessCreate/5,
	 isMooProcess/2,
	 isMooProcess/5,
	 createProcessedGoal/1,
	 mooProcessSelfClean/0,
	 showMooStatisticsHTML/0,
	 cleanOldProcesses/0,
	 showMooProcessHTML/0]).
  */
% :-include('moo_header.pl').

:-dynamic(isMooProcess/5).


getThread(Id):-
	prolog_thread_self(Id).


createProcessedGoal(Goal):-
      mooProcessCreate((prolog_thread_at_exit((
	 (getThread(Id),prolog_thread_exit(i_am_done(Id))))),Goal),Id,[]).


mooProcessCreate(Perms,Name,Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(mooProcessSelfClean),Goal),Id,Options),
        asserta(isMooProcess(Perms,Name,Goal,Id,Options)).

mooProcessCreate(Name,Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(mooProcessSelfClean),Goal),Id,Options),
        asserta(isMooProcess(killable,Name,Goal,Id,Options)).

mooProcessCreate(Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(mooProcessSelfClean),Goal),Id,Options),
        asserta(isMooProcess(killable,thread(Id),Goal,Id,Options)).

mooProcessCreate(Goal):-
        mooProcessCreate(Goal,_Id,[detach(true)]).

isMooProcess(ID,Goal):-
        isMooProcess(_,_,Goal,ID,_).

debugProcess(T):-
	prolog_thread_signal(T, (attach_console, true)).


mooProcessSelfClean:-
      trace, 
      getThread(Id),
      retractall(isMooProcess(_Perms,_Name,_Goal,Id,_Options)).




showMooStatisticsHTML:-
   writeFmt('<pre>'),prolog_statistics,writeFmt('</pre>').

showMooProcessHTML:-
        showMooStatisticsHTML,
        writeFmt('<hr><table border=1 width=80%><th>Id</th><th>Name</th><th>Status</th><th>Actions</th><th>Options</th><th>Goals</th>',[]),
        prolog_current_thread(Id,Status),
        isMooProcess(Perms,Name,Goal,Id,Options),
        writeMooProcessesHTML(Perms,Name,Goal,Id,Options,Status),
        fail.
showMooProcessHTML:-
        writeFmt('</table>',[]).


writeMooProcessesHTML(nokill,Name,Goal,Id,Options,Status):-
        writeFmt('<tr><td>~w</td><td><nobr>~w</td><td>~w</td><td>&nbsp;</a></td><td>~w</td><td>~w</td><tr>\n ',[Id,Name,Status,Options,Goal]),!.

writeMooProcessesHTML(Perms,Name,Goal,Id,Options,Status):-
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
% Is is the type of Message catagory for the user agent.. A list of these are in TABLE 1.1 in <http://127.0.0.1/moo_interface_advanced.html>
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
:-module(moo_generation,
	 [ 
	 writeDebug/1,
	 writeDebug/2,
	 writeDebugFast/1,
	 logOnFailureIgnore/1,
	 setMooOptionExplicitWriteSettings/0,
	 setMooOptionImplicitWriteSettings/0,
	 sendNote/1,
	 sendNote/4,
	 writeFailureLog/2,
	 debugOnFailure/2,
	 writeObject/2,
	 writeObject/3,
	 writeObject_conj/2]).
					 */

% :-include('moo_header.pl').

% :-use_module(moo_globalisms).

% Assertion Time Errors

% Contradiction: The assertion contradicts other assertion(s) in the knowledge base. ; RAP note: this should be followed by a explaination as per the XML element definition for "explaination" 
% Syntax error: Illegal character in assertion 
% Syntax error: Unmatched parentheses in assertion 
% Syntax error: Missing parentheses in assertion 
% Syntax error: Unspecified 
% Argument type violation ; RAP note: this should be followed by a explaination of the type violation as per the XML element definition for "explaination" 
% Out of memory error 
% Broken socket: The connection between the web-based GUI and the belief engine is broken 
% Redundant assertion: ; RAP note: this should be followed by a explaination of the type violation as per the XML element definition for "explaination" 
% Undefined constant: Do you wish to add the constants to the Context? ; RAP note: this should be followed by a list of constants and a prompt to the user 


% ==========================================================
%  Sending Notes
% ==========================================================
writeDebug(T):-!.  writeDebug(C,T):-!.
 
%writeDebug(T):-(isMooOption(opt_debug=off)),!.
%writeDebug(C,T):-(isMooOption(opt_debug=off)),!.

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

dumpstack_argument(T):-isMooOption(opt_debug=off),!.  
	
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
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,nv(Subj),Vars,SS),
            toMarkUp(kif,nv(Message),Vars,SA),
            writeFmt('<font color=red>canonicalizer</font>: ~w "~w" (from ~w). \n',[SA,SS,SFrom]),!.

/*

sendNote_1(debug,From,Subj,Message,Vars):- %isMooOption(disp_notes_nonuser=on),!,
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,Subj,Vars,SS),
            toMarkUp(kif,Message,Vars,SA),
            writeFmt('% debug: ~w "~w" (from ~w). \n',[SA,SS,SFrom]).
sendNote_1(debug,From,Subj,Message,Vars):-!.
*/


sendNote_1(To,From,Subj,Message,Vars):- isMooOption(client=consultation),  !, 
            toMarkUp(kif,To,Vars,STo),
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,nv(Subj),Vars,S),
            toMarkUp(kif,nv(Message),Vars,A),
            fmtString(Output,'~w (~w from ~w) ',[A,S,SFrom]),
	    sayn(Output),!.

sendNote_1(To,From,'Rejected',Message,Vars):- isMooOption(client=automata),  !.

sendNote_1(To,From,Subj,Message,Vars):- isMooOption(client=automata),  !, 
            toMarkUp(kif,To,Vars,STo),
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,nv(Subj),Vars,S),
            toMarkUp(kif,nv(Message),Vars,A),
            writeFmt(user_error,'% ~w (~w from ~w) ',[A,S,SFrom]).

sendNote_1(To,From,Subj,Message,Vars):- isMooOption(client=html),  !, %  In Html
            toMarkUp(kif,To,Vars,STo),
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,nv(Subj),Vars,S),
            toMarkUp(html,nv(Message),Vars,A),
            writeFmt('<hr><B>To=<font color=green>~w</font> From=<font color=green>~w</font> Subj=<font color=green>~w</font></B><BR>~w\n',[To,From,S,A]),!.

sendNote_1(To,From,Subj,Message,Vars):- isMooOption(client=console),!, % In KIF
            toMarkUp(kif,To,Vars,STo),
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,nv(Subj),Vars,SS),
            toMarkUp(kif,nv(Message),Vars,SA),
            writeFmt(user_error,'; ~w: ~w "~w" (from ~w). \n',[STo,SA,SS,SFrom]),!.
  
sendNote_1(To,From,Subj,Message,Vars):-  % In KIF
            toMarkUp(kif,To,Vars,STo),
            toMarkUp(kif,From,Vars,SFrom),
            toMarkUp(kif,nv(Subj),Vars,SS),
            toMarkUp(kif,nv(Message),Vars,SA),
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

		
writeObject(OBJ,Vars):- isMooOption(client=html),!,
		((toMarkUp(html,OBJ,Vars,Chars),write(Chars))),!.
		
writeObject(OBJ,Vars):- isMooOption(client=atomata),!,
		((toMarkUp(kif,OBJ,Vars,Chars),write(Chars))),!.

writeObject(OBJ,Vars):- isMooOption(client=console),!,
		((toMarkUp(kif,OBJ,Vars,Chars),write(Chars))),!.

writeObject(OBJ,Vars):- isMooOption(client=consultation),!,
		(say(OBJ,Vars)),!.

writeObject(OBJ,Vars):- !,
		((toMarkUp(kif,OBJ,Vars,Chars),write(Chars))),!.


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


writeIfOption(C,P):-once_ignore(writeUAEvent(C,P,_)).

writeIfOption(C,M,Vars):-once_ignore(writeUAEvent(C,M,Vars)).

setMooOptionExplicitWriteSettings:-
             setMooOption(disp_explicit='off'),
             setMooOption(disp_modification='off'),
             setMooOption(disp_debug='off'),
             setMooOption(disp_note_user='off'),
             setMooOption(disp_notes_nonuser='off'),
             setMooOption(disp_qresults='off'),
             setMooOption(disp_explaination_true='off'),
             setMooOption(disp_explaination_other='off'),
             setMooOption(disp_bindings='off'),
             setMooOption(disp_answers_num_yes='off'),
             setMooOption(disp_answers_num_no='off'),
             setMooOption(disp_answers_num_definate='off'),
             setMooOption(disp_answers_num_tries='off'),
             setMooOption(disp_cputime='off'),
             setMooOption(disp_compiled='off'),
             setMooOption(disp_ground_forms='off').

setMooOptionImplicitWriteSettings:-
	       setMooOptionExplicitWriteSettings,
             setMooOption(disp_explicit='off'),
             setMooOption(disp_modification='on'),
             setMooOption(disp_debug='on'),
             setMooOption(disp_note_user='on'),
             setMooOption(disp_notes_nonuser='on'),
             setMooOption(disp_explaination_true='on'),
             setMooOption(disp_explaination_other='off'),
             setMooOption(disp_bindings='on'),
             setMooOption(disp_answers_num_yes='on'),
             setMooOption(disp_answers_num_no='on'),
             setMooOption(disp_answers_num_definate='on'),
             setMooOption(disp_answers_num_tries='on'),
             setMooOption(disp_cputime='on'),
             setMooOption(disp_compiled='on'),
             setMooOption(disp_ground_forms='on'),
	     setMooOption(traceOutput,yes),
	      setMooOption(prover,decider),
	      setMooOption(translationMode,flatRelational),
	      setMooOption(decider,on),
	      writeSTDERR(setMooOptionExplicitWriteSettings).






/*

15 Display options

disp_request
disp_note_user
disp_notes_nonuser
disp_compile
disp_modification
disp_debug
disp_trace
disp_answers
disp_explaination_true
disp_truth_othe r
disp_result
disp_statistics
disp_errors
disp_success
disp_other

10 options

opt_answers_max=[0-n] Default is Unlimited
opt_answers_min=[0-n]  Default is 1
opt_backchain_depth_max=[0-n] Default is 10
opt_deduceSurface_max=[0-n] Default is 1000
opt_timeout=[seconds] Default=60
opt_readonly=[true|false] Default is False
opt_deduce_assert=[true|false] Defualt is True
opt_language=[pnx_nf|getNegationForm|hylog|prolog|sigmese]   Default is Sigmese
opt_format=[kif|prolog]  Default is KIF
opt_compiled=[true|false]  Default is false

3 Callbacks

cb_error=[true|false] Default is false
cb_answer=[true|false] Default is false
cb_consult=[true|false] Default is false


*/

write_val(Any,Vars):- isMooOption(client=html)
      -> write_val_xml(Any,Vars) ;
      write_sterm(Any,Vars).
      
write_val_xml(Any,Vars):-
      toMarkUp(leml,Any,Vars,Chars),write(Chars),nl.


         
writeUAEvent(_,_,_):-isMooOption(disp_hide_all=true),!.
writeUAEvent(_,_,_):-telling_file,!.
writeUAEvent(Class,_,_):-isMooOption(Class=false),!.
writeUAEvent(Class,_,_):-isMooOption(disp_explicit=true),not(isMooOption(_Class=true)),!.

writeUAEvent(request_start,Note,Vars):-!,
         (isMooOption(client=html) -> 
          (writeFmt('<Answer>\n'),le_push('Answer'));
          true).

writeUAEvent(request_end,(Result,Normal,Elapsed,Num,Bindings),Vars):-!, 
                  (isMooOption(client=html) -> 
                     ((    
                       (toMarkUp(leml,note('user',logicEngine,Result,(Result,Normal,Elapsed,Num,Bindings)),Vars,Chars),once((var(Chars);write(Chars)))),
                       writeFmt('<Summary result="~w" solutions="~d" bindings="~d" cpu="~f"/>\n</Answer>\n',[Result,Num,Bindings,Elapsed]),
                       le_pull('Answer')
                     ));
                       writeFmt('\n%%  ~w solutions="~d" bindings="~d" cpu="~f"\n',[Result,Num,Bindings,Elapsed])).

writeUAEvent(Class,Message,Vars):-not(isMooOption(client=html)),!, toMarkUp(kif,[Class,Message],Vars,Chars),write(Chars),nl.
writeUAEvent(Class,Message,Vars):-isMooOption(client=html),!, event_to_chars(leml,Class,_Message,Vars,Chars),write(Chars),nl.
writeUAEvent(cb_consultation, assertion([PredicateI|ConsultTemplate],_Context_atom,_SN), continue):- 
               agentConsultation(_Context_atom,[PredicateI|ConsultTemplate], _ListOfGafsAsserted).
writeUAEvent(_,_,_):-!.


/*toMarkUp(Sterm,VS,Chars):-
           once(( isMooOption(client=html) -> 
            toMarkUp(leml,Sterm,VS,Chars);
            toMarkUp(kif,Sterm,VS,Chars))).
  */

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



/*
:-module(moo_globalisms,[
	 setMooOption/1,
	 setMooOption/2,
	 getMooOption/1,
	 getMooOption/2,
	 ensureMooOption/2,
	 ensureMooOption/3,
	 setMooOptionDefaults/0]).
*/


% :-ensure_loaded(moo_threads).

% Database
:-dynamic(mooCache/1).
:-dynamic(mooCache/2).
:-dynamic(mooCache/3).
:-dynamic(mooCache/4).
:-dynamic(mooCache/5).
:-dynamic(mooCache/6).
:-dynamic(mooCache/7).
:-dynamic(mooCache/8).
:-dynamic(mooCache/9).

% User Agent
:-dynamic('$MooOption'/3).
:-dynamic(saved_note/4).
:-dynamic(act_mem/3).

% Database
:-dynamic(tq_attempted_request/0).
:-dynamic(title/1).

% TQ System
:-dynamic(tq_missed_one_answer/0).
:-dynamic(tq_least_one_answer/0).
:-dynamic(t_answer_found/1).


% ===================================================================
% OPERATION PREDICATES
% ===================================================================
% Defaults
:-dynamic(getDefaultKB/1).
:-dynamic(get_default_request_context/1).
:-dynamic(get_default_assertion_context/1).
:-dynamic(version_tag/1).

:-dynamic(answer_found/1).
:-dynamic(moo_K_scenario/2).
:-dynamic(telling_prolog/0).  % If set asserts clauses into prolog database
:-dynamic(telling_file/0).   % If set write assertions to file
:-dynamic(disp_debug/5).         %PREDICATE RESOLUTON
:-dynamic(contexts/2).            %CONTEXT STATES
:-dynamic(setting_table/2).
:-dynamic(tabling/1).
:-dynamic(tabled_t/1).
:-dynamic(tabled_f/1).
:-dynamic(answer_yes/0).
:-dynamic(already_asked/2).
:-dynamic(save_all/2).
:-dynamic(moo_K_scenario/6).         %We keep a cache of forall consultations
:-dynamic(consultation_mode_on/0).
:-dynamic(resource_cache/2).
:-dynamic(debuggerize/0).

:-dynamic( le_ele/1).


:-dynamic('surface-word'/2).
:-dynamic('surface-macro'/2).
:-dynamic('browser-only'/1).
:-dynamic('not-implemented'/1).
:-dynamic('surface-atom'/1).
:-dynamic('surface-single-arity'/1).
:-dynamic('surface-multiple-arity'/1).
:-dynamic('surface-instance'/2).
:-dynamic('surface-subclass'/2).
:-dynamic('surface-class'/1).
:-dynamic('surface-quantifier'/1).



indexPredicate(positive_fact_cache(1,1,1,1)).
indexPredicate(positive_rule_cache(1,1,1,1)).
indexPredicate(negative_fact_cache(1,1,1,1)).
indexPredicate(negative_rule_cache(1,1,1,1)).

:-dynamic(have_arity/4).

% these hold prototypes of calls
:-dynamic(positive_fact_cache/4).
:-dynamic(positive_rule_cache/4).
:-dynamic(negative_fact_cache/4).
:-dynamic(negative_rule_cache/4).

:-dynamic(make_positive_cache/2).


:-dynamic('in-active-memory'/2).

:-dynamic('should_be_loaded'/2).



setMooOption([]):-!.
setMooOption([H|T]):-!,
      setMooOption(H),!,
      setMooOption(T),!.
setMooOption(Var=_):-var(Var),!.
setMooOption(_=Var):-var(Var),!.
setMooOption((N=V)):-nonvar(N),!,setMooOption_thread(N,V),!.
setMooOption(N):-atomic(N),!,setMooOption_thread(N,true).
	
setMooOption(Name,Value):-setMooOption_thread(Name,Value).
setMooOption_thread(Name,Value):-
	((getThread(Process),
	retractall('$MooOption'(Process,Name,_)),
	asserta('$MooOption'(Process,Name,Value)),!)).


unsetMooOption(Name=Value):-nonvar(Name),
	unsetMooOption_thread(Name,Value).
unsetMooOption(Name):-nonvar(Name),
	unsetMooOption_thread(Name,_).
unsetMooOption(Name):-(retractall('$MooOption'(_Process,Name,_Value))).


unsetMooOption_thread(Name):-
	unsetMooOption_thread(Name,_Value).

unsetMooOption_thread(Name,Value):-
	getThread(Process),
	retractall('$MooOption'(Process,Name,Value)).
	
getMooOption_nearest_thread(Name,Value):-
	getMooOption_thread(Name,Value),!.
getMooOption_nearest_thread(Name,Value):-
	'$MooOption'(_,Name,Value),!.
getMooOption_nearest_thread(_Name,_Value):-!.



isMooOption(Name=Value):-!,isMooOption(Name,Value).
isMooOption(Name):-!,isMooOption(Name,true).

isMooOption(Name,Value):-getMooOption_thread(Name,Value).

getMooOption_thread(Name,Value):-
	((getThread(Process),
	'$MooOption'(Process,Name,Value))),!.


getMooOption(Name=Value):-nonvar(Name),!,ensureMooOption(Name,_,Value).
getMooOption(Name=Default,Value):-nonvar(Name),!,ensureMooOption(Name,Default,Value).
getMooOption(Name,Value):-nonvar(Name),!,ensureMooOption(Name,_,Value).


ensureMooOption(Name=Default,Value):-
	ensureMooOption(Name,Default,Value),!.
	
ensureMooOption(Name,_Default,Value):-
	getMooOption_thread(Name,Value),!.

ensureMooOption(Name,Default,Default):-
	setMooOption_thread(Name,Default),!.

ensureMooOption(Name,_Default,Value):-nonvar(Name),!,   
	setMooOption_thread(Name,Value),!.

ensureMooOption(_Name,Default,Default).



setMooOptionDefaults:-
             (unsetMooOption(_)),
             setMooOption(opt_callback='sendNote'),
             setMooOption(cb_consultation='off'),
             setMooOption(opt_debug='off'),
             setMooOption(cb_error='off'),
             setMooOption(cb_result_each='off'),

% User Agent Defaults for Blank Variables
             setMooOption(opt_cxt_request='GlobalContext'),
             setMooOption(opt_ctx_assert='GlobalContext'),
             setMooOption(opt_tracking_number='generate'),
             setMooOption(opt_agent='ua_parse'),
             setMooOption(opt_precompiled='off'),
             getMooOption(opt_theory,Context),setMooOption(opt_theory=Context),
             setMooOption(opt_notation='kif'),
             setMooOption(opt_timeout=2),
             setMooOption(opt_readonly='off'),
             setMooOption(opt_debug='off'),
             setMooOption(opt_compiler='Byrd'),
             setMooOption(opt_language = 'pnx_nf'),

%Request Limits
             setMooOption(opt_answers_min=1),
             setMooOption(opt_answers_max=999), %TODO Default
             setMooOption(opt_backchains_max=5),
             setMooOption(opt_deductions_max=100),
             setMooOption(opt_backchains_max_neg=5),
             setMooOption(opt_deductions_max_neg=20),
             setMooOption(opt_forwardchains_max=1000),
             setMooOption(opt_max_breath=1000), %TODO Default

%Request Contexts
             setMooOption(opt_explore_related_contexts='off'),
             setMooOption(opt_save_justifications='off'),
             setMooOption(opt_deductions_assert='on'),
             setMooOption(opt_truth_maintence='on'),
             setMooOption(opt_forward_assertions='on'),
             setMooOption(opt_deduce_domains='on'),
             setMooOption(opt_notice_not_say=off),


%Request Pobibility
             setMooOption(opt_certainty_max=1),
             setMooOption(opt_certainty_min=1),
             setMooOption(opt_certainty=1),
             setMooOption(opt_resource_commit='on').


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
:-module(moo_threads,
      [ getThread/1,
	 mooProcessCreate/1,
	 mooProcessCreate/3,
	 mooProcessCreate/4,
	 mooProcessCreate/5,
	 isMooProcess/2,
	 isMooProcess/5,
	 createProcessedGoal/1,
	 mooProcessSelfClean/0,
	 showMooStatisticsHTML/0,
	 cleanOldProcesses/0,
	 showMooProcessHTML/0]).
  */
% :-include('moo_header.pl').

:-dynamic(isMooProcess/5).


getThread(Id):-
	prolog_thread_self(Id).


createProcessedGoal(Goal):-
      mooProcessCreate((prolog_thread_at_exit((
	 (getThread(Id),prolog_thread_exit(i_am_done(Id))))),Goal),Id,[]).


mooProcessCreate(Perms,Name,Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(mooProcessSelfClean),Goal),Id,Options),
        asserta(isMooProcess(Perms,Name,Goal,Id,Options)).

mooProcessCreate(Name,Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(mooProcessSelfClean),Goal),Id,Options),
        asserta(isMooProcess(killable,Name,Goal,Id,Options)).

mooProcessCreate(Goal,Id,Options):-
        prolog_thread_create((prolog_thread_at_exit(mooProcessSelfClean),Goal),Id,Options),
        asserta(isMooProcess(killable,thread(Id),Goal,Id,Options)).

mooProcessCreate(Goal):-
        mooProcessCreate(Goal,_Id,[detach(true)]).

isMooProcess(ID,Goal):-
        isMooProcess(_,_,Goal,ID,_).

debugProcess(T):-
	prolog_thread_signal(T, (attach_console, true)).


mooProcessSelfClean:-
      trace, 
      getThread(Id),
      retractall(isMooProcess(_Perms,_Name,_Goal,Id,_Options)).




showMooStatisticsHTML:-
   writeFmt('<pre>'),prolog_statistics,writeFmt('</pre>').

showMooProcessHTML:-
        showMooStatisticsHTML,
        writeFmt('<hr><table border=1 width=80%><th>Id</th><th>Name</th><th>Status</th><th>Actions</th><th>Options</th><th>Goals</th>',[]),
        prolog_current_thread(Id,Status),
        isMooProcess(Perms,Name,Goal,Id,Options),
        writeMooProcessesHTML(Perms,Name,Goal,Id,Options,Status),
        fail.
showMooProcessHTML:-
        writeFmt('</table>',[]).


writeMooProcessesHTML(nokill,Name,Goal,Id,Options,Status):-
        writeFmt('<tr><td>~w</td><td><nobr>~w</td><td>~w</td><td>&nbsp;</a></td><td>~w</td><td>~w</td><tr>\n ',[Id,Name,Status,Options,Goal]),!.

writeMooProcessesHTML(Perms,Name,Goal,Id,Options,Status):-
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




