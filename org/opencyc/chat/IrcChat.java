
package org.opencyc.chat;

import java.lang.*;
import java.io.*;
import java.util.*;
import java.net.*;

import org.opencyc.api.*;
import org.opencyc.chat.*;
import org.opencyc.cycobject.*;
import org.opencyc.kif.*;
import org.opencyc.util.*;
import ViolinStrings.*;


public class IrcChat extends Thread  implements ChatSender {

    /**
     * IRC Bot details
     */

    // Name Bot goes by on IRC
    public String ircNick = "Cycbot";

    // WHOIS Information
    public String ircComment = "http://www.opencyc.org";

    // IRC Auto-join
    public String ircChannel = "#cyclbot";

    /**
     * IRC Sever details
     */
    public String ircServer = "irc.openprojects.net";
    public int ircPort = 6667;

    // IRC Unkown message replies sentence to
    public String ircDestination = "#cyclbot";

    // ArrayList of paraphrased writable locations
    public ArrayList paraphrased = new ArrayList();

    // IRC Debug messages sentence to ( may send to an IRC username instead of channel )
    public String ircDebug = "#cycbot";

    /**
     * IRC Server comunication
     */
    private Socket ircServerSocket =null;
    private InputStream ircInputStream = null;
    private OutputStream ircOutputStream = null;
    private BufferedReader ircInputReader = null;
    private BufferedWriter ircOutputWriter = null;

    /**
     * Telent DCC Chat Server 
     */
    //public DccServerThread dccServer = null;

    /**
     * reference to CycAccess (OpenCyc server)
     */
    public CycAccess cyc = null;


    /**
     * reference to ChatterBot
     */
    public org.opencyc.chat.ChatterBot chatterBot = null;



    public boolean running = false;


    /**
     * Creates a basic unstarted IRC Bot
     *     
     */
    public IrcChat() {
    }

    /**
     * Creates a full running IRC Bot
    *     
    */
    public IrcChat(CycAccess access, String nick, String comment, String server, int port, String channel) {
	// set the bot's nickname and description
	ircNick = nick;
	ircComment = comment;
	ircServer = server;
	ircPort = port;
	ircChannel = channel;
	cyc = access;
    }


    /**
     * Provide a command line function to launch the IrcChat application.  
     *     
     */
    public static void main(String[] args) {
	try {
	    IrcChat ircBot = new IrcChat();
	    ircBot.cyc = new CycAccess();
	    if ( args.length > 0 ) ircBot.ircNick = args[0];
	    if ( args.length > 1 ) ircBot.ircChannel = args[1];
	    if ( args.length > 2 ) ircBot.ircServer = args[2];
	    if ( args.length > 3 ) ircBot.ircPort = Integer.parseInt((args[3]));
	    System.out.println("Lauching IrcChat: n\nick='"+ircBot.ircNick+"' \nchannel='"+ircBot.ircChannel+"' \nserver='"+ircBot.ircServer+":'"+ircBot.ircPort+"");
	    ircBot.run();
	    System.exit(0);
	} catch ( Exception e ) {
	    e.printStackTrace(System.err);
	    System.exit(1);
	}
    }

    /**
     * Connects Bot to an IRC server
     *     
     */
    public void ircConnect() {
	Log.makeLog();
	try {
	    ircServerSocket = new Socket(ircServer, ircPort);
	} catch ( Exception e ) {
	    System.err.println("error Connecting to IRC server");
	    e.printStackTrace();
	}

	try {
	    ircInputStream = ircServerSocket.getInputStream();    
	    ircOutputStream = ircServerSocket.getOutputStream();
	} catch ( Exception e ) {
	    System.err.println("error opening streams to IRC server");
	    e.printStackTrace();                                   
	}

	ircInputReader = new BufferedReader(new InputStreamReader(ircInputStream));
	ircOutputWriter = new BufferedWriter(new OutputStreamWriter(ircOutputStream));  

	try {
	    // send user info
	    ircOutputWriter.write("user " + ircNick + " opencyc irc :" + ircComment);
	    ircOutputWriter.newLine();
	    ircOutputWriter.write("nick " + ircNick);
	    ircOutputWriter.newLine();
	    ircOutputWriter.flush();
	} catch ( Exception e ) {
	    System.out.println("ircLogOn error: " + e);
	}
	ircJoin(ircChannel);
	//paraphrased.add(ircChannel);
	ircJoin(ircDebug);

	this.running = true;

	startChatterBot();
	startPlugins();
	return;
    }

