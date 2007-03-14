// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OCDialog.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.*;
import com.middleearthgames.orderchecker.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Enumeration;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.tree.*;

// Referenced classes of package com.middleearthgames.orderchecker.gui:
//            ExampleFileFilter, PrefixFileFilter, CharInfoDlg, ResultsDlg, 
//            GameDeleteDlg, HelpDlg, AboutDlg, OCTreeNode

public class OCDialog extends JPanel
{
    
    public OCDialog(JPanel panel, Data data) {
        frame = null;
        splitPane = null;
        tree = new JTree(new DefaultTreeModel(root));
        bottomPane = new JScrollPane(tree);
        this.panel = panel;
        this.data = data;
        EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
        BevelBorder bb = new BevelBorder(1);
        CompoundBorder cb = new CompoundBorder(eb, bb);
        setBorder(new CompoundBorder(cb, eb));
        configureBottomPane();
        setLayout(new BorderLayout());
        add("Center", bottomPane);
    }

    public OCDialog(JFrame frame, Data data)
    {
        tree = new JTree(new DefaultTreeModel(root));
        bottomPane = new JScrollPane(tree);
        this.frame = frame;
        this.data = data;
        EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
        BevelBorder bb = new BevelBorder(1);
        CompoundBorder cb = new CompoundBorder(eb, bb);
        setBorder(new CompoundBorder(cb, eb));
        configureMenu();
        configureTopPane();
        configureBottomPane();
        splitPane = new JSplitPane(0, topPane, bottomPane);
        splitPane.setContinuousLayout(true);
        setLayout(new BorderLayout());
        add("Center", splitPane);
    }

