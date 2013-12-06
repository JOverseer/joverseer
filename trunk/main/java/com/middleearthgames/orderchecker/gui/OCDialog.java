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
        this.frame = null;
        this.splitPane = null;
        this.tree = new JTree(new DefaultTreeModel(this.root));
        this.bottomPane = new JScrollPane(this.tree);
        this.panel = panel;
        this.data = data;
        EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
        BevelBorder bb = new BevelBorder(1);
        CompoundBorder cb = new CompoundBorder(eb, bb);
        setBorder(new CompoundBorder(cb, eb));
        configureBottomPane();
        setLayout(new BorderLayout());
        add("Center", this.bottomPane);
    }

    public OCDialog(JFrame frame, Data data)
    {
        this.tree = new JTree(new DefaultTreeModel(this.root));
        this.bottomPane = new JScrollPane(this.tree);
        this.frame = frame;
        this.data = data;
        EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
        BevelBorder bb = new BevelBorder(1);
        CompoundBorder cb = new CompoundBorder(eb, bb);
        setBorder(new CompoundBorder(cb, eb));
        configureMenu();
        configureTopPane();
        configureBottomPane();
        this.splitPane = new JSplitPane(0, this.topPane, this.bottomPane);
        this.splitPane.setContinuousLayout(true);
        setLayout(new BorderLayout());
        add("Center", this.splitPane);
    }

    private void configureMenu()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        this.notesFile = new JMenuItem("Character Notes", 78);
        this.notesFile.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                new CharInfoDlg(OCDialog.this.frame, Main.main.getNation(), OCDialog.this.tree, OCDialog.this.data);
            }

        }
);
        fileMenu.add(this.notesFile);
        this.exportFile = new JMenuItem("Export", 69);
        this.exportFile.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                new ResultsDlg(OCDialog.this.frame, Main.main.getNation(), OCDialog.this.tree, OCDialog.this.data);
            }

        }
);
        fileMenu.add(this.exportFile);
        fileMenu.addSeparator();
        JMenuItem deleteMenu = new JMenuItem("Delete Game Info", 68);
        deleteMenu.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                new GameDeleteDlg(OCDialog.this.frame, OCDialog.this.data);
            }

        }
);
        fileMenu.add(deleteMenu);
        fileMenu.addMenuListener(new MenuListener() {

            @Override
			public void menuSelected(MenuEvent e)
            {
                OCDialog.this.exportFile.setEnabled(Main.main.getNation() != null);
                OCDialog.this.notesFile.setEnabled(Main.main.getNation() != null);
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
        menuBar.add(fileMenu);
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        JMenuItem helpHelp = new JMenuItem("Instructions", 73);
        helpHelp.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                new HelpDlg(OCDialog.this.frame, OCDialog.this.data);
            }

        }
);
        helpMenu.add(helpHelp);
        helpMenu.addSeparator();
        JMenuItem helpAbout = new JMenuItem("About", 65);
        helpAbout.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                new AboutDlg(OCDialog.this.frame);
            }

        }
);
        helpMenu.add(helpAbout);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
        this.frame.setJMenuBar(menuBar);
    }

    private void configureTopPane()
    {
        this.turnLabel.setText("Turn Result");
        this.ordersLabel.setText("MEOW/Automagic Orders");
        this.gameTypeLabel.setText("Game Type");
        this.turnPath.setText(this.data.getTurnResultsPath());
        this.ordersPath.setText(this.data.getOrdersPath());
        this.gameTypes = new JComboBox(this.data.getGameDescriptions());
        this.gameTypes.setSelectedItem(this.data.getGameDescription());
        this.turnButton.setText("Browse...");
        this.ordersButton.setText("Browse...");
        this.goButton.setText("Perform Order Checking");
        this.turnButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                turnActionPerformed();
            }

        }
);
        this.ordersButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                ordersActionPerformed();
            }

        }
);
        this.goButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                goActionPerformed();
            }

        }
);
        this.topPane.setLayout(this.layoutManager);
        LayoutTopPane();
    }

    private void LayoutTopPane()
    {
        Insets topSpacing = new Insets(10, 10, 0, 10);
        Insets bothSpacing = new Insets(10, 10, 10, 10);
        Insets noSpacing = new Insets(0, 10, 0, 10);
        addLabel(topSpacing, this.turnLabel);
        addTextField(noSpacing, this.turnPath);
        addButton(noSpacing, this.turnButton);
        addIcon(noSpacing, this.turnResult);
        addLabel(topSpacing, this.ordersLabel);
        addTextField(noSpacing, this.ordersPath);
        addButton(noSpacing, this.ordersButton);
        addIcon(noSpacing, this.ordersResult);
        JPanel gameTypePane = new JPanel();
        gameTypePane.add(this.gameTypeLabel);
        gameTypePane.add(this.gameTypes);
        addPane(topSpacing, gameTypePane);
        this.constraint.gridwidth = 0;
        this.constraint.weightx = 0.0D;
        this.constraint.weighty = 1.0D;
        this.constraint.fill = 2;
        this.constraint.insets = bothSpacing;
        this.constraint.anchor = 11;
        this.layoutManager.setConstraints(this.goButton, this.constraint);
        this.topPane.add(this.goButton);
    }

    private void addLabel(Insets insets, JLabel label)
    {
        this.constraint.anchor = 18;
        this.constraint.gridwidth = 0;
        this.constraint.weightx = 0.0D;
        this.constraint.fill = 0;
        this.constraint.insets = insets;
        this.layoutManager.setConstraints(label, this.constraint);
        this.topPane.add(label);
    }

    private void addTextField(Insets insets, JTextField field)
    {
        this.constraint.anchor = 10;
        this.constraint.gridwidth = 1;
        this.constraint.weightx = 1.0D;
        this.constraint.fill = 2;
        this.constraint.insets = insets;
        this.layoutManager.setConstraints(field, this.constraint);
        this.topPane.add(field);
    }

    private void addPane(Insets insets, JPanel field)
    {
        this.constraint.anchor = 17;
        this.constraint.gridwidth = 0;
        this.constraint.weightx = 1.0D;
        this.constraint.fill = 0;
        this.constraint.insets = insets;
        this.layoutManager.setConstraints(field, this.constraint);
        this.topPane.add(field);
    }

    private void addButton(Insets insets, JButton button)
    {
        this.constraint.anchor = 10;
        this.constraint.gridwidth = 1;
        this.constraint.weightx = 0.0D;
        this.constraint.fill = 0;
        this.constraint.insets = insets;
        this.layoutManager.setConstraints(button, this.constraint);
        this.topPane.add(button);
    }

    private void addIcon(Insets insets, JLabel label)
    {
        this.constraint.anchor = 10;
        this.constraint.gridwidth = 0;
        this.constraint.weightx = 0.0D;
        this.constraint.fill = 0;
        this.constraint.insets = insets;
        this.layoutManager.setConstraints(label, this.constraint);
        this.topPane.add(label);
    }

    private void configureBottomPane()
    {
        this.tree.setEditable(false);
        this.tree.setExpandsSelectedPaths(true);
        this.tree.setRootVisible(false);
        class _cls1ResultTreeRenderer extends DefaultTreeCellRenderer
        {

            @Override
			public Component getTreeCellRendererComponent(JTree tree1, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus1)
            {
                try {
                    super.getTreeCellRendererComponent(tree1, value, sel, expanded, leaf, row, hasFocus1);
                    if(value != OCDialog.this.root)
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

        this.tree.setCellRenderer(new _cls1ResultTreeRenderer());
    }

    public void createResultsTree()
    {
        this.root.removeAllChildren();
        Main.main.getNation().addTreeNodes(this.tree, this.root);
        ((DefaultTreeModel)this.tree.getModel()).nodeStructureChanged(this.root);
        javax.swing.tree.TreeNode path[];
        for(Enumeration allNodes = this.root.depthFirstEnumeration(); allNodes.hasMoreElements(); this.tree.expandPath(new TreePath(path)))
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
        String filename = this.turnPath.getText();
        if(filename.length() > 0)
        {
            File f = new File(filename);
            if(f.exists())
                chooser.setSelectedFile(new File(filename));
            else
                chooser.setCurrentDirectory(new File(f.getParent()));
        } else
        if(this.ordersPath.getText().length() > 0)
        {
            File f = new File(this.ordersPath.getText());
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
            this.turnPath.setText(selFile.getPath());
        }
    }

    private void ordersActionPerformed()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Orders File");
        chooser.setMultiSelectionEnabled(false);
        String filename = this.ordersPath.getText();
        if(filename.length() > 0)
        {
            File f = new File(filename);
            if(f.exists())
                chooser.setSelectedFile(new File(filename));
            else
                chooser.setCurrentDirectory(new File(f.getParent()));
        } else
        if(this.turnPath.getText().length() > 0)
        {
            File f = new File(this.turnPath.getText());
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
            this.ordersPath.setText(selFile.getPath());
        }
    }

    public void goActionPerformed()
    {
        if(this.turnPath.getText().length() == 0)
        {
            processError(this.turnResult, "You need to specify a turn result!");
            return;
        }
        if(this.ordersPath.getText().length() == 0)
        {
            processError(this.ordersResult, "You need to specify an orders file!");
            return;
        }
        ImportTurnXml turnData = new ImportTurnXml(this.turnPath.getText());
        boolean result = turnData.getTurnData();
        if(!result)
        {
            String msg = "Your turn result file could not be processed!\nPlease make sure you selected the correct Middle Earth XML \nturn result.";
            processError(this.turnResult, msg);
            return;
        }
        Main.main.setNation(turnData.parseTurnData());
        result = Main.main.getNation().isNationComplete();
        if(!result)
        {
            String msg = "Your turn result was processed but not all of the\nexpected  information was present!";
            processError(this.turnResult, msg);
            return;
        }
        this.turnResult.setIcon(goIcon);
        this.data.setTurnResultsPath(this.turnPath.getText());
        ImportOrdersCsv orders = new ImportOrdersCsv(this.ordersPath.getText(), Main.main.getNation());
        result = orders.getOrders();
        if(!result)
        {
            orders.closeFile();
            processError(this.ordersResult, "Your orders file could not be opened!");
            return;
        }
        String error = orders.parseOrders();
        orders.closeFile();
        if(error != null)
        {
            error = error + "\n\nOrder checking cancelled.";
            processError(this.ordersResult, error);
            return;
        }
        this.ordersResult.setIcon(goIcon);
        this.data.setOrdersPath(this.ordersPath.getText());
        this.data.setGameType((String)this.gameTypes.getSelectedItem());
        Main.main.setRuleSet(new Ruleset());
        ImportRulesCsv rules = new ImportRulesCsv(this.data.getRulesPath(), Main.main.getRuleSet());
        result = rules.getRules();
        if(!result)
        {
            rules.closeFile();
            Main.displayErrorMessage("The rules file (" + this.data.getRulesPath() + ") could not be opened!");
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
        ImportTerrainCsv terrain = new ImportTerrainCsv(this.data.getTerrainPath(), Main.main.getMap());
        result = terrain.getMapInformation();
        if(!result)
        {
            terrain.closeFile();
            Main.displayErrorMessage("The terrain file (" + this.data.getTerrainPath() + ") could not be opened!");
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
            this.splitPane.resetToPreferredSizes();
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
        return this.panel;
    }

    
    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    
    public JTextField getOrdersPath() {
        return this.ordersPath;
    }

    
    public void setOrdersPath(JTextField ordersPath) {
        this.ordersPath = ordersPath;
    }

    
    public JTextField getTurnPath() {
        return this.turnPath;
    }

    
    public void setTurnPath(JTextField turnPath) {
        this.turnPath = turnPath;
    }
    
    


    
    public JFrame getFrame() {
        return this.frame;
    }

    
    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void goActionPerformedMscoon()
    {
        boolean result;
        if(this.ordersPath.getText().length() == 0)
        {
            processError(this.ordersResult, "You need to specify an orders file!");
            return;
        }
        result = Main.main.getNation().isNationComplete();
        if(!result)
        {
            String msg = "Your turn result was processed but not all of the\nexpected  information was present!";
            processError(this.turnResult, msg);
            return;
        }
        this.turnResult.setIcon(goIcon);
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
        ImportRulesCsv rules = new ImportRulesCsv(this.data.getRulesPath(), Main.main.getRuleSet());
        result = rules.getRules();
        if(!result)
        {
            rules.closeFile();
            Main.displayErrorMessage("The rules file (" + this.data.getRulesPath() + ") could not be opened!");
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
        ImportTerrainCsv terrain = new ImportTerrainCsv(this.data.getTerrainPath(), Main.main.getMap());
        result = terrain.getMapInformation();
        if(!result)
        {
            terrain.closeFile();
            Main.displayErrorMessage("The terrain file (" + this.data.getTerrainPath() + ") could not be opened!");
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