    /**
     * Disconnct Bot from an IRC server
     *     
     */
    public void ircDisconnect() {
	try {
	    chatterBot.finalize();
	    chatterBot = null;
	    ircOutputWriter.write("QUIT this.ircDisconnect();");
	    ircOutputWriter.newLine();
	    ircOutputWriter.flush();
	} catch ( Exception e ) {
	    System.out.println("ircLogOff error: " + e);
	    e.printStackTrace();
	}

	// close the IO streams to the IRC server
	try {
	    ircInputReader.close();
	    ircOutputWriter.close();
	    ircInputReader = null;
	    ircOutputWriter = null;
	    ircServerSocket.close();
	    ircServerSocket = null;

	} catch ( IOException e ) {
	    System.err.println("Error ircDisconnecting from IRC server");
	    e.printStackTrace();
	}
    }

    public void run() {
	ircConnect();
	if ( ircChannel!=null ) ircJoin(ircChannel);
	while ( !this.interrupted() && running )
	    try {
		this.serviceLoop();
	    } catch ( Exception e ) {
		System.out.println(""+e);
	    }
    }

    public void restartChatterBot() {
	terminateChatterBot();
	startChatterBot();
    }

    public void terminateChatterBot() {
	if ( chatterBot!=null ) {
	    try {
		chatterBot.finalize();
	    } catch ( Exception e ) {
		e.printStackTrace(System.err);
	    }
	    chatterBot = null;
	}
    }

    public void startChatterBot() {
	if ( chatterBot==null ) {
	    try {
		chatterBot = new ChatterBot(this);
		chatterBot.initialize();
	    } catch ( Exception e ) {
		e.printStackTrace(System.err);
	    }
	}
    }

    public void serviceLoop() throws Exception {
	serviceIRCServer();
    }

    /**
     * Sends a raw string to the IRC server
     */
    public boolean ircSend(String message) {
	System.out.println("irc: '" + message + "'");
	try {
	    ircOutputWriter.write(message);
	    ircOutputWriter.newLine();
	    ircOutputWriter.flush();
	} catch ( IOException e ) {
	    return false;
	}
	return true;
    }

    public void ircJoin(String channel) {
	ircSend("JOIN "+channel);
    }

    public void ircPart(String channel) {
	ircSend("PART "+channel);
    }
    /**
     * Send a notice to an IRC user
     * @param destination String
     * @param message String
     */
    public void sendNotice(String destination, String message) {
	ircSend("notice " + destination + " :" + message);
    }

    /**
     * Send a public message to an IRC user
     * @param destination String
     * @param message String
     */
    public boolean sendMessage(String destination, Object post) {

	if ( post==null || destination==null ) return false;

	// Wait a 1/2 sec (Keeps from flooding off server)
	try {
	    Thread.sleep(500);
	} catch ( InterruptedException e ) {
	}

	if ( post instanceof Iterator ) {
	    while ( ((Iterator)post).hasNext() ) {
		try {
		    if ( ircInputReader.ready() )
			if ( ircInputReader.readLine().trim().endsWith(".") ) return true;
		} catch ( Exception e ) {
		}
		sendMessage(destination,((Iterator)post).next());
	    }
	    return true;
	}

	if ( post instanceof BufferedReader ) {
	    String line = null;
	    try {
		while ( (line = ((BufferedReader)post).readLine()) != null ) sendMessage(destination,line);
	    } catch ( Exception e ) {
		System.out.println(""+e);
		return false;
	    }
	    return true;
	}

	if ( post instanceof CycList && isParaphrased(destination) )
	    return sendMessage(destination,attemptParaphrase((CycList)post) + " (" + ((CycList)post).toString() + ")");


	String message = post.toString().trim();

	if ( ViolinStrings.Strings.contains(message,"\n") || ViolinStrings.Strings.contains(message,"\r") )
	    return sendMessage(destination,new BufferedReader(new StringReader(message)));

	if ( message.length() > 200 ) {
	    int justify = message.substring(190).indexOf(' ')+190;
	    ircSend("privmsg " + destination + " :" + message.substring(0,justify-1));
	    return sendMessage(destination,message.substring(justify));
	}

	return ircSend("privmsg " + destination + " :" + message);

    }

