// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HelpDlg.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.io.Data;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

class HelpDlg extends JDialog
{

    HelpDlg(JFrame frame, Data data)
    {
        super(frame, "Instructions", true);
        this.pane = new JPanel();
        this.aboutPane = new JEditorPane();
        this.okButton = new JButton();
        this.data = data;
        layoutContents();
        populateText();
        this.okButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                okActionPerformed();
            }

        }
);
        getContentPane().add(this.pane);
        pack();
        getRootPane().setDefaultButton(this.okButton);
        addKeyListener(new KeyAdapter() {

            @Override
			public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    HelpDlg.this.okButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent we)
            {
                HelpDlg.this.okButton.doClick();
            }

        }
);
        Dimension size = data.getWindowSize(2);
        setSize(size);
        setLocation(data.getWindowLocation(2));
        setVisible(true);
    }

    private void layoutContents()
    {
        this.aboutPane.setContentType("text/html");
        this.aboutPane.setEditable(false);
        JScrollPane textPane = new JScrollPane();
        textPane.setBorder(null);
        textPane.getViewport().add(this.aboutPane, null);
        this.okButton.setPreferredSize(new Dimension(73, 27));
        this.okButton.setText("OK");
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Color.white);
        buttonPane.add(this.okButton, null);
        this.pane.setLayout(new BorderLayout());
        this.pane.setBackground(Color.white);
        this.pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.pane.add(textPane, "Center");
        this.pane.add(buttonPane, "South");
    }

    private void populateText()
    {
        StringBuffer text = new StringBuffer();
        try
        {
            File inputFile = new File(helpFile);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            for(String line = reader.readLine(); line != null; line = reader.readLine())
                text.append(line);

            reader.close();
        }
        catch(Exception ex)
        {
            if(text.length() == 0)
                text.append("<p><b>ERROR</b> - a problem was encountered while trying to read the help file contents.  Please look for a Help.htm file in the folder where OrderChecker was installed.");
        }
        this.aboutPane.setText(text.toString());
        this.aboutPane.setCaretPosition(0);
    }

    private void okActionPerformed()
    {
        this.data.setWindowSize(2, getSize());
        this.data.setWindowLocation(2, getLocationOnScreen());
        dispose();
    }

    private static final String helpFile = "help.htm";
    private JPanel pane;
    private JEditorPane aboutPane;
    private JButton okButton;
    private final Data data;


}
