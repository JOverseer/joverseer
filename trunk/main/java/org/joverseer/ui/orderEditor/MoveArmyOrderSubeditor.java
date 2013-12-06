package org.joverseer.ui.orderEditor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.Order;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for the Army Movement Orders. It provides a set of buttons with which the
 * user selects the desired directions to fill in the order
 * 
 * @author Marios Skounakis
 */
public class MoveArmyOrderSubeditor extends AbstractOrderSubeditor {
    JComboBox movementStyle;
    ArrayList<String> dirs = new ArrayList<String>();
    JTextField directionParams;
    
    public MoveArmyOrderSubeditor(Order o) {
        super(o);
        }

    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo) {
        
        tlb.cell(new JLabel("Style : "), "colspec=left:70px");
        tlb.cell(this.movementStyle = new JComboBox(), "colspec=left:130px");
        this.movementStyle.setPreferredSize(new Dimension(60, 18));
        this.movementStyle.addItem("");
        this.movementStyle.addItem("no");
        this.movementStyle.addItem("ev");
        this.movementStyle.setSelectedItem(o.getParameter(o.getLastParamIndex()));
        this.movementStyle.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                updateEditor();
            }
        });
        
        this.directionParams = new JTextField();
        this.directionParams.setVisible(false);

        String txt = "";
        for (int i=0; i<o.getLastParamIndex(); i++) {
            if (o.getParameter(i) == null) break;
            this.dirs.add(o.getParameter(i));
            txt += (txt.equals("") ? "" : Order.DELIM) + o.getParameter(i);
        }
        this.directionParams.setText(txt);
        
        tlb.row();
        TableLayoutBuilder stlb = new TableLayoutBuilder();
        JButton btn;
        stlb.cell(btn = new JButton("nw"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("nw");
                updateEditor();
            }
        });
        
        stlb.cell(new JLabel());
        
        stlb.cell(btn = new JButton("ne"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("ne");
                updateEditor();
            }
        });
        
        stlb.row();

        
        stlb.cell(btn = new JButton("w"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("w");
                updateEditor();
            }
        });
        
        stlb.cell(btn = new JButton("h"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("h");
                updateEditor();
            }
        });
        
        stlb.cell(btn = new JButton("e"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("e");
                updateEditor();
            }
        });
        
        stlb.row();
        
        stlb.cell(btn = new JButton("sw"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("sw");
                updateEditor();
            }
        });
        
        //stlb.cell();
        stlb.cell(btn = new JButton("<--"));
        btn.setToolTipText("Back (delete last move)");
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                if (MoveArmyOrderSubeditor.this.dirs.size() > 0) {
                    MoveArmyOrderSubeditor.this.dirs.remove(MoveArmyOrderSubeditor.this.dirs.size()-1);
                    updateEditor();
                }
            }
        });
        
        stlb.cell(btn = new JButton("se"));
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add("se");
                updateEditor();
            }
        });
        
//        stlb.row();
        

        tlb.cell(new JLabel(" "));
        JPanel pnl = stlb.getPanel();
        pnl.setBackground(Color.white);
        tlb.cell(pnl);
        
        components.add(this.directionParams);
        components.add(this.movementStyle);
    }
    
    

    @Override
	public void updateEditor() {
        while (this.dirs.size() > 14) {
            this.dirs.remove(this.dirs.size()-1);
        }
        String text = "";
        for (String dir : this.dirs) {
            text += (text.equals("") ? "" : Order.DELIM) + dir;
        }
        this.directionParams.setText(text);
        getEditor().updateParameters();
    }
}