    public String attemptParaphrase(Object post) {
	//Log.current.println("attemptParaphrase=" + post);
	if ( post == null ) return null;
	try {

	    if ( post instanceof Iterator ) {
		if ( !(((Iterator)post).hasNext()) ) return "none.";
		StringBuffer sb = new StringBuffer(attemptParaphrase(((Iterator)post).next()));
		while ( ((Iterator)post).hasNext() )
		    sb.append(", ").append(attemptParaphrase(((Iterator)post).next()));
		return sb.toString();
	    }

	    if ( post instanceof CycConstant )
		return cyc.converseString("(generate-phrase " + ((CycConstant)post).stringApiValue() +  ")");

	    if ( post instanceof CycNart )
		return cyc.converseString("(generate-phrase '" + ((CycNart)post).cyclify() +  ")");

	    if ( post instanceof CycVariable )
		return(((CycVariable)post).stringApiValue());

	    if ( post instanceof CycList ) {

		if ( ((CycList)post).isEmpty() )
		    return "an empty list ";

		if ( !((CycList)post).isProperList() )
		   // return attemptParaphrase(((CycList)post).first()) + " = " + (((CycList)post).rest());
		   return attemptParaphrase(((CycList)post).first()) + " = " + attemptParaphrase(((CycList)post).rest());

		if ( ((CycList)post).first() instanceof CycList ) return attemptParaphrase(((CycList)post).iterator());

		return cyc.converseString("(generate-phrase '" + ((CycList)post).cyclify() +  ")");
	    }

	} catch ( Exception e ) {
	    e.printStackTrace();
	}
	return post.toString();
    }

    /**
     * Receives and parses IRC Server messages
     */
    public void serviceIRCServer() throws Exception {

	// Wait a 1/10th sec
	try {
	    Thread.sleep(100);
	} catch ( InterruptedException e ) {
	}

	// Data ?
	if ( !ircInputReader.ready() ) return;

	String message = ircInputReader.readLine();

	//System.out.println(message);

	// send a pong back
	if ( message.substring(0,4).equalsIgnoreCase("ping") ) {
	    ircSend("pong " + message.substring(5));
	    return;
	}
	serviceIRCSession(message);
    }


    /**
     * Receives and parses IRC Session messages
     */
    public void serviceIRCSession(String message) {
	String prefix = null;
	String command = null;
	String params = null;
	String user = null;


	// check for the prefix
	if ( message.substring(0,1).equals(":") ) {
	    prefix = message.substring(1, message.indexOf(' '));
	    message = message.substring(message.indexOf(' ') + 1);
	}

	// extract the command
	command = message.substring(0, message.indexOf(' '));

	// get the parameters (the rest of the message)
	params = message.substring(message.indexOf(' ') + 1);

	if ( params.toLowerCase().startsWith(":closing") ) {
	    ircConnect();
	    return;
	}

	try {
	    int col = params.indexOf(':');
	    String destination = params.substring(0,col-1).trim();
	    ircDestination = destination;
	    params = params.substring(col+1).trim();

	    serviceIRCTransaction(prefix.substring(0, prefix.indexOf('!')),prefix,command,destination, params);
	} catch ( Exception e ) {
	}
    }
    /**
     * Process an IRC Transaction
     * @param destination String nickname of the user who sentence the message
     * @param message String the command
    
    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	
    
     */
    public void serviceIRCTransaction(String from, String hostmask, String command, String destination,String params) {

	if ( hostmask.startsWith(ircNick) ) return;
	if ( command.equalsIgnoreCase("PRIVMSG") )
	    servicePublicMessage(from, hostmask, destination, params.trim());
    }

