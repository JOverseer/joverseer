package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.domain.Order;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class MoveArmyOrderSubeditor extends AbstractOrderSubeditor {
    JComboBox movementStyle;
    ArrayList<String> dirs = new ArrayList<String>();
    
    public MoveArmyOrderSubeditor(Order o) {
        super(o);
    }

    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        
        tlb.cell(new JLabel("Style : "));
        tlb.cell(movementStyle = new JComboBox());
        movementStyle.setPreferredSize(new Dimension(60, 18));
        movementStyle.addItem("");
        movementStyle.addItem("no");
        movementStyle.addItem("ev");
        movementStyle.setSelectedItem(o.getParameter(0));
        movementStyle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                updateEditor();
            }
        });
        
        for (int i=1; i<20; i++) {
            if (o.getParameter(i) == null) break;
            dirs.add(o.getParameter(i));
        }
        
        tlb.row();
        TableLayoutBuilder stlb = new TableLayoutBuilder();
        JButton btn;
        stlb.cell(btn = new JButton("nw"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("nw");
                updateEditor();
            }
        });
        
        stlb.cell();
        
        stlb.cell(btn = new JButton("ne"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("ne");
                updateEditor();
            }
        });
        
        stlb.row();

        
        stlb.cell(btn = new JButton("w"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("w");
                updateEditor();
            }
        });
        
        stlb.cell(btn = new JButton("h"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("h");
                updateEditor();
            }
        });
        
        stlb.cell(btn = new JButton("e"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("e");
                updateEditor();
            }
        });
        
        stlb.row();
        
        stlb.cell(btn = new JButton("sw"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("sw");
                updateEditor();
            }
        });
        
        stlb.cell();
        
        stlb.cell(btn = new JButton("se"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.add("se");
                updateEditor();
            }
        });
        
        stlb.row();
        
        stlb.cell(btn = new JButton("back"));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                dirs.remove(dirs.size()-1);
                updateEditor();
            }
        });

        tlb.cell(new JLabel(" "));
        tlb.cell(stlb.getPanel());
        
        
    }

    public void updateEditor() {
        String text = "";
        String val = "-";
        if (movementStyle.getSelectedItem() != null && !movementStyle.getSelectedItem().equals("")) {
            val = movementStyle.getSelectedItem().toString();
        } 
        text += (text.equals("") ? "" : " ") + val;
        for (String dir : dirs) {
            text += (text.equals("") ? "" : " ") + dir;
        }
        getEditor().parameters.setText(text);
    }
}
