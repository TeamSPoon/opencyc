package org.opencyc.cycagent.coabs;

/*
*  Copyright:    Copyright (c) 1999-2001 Global InfoTek, Inc.
*  Author:       Martha L. Kahn
*  Date:         March, 1999
*  Company:      Global InfoTek, Inc.
*  Description:  GUI used for testing AgentRegistrationHelper and Directory
*                methods.
*
********************************************************************************
* This code is the exclusive property of Global InfoTek Inc. and may not
* be used for any purpose without the written permission of Global
* InfoTek, Inc.
********************************************************************************
*/

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.globalinfotek.coabsgrid.Message;
import com.globalinfotek.coabsgrid.MessageListener;
import com.globalinfotek.coabsgrid.paamtutorial.WeatherAgent;

/**
 *  A GUI to test AgentRegistrationHelper and Directory methods.<p>
 *  Send bug reports to coabsgrid-bugs@globalinfotek.com
 *  @see Agent1Test
 *  @see Agent2Test
 *  @see JFCAgent
 *  @see WeatherAgent
 *  @see AgentTestGUI
 *  @see AgentTestInterface
 *  @author Martha L. Kahn, Global InfoTek, Inc.
 */
public class AgentTestGUI extends JFrame implements MessageListener {

    private AgentTestInterface agent;
    private JTextArea queueTextArea;

    public AgentTestGUI(final AgentTestInterface agent) {
        this.agent = agent;
        agent.addMessageListener(this);

        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        setSize(new Dimension(770, 700));
        setTitle(agent.getName() + " Test");

        Font font = new Font("Monospaced", Font.BOLD, 16);

        Box boxV1 = new Box(BoxLayout.Y_AXIS);
        boxV1.add(Box.createVerticalStrut(50));

        JButton registerButton = new JButton("register-agent");
        registerButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    agent.register();
                }
            });
        boxV1.add(registerButton, null);

        JButton deregisterButton = new JButton("deregister-agent");
        deregisterButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    agent.deregister();
                }
            });
        boxV1.add(deregisterButton, null);

        JButton modifyButton = new JButton("modify-agent");
        modifyButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    agent.modify();
                }
            });
        boxV1.add(modifyButton, null);

        boxV1.add(new JSeparator());

        Box boxVforward = new Box(BoxLayout.Y_AXIS);
        boxVforward.setBackground(Color.yellow);
        boxVforward.setForeground(Color.yellow);

        JLabel recipientLabel = new JLabel("Recipient:");
        boxVforward.add(recipientLabel, null);

        final JTextField recipientTextField = new JTextField();
        recipientTextField.setFont(font);
        recipientTextField.setPreferredSize(new Dimension(300, 30));
        recipientTextField.setMaximumSize(new Dimension(500, 30));
        recipientTextField.setText(agent.getReceiver());
        boxVforward.add(recipientTextField, null);

        JLabel messageLabel = new JLabel("Message:");
        boxVforward.add(messageLabel, null);

        JScrollPane messageScrollPane = new JScrollPane();
        messageScrollPane.setPreferredSize(new Dimension(300, 550));
        messageScrollPane.setMaximumSize(new Dimension(800, 550));
        final JTextArea messageTextArea = new JTextArea();
        messageTextArea.setFont(font);
        messageTextArea.setLineWrap(true);
        messageTextArea.setPreferredSize(new Dimension(250, 600));
        messageTextArea.setText(agent.getRawText());

        messageScrollPane.getViewport().add(messageTextArea, null);
        boxVforward.add(messageScrollPane, null);

        JButton forwardButton = new JButton("forward");
        forwardButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    agent.setReceiver(recipientTextField.getText());
                    agent.setRawText(messageTextArea.getText());
                    agent.forward();
                }
            });
        boxVforward.add(forwardButton, null);
        boxV1.add(boxVforward);

        Box boxV2 = new Box(BoxLayout.Y_AXIS);
        JLabel messageQueueLabel = new JLabel("Message Queue:");
        boxV2.add(messageQueueLabel);

        queueTextArea = new JTextArea("Messages received:\n------------------\n");
        queueTextArea.setFont(font);
        queueTextArea.setLineWrap(true);
        queueTextArea.setEditable(false);
        JScrollPane queueScrollPane = new JScrollPane(queueTextArea);
        queueScrollPane.setPreferredSize(new Dimension(450, 600));
        boxV2.add(queueScrollPane);

        Box boxH = new Box(BoxLayout.X_AXIS);
        boxH.add(boxV1);
        boxH.add(Box.createHorizontalStrut(20));
        boxH.add(boxV2);
        getContentPane().add(boxH);
    }

    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    /**
     * Adds message to scrolling text area. Implements MessageListener.
     */
    public void messageAdded(Message message) {
        queueTextArea.append("\n" + message.toString() + "\n");
        queueTextArea.setCaretPosition(queueTextArea.getDocument().getLength());
    }

    /**
     * Provides the Main method. Arg should be fully qualified class name
     * of a class that implements AgentTestInterface. See scripts agent1gui
     * and agent2gui for examples. Creates instance of the class and passes
     * it as argument to the AgentTestGUI.
     */
    public static void main(String[] args) throws IOException,
        ClassNotFoundException, InstantiationException, IllegalAccessException {
        String testAgentClassName = args[0];
        Class testAgentClass = Class.forName(testAgentClassName);
        Object testAgent = testAgentClass.newInstance();
        JFrame frame =
            new AgentTestGUI((AgentTestInterface) testAgent);
        frame.validate();
        frame.setVisible(true);
        System.out.println ("started GUI for: " + args[0]);
    }
}
