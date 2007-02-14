package org.joverseer.ui.viewers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.joverseer.domain.HexInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;


public class HexInfoViewer extends AbstractForm {

    public static final String FORM_PAGE = "HexInfoViewer";

    HashMap<MovementDirection, JTextField> infCosts = new HashMap<MovementDirection, JTextField>();
    HashMap<MovementDirection, JTextField> cavCosts = new HashMap<MovementDirection, JTextField>();

    JTextField hexNo;
    JTextField climate;

    public HexInfoViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
        JLabel l;
        lb.append(l = new JLabel("Hex No :"), 2, 1);
        lb.append(hexNo = new JTextField(), 2, 1);
        hexNo.setBorder(null);
        lb.append(climate = new JTextField(), 2, 1);
        climate.setPreferredSize(new Dimension(50, 12));
        climate.setBorder(null);
        lb.nextLine();
        lb.append(new JSeparator(), 5, 1);
        lb.nextLine();

        lb.append(l = new JLabel("Inf"), 2, 1);
        Font f = new Font(l.getFont().getName(), Font.BOLD, l.getFont().getSize());
        l.setFont(f);
        lb.append(l = new JLabel(" "));
        l.setPreferredSize(new Dimension(20, 12));
        lb.append(l = new JLabel("Cav"), 2, 1);
        l.setFont(f);
        lb.nextLine();
        for (MovementDirection md : MovementDirection.values()) {
            lb.append(new JLabel(md.getDir().toUpperCase() + " :"));
            JTextField tf = new JTextField();
            tf.setHorizontalAlignment(JLabel.RIGHT);
            infCosts.put(md, tf);
            tf.setBorder(null);
            tf.setPreferredSize(new Dimension(20, 12));
            lb.append(tf);

            lb.append(l = new JLabel(" "));

            lb.append(new JLabel(md.getDir().toUpperCase() + " :"));
            tf = new JTextField();
            tf.setHorizontalAlignment(JLabel.RIGHT);
            cavCosts.put(md, tf);
            tf.setBorder(null);
            tf.setPreferredSize(new Dimension(20, 12));
            lb.append(tf);
            lb.nextLine();
        }
        JPanel panel = lb.getPanel();
        panel.setBackground(Color.WHITE);
        return panel;
    }

    public void setFormObject(Object object) {
        super.setFormObject(object);
        if (object != null) {
            Hex h = (Hex) object;
            String hexNoStr = String.valueOf(h.getColumn());
            if (h.getColumn() < 10) {
                hexNoStr = "0" + hexNoStr;
            }
            if (h.getRow() < 10) {
                hexNoStr = hexNoStr + "0";
            }
            hexNoStr += String.valueOf(h.getRow());
            hexNo.setText(hexNoStr);
            
            Game g = GameHolder.instance().getGame();
            HexInfo hi = (HexInfo)g.getTurn().getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", h.getHexNo());
            climate.setText(hi.getClimate() != null ? hi.getClimate().toString() : "");

            int startHexNo = h.getColumn() * 100 + h.getRow();
            if (startHexNo > 0) {
                for (MovementDirection md : MovementDirection.values()) {
                    int cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), false, true);
                    JTextField tf = (JTextField) infCosts.get(md);
                    String costStr = (cost > 0 ? String.valueOf(cost) : "-");
                    tf.setText(String.valueOf(costStr));

                    cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), true, true);
                    tf = (JTextField) cavCosts.get(md);
                    costStr = (cost > 0 ? String.valueOf(cost) : "-");
                    tf.setText(String.valueOf(costStr));
                }
                return;
            }
            for (JTextField tf : (Collection<JTextField>) infCosts.values()) {
                tf.setText("");
            }
            for (JTextField tf : (Collection<JTextField>) cavCosts.values()) {
                tf.setText("");
            }
        }
    }
}
