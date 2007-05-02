// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GameDeleteDlg.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.io.Data;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

class GameDeleteDlg extends JDialog
{

    GameDeleteDlg(JFrame frame, Data data)
    {
        super(frame, "Delete Game Info", true);
        this.data = data;
        layoutContents();
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                okActionPerformed();
            }

        }
);
        cancelButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                cancelActionPerformed();
            }

        }
);
        gameList.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane listPane = new JScrollPane(gameList);
        JPanel buttonPane = new JPanel();
        buttonPane.add(okButton);
        buttonPane.add(cancelButton);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(listPane, "Center");
        pane.add(buttonPane, "South");
        pack();
        getRootPane().setDefaultButton(cancelButton);
        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    cancelButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent we)
            {
                cancelButton.doClick();
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
        gameList = new JList(data.getGames());
        gameList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e)
            {
                setState();
            }

        }
);
        okButton.setText("Delete Game Info");
        okButton.setEnabled(false);
        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(okButton.getPreferredSize());
    }

    private void setState()
    {
        int index = gameList.getSelectedIndex();
        okButton.setEnabled(index != -1);
    }

    private void okActionPerformed()
    {
        Object selected[] = gameList.getSelectedValues();
        for(int i = 0; i < selected.length; i++)
            data.getGames().remove(selected[i]);

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
