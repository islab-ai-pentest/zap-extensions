/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2014 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.addon.aiassistant;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An example ZAP extension which adds a top level menu item, a pop up menu item
 * and a status panel.
 *
 * <p>
 * {@link ExtensionAdaptor} classes are the main entry point for adding/loading
 * functionalities
 * provided by the add-ons.
 *
 * @see #hook(ExtensionHook)
 */
public class ExtensionSimpleExample extends ExtensionAdaptor {

    // The name is public so that other extensions can access it
    public static final String NAME = "ExtensionAIAssistance";

    // The i18n prefix, by default the package name - defined in one place to make
    // it easier
    // to copy and change this example
    protected static final String PREFIX = "aiAssistance";

    /**
     * Relative path (from add-on package) to load add-on resources.
     *
     * @see Class#getResource(String)
     */
    private static final String RESOURCES = "resources";

    private ChatGPTClient chatGPTClient = new ChatGPTClient("API-KEY-HERE");
    private static final Logger LOGGER = LogManager.getLogger(ExtensionSimpleExample.class);

    private AbstractPanel statusPanel;
    private JTextField inputField;
    private JPanel messagePanel;
    private JScrollPane scrollPane;

    public ExtensionSimpleExample() {
        super(NAME);
        setI18nPrefix(PREFIX);
    }

    @Override
    public void hook(ExtensionHook extensionHook) {
        super.hook(extensionHook);

        // As long as we're not running as a daemon
        if (hasView()) {
            extensionHook.getHookView().addStatusPanel(getStatusPanel());
        }
    }

    @Override
    public boolean canUnload() {
        return true;
    }

    private ActionListener listener() {
        return e -> {
            if (e.getActionCommand().equals("send")) {
                String message = inputField.getText();
                inputField.setText("");

                if (message.isEmpty()) {
                    return;
                }

                messagePanel.add(AddMessage("Me", message, Color.BLUE));

                try {
                    String response = chatGPTClient.sendMessage(message);
                    messagePanel.add(AddMessage("AI", response, Color.RED));
                } catch (Exception e1) {
                    messagePanel.add(AddMessage("AI", e1.getMessage(), Color.RED));
                    LOGGER.error(message, e1);
                }

                messagePanel.revalidate();
                messagePanel.repaint();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                        verticalBar.setValue(verticalBar.getMaximum());
                    }
                });
            }
        };
    }

    private JPanel AddMessage(String subject, String message, Color color) {
        JPanel messageContainer = new JPanel(new GridBagLayout());

        GridBagConstraints subjectConstraints = new GridBagConstraints();
        subjectConstraints.gridx = 0;
        subjectConstraints.gridy = 0;
        subjectConstraints.weightx = 0.0;
        subjectConstraints.weighty = 0.0;
        subjectConstraints.anchor = GridBagConstraints.SOUTHWEST;
        JLabel subjectLabel = new JLabel(subject + ": ");
        subjectLabel.setForeground(color);
        subjectLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        messageContainer.add(subjectLabel, subjectConstraints);

        GridBagConstraints messageConstraints = new GridBagConstraints();
        messageConstraints.gridx = 1;
        messageConstraints.gridy = 0;
        messageConstraints.weightx = 1.0;
        messageConstraints.weighty = 1.0;
        messageConstraints.anchor = GridBagConstraints.SOUTHWEST;
        messageConstraints.fill = GridBagConstraints.HORIZONTAL;
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setMargin(new Insets(0, 10, 0, 0));
        messageArea.setForeground(color);
        messageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        messageContainer.add(messageArea, messageConstraints);

        return messageContainer;
    }

    private AbstractPanel getStatusPanel() {
        if (statusPanel == null) {
            statusPanel = new AbstractPanel();
            statusPanel.setLayout(new CardLayout());
            statusPanel.setName(Constant.messages.getString(PREFIX + ".panel.title"));
            statusPanel.setIcon(new ImageIcon(getClass().getResource(RESOURCES + "/cake.png")));

            // Main Panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            statusPanel.add(mainPanel);

            // Chat Panel
            JPanel chatPanel = new JPanel(new BorderLayout());

            messagePanel = new JPanel();
            messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
            messagePanel.setForeground(Color.PINK);

            scrollPane = new JScrollPane(messagePanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

            JPanel inputPanel = new JPanel(new BorderLayout());

            inputField = new JTextField(20);
            inputField.setActionCommand("send");
            inputField.addActionListener(listener());
            JButton sendButton = new JButton("Send");
            sendButton.setActionCommand("send");
            sendButton.addActionListener(listener());

            inputPanel.add(inputField, BorderLayout.CENTER);
            inputPanel.add(sendButton, BorderLayout.EAST);

            chatPanel.add(scrollPane, BorderLayout.CENTER);
            chatPanel.add(inputPanel, BorderLayout.SOUTH);

            mainPanel.add(chatPanel, BorderLayout.CENTER);
        }
        return statusPanel;
    }

    @Override
    public String getDescription() {
        return Constant.messages.getString(PREFIX + ".desc");
    }
}
