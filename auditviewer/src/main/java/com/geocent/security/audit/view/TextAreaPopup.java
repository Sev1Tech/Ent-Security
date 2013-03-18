package com.geocent.security.audit.view;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author bpriest
 */
public class TextAreaPopup extends JDialog {

    private JTextArea textArea;
    private JOptionPane optionPane;

    public TextAreaPopup(String info) {
        super();

        textArea = new JTextArea();
        textArea.setText(info);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setSize(800, 400);
        optionPane = new JOptionPane(textArea);

        setContentPane(optionPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        pack();
    }
}
