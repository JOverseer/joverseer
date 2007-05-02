// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AboutDlg.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.Main;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class AboutDlg extends JDialog
{

    AboutDlg(JFrame frame)
    {
        super(frame, "OrderChecker", true);
        pane = new JPanel();
        meLabel = new JLabel();
        aboutPane = new JEditorPane();
        okButton = new JButton();
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
        Dimension prefer = getPreferredSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - prefer.width) / 2, (screenSize.height - prefer.height) / 2);
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
        setVisible(true);
    }

    private void layoutContents()
    {
        meLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        meLabel.setIcon(meIcon);
        meLabel.setHorizontalAlignment(0);
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
        pane.setPreferredSize(new Dimension(500, 400));
        pane.add(meLabel, "North");
        pane.add(textPane, "Center");
        pane.add(buttonPane, "South");
    }

    private void populateText()
    {
        StringBuffer text = new StringBuffer();
        text.append("<center><b>" + Main.getVersionString() + "</b>");
        text.append(" (" + Main.getVersionDate() + ")<p>");
        text.append("&#169; <i>2004-2005 Bernie Gaider</i></center><p>");
        text.append("Thanks to my fellow Free in game 235 of Middle Earth 2950 who helped to get the program off the ground.  ");
        text.append("Also to the fine folks at Middle Earth Games, especially Edward and Clint, who continue to provide support and assistance.<p>");
        text.append("This program is freeware.  Please send any feedback or bug reports to <u>bgaider@adelphia.net</u>.");
        aboutPane.setText(text.toString());
        aboutPane.setCaretPosition(0);
    }

    private void okActionPerformed()
    {
        dispose();
    }

    private static final ImageIcon meIcon = new ImageIcon("images/melogoC.jpg");
    private JPanel pane;
    private JLabel meLabel;
    private JEditorPane aboutPane;
    private JButton okButton;



}
