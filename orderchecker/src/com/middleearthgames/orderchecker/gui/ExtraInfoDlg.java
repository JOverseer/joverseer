// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ExtraInfoDlg.java

package com.middleearthgames.orderchecker.gui;

import com.middleearthgames.orderchecker.Nation;
import com.middleearthgames.orderchecker.io.Data;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

public class ExtraInfoDlg extends JDialog
{

    public ExtraInfoDlg(JFrame frame, Nation nation, Data data, Vector requests)
    {
        super(frame, "OrderChecker Setup", true);
        this.okButton = new JButton();
        this.tabbedPane = new JTabbedPane();
        this.alignments = new JComboBox[25];
        this.nation = nation;
        this.data = data;
        this.contents = requests;
        if(this.contents.size() > 0)
        {
            layoutInformationRequests();
            setInitialStates();
        }
        layoutNationAlignment();
        layoutParsingResults();
        this.okButton.setText("OK");
        this.okButton.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e)
            {
                okActionPerformed();
            }

        }
);
        this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(this.tabbedPane);
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(scrollPane, "Center");
        pane.add(this.okButton, "South");
        pack();
        java.awt.Dimension prefer = getPreferredSize();
        Data.checkScreenSize(prefer);
        setSize(prefer);
        setLocation(Data.getScreenLocation(prefer));
        getRootPane().setDefaultButton(this.okButton);
        addKeyListener(new KeyAdapter() {

            @Override
			public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == 27)
                    ExtraInfoDlg.this.okButton.doClick();
            }

        }
);
        addWindowListener(new WindowAdapter() {

            @Override
			public void windowClosing(WindowEvent we)
            {
                ExtraInfoDlg.this.okButton.doClick();
            }

        }
);
        setVisible(true);
    }

    private void layoutInformationRequests()
    {
        JPanel pane = new JPanel();
        GridBagLayout layoutManager = new GridBagLayout();
        pane.setLayout(layoutManager);
        GridBagConstraints layoutConstraint = new GridBagConstraints();
        layoutConstraint.insets = new Insets(10, 10, 0, 10);
        layoutConstraint.gridwidth = 0;
        layoutConstraint.anchor = 17;
        int size = this.contents.size();
        boolean added[] = new boolean[size];
        String currentTag = null;
        for(int i = 0; i < size; i++)
        {
            if(added[i])
                continue;
            if(i > 0)
            {
                JSeparator separator = new JSeparator();
                layoutManager.setConstraints(separator, layoutConstraint);
                pane.add(separator);
            }
            JCheckBox newItem = (JCheckBox)this.contents.get(i);
            currentTag = getTag(newItem);
            removeTag(newItem);
            layoutManager.setConstraints(newItem, layoutConstraint);
            pane.add(newItem);
            added[i] = true;
            for(int j = i + 1; j < size; j++)
            {
                if(added[j])
                    continue;
                JCheckBox newItem2 = (JCheckBox)this.contents.get(j);
                if(!getTag(newItem2).equals(currentTag))
                    continue;
                removeTag(newItem2);
                int index = firstInstanceInList(newItem2);
                if(index >= j)
                {
                    layoutManager.setConstraints(newItem2, layoutConstraint);
                    pane.add(newItem2);
                }
                added[j] = true;
            }

        }

        this.tabbedPane.add("Additional Information", pane);
    }

    private void layoutNationAlignment()
    {
        JPanel pane = new JPanel();
        GridBagLayout layoutManager = new GridBagLayout();
        pane.setLayout(layoutManager);
        GridBagConstraints layoutConstraint = new GridBagConstraints();
        for(int i = 0; i < 10; i++)
        {
            for(int col = 1; col <= 3; col++)
            {
                boolean endOfRow = false;
                if(col == 3 && i < 5 || col == 2 && i >= 5)
                    endOfRow = true;
                if(col != 3 || i < 5)
                {
                    JLabel nationName = new JLabel();
                    int nationIndex = i + (col - 1) * 10;
                    nationName.setText(this.nation.getNationName(nationIndex + 1));
                    this.alignments[nationIndex] = getAlignmentField(nationIndex);
                    layoutConstraint.gridwidth = 1;
                    layoutConstraint.anchor = 13;
                    layoutConstraint.insets = new Insets(10, 10, 0, 10);
                    layoutManager.setConstraints(nationName, layoutConstraint);
                    pane.add(nationName);
                    layoutConstraint.gridwidth = endOfRow ? 0 : 1;
                    layoutConstraint.anchor = 17;
                    layoutConstraint.insets = new Insets(10, 0, 0, col == 3 ? 10 : 20);
                    layoutManager.setConstraints(this.alignments[nationIndex], layoutConstraint);
                    pane.add(this.alignments[nationIndex]);
                }
            }

        }

        this.tabbedPane.add("Nations", pane);
    }

    private void layoutParsingResults()
    {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        StringBuffer line = new StringBuffer();
        line.append("<p><b><u>" + this.nation.getNationName(this.nation.getNation()) + ", Game " + this.nation.getGame() + ", Turn " + this.nation.getTurn() + "</u></b><p>");
        line.append("Characters imported: " + this.nation.getCharacterCount() + "<br>");
        line.append("Population centers imported: " + this.nation.getPopulationCenterCount() + "<br>");
        line.append("Armies imported: " + this.nation.getArmyCount() + "<p>");
        line.append("<b>Data from " + this.nation.getNationParsed().size() + " other nations successfully imported</b>:<br><center>");
        if(this.nation.getNationParsed().size() == 0)
        {
            line.append("None<br>");
        } else
        {
            for(int i = 0; i < this.nation.getNationParsed().size(); i++)
            {
                Integer nationNumber = (Integer)this.nation.getNationParsed().get(i);
                line.append(this.nation.getNationName(nationNumber.intValue()) + "<br>");
            }

        }
        line.append("</center>");
        JEditorPane textPane = new JEditorPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setText(line.toString());
        textPane.setCaretPosition(0);
        this.tabbedPane.add("Parsing Results", textPane);
    }

    private String getTag(JCheckBox box)
    {
        int index = box.getText().indexOf(':');
        return box.getText().substring(0, index);
    }

    private void removeTag(JCheckBox box)
    {
        String text = box.getText();
        int index = text.indexOf(':');
        text = text.substring(index + 1);
        box.setText(text);
    }

    private void okActionPerformed()
    {
        int size = this.contents.size();
        for(int i = 0; i < size; i++)
        {
            JCheckBox newItem = (JCheckBox)this.contents.get(i);
            int index = firstInstanceInList(newItem);
            if(index < i)
            {
                JCheckBox master = (JCheckBox)this.contents.get(index);
                newItem.setSelected(master.isSelected());
            }
        }

        for(int i = 0; i < 25; i++)
        {
            int alignment = this.alignments[i].getSelectedIndex();
            this.data.setNationAlignment(i, alignment, this.nation);
        }

        this.data.setCheckBoxes(this.contents, this.nation);
        dispose();
    }

    private int firstInstanceInList(JCheckBox box)
    {
        int size = this.contents.size();
        for(int i = 0; i < size; i++)
        {
            JCheckBox component = (JCheckBox)this.contents.get(i);
            if(component.getText().equals(box.getText()))
                return i;
        }

        return -1;
    }

    private JComboBox getAlignmentField(int index)
    {
        JComboBox box = new JComboBox(new String[] {
            "Neutral", "Free People", "Dark Servant"
        });
        int alignment = this.data.getNationAlignment(index, this.nation);
        if(alignment != -1)
            box.setSelectedIndex(alignment);
        return box;
    }

    private void setInitialStates()
    {
        Vector previousContents = this.data.getCheckBoxes(this.nation);
        if(previousContents == null)
            return;
        for(int i = 0; i < this.contents.size(); i++)
        {
            JCheckBox box = (JCheckBox)this.contents.get(i);
            JCheckBox previousBox = Data.isDuplicateRequest(previousContents, box);
            if(previousBox != null)
                box.setSelected(previousBox.isSelected());
        }

    }

    private JButton okButton;
    private JTabbedPane tabbedPane;
    private Nation nation;
    private Data data;
    private Vector contents;
    private JComboBox alignments[];


}
