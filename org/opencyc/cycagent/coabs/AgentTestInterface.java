package org.opencyc.cycagent.coabs;

import com.globalinfotek.coabsgrid.MessageListener;
import java.rmi.RemoteException;

/**
 *  Copyright:    Copyright (c) 1999-2001 Global InfoTek, Inc.
 *  Author:       Martha L. Kahn
 *  Date:         March, 1999
 *  Company:      Global InfoTek, Inc.
 *  Description:  Interface used by agents using the AgentTestGUI.
 *
 ********************************************************************************
 * This code is the exclusive property of Global InfoTek Inc. and may not
 * be used for any purpose without the written permission of Global
 * InfoTek, Inc.
 ********************************************************************************
 *  An inteface implemented by agents so they can be used by AgentTestGUI.
 *
 *  Send bug reports to coabsgrid-bugs@globalinfotek.com
 *  @see Agent2Test
 *  @see JFCAgent
 *  @see WeatherAgent
 *  @see Agent1Test
 *  @author Martha L. Kahn, Global InfoTek, Inc.
 */
public interface AgentTestInterface {

    /**
     * Gets the agent name for the test GUI title.
     */
    public String getName();

    /**
     * Gets the the message receiver for the test GUI.
     */
    public String getReceiver();

    /**
     * Sets the message receiver.
     * Used by the GUI when the user edits the message receiver.
     *
     * @param r the message receiver
     */
    public void setReceiver(String r);

    /**
     * Gets the message text. Used by the GUI to display the message text.
     */
    public String getRawText();

    /**
     * Sets the message text.
     * Used by the GUI when the user edits the message text.
     *
     * @param text the message text
     */
    public void setRawText(String text);

    /**
     * Adds a message listener.
     * Just for working with the AgentTestFrame.
     * Lets the frame be a listener for new messages on the queue
     * so it can print them out.
     *
     * @param ml the message listener
     */
    public void addMessageListener(MessageListener ml);

    // FIPA AMS
    /**
     * Register this agent.
     */
    public void register();

    /**
     * Deregisters this agent.
     */
    public void deregister();

    /**
     * Modifies this agent's advertised capabilities.
     */
    public void modify();

    // FIPA ACC
    /**
     * Forwards the message.
     */
    public void forward();
}
