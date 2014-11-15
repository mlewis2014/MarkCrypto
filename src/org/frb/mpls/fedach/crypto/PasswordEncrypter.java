/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: -
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of Oracle or the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. THIS SOFTWARE IS PROVIDED
 * BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.frb.mpls.fedach.crypto;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This is a screen that is designed to 
 *  1) take a user-inputted password,
 *  2) generate a key sequence 
 *  3) create a 3DES encypted password (using the key sequence).
 *  4) display the encrypted password (base64 encoded)
 *  5) display the key sequence used.
 *    *   
 *   Provides the ability to generate a TripleDES encrypted password
 *   that can be copied into a .properties file (instead of having 
 *   to deploy a password
 *   
 * @author i1mal00
 * 
 */
public class PasswordEncrypter extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private String newline = "\n";
    protected static final String passwordFieldString = "Password";
    protected static final String verifypassword = "Verify Password";
    protected static final String buttonString = "JButton";

    protected String firstPass = "";
    protected String verifyPass = "";

    protected JLabel actionLabel;
    protected JTextArea textArea;
    protected JTextArea textAreaKey;

    public PasswordEncrypter() {
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout());

        // Create a password field.
        JPasswordField passwordField = new JPasswordField(30);
        passwordField.setActionCommand(passwordFieldString);
        passwordField.addActionListener(this);

        // Create a password field.
        JPasswordField passwordField2 = new JPasswordField(30);
        passwordField2.setActionCommand(verifypassword);
        passwordField2.addActionListener(this);

        JLabel passwordFieldLabel = new JLabel(passwordFieldString + ": ");

        passwordFieldLabel.setLabelFor(passwordField);
        JLabel passwordFieldLabel2 = new JLabel(verifypassword + ": ");
        passwordFieldLabel2.setLabelFor(passwordField2);

        // Create a label to put messages during an action event.
        actionLabel = new JLabel("Type text in a field and press Enter.");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Lay out the text controls and the labels.
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();

        textControlsPane.setLayout(gridbag);

        JLabel[] labels = { passwordFieldLabel, passwordFieldLabel2};
        JTextField[] textFields = { passwordField, passwordField2};
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);

        c.gridwidth = GridBagConstraints.REMAINDER; // last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        textControlsPane.add(actionLabel, c);
        textControlsPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory
            .createTitledBorder("Enter new password"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Create a text area.
        // textArea =
        // new JTextArea("This is an editable JTextArea. " +
        // "A text area is a \"plain\" text component, "
        // + "which means that although it can display text "
        // + "in any font, all of the text is in the same font.");

        textArea =
            new JTextArea("Copy this encrypted password to the appropriate properties file. "
                + "Copy the generated key sequence to the \"simple encryption\" file.");

        textArea.setFont(new Font("Serif", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(5, 100));
        areaScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Encrypted password"), BorderFactory.createEmptyBorder(5, 5, 5,
                5)), areaScrollPane.getBorder()));

        textAreaKey = new JTextArea("key sequence goes here. ");
        textAreaKey.setFont(new Font("Serif", Font.PLAIN, 16));
        textAreaKey.setWrapStyleWord(true);
        JScrollPane keyAreaScrollPane = new JScrollPane(textAreaKey);
        keyAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        keyAreaScrollPane.setPreferredSize(new Dimension(20, 100));
        keyAreaScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Key sequence"), BorderFactory.createEmptyBorder(5, 5, 5, 5)),
            keyAreaScrollPane.getBorder()));

        // Put everything together.
        JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.add(textControlsPane, BorderLayout.PAGE_START);
        leftPane.add(areaScrollPane, BorderLayout.CENTER);
        leftPane.add(keyAreaScrollPane, BorderLayout.SOUTH);

        add(leftPane, BorderLayout.LINE_START);

        JPanel bottomPane = new JPanel(new BorderLayout());

        add(bottomPane, BorderLayout.LINE_END);

    }

    private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, GridBagLayout gridbag,
            Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        int numLabels = labels.length;

        for (int i = 0; i < numLabels; i++) {
            c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
            c.fill = GridBagConstraints.NONE; // reset to default
            c.weightx = 0.0; // reset to default
            container.add(labels[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER; // end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            container.add(textFields[i], c);
        }
    }

    public void actionPerformed(ActionEvent e) {
        String prefix = "You typed \"";

        if (passwordFieldString.equals(e.getActionCommand())) {
            JPasswordField source = (JPasswordField) e.getSource();
            firstPass = new String(source.getPassword());

            actionLabel.setText(prefix + firstPass + "\"");
            textArea.setText(prefix + firstPass + "\"");
        } else if (verifypassword.equals(e.getActionCommand())) {
            JPasswordField source = (JPasswordField) e.getSource();
            verifyPass = new String(source.getPassword());

            if (firstPass.equals(verifyPass)) {
                actionLabel.setText("Passwords match!");
                textArea.setText(prefix + verifyPass + "\"");
            } else {
                actionLabel.setText("Passwords do not match!" + "\"");
                textArea.setText(" \"");
            }

        } else if (buttonString.equals(e.getActionCommand())) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path, String description) {
        java.net.URL imgURL = PasswordEncrypter.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    /**
     * 
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("3DES Password Encrypter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.add(new PasswordEncrypter());

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        // Schedule a job for the event dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                createAndShowGUI();
            }
        });
    }
}