    /**
     * Process a Public message (PRIVMSG to Channel)
     * @param destination String nickname of the user who sentence the message
     * @param message String the command
    
    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	
    
     */
    public void servicePublicMessage(String from, String hostmask, String returnpath,String params) {

	String lcparams = params.toLowerCase().trim();

	int ccol = params.indexOf(':');
	if ( ccol<0 ) ccol = params.indexOf(' ');

	if ( ccol>1 ) {
	    String token = lcparams.substring(0,ccol).trim();
	    params = params.substring(ccol+1).trim();
	    if ( serviceToken(from, hostmask, returnpath, token,params) ) return;
	} else {
	    if ( serviceToken(from, hostmask, returnpath, lcparams, params) ) return;
	}

	serviceChatter(from,hostmask,params,returnpath);
    }

    /**
     * Process a Token message 
     * @param destination String nickname of the user who sentence the message
     * @param message String the command
    
    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	
    
     */
    public boolean serviceToken(String from, String hostmask, String returnpath,String token,String params) {
	System.out.println("token: '" + token + "' params: '" + params + "'");

	if ( token.equals("hello") ) {
	    sendMessage(returnpath, "hello " + from);
	    return true;
	}
	if ( token.equals("time") ) {
	    sendMessage(returnpath, "the time was " + (new Date()).toString());
	    return true;
	}
	if ( token.equals("restart") ) {
	    restartChatterBot();
	    return true;
	}
	if ( token.equals("help") ) {
	    sendHelp(returnpath,params);
	    return true;
	}
	if ( token.equals("echo") ) {
	    sendMessage(returnpath, params);
	    return true;
	}
	if ( token.equals("cyclify") ) {
	    sendMessage(returnpath, toCycListString(params));
	    return true;
	}
	if ( token.equals("paraphrase") ) {
	    if ( params.startsWith("#") ) {
		paraphrased.add(params);
		return true;
	    }
	    sendMessage(returnpath, attemptParaphrase(toCycList(params)));
	    return true;
	}
	if ( token.equals("noparaphrase") ) {
	    paraphrased.remove(params);
	    return true;
	}
	if ( token.equals("subl") ) {
	    serviceSubL(from,returnpath, params);
	    return true;
	}
	if ( token.equals("debug") ) {
	    ircDebug = params;
	    return true;
	}
	if ( token.equals("ask") ) {
	    serviceQuery(from,returnpath, params);
	    return true;
	}
	if ( token.equals("mt") ) {
	    try {
		CycConstant mt = cyc.makeCycConstant(params);
		cyc.assertIsa(mt,cyc.makeCycConstant("#$Microtheory"),cyc.baseKB);
		mtUser.put(from,mt);
	    } catch ( Exception e ) {
	    }
	    return true;
	}
	if ( token.equals("prove") ) {
	    serviceProve(from,returnpath, params);
	    return true;
	}
	if ( token.equals("query") ) {
	    serviceQueryUser(from,returnpath, params);
	    return true;
	}
	if ( token.equals("assert") ) {
	    serviceAssert(from,returnpath, params);
	    return true;
	}

	if ( token.equals("join") ) {
	    ircJoin(params);
	    return true;
	}
	if ( token.startsWith("part") ) {
	    ircPart(params);
	    return true;
	}
	if ( token.startsWith("putserv") ) {
	    ircSend(params);
	    return true;
	}

	if ( servicePlugin( from,  hostmask,  returnpath, token, params) ) return true;

	return false;

    }

    public boolean servicePlugin(String from, String hostmask, String returnpath,String token,String params) {
	return false;
    }

    public void startPlugins() {
	return;
    }

    public void sendHelp(String returnpath, String params) {
	sendMessage(returnpath,"usage: help <hello|time|join|part|ask|query|assert|cyclify>");
    }

