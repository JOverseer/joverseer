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

	private static final long serialVersionUID = 1L;
	AboutDlg(JFrame frame)
    {
        super(frame, "OrderChecker", true);
        this.pane = new JPanel();
        this.meLabel = new JLabel();
        this.aboutPane = new JEditorPane();
        this.okButton = new JButton();
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
        Dimension prefer = getPreferredSize();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width - prefer.width) / 2, (screenSize.height - prefer.height) / 2);
        getRootPane().setDefaultButton(this.okButton);
        addKeyListener(new KeyAdapter() {

            @Override
			public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    AboutDlg.this.okButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent we)
            {
                AboutDlg.this.okButton.doClick();
            }

        }
);
        setVisible(true);
    }

    private void layoutContents()
    {
        this.meLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        this.meLabel.setIcon(meIcon);
        this.meLabel.setHorizontalAlignment(0);
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
        this.pane.setPreferredSize(new Dimension(500, 400));
        this.pane.add(this.meLabel, "North");
        this.pane.add(textPane, "Center");
        this.pane.add(buttonPane, "South");
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
        this.aboutPane.setText(text.toString());
        this.aboutPane.setCaretPosition(0);
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
