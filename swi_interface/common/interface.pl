% ===================================================================
% Connecter to Cyc TCP Server
% ===================================================================
:-dynamic(cycConnected/1).
:-dynamic(cycOutputStream/1).
:-dynamic(cycInputStream/1).
:-dynamic(cycChatMode/1).

establishConnection:-cycConnected(_),!.
establishConnection:-
		tcp_socket(SocketId),
		tcp_connect(SocketId,'127.0.0.1':3601),
		tcp_open_socket(SocketId, InStream, OutStream),!,
		format(user_error,'Connected to Cyc TCP Server {~w,~w}\n',[InStream,OutStream]),
		flush_output(user_error),
		assert(cycConnected(SocketId)),
		assert(cycOutputStream(OutStream)),
		assert(cycInputStream(InStream)),
		assert(cycConnected),!.

disConnection:-
      retract(cycConnected(SocketId)),
      close(SocketId),!.
      


