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
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.Messages;
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

    class BaseMovementActionListener implements ActionListener {
    	protected String message;
    	public BaseMovementActionListener(String aMessage) {
			this.message = aMessage;
		}
    	
    	@Override
		public void actionPerformed(ActionEvent arg0) {
            updateEditor();
        }
    }
    class AddMovementActionListener extends BaseMovementActionListener {
    	public AddMovementActionListener(String aMessageKey) {
			super(aMessageKey);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
    		MoveArmyOrderSubeditor.this.dirs.add(this.message);
            updateEditor();
        }
    }
    class AddMovementButton extends JButton {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		AddMovementButton(String aMessage) {
    		super(aMessage);
    		this.addActionListener(new AddMovementActionListener(aMessage));
    	}
    }
    
    public MoveArmyOrderSubeditor(OrderEditor oe,Order o,GameHolder gameHolder) {
        super(oe,o,gameHolder);
        }

    @Override
	public void addComponents(TableLayoutBuilder tlb, ArrayList<JComponent> components, Order o, int paramNo,boolean applyInitValue) {

        tlb.cell(new JLabel(Messages.getString("MoveArmyOrderSubeditor.StyleColon")), "colspec=left:70px"); //$NON-NLS-1$ //$NON-NLS-2$
        tlb.cell(this.movementStyle = new JComboBox(), "colspec=left:130px"); //$NON-NLS-1$
        this.movementStyle.setPreferredSize(new Dimension(60, 18));
        this.movementStyle.addItem(""); //$NON-NLS-1$
        this.movementStyle.addItem(Messages.getString("MoveArmyOrderSubeditor.movement.normal")); //$NON-NLS-1$
        this.movementStyle.addItem(Messages.getString("MoveArmyOrderSubeditor.movement.evasive")); //$NON-NLS-1$
        this.movementStyle.setSelectedItem(o.getParameter(o.getLastParamIndex()));
        this.movementStyle.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                updateEditor();
            }
        });

        this.directionParams = new JTextField();
        this.directionParams.setVisible(false);

        String txt = ""; //$NON-NLS-1$
        for (int i=0; i<o.getLastParamIndex(); i++) {
            if (o.getParameter(i) == null) break;
            this.dirs.add(o.getParameter(i));
            txt += (txt.equals("") ? "" : Order.DELIM) + o.getParameter(i); //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.directionParams.setText(txt);

        tlb.row();
        TableLayoutBuilder stlb = new TableLayoutBuilder();
        JButton btn;
        stlb.cell(btn = new JButton(Messages.getString("MoveArmyOrderSubeditor.direction.nw"))); //$NON-NLS-1$
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                MoveArmyOrderSubeditor.this.dirs.add(Messages.getString("MoveArmyOrderSubeditor.direction.nw")); //$NON-NLS-1$
                updateEditor();
            }
        });

        stlb.cell(new JLabel());

        stlb.cell(new AddMovementButton(Messages.getString("MoveArmyOrderSubeditor.direction.ne"))); //$NON-NLS-1$
        stlb.row();

        stlb.cell(new AddMovementButton(Messages.getString("MoveArmyOrderSubeditor.direction.w"))); //$NON-NLS-1$
        stlb.cell(new AddMovementButton(Messages.getString("MoveArmyOrderSubeditor.direction.home"))); //$NON-NLS-1$
        stlb.cell(new AddMovementButton(Messages.getString("MoveArmyOrderSubeditor.direction.e"))); //$NON-NLS-1$

        stlb.row();

        stlb.cell(btn = new AddMovementButton(Messages.getString("MoveArmyOrderSubeditor.direction.sw"))); //$NON-NLS-1$

        //stlb.cell();
        stlb.cell(btn = new JButton("<--")); //$NON-NLS-1$
        btn.setToolTipText(Messages.getString("MoveArmyOrderSubeditor.back")); //$NON-NLS-1$
        btn.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                if (MoveArmyOrderSubeditor.this.dirs.size() > 0) {
                    MoveArmyOrderSubeditor.this.dirs.remove(MoveArmyOrderSubeditor.this.dirs.size()-1);
                    updateEditor();
                }
            }
        });

        stlb.cell(new AddMovementButton(Messages.getString("MoveArmyOrderSubeditor.direction.se"))); //$NON-NLS-1$

//        stlb.row();


        tlb.cell(new JLabel(" ")); //$NON-NLS-1$
        JPanel pnl = stlb.getPanel();
        pnl.setBackground(Color.white);
        tlb.cell(pnl);

        // note that the order of these add matters...it is the order that the abstractOrtderEditor generates the parameters as text
        components.add(this.directionParams);
        components.add(this.movementStyle);
    }



    @Override
	public void updateEditor() {
        while (this.dirs.size() > 14) {
            this.dirs.remove(this.dirs.size()-1);
        }
        String text = ""; //$NON-NLS-1$
        for (String dir : this.dirs) {
            text += (text.equals("") ? "" : Order.DELIM) + dir; //$NON-NLS-1$ //$NON-NLS-2$
        }
        this.directionParams.setText(text);
        // hack to stop spurious - appearing
        boolean oldHidden = getEditor().isHideUnspecifiedParameters();
        getEditor().setHideUnspecifiedParameters(true);
        getEditor().updateParameters();
        getEditor().setHideUnspecifiedParameters(oldHidden);
    }

	@Override
	public JComponent getPrimaryComponent(String initValue) {
		// TODO Auto-generated method stub
		return null;
	}
}