    /**
    * Process a SubL command 
     * @param destination String nickname of the user who sentence the message
     * @param message String the command
    
    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	
    
     */
    public void serviceSubL(String cyclist,String returnpath, String subl) {
	try {
	    sendAnswers(returnpath,cyc.converseObject(subl));
	} catch ( Exception e ) {
	    sendMessage(returnpath, ""+ e    /*  + "\" " + " trying to eval \"" + subl + "\" " + "from \"" + cyclist + "\" with returnpath \""+returnpath + "\""*/ );
	}
    }

    /**
    * Process a Query command 
     * @param destination String nickname of the user who sentence the message
     * @param message String the command

    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	

     */

    public void serviceQuery(String cyclist,String returnpath, String query) {
	try {
	    sendAnswers(returnpath, cyc.converseObject( "(cyc-query '" +toCycListString(query) + " #$InferencePSC)"));
	} catch ( Exception e ) {
	    sendMessage(returnpath, ""+ e);
	}
    }
    /**
    * Process a Prove command (Query with proof)
     * @param destination String nickname of the user who sentence the message
     * @param message String the command

    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	

     */
    public void serviceProve(String cyclist,String returnpath, String query) {
	try {
	    sendAnswers(returnpath, cyc.converseObject( "(fi-prove '" +toCycListString(query) + " #$InferencePSC)"));
	} catch ( Exception e ) {
	    sendMessage(returnpath, ""+ e);
	}
    }

    /**
    * Process an Ask command 
     * @param destination String nickname of the user who sentence the message
     * @param message String the command

    params are in the form
    <my nick> :<message> 
    or
    <my nick> <message>	

     */

    public void serviceQueryUser(String cyclist,String returnpath, String query) {
	try {
	    sendAnswers(returnpath, cyc.converseObject( "(cyc-query '" +toCycListString(query) + " " + mtForUser(cyclist).stringApiValue() + ")"));
	} catch ( Exception e ) {
	    sendMessage(returnpath, ""+ e);
	}
    }

    /**
    * Process an Assert command 
     * @param destination String nickname of the user who sentence the message
     * @param message String the command

    params are in the form
    assert:<message> 
    or
    <my nick>  <message>	

     */

    public void serviceAssert(String cyclist,String returnpath, String sentence) {
	serviceSubL(cyclist,returnpath, "(cyc-assert '" +toCycListString(sentence) + " " + mtForUser(cyclist).stringApiValue() + ")");
    }

    /**
    * Process an Assert command 
     * @param destination String nickname of the user who sentence the message
     * @param message String the command

    params are in the form
    assert:<message> 
    or
    <my nick>  <message>	

     */
    public void serviceChatter(String cyclist,String identity,String message, String returnpath) {
	ircDestination = returnpath;
	return;


	//	try {
	//          if ( chatterBot!=null ) chatterBot.receiveChatMessage(cyclist, cyclist    /*identity hostmask?*/,message);
	//	} catch ( Exception e ) {
	//	    e.printStackTrace(System.out);
	// sendDebug(""+e);
	//	}
    }


    public HashMap mtUser = new HashMap();

    /**
     * Returns a Mt for a user
     */
    public CycFort mtForUser(String cyclist) {

	CycConstant mt = (CycConstant) mtUser.get(cyclist);
	if ( mt==null ) {
	    try {
		mt = cyc.makeCycConstant("#$"+cyclist+"ChatMt");
		cyc.assertIsa(mt,cyc.makeCycConstant("#$Microtheory"),cyc.baseKB);
	    } catch ( Exception e ) {
		mt = cyc.baseKB; 
	    }
	    sendMessage(ircDestination,"Using microtheory" + mt.cyclify() + " for assertions until " + cyclist + " types \"mt <something>\"");
	    mtUser.put(cyclist,mt);
	}
	return(CycFort)mt;
    }


    /**
     * Returns true if Paraphrased for a destination/returnpath
     */
    public boolean isParaphrased(String destination) {
	return( paraphrased.contains(destination));
    }

