// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ResultsDlg.java

package com.middleearthgames.orderchecker.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.middleearthgames.orderchecker.Character;
import com.middleearthgames.orderchecker.Main;
import com.middleearthgames.orderchecker.Nation;
import com.middleearthgames.orderchecker.io.Data;

// Referenced classes of package com.middleearthgames.orderchecker.gui:
//            OCTreeNode

class ResultsDlg extends JDialog
{

    ResultsDlg(JFrame frame, Nation nation, JTree tree, Data data)
    {
        super(frame, "Order Checking Results", true);
        this.pane = new JPanel();
        this.aboutPane = new JTextArea();
        this.okButton = new JButton();
        this.nation = nation;
        this.tree = tree;
        this.data = data;
        addMenu();
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
                    ResultsDlg.this.okButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent we)
            {
                ResultsDlg.this.okButton.doClick();
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
        this.cut = new JMenuItem(new javax.swing.text.DefaultEditorKit.CutAction());
        this.cut.setText("Cut");
        this.cut.setMnemonic('U');
        this.cut.setAccelerator(KeyStroke.getKeyStroke(88, 2));
        mainMenu.add(this.cut);
        this.copy = new JMenuItem(new javax.swing.text.DefaultEditorKit.CopyAction());
        this.copy.setText("Copy");
        this.copy.setMnemonic('C');
        this.copy.setAccelerator(KeyStroke.getKeyStroke(67, 2));
        mainMenu.add(this.copy);
        this.paste = new JMenuItem(new javax.swing.text.DefaultEditorKit.PasteAction());
        this.paste.setText("Paste");
        this.paste.setMnemonic('P');
        this.paste.setAccelerator(KeyStroke.getKeyStroke(86, 2));
        mainMenu.add(this.paste);
        mainMenu.addSeparator();
        JMenuItem menuItem = new JMenuItem("Select All", 65);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(65, 2));
        menuItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.aboutPane.selectAll();
            }

        }
);
        mainMenu.add(menuItem);
        mainMenu.addMenuListener(new MenuListener() {

            @Override
			public void menuSelected(MenuEvent e)
            {
                boolean selected = ResultsDlg.this.aboutPane.getSelectedText() != null;
                ResultsDlg.this.cut.setEnabled(selected);
                ResultsDlg.this.copy.setEnabled(selected);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                ResultsDlg.this.paste.setEnabled(clipboard.getContents(this) != null);
            }

            @Override
			public void menuCanceled(MenuEvent menuevent)
            {
            }

            @Override
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

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setExportFormat(0);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(this.data.getExportFormat() == 0)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        radioItem = new JRadioButtonMenuItem("HTML");
        radioItem.setMnemonic('H');
        radioItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setExportFormat(1);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(this.data.getExportFormat() == 1)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        radioItem = new JRadioButtonMenuItem("BBCode");
        radioItem.setMnemonic('B');
        radioItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setExportFormat(2);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(this.data.getExportFormat() == 2)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        formatMenu.addSeparator();
        JCheckBoxMenuItem boxItem = new JCheckBoxMenuItem("Show End Turn Info", this.data.getEndOfTurnInfo());
        boxItem.setMnemonic('E');
        boxItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setEndOfTurnInfo(!ResultsDlg.this.data.getEndOfTurnInfo());
                populateText();
            }

        }
);
        formatMenu.add(boxItem);
        boxItem = new JCheckBoxMenuItem("Show Character Notes", this.data.getShowCharNotes());
        boxItem.setMnemonic('C');
        boxItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setShowCharNotes(!ResultsDlg.this.data.getShowCharNotes());
                populateText();
            }

        }
);
        formatMenu.add(boxItem);
        boxItem = new JCheckBoxMenuItem("Show All Order Results", this.data.getShowAllInfo());
        boxItem.setMnemonic('R');
        boxItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setShowAllInfo(!ResultsDlg.this.data.getShowAllInfo());
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

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setSortFormat(0);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(this.data.getSortFormat() == 0)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        radioItem = new JRadioButtonMenuItem("Sort By Character Location");
        radioItem.setMnemonic('L');
        radioItem.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ResultsDlg.this.data.setSortFormat(1);
                populateText();
            }

        }
);
        group.add(radioItem);
        if(this.data.getSortFormat() == 1)
            group.setSelected(radioItem.getModel(), true);
        formatMenu.add(radioItem);
        menuBar.add(formatMenu);
        setJMenuBar(menuBar);
    }

    private void layoutContents()
    {
        this.aboutPane.setEditable(true);
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
        int format = this.data.getExportFormat();
        text.append(this.nation.getGameType() + this.lineBreak[format] + "\n");
        text.append("Game: " + this.nation.getGame() + ", Turn: " + this.nation.getTurn() + this.lineBreak[format] + "\n");
        text.append("Due Date: " + this.nation.getDueDate() + this.lineBreak[format] + "\n");
        text.append("Player: " + this.nation.getNation() + ": " + this.nation.getPlayer() + this.lineBreak[format] + "\n");
        text.append(Main.getVersionString() + " (" + Main.getVersionDate() + ")" + this.paragraphBreak[format] + "\n");
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)this.tree.getModel().getRoot();
        if(this.data.getSortFormat() == 0)
        {
            OCTreeNode node;
            for(Enumeration characters = root.children(); characters.hasMoreElements(); populateCharacterInfo(text, format, node))
                node = (OCTreeNode)characters.nextElement();

        } else
        if(this.data.getSortFormat() == 1)
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
            throw new RuntimeException("Invalid sort format (" + this.data.getSortFormat() + " used!");
        }
        this.aboutPane.setText(text.toString());
        this.aboutPane.setCaretPosition(0);
    }

    private void populateCharacterInfo(StringBuffer text, int format, OCTreeNode node)
    {
        text.append("\n" + this.paragraphBreak[format] + this.boldBegin[format] + node.toString() + this.boldEnd[format] + this.lineBreak[format] + "\n");
        for(Enumeration orders = node.children(); orders.hasMoreElements();)
        {
            OCTreeNode order = (OCTreeNode)orders.nextElement();
            text.append(order.toString() + this.lineBreak[format] + "\n");
            Enumeration results = order.children();
            while(results.hasMoreElements()) 
            {
                OCTreeNode result = (OCTreeNode)results.nextElement();
                String resultString = result.getResultString(this.data.getShowAllInfo());
                if(resultString.length() > 0)
                    text.append("\t" + resultString + this.lineBreak[format] + "\n");
            }
        }

        Character character = (Character)node.getUserObject();
        if(this.data.getEndOfTurnInfo())
        {
            String info = character.getEndOfTurnInfo();
            if(info != null)
                text.append(this.paragraphBreak[format] + info + "\n");
        }
        String notes = this.data.getCharacterNotes(this.nation, character.getName());
        if(notes.length() > 0 && this.data.getShowCharNotes())
            text.append(this.paragraphBreak[format] + this.italicBegin[format] + notes + this.italicEnd[format] + "\n");
    }

    private void okActionPerformed()
    {
        this.data.setWindowSize(1, getSize());
        this.data.setWindowLocation(1, getLocationOnScreen());
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
