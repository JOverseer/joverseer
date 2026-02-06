package org.joverseer.ui.orderEditor;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Subeditor for the 940 order
 *
 * @author Marios Skounakis
 */
public class CastLoSpellOrderSubeditor extends AbstractOrderSubeditor {
    String paramName;
    JComboBox parameter;
    JTextField spellNo;
    JLabel paramLabel;
    int orderNo;
    TableLayoutBuilder tlb;
    ArrayList<JComponent> components;
    JPanel secondParamPanel;
    JComponent currentComp;
    JPanel currentCompPanel;
    String currentType = null;

    public CastLoSpellOrderSubeditor(OrderEditor oe,Order o,GameHolder gameHolder) {
        super(oe,o,gameHolder);
        this.paramName = "Spell";
        this.orderNo = 940;
    }

    protected void loadSpellCombo() {
    	GameMetadata gm = this.gameHolder.getGame().getMetadata();
    	Character c = getOrder().getCharacter();
        this.parameter.addItem("");
    	for (SpellInfo si : (ArrayList<SpellInfo>)gm.getSpells().getItems()) {
            if (si.getOrderNumber() == this.orderNo) {
            	SpellProficiency sp = c.findSpellMatching(si.getNumber());
	             this.parameter.addItem(si.getNumber() + " - " + si.getName() + ((sp != null) ? " (known)" : ""));
            }
        }
    }

    @Override
    public void addComponents(TableLayoutBuilder tlb1, ArrayList<JComponent> components1, Order o, int paramNo,boolean applyInitValue) {
        this.tlb = tlb1;
        this.components = components1;
        tlb1.cell(new JLabel(this.paramName), "colspec=left:70px");
        tlb1.cell(this.parameter = new JComboBox(), "colspec=left:230px");
        this.parameter.setPreferredSize(new Dimension(180, 18));
        tlb1.row();
        tlb1.cell(this.spellNo = new JTextField());
        this.spellNo.setVisible(false);
        tlb1.row();

        loadSpellCombo();

        components1.add(this.spellNo);

        tlb1.row();

        this.secondParamPanel = new JPanel();
        tlb1.cell(this.secondParamPanel, "colspan=2");
        tlb1.row();

        // find and preload current spell (from order)
        if (o.getParameter(paramNo) != null) {
            String spellId = o.getParameter(paramNo);
            for (int i=0; i<this.parameter.getItemCount(); i++) {
                if (this.parameter.getItemAt(i).toString().startsWith(spellId + " ")) {
                    this.parameter.setSelectedIndex(i);
                    this.spellNo.setText(o.getParameter(paramNo));
                    refreshSecondParameter();
                }
            }
        }
        this.parameter.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent arg0) {
                if (CastLoSpellOrderSubeditor.this.parameter.getSelectedItem() != null) {
                    String spId = CastLoSpellOrderSubeditor.this.parameter.getSelectedItem().toString();
                    CastLoSpellOrderSubeditor.this.spellNo.setText(spId.substring(0, spId.indexOf(" ")));
                } else {
                    CastLoSpellOrderSubeditor.this.spellNo.setText("");
                }
                refreshSecondParameter();
                updateEditor();
            }
        });
    }

    /**
     * Refreshes the component that edits the second parameter of the 940 order
     * according to the selected spell
     */
    private void refreshSecondParameter() {
        try {
            int spellId = Integer.parseInt(this.spellNo.getText());
        	int counter =1;
        	boolean multiple=false;
            String paramType = "";
            if (",406,408,417,420,422,424,426,430,436,".indexOf("," + spellId + ",") > -1) {
                paramType = "cid";
            } else if (",416,".indexOf("," + spellId + ",") > -1) {
                paramType = "pro";
            } else if (",404,419,432,".indexOf("," + spellId + ",") > -1) {
                paramType = "nat";
            } else if (",402,410,".indexOf("," + spellId + ",") > -1) {
                paramType = "alg";
            } else if (",412,".indexOf("," + spellId + ",") > -1) {
                multiple = true;
                paramType = "art";
            } else if (",418,428,".indexOf("," + spellId + ",") > -1) {
                paramType = "art";
            } else if (",413,414,415,434,".indexOf("," + spellId + ",") > -1) {
                paramType = "hex";
            }
            // a lot of hacking is going on here to adjust the components
            // and update the OrderEditor appropriately
            if (this.currentComp != null) {
                this.secondParamPanel.remove(this.currentCompPanel);
                this.components.remove(this.currentComp);
            };
            AbstractOrderSubeditor sub = null;
            if (this.currentType != null && !this.currentType.equals(paramType)) {
                getOrder().setParameter(1, "");
                this.currentType = paramType;
            } else {
                this.currentType = paramType;
            }
            if (paramType.equals("cid")) {
                sub = new SingleParameterOrderSubeditor(this.getEditor(), "Char", getOrder(),this.gameHolder);
            } else if (paramType.equals("b")) {
                sub = new SingleParameterOrderSubeditor(this.getEditor(), "Arti No", getOrder(),this.gameHolder);
            } else if (paramType.equals("hex")) {
                sub = new SingleParameterOrderSubeditor(this.getEditor(), "Hex", getOrder(),this.gameHolder);
            } else if (paramType.equals("nat")) {
                sub = new NationParameterOrderSubeditor(this.getEditor(),"Nation", getOrder(),this.gameHolder);
            } else if (paramType.equals("pro")) {
                sub = new DropDownParameterOrderSubeditor(this.getEditor(),"Product", getOrder(), new String[]{"le", "br", "st", "mi", "fo", "ti", "mo", "go"}, new String[]{"Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts", "Gold"},this.gameHolder);
            } else if (paramType.equals("alg")) {
                sub = new DropDownParameterOrderSubeditor(this.getEditor(),"Alleg", getOrder(), new String[]{"g", "e", "n"}, new String[]{"Good", "Evil", "Neutral"},this.gameHolder);
            }else if (paramType.equals("art")) {
                sub = new ArtifactNumberParameterOrderSubeditor(this.getEditor(),"Arti", getOrder(),this.gameHolder);
            } else {
            	throw new Exception("Config error");
            }

            TableLayoutBuilder tlb1 = new TableLayoutBuilder();
            sub.addComponents(tlb1, this.components, getOrder(), counter,false);
            tlb1.row();
            sub.setEditor(getEditor());
            
            if (multiple) {
                do {
                	counter++;
                	sub = new ArtifactNumberParameterOrderSubeditor(this.getEditor(),"Arti", getOrder(),this.gameHolder);
                	sub.addComponents(tlb1, this.components, getOrder(), counter,false);
                	tlb1.row();
//                tlb1.cell(new JLabel(" ")); //DAS- I don't understand what this is doing unless to force a thick row?
//            	tlb1.row();
                	sub.setEditor(getEditor());
                } while (counter < 6);
            }

            this.currentComp = this.components.get(1);
            this.currentCompPanel = tlb1.getPanel();
            this.secondParamPanel.add(this.currentCompPanel);
            this.secondParamPanel.updateUI();
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

	@Override
	public JComponent getPrimaryComponent(String initValue) {
		// TODO Auto-generated method stub
		return null;
	}

}
