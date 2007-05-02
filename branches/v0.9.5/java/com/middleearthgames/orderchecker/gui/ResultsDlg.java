// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ResultsDlg.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.*;
import com.middleearthgames.orderchecker.Character;
import com.middleearthgames.orderchecker.io.Data;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

// Referenced classes of package com.middleearthgames.orderchecker.gui:
//            OCTreeNode

class ResultsDlg extends JDialog
{

    ResultsDlg(JFrame frame, Nation nation, JTree tree, Data data)
    {
        super(frame, "Order Checking Results", true);
        pane = new JPanel();
        aboutPane = new JTextArea();
        okButton = new JButton();
        this.nation = nation;
        this.tree = tree;
        this.data = data;
        addMenu();
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
        Dimension size = data.getWindowSize(1);
        setSize(size);
        setLocation(data.getWindowLocation(1));
        setVisible(true);
    }

    private void addMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu mainMenu = new JMenu("Edit");
        mainMenu.setMnemonic('E');
        cut = new JMenuItem(new javax.swing.text.DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setMnemonic('U');
        cut.setAccelerator(KeyStroke.getKeyStroke(88, 2));
        mainMenu.add(cut);
        copy = new JMenuItem(new javax.swing.text.DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setMnemonic('C');
        copy.setAccelerator(KeyStroke.getKeyStroke(67, 2));
        mainMenu.add(copy);
        paste = new JMenuItem(new javax.swing.text.DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setMnemonic('P');
        paste.setAccelerator(KeyStroke.getKeyStroke(86, 2));
        mainMenu.add(paste);
        mainMenu.addSeparator();
        JMenuItem menuItem = new JMenuItem("Select All", 65);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(65, 2));
        menuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                aboutPane.selectAll();
            }

        }
);
        mainMenu.add(menuItem);
        mainMenu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e)
            {
                boolean selected = aboutPane.getSelectedText() != null;
                cut.setEnabled(selected);
                copy.setEnabled(selected);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                paste.setEnabled(clipboard.getContents(this) != null);
            }

            public void menuCanceled(MenuEvent menuevent)
            {
            }

            public void menuDeselected(MenuEvent menuevent)
            {
            }

        }
);
        menuBar.add(mainMenu);
        JMenu formatMenu = new JMenu("Format");
        mainMenu.setMnemonic(70);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem radioItem = new JRadioButtonMenuItem("Normal");
        radioItem.setMnemonic('N');
        radioItem.setSelected(true);
        radioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setExportFormat(0);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(data.getExportFormat() == 0)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        radioItem = new JRadioButtonMenuItem("HTML");
        radioItem.setMnemonic('H');
        radioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setExportFormat(1);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(data.getExportFormat() == 1)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        radioItem = new JRadioButtonMenuItem("BBCode");
        radioItem.setMnemonic('B');
        radioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setExportFormat(2);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(data.getExportFormat() == 2)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        formatMenu.addSeparator();
        JCheckBoxMenuItem boxItem = new JCheckBoxMenuItem("Show End Turn Info", data.getEndOfTurnInfo());
        boxItem.setMnemonic('E');
        boxItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setEndOfTurnInfo(!data.getEndOfTurnInfo());
                populateText();
            }

        }
);
        formatMenu.add(boxItem);
        boxItem = new JCheckBoxMenuItem("Show Character Notes", data.getShowCharNotes());
        boxItem.setMnemonic('C');
        boxItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setShowCharNotes(!data.getShowCharNotes());
                populateText();
            }

        }
);
        formatMenu.add(boxItem);
        boxItem = new JCheckBoxMenuItem("Show All Order Results", data.getShowAllInfo());
        boxItem.setMnemonic('R');
        boxItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setShowAllInfo(!data.getShowAllInfo());
                populateText();
            }

        }
);
        formatMenu.add(boxItem);
        formatMenu.addSeparator();
        group = new ButtonGroup();
        radioItem = new JRadioButtonMenuItem("Sort By Character Name");
        radioItem.setMnemonic('S');
        radioItem.setSelected(true);
        radioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setSortFormat(0);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(data.getSortFormat() == 0)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        radioItem = new JRadioButtonMenuItem("Sort By Character Location");
        radioItem.setMnemonic('L');
        radioItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                data.setSortFormat(1);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(data.getSortFormat() == 1)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        menuBar.add(formatMenu);
        setJMenuBar(menuBar);
    }

    private void layoutContents()
    {
        aboutPane.setEditable(true);
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
        int format = data.getExportFormat();
        text.append(nation.getGameType() + lineBreak[format] + "\n");
        text.append("Game: " + nation.getGame() + ", Turn: " + nation.getTurn() + lineBreak[format] + "\n");
        text.append("Due Date: " + nation.getDueDate() + lineBreak[format] + "\n");
        text.append("Player: " + nation.getNation() + ": " + nation.getPlayer() + lineBreak[format] + "\n");
        text.append(Main.getVersionString() + " (" + Main.getVersionDate() + ")" + paragraphBreak[format] + "\n");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        if(data.getSortFormat() == 0)
        {
            OCTreeNode node;
            for(Enumeration characters = root.children(); characters.hasMoreElements(); populateCharacterInfo(text, format, node))
                node = (OCTreeNode)characters.nextElement();

        } else
        if(data.getSortFormat() == 1)
        {
            Vector selectedList = new Vector();
            int lowest;
            do
            {
                lowest = 9999;
                OCTreeNode lastNode = null;
                Character lastCharacter = null;
                Enumeration characters = root.children();
                do
                {
                    if(!characters.hasMoreElements())
                        break;
                    OCTreeNode node = (OCTreeNode)characters.nextElement();
                    Character currentChar = (Character)node.getUserObject();
                    if(!selectedList.contains(currentChar))
                    {
                        int currentLocation = currentChar.getLocation(0);
                        if(currentLocation < lowest)
                        {
                            lowest = currentLocation;
                            lastNode = node;
                            lastCharacter = currentChar;
                        }
                    }
                } while(true);
                if(lastNode != null)
                {
                    populateCharacterInfo(text, format, lastNode);
                    selectedList.add(lastCharacter);
                }
            } while(lowest != 9999);
        } else
        {
            throw new RuntimeException("Invalid sort format (" + data.getSortFormat() + " used!");
        }
        aboutPane.setText(text.toString());
        aboutPane.setCaretPosition(0);
    }

    private void populateCharacterInfo(StringBuffer text, int format, OCTreeNode node)
    {
        text.append("\n" + paragraphBreak[format] + boldBegin[format] + node.toString() + boldEnd[format] + lineBreak[format] + "\n");
        for(Enumeration orders = node.children(); orders.hasMoreElements();)
        {
            OCTreeNode order = (OCTreeNode)orders.nextElement();
            text.append(order.toString() + lineBreak[format] + "\n");
            Enumeration results = order.children();
            while(results.hasMoreElements()) 
            {
                OCTreeNode result = (OCTreeNode)results.nextElement();
                String resultString = result.getResultString(data.getShowAllInfo());
                if(resultString.length() > 0)
                    text.append("\t" + resultString + lineBreak[format] + "\n");
            }
        }

        Character character = (Character)node.getUserObject();
        if(data.getEndOfTurnInfo())
        {
            String info = character.getEndOfTurnInfo();
            if(info != null)
                text.append(paragraphBreak[format] + info + "\n");
        }
        String notes = data.getCharacterNotes(nation, character.getName());
        if(notes.length() > 0 && data.getShowCharNotes())
            text.append(paragraphBreak[format] + italicBegin[format] + notes + italicEnd[format] + "\n");
    }

    private void okActionPerformed()
    {
        data.setWindowSize(1, getSize());
        data.setWindowLocation(1, getLocationOnScreen());
        dispose();
    }

    String lineBreak[] = {
        "", "<br>", ""
    };
    String paragraphBreak[] = {
        "", "<p>", ""
    };
    String boldBegin[] = {
        "", "<b>", "[b]"
    };
    String boldEnd[] = {
        "", "</b>", "[/b]"
    };
    String italicBegin[] = {
        "", "<i>", "[i]"
    };
    String italicEnd[] = {
        "", "</i>", "[/i]"
    };
    private JPanel pane;
    private JTextArea aboutPane;
    private JButton okButton;
    private JMenuItem cut;
    private JMenuItem copy;
    private JMenuItem paste;
    private final Nation nation;
    private final JTree tree;
    private final Data data;








}
