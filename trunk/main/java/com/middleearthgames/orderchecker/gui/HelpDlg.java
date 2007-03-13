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
        pane = new JPanel();
        aboutPane = new JEditorPane();
        okButton = new JButton();
        this.data = data;
        layoutContents();
        populateText();
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                okActionPerformed();
            }

        }
);
        getContentPane().add(pane);
        pack();
        getRootPane().setDefaultButton(okButton);
        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    okButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we)
            {
                okButton.doClick();
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
        aboutPane.setContentType("text/html");
        aboutPane.setEditable(false);
        JScrollPane textPane = new JScrollPane();
        textPane.setBorder(null);
        textPane.getViewport().add(aboutPane, null);
        okButton.setPreferredSize(new Dimension(73, 27));
        okButton.setText("OK");
        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Color.white);
        buttonPane.add(okButton, null);
        pane.setLayout(new BorderLayout());
        pane.setBackground(Color.white);
        pane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pane.add(textPane, "Center");
        pane.add(buttonPane, "South");
    }

    private void populateText()
    {
        StringBuffer text = new StringBuffer();
        try
        {
            File inputFile = new File("help.htm");
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
        aboutPane.setText(text.toString());
        aboutPane.setCaretPosition(0);
    }

    private void okActionPerformed()
    {
        data.setWindowSize(2, getSize());
        data.setWindowLocation(2, getLocationOnScreen());
        dispose();
    }

    private static final String helpFile = "help.htm";
    private JPanel pane;
    private JEditorPane aboutPane;
    private JButton okButton;
    private final Data data;


}