    /**
     * Cyclifys a sentence a string
     */
    public CycList toCycList(String sentence) {
	try {
	    return(((CycList)((new CycListKifParser(cyc)).read(sentence))));
	} catch ( Exception e ) {
	    return null;
	}

    }
    /**
     * Cyclifys a sentence to a string
     */
    public String toCycListString(String sentence) {
	try {
	    return(((CycList)((new CycListKifParser(cyc)).read(sentence))).cyclify());
	} catch ( Exception e ) {
	    return null;
	}

    }

    public static CycSymbol SYMBOL_NIL = new CycSymbol("NIL");

    /**
     * Sends the Answer message from Cyc to returnpath
     */
    public void sendAnswers(String returnpath, Object results) {
	if ( results instanceof CycSymbol ) {
	    if (results.equals(SYMBOL_NIL)) {
		sendMessage(returnpath,"no answers found");
		return;
	    }
	}
	if ( results instanceof CycList ) {
	    CycList answers = (CycList) results;
	    if ( answers.size()==1 && answers.first().equals(IrcChat.SYMBOL_NIL)) {
		sendMessage(returnpath,"true sentence");
		return;
	    }
	    
	    if ( answers.toString().length()>120 ) {
		if ( answers.size()>50 ) {
		    sendMessage(returnpath,"Your question returned " + answers.size() + " answers .. please refine. (here are the first five)");
		    CycList five = new CycList();
		    for ( int i=0 ; i<5 ; i++ )	five.add(answers.get(i));
		    sendAnswers(returnpath,five);
		    return;
		}
		sendMessage(returnpath,answers.iterator());
		return;
	    }
	}
	sendMessage(returnpath,results);
    }

    public void sendDebug(String message) {
	sendMessage(ircDebug ,message);
    }


    /**
     * Sends the chat message from Cyc into the chat system.
     */
    public void sendChatMessage(String chatMessage) {
       // sendMessage(ircDestination,chatMessage);
    }

    /**
     * Receives chat messages from the user.
     */
    private String receiveChatMessage() throws IOException {
	System.out.print("user> ");
	return "foo";
    }

    public void recievedConsoleMsg(DccClientHandlerThread client,String message) {
	System.out.print("recievedConsoleMsg " + client + ": " + message);
    }

    public void listenForConnections(int port) {
	try {
	    //dccServer = new DccServerThread(this,port);
	    //dccServer.start();
	} catch ( Exception e ) {
	}
    }

    public class DccServerThread extends Thread {
	public boolean listening = true;
	private ServerSocket serverSocket = null;
	private int serverPort = 4444;
	private IrcChat IrcChat = null;
	public HashMap clients = null;

	public DccServerThread(IrcChat ircBot, int port) throws IOException {
	    IrcChat =  ircBot;
	    serverPort = port;
	    clients = new HashMap();
	    serverSocket = new ServerSocket(port);
	    this.start();
	}

	public void run() {
	    try {
		while ( listening ) {
		    Socket thisClient = serverSocket.accept();
		    String username = getLogin(thisClient);
		    DccClientHandlerThread clientThread = new DccClientHandlerThread(IrcChat, thisClient);
		    clientThread.run();
		    clients.put(username,clientThread);
		}

		serverSocket.close();
	    } catch ( Exception e ) {
	    }

	}

	public String getLogin(Socket thisClient) {
	    return thisClient.toString();
	}
    }

    public class DccClientHandlerThread extends Thread {
	private Socket socket = null;
	private IrcChat IrcChat = null;
	private PrintWriter out = null;
	private BufferedReader in = null;

	public DccClientHandlerThread(IrcChat ircBot,Socket socket) {
	    super("DccClientThread");
	    this.socket = socket;
	}

	public void println(String message) {
	    out.println(message);
	}

	public void run() {
	    String inputLine = null;

	    try {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		while ( (inputLine = in.readLine()) != null ) {
		    IrcChat.recievedConsoleMsg(this,inputLine);
		}

	    } catch ( IOException e ) {
		e.printStackTrace();
	    }
	}
	public void disconnect() {

	    try {
		out.close();
		in.close();
		socket.close();

	    } catch ( IOException e ) {
		e.printStackTrace();
	    }
	}
    }

}



