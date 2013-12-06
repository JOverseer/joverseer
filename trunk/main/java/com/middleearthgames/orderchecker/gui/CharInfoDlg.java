// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CharInfoDlg.java

package com.middleearthgames.orderchecker.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;

import com.middleearthgames.orderchecker.Character;
import com.middleearthgames.orderchecker.Nation;
import com.middleearthgames.orderchecker.io.Data;

// Referenced classes of package com.middleearthgames.orderchecker.gui:
//            OCTreeNode

class CharInfoDlg extends JDialog
{

    CharInfoDlg(JFrame frame, Nation nation, JTree tree, Data data)
    {
        super(frame, "Character Notes", true);
        this.pane = new JPanel(new BorderLayout());
        this.okButton = new JButton();
        this.cancelButton = new JButton();
        this.textIndex = 0;
        this.nation = nation;
        this.tree = tree;
        this.data = data;
        JLabel label = new JLabel();
        this.normalFont = label.getFont();
        this.boldFont = new Font(this.normalFont.getName(), 1, this.normalFont.getSize());
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
        getContentPane().add(this.pane);
        pack();
        getRootPane().setDefaultButton(this.okButton);
        addKeyListener(new KeyAdapter() {

            @Override
			public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    CharInfoDlg.this.okButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent we)
            {
                CharInfoDlg.this.cancelButton.doClick();
            }

        }
);
        java.awt.Dimension size = data.getWindowSize(3);
        setSize(size);
        setLocation(data.getWindowLocation(3));
        setVisible(true);
    }

    private void layoutContents()
    {
        JPanel mainPane = new JPanel();
        mainPane.setBorder(new EmptyBorder(10, 20, 0, 20));
        GridBagLayout layoutManager = new GridBagLayout();
        mainPane.setLayout(layoutManager);
        GridBagConstraints layoutConstraint = new GridBagConstraints();
        layoutConstraint.gridwidth = 0;
        layoutConstraint.anchor = 17;
        layoutConstraint.weighty = 0.0D;
        GridBagConstraints textConstraint = new GridBagConstraints();
        textConstraint.gridwidth = 0;
        textConstraint.anchor = 18;
        textConstraint.fill = 1;
        textConstraint.weighty = 1.0D;
        textConstraint.weightx = 1.0D;
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
        this.textCharacters = new String[root.getChildCount()];
        this.textNotes = new JTextArea[root.getChildCount()];
        for(Enumeration characters = root.children(); characters.hasMoreElements();)
        {
            OCTreeNode node = (OCTreeNode)characters.nextElement();
            String charDesc = node.toString();
            int index = node.toString().indexOf('\n');
            String content[];
            if(index >= 0)
            {
                content = new String[2];
                content[0] = charDesc.substring(0, index);
                content[1] = charDesc.substring(index + 1, charDesc.length());
            } else
            {
                content = new String[1];
                content[0] = charDesc;
            }
            for(int i = 0; i < content.length; i++)
            {
                JLabel characterInfo = new JLabel();
                characterInfo.setFont(this.boldFont);
                characterInfo.setText(content[i]);
                layoutConstraint.insets = new Insets(i != 0 ? 0 : 10, 0, 0, 0);
                layoutManager.setConstraints(characterInfo, layoutConstraint);
                mainPane.add(characterInfo);
            }

            for(Enumeration orders = node.children(); orders.hasMoreElements();)
            {
                OCTreeNode order = (OCTreeNode)orders.nextElement();
                JLabel orderInfo = new JLabel();
                orderInfo.setFont(this.normalFont);
                orderInfo.setText(order.toString());
                layoutConstraint.insets = new Insets(0, 0, 0, 0);
                layoutManager.setConstraints(orderInfo, layoutConstraint);
                mainPane.add(orderInfo);
                Enumeration results = order.children();
                while(results.hasMoreElements()) 
                {
                    OCTreeNode result = (OCTreeNode)results.nextElement();
                    if(result.toString().length() > 4)
                    {
                        JLabel resultInfo = new JLabel();
                        resultInfo.setFont(this.normalFont);
                        resultInfo.setText(result.toString());
                        layoutConstraint.insets = new Insets(0, 20, 0, 0);
                        layoutManager.setConstraints(resultInfo, layoutConstraint);
                        mainPane.add(resultInfo);
                    }
                }
            }

            Character character = (Character)node.getUserObject();
            String info = character.getEndOfTurnInfo();
            if(info != null)
            {
                JLabel endInfo = new JLabel();
                endInfo.setFont(this.normalFont);
                endInfo.setText(info);
                layoutConstraint.insets = new Insets(0, 0, 0, 0);
                layoutManager.setConstraints(endInfo, layoutConstraint);
                mainPane.add(endInfo);
            }
            String notes = this.data.getCharacterNotes(this.nation, character.getName());
            this.textCharacters[this.textIndex] = character.getName();
            JTextArea charNotes = new JTextArea();
            charNotes.setEditable(true);
            charNotes.setLineWrap(true);
            charNotes.setWrapStyleWord(true);
            charNotes.setRows(4);
            charNotes.setMargin(new Insets(0, 5, 0, 5));
            charNotes.setText(notes);
            this.textNotes[this.textIndex] = charNotes;
            JScrollPane textPane = new JScrollPane(charNotes);
            textPane.setBorder(new EtchedBorder(1));
            textConstraint.insets = new Insets(10, 0, 10, 0);
            layoutManager.setConstraints(textPane, textConstraint);
            mainPane.add(textPane);
            this.textIndex++;
        }

        JPanel buttonPane = new JPanel();
        buttonPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        this.okButton.setText("OK");
        buttonPane.add(this.okButton);
        this.cancelButton.setText("Cancel");
        buttonPane.add(this.cancelButton);
        java.awt.Dimension cancelSize = this.cancelButton.getPreferredSize();
        this.okButton.setPreferredSize(cancelSize);
        JScrollPane mainScrollPane = new JScrollPane(mainPane);
        this.pane.add(mainScrollPane, "Center");
        this.pane.add(buttonPane, "South");
    }

    private void saveWindowState()
    {
        this.data.setWindowSize(3, getSize());
        this.data.setWindowLocation(3, getLocationOnScreen());
    }

    private void okActionPerformed()
    {
        for(int i = 0; i < this.textIndex; i++)
            this.data.setCharacterNotes(this.nation, this.textCharacters[i], this.textNotes[i].getText());

        saveWindowState();
        dispose();
    }

    private void cancelActionPerformed()
    {
        saveWindowState();
        dispose();
    }

    private static final int DEFAULT_ROWS = 4;
    private JPanel pane;
    private JButton okButton;
    private JButton cancelButton;
    private String textCharacters[];
    private JTextArea textNotes[];
    private int textIndex;
    private final Font normalFont;
    private final Font boldFont;
    private final Nation nation;
    private final JTree tree;
    private final Data data;




}
