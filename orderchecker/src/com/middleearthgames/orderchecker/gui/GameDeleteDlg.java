// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GameDeleteDlg.java

package com.middleearthgames.orderchecker.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.middleearthgames.orderchecker.io.Data;

class GameDeleteDlg extends JDialog
{

    GameDeleteDlg(JFrame frame, Data data)
    {
        super(frame, "Delete Game Info", true);
        this.data = data;
        layoutContents();
        this.okButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                okActionPerformed();
            }

        }
);
        this.cancelButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                cancelActionPerformed();
            }

        }
);
        this.gameList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane listPane = new JScrollPane(this.gameList);
        JPanel buttonPane = new JPanel();
        buttonPane.add(this.okButton);
        buttonPane.add(this.cancelButton);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(listPane, "Center");
        pane.add(buttonPane, "South");
        pack();
        getRootPane().setDefaultButton(this.cancelButton);
        addKeyListener(new KeyAdapter() {

            @Override
			public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    GameDeleteDlg.this.cancelButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent we)
            {
                GameDeleteDlg.this.cancelButton.doClick();
            }

        }
);
        java.awt.Dimension prefer = getPreferredSize();
        Data.checkScreenSize(prefer);
        setSize(prefer);
        setLocation(Data.getScreenLocation(prefer));
        setVisible(true);
    }

    private void layoutContents()
    {
        this.gameList = new JList(this.data.getGames());
        this.gameList.addListSelectionListener(new ListSelectionListener() {

            @Override
			public void valueChanged(ListSelectionEvent e)
            {
                setState();
            }

        }
);
        this.okButton.setText("Delete Game Info");
        this.okButton.setEnabled(false);
        this.cancelButton.setText("Cancel");
        this.cancelButton.setPreferredSize(this.okButton.getPreferredSize());
    }

    private void setState()
    {
        int index = this.gameList.getSelectedIndex();
        this.okButton.setEnabled(index != -1);
    }

    private void okActionPerformed()
    {
        Object selected[] = this.gameList.getSelectedValues();
        for(int i = 0; i < selected.length; i++)
            this.data.getGames().remove(selected[i]);

        dispose();
    }

    private void cancelActionPerformed()
    {
        dispose();
    }

    private JList gameList;
    private final JButton okButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final Data data;




}
