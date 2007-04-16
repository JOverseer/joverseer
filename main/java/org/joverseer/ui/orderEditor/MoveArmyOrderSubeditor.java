package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.joverseer.domain.Order;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class MoveArmyOrderSubeditor extends AbstractOrderSubeditor {
    JComboBox movementStyle;
    ArrayList<String> dirs = new ArrayList<String>();
    JTextField directionParams;
    
    public MoveArmyOrderSubeditor(Order o) {
        super(o);
    }

    public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        
        tlb.cell(new JLabel("Style : "), "colspec=left:70px");
        tlb.cell(movementStyle = new JComboBox(), "colspec=left:130px");
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
        
        directionParams = new JTextField();
        directionParams.setVisible(false);

        String txt = "";
        for (int i=1; i<20; i++) {
            if (o.getParameter(i) == null) break;
            dirs.add(o.getParameter(i));
            txt += (txt.equals("") ? "" : Order.DELIM) + o.getParameter(i);
        }
        directionParams.setText(txt);
        
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
                if (dirs.size() > 0) {
                    dirs.remove(dirs.size()-1);
                    updateEditor();
                }
            }
        });

        tlb.cell(new JLabel(" "));
        tlb.cell(stlb.getPanel());
        
        components.add(movementStyle);
        components.add(directionParams);
    }
    
    

    public void updateEditor() {
        while (dirs.size() > 14) {
            dirs.remove(dirs.size()-1);
        }
        String text = "";
        for (String dir : dirs) {
            text += (text.equals("") ? "" : Order.DELIM) + dir;
        }
        directionParams.setText(text);
        getEditor().updateParameters();
    }
}