    private void configureMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        notesFile = new JMenuItem("Character Notes", 78);
        notesFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                new CharInfoDlg(frame, Main.main.getNation(), tree, data);
            }

        }
);
        fileMenu.add(notesFile);
        exportFile = new JMenuItem("Export", 69);
        exportFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                new ResultsDlg(frame, Main.main.getNation(), tree, data);
            }

        }
);
        fileMenu.add(exportFile);
        fileMenu.addSeparator();
        JMenuItem deleteMenu = new JMenuItem("Delete Game Info", 68);
        deleteMenu.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                new GameDeleteDlg(frame, data);
            }

        }
);
        fileMenu.add(deleteMenu);
        fileMenu.addMenuListener(new MenuListener() {

            public void menuSelected(MenuEvent e)
            {
                exportFile.setEnabled(Main.main.getNation() != null);
                notesFile.setEnabled(Main.main.getNation() != null);
            }

            public void menuCanceled(MenuEvent menuevent)
            {
            }

            public void menuDeselected(MenuEvent menuevent)
            {
            }

        }
);
        menuBar.add(fileMenu);
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        JMenuItem helpHelp = new JMenuItem("Instructions", 73);
        helpHelp.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                new HelpDlg(frame, data);
            }

        }
);
        helpMenu.add(helpHelp);
        helpMenu.addSeparator();
        JMenuItem helpAbout = new JMenuItem("About", 65);
        helpAbout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                new AboutDlg(frame);
            }

        }
);
        helpMenu.add(helpAbout);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);
    }

    private void configureTopPane()
    {
        turnLabel.setText("Turn Result");
        ordersLabel.setText("MEOW/Automagic Orders");
        gameTypeLabel.setText("Game Type");
        turnPath.setText(data.getTurnResultsPath());
        ordersPath.setText(data.getOrdersPath());
        gameTypes = new JComboBox(data.getGameDescriptions());
        gameTypes.setSelectedItem(data.getGameDescription());
        turnButton.setText("Browse...");
        ordersButton.setText("Browse...");
        goButton.setText("Perform Order Checking");
        turnButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                turnActionPerformed();
            }

        }
);
        ordersButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                ordersActionPerformed();
            }

        }
);
        goButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                goActionPerformed();
            }

        }
);
        topPane.setLayout(layoutManager);
        LayoutTopPane();
    }

    private void LayoutTopPane()
    {
        Insets topSpacing = new Insets(10, 10, 0, 10);
        Insets bothSpacing = new Insets(10, 10, 10, 10);
        Insets noSpacing = new Insets(0, 10, 0, 10);
        addLabel(topSpacing, turnLabel);
        addTextField(noSpacing, turnPath);
        addButton(noSpacing, turnButton);
        addIcon(noSpacing, turnResult);
        addLabel(topSpacing, ordersLabel);
        addTextField(noSpacing, ordersPath);
        addButton(noSpacing, ordersButton);
        addIcon(noSpacing, ordersResult);
        JPanel gameTypePane = new JPanel();
        gameTypePane.add(gameTypeLabel);
        gameTypePane.add(gameTypes);
        addPane(topSpacing, gameTypePane);
        constraint.gridwidth = 0;
        constraint.weightx = 0.0D;
        constraint.weighty = 1.0D;
        constraint.fill = 2;
        constraint.insets = bothSpacing;
        constraint.anchor = 11;
        layoutManager.setConstraints(goButton, constraint);
        topPane.add(goButton);
    }

    private void addLabel(Insets insets, JLabel label)
    {
        constraint.anchor = 18;
        constraint.gridwidth = 0;
        constraint.weightx = 0.0D;
        constraint.fill = 0;
        constraint.insets = insets;
        layoutManager.setConstraints(label, constraint);
        topPane.add(label);
    }

    private void addTextField(Insets insets, JTextField field)
    {
        constraint.anchor = 10;
        constraint.gridwidth = 1;
        constraint.weightx = 1.0D;
        constraint.fill = 2;
        constraint.insets = insets;
        layoutManager.setConstraints(field, constraint);
        topPane.add(field);
    }

    private void addPane(Insets insets, JPanel field)
    {
        constraint.anchor = 17;
        constraint.gridwidth = 0;
        constraint.weightx = 1.0D;
        constraint.fill = 0;
        constraint.insets = insets;
        layoutManager.setConstraints(field, constraint);
        topPane.add(field);
    }

    private void addButton(Insets insets, JButton button)
    {
        constraint.anchor = 10;
        constraint.gridwidth = 1;
        constraint.weightx = 0.0D;
        constraint.fill = 0;
        constraint.insets = insets;
        layoutManager.setConstraints(button, constraint);
        topPane.add(button);
    }

    private void addIcon(Insets insets, JLabel label)
    {
        constraint.anchor = 10;
        constraint.gridwidth = 0;
        constraint.weightx = 0.0D;
        constraint.fill = 0;
        constraint.insets = insets;
        layoutManager.setConstraints(label, constraint);
        topPane.add(label);
    }

    private void configureBottomPane()
    {
        tree.setEditable(false);
        tree.setExpandsSelectedPaths(true);
        tree.setRootVisible(false);
        class _cls1ResultTreeRenderer extends DefaultTreeCellRenderer
        {

            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
            {
                try {
                    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                    if(value != root)
                    {
                        OCTreeNode node = (OCTreeNode)value;
                        setFont(node.getActiveFont());
                        if(node.getNodeType() == 2)
                        {
                            String currentText = getText();
                            if(currentText.length() > 3)
                            {
                                currentText = currentText.substring(4);
                                setText(currentText);
                            }
                        }
                        ImageIcon icon = node.getIcon();
                        if(icon != null)
                            setIcon(node.getIcon());
                    }
                    return this;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    return this;
                }
            }

            public _cls1ResultTreeRenderer()
            {
            }
        }

        tree.setCellRenderer(new _cls1ResultTreeRenderer());
    }

    public void createResultsTree()
    {
        root.removeAllChildren();
        Main.main.getNation().addTreeNodes(tree, root);
        ((DefaultTreeModel)tree.getModel()).nodeStructureChanged(root);
        javax.swing.tree.TreeNode path[];
        for(Enumeration allNodes = root.depthFirstEnumeration(); allNodes.hasMoreElements(); tree.expandPath(new TreePath(path)))
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)allNodes.nextElement();
            path = node.getPath();
        }

    }

    private void turnActionPerformed()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Turn Result");
        chooser.setMultiSelectionEnabled(false);
        String filename = turnPath.getText();
        if(filename.length() > 0)
        {
            File f = new File(filename);
            if(f.exists())
                chooser.setSelectedFile(new File(filename));
            else
                chooser.setCurrentDirectory(new File(f.getParent()));
        } else
        if(ordersPath.getText().length() > 0)
        {
            File f = new File(ordersPath.getText());
            chooser.setCurrentDirectory(new File(f.getParent()));
        }
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("xml");
        filter.setDescription("XML Files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == 0)
        {
            File selFile = chooser.getSelectedFile();
            turnPath.setText(selFile.getPath());
        }
    }

    private void ordersActionPerformed()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Orders File");
        chooser.setMultiSelectionEnabled(false);
        String filename = ordersPath.getText();
        if(filename.length() > 0)
        {
            File f = new File(filename);
            if(f.exists())
                chooser.setSelectedFile(new File(filename));
            else
                chooser.setCurrentDirectory(new File(f.getParent()));
        } else
        if(turnPath.getText().length() > 0)
        {
            File f = new File(turnPath.getText());
            chooser.setCurrentDirectory(new File(f.getParent()));
        }
        PrefixFileFilter filter = new PrefixFileFilter();
        filter.addPrefix("me");
        filter.setDescription("Middle Earth Order Files");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(this);
        if(returnVal == 0)
        {
            File selFile = chooser.getSelectedFile();
            ordersPath.setText(selFile.getPath());
        }
    }

    public void goActionPerformed()
    {
        if(turnPath.getText().length() == 0)
        {
            processError(turnResult, "You need to specify a turn result!");
            return;
        }
        if(ordersPath.getText().length() == 0)
        {
            processError(ordersResult, "You need to specify an orders file!");
            return;
        }
        ImportTurnXml turnData = new ImportTurnXml(turnPath.getText());
        boolean result = turnData.getTurnData();
        if(!result)
        {
            String msg = "Your turn result file could not be processed!\nPlease make sure you selected the correct Middle Earth XML \nturn result.";
            processError(turnResult, msg);
            return;
        }
        Main.main.setNation(turnData.parseTurnData());
        result = Main.main.getNation().isNationComplete();
        if(!result)
        {
            String msg = "Your turn result was processed but not all of the\nexpected  information was present!";
            processError(turnResult, msg);
            return;
        }
        turnResult.setIcon(goIcon);
        data.setTurnResultsPath(turnPath.getText());
        ImportOrdersCsv orders = new ImportOrdersCsv(ordersPath.getText(), Main.main.getNation());
        result = orders.getOrders();
        if(!result)
        {
            orders.closeFile();
            processError(ordersResult, "Your orders file could not be opened!");
            return;
        }
        String error = orders.parseOrders();
        orders.closeFile();
        if(error != null)
        {
            error = error + "\n\nOrder checking cancelled.";
            processError(ordersResult, error);
            return;
        }
        ordersResult.setIcon(goIcon);
        data.setOrdersPath(ordersPath.getText());
        data.setGameType((String)gameTypes.getSelectedItem());
        Main.main.setRuleSet(new Ruleset());
        ImportRulesCsv rules = new ImportRulesCsv(data.getRulesPath(), Main.main.getRuleSet());
        result = rules.getRules();
        if(!result)
        {
            rules.closeFile();
            Main.displayErrorMessage("The rules file (" + data.getRulesPath() + ") could not be opened!");
            return;
        }
        error = rules.parseRules();
        rules.closeFile();
        if(error != null)
        {
            error = error + "\n\nOrder checking cancelled.";
            Main.displayErrorMessage(error);
            return;
        }
        if(!Main.main.getRuleSet().isRuleSetComplete())
        {
            Main.displayErrorMessage("The rules file was processed but appears to be missing data!");
            return;
        }
        Main.main.setMap(new Map());
        ImportTerrainCsv terrain = new ImportTerrainCsv(data.getTerrainPath(), Main.main.getMap());
        result = terrain.getMapInformation();
        if(!result)
        {
            terrain.closeFile();
            Main.displayErrorMessage("The terrain file (" + data.getTerrainPath() + ") could not be opened!");
            return;
        }
        error = terrain.parseTerrain();
        terrain.closeFile();
        if(error != null)
        {
            error = error + "\n\nOrder checking cancelled.";
            Main.displayErrorMessage(error);
            return;
        }
        if(!Main.main.getMap().isMapComplete())
        {
            Main.displayErrorMessage("The map file was processed but appears to be missing data!");
            return;
        } else
        {
            splitPane.resetToPreferredSizes();
            Main.main.processOrders();
            return;
        }
    }

    private void processError(JLabel result, String msg)
    {
        result.setIcon(stopIcon);
        //splitPane.resetToPreferredSizes();
        Main.displayErrorMessage(msg);
    }
    
    

    private static final ImageIcon goIcon = new ImageIcon("images/button06_yes.gif");
    private static final ImageIcon stopIcon = new ImageIcon("images/button06_no.gif");
    private final JSplitPane splitPane;
    private final JPanel topPane = new JPanel();
    private final GridBagLayout layoutManager = new GridBagLayout();
    private final GridBagConstraints constraint = new GridBagConstraints();
    private final JLabel turnLabel = new JLabel();
    private JTextField turnPath = new JTextField();
    private final JButton turnButton = new JButton();
    private final JLabel turnResult = new JLabel();
    private final JLabel ordersLabel = new JLabel();
    private JTextField ordersPath = new JTextField();
    private final JButton ordersButton = new JButton();
    private final JLabel ordersResult = new JLabel();
    private final JLabel gameTypeLabel = new JLabel();
    private JComboBox gameTypes;
    private final JButton goButton = new JButton();
    private JMenuItem exportFile;
    private JMenuItem notesFile;
    private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();
    private final JTree tree;
    private final JScrollPane bottomPane;
    private JFrame frame;
    private final Data data;

    private JPanel panel;

    
    public JPanel getPanel() {
        return panel;
    }

    
    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    
    public JTextField getOrdersPath() {
        return ordersPath;
    }

    
    public void setOrdersPath(JTextField ordersPath) {
        this.ordersPath = ordersPath;
    }

    
    public JTextField getTurnPath() {
        return turnPath;
    }

    
    public void setTurnPath(JTextField turnPath) {
        this.turnPath = turnPath;
    }
    
    


    
    public JFrame getFrame() {
        return frame;
    }

    
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void goActionPerformedMscoon()
    {
        boolean result;
        if(ordersPath.getText().length() == 0)
        {
            processError(ordersResult, "You need to specify an orders file!");
            return;
        }
        result = Main.main.getNation().isNationComplete();
        if(!result)
        {
            String msg = "Your turn result was processed but not all of the\nexpected  information was present!";
            processError(turnResult, msg);
            return;
        }
        turnResult.setIcon(goIcon);
        //data.setTurnResultsPath(turnPath.getText());
        String error;
//        ImportOrdersCsv orders = new ImportOrdersCsv(ordersPath.getText(), Main.main.getNation());
//        result = orders.getOrders();
//        if(!result)
//        {
//            orders.closeFile();
//            processError(ordersResult, "Your orders file could not be opened!");
//            return;
//        }
//        error = orders.parseOrders();
//        orders.closeFile();
//        if(error != null)
//        {
//            error = error + "\n\nOrder checking cancelled.";
//            processError(ordersResult, error);
//            return;
//        }
//        ordersResult.setIcon(goIcon);
//        data.setOrdersPath(ordersPath.getText());
        //data.setGameType((String)gameTypes.getSelectedItem());
        
        Main.main.setRuleSet(new Ruleset());
        ImportRulesCsv rules = new ImportRulesCsv(data.getRulesPath(), Main.main.getRuleSet());
        result = rules.getRules();
        if(!result)
        {
            rules.closeFile();
            Main.displayErrorMessage("The rules file (" + data.getRulesPath() + ") could not be opened!");
            return;
        }
        error = rules.parseRules();
        rules.closeFile();
        if(error != null)
        {
            error = error + "\n\nOrder checking cancelled.";
            Main.displayErrorMessage(error);
            return;
        }
        if(!Main.main.getRuleSet().isRuleSetComplete())
        {
            Main.displayErrorMessage("The rules file was processed but appears to be missing data!");
            return;
        }
        Main.main.setMap(new Map());
        ImportTerrainCsv terrain = new ImportTerrainCsv(data.getTerrainPath(), Main.main.getMap());
        result = terrain.getMapInformation();
        if(!result)
        {
            terrain.closeFile();
            Main.displayErrorMessage("The terrain file (" + data.getTerrainPath() + ") could not be opened!");
            return;
        }
        error = terrain.parseTerrain();
        terrain.closeFile();
        if(error != null)
        {
            error = error + "\n\nOrder checking cancelled.";
            Main.displayErrorMessage(error);
            return;
        }
        if(!Main.main.getMap().isMapComplete())
        {
            Main.displayErrorMessage("The map file was processed but appears to be missing data!");
            return;
        } else
        {
            //splitPane.resetToPreferredSizes();
            Main.main.processOrders();
            return;
        }
    }







}
