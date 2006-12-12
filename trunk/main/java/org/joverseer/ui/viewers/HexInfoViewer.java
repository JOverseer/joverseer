package org.joverseer.ui.viewers;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.binding.form.FormModel;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.metadata.domain.Hex;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.HashMap;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 9 Δεκ 2006
 * Time: 8:05:33 μμ
 * To change this template use File | Settings | File Templates.
 */
public class HexInfoViewer extends AbstractForm {
    public static final String FORM_PAGE = "HexInfoViewer";

    HashMap infCosts = new HashMap();
    HashMap cavCosts = new HashMap();

    JTextField hexNo;

    public HexInfoViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        GridBagLayoutBuilder lb = new GridBagLayoutBuilder();
        JLabel l;
        lb.append(l = new JLabel("Hex No :"), 2, 1);
        lb.append(hexNo = new JTextField(), 3, 1);
        hexNo.setBorder(null);
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
            Hex h = (Hex)object;
            String hexNoStr = String.valueOf(h.getColumn());
            if (h.getColumn() < 10) {
                hexNoStr = "0" + hexNoStr;
            }
            if (h.getRow() < 10) {
                hexNoStr = hexNoStr + "0";
            }
            hexNoStr += String.valueOf(h.getRow());
            hexNo.setText(hexNoStr);

            int startHexNo = h.getColumn() * 100 + h.getRow();
            if (startHexNo > 0) {
                for (MovementDirection md : MovementDirection.values()) {
                    int cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), false, true);
                    JTextField tf = (JTextField)infCosts.get(md);
                    String costStr = (cost > 0 ? String.valueOf(cost) : "-");
                    tf.setText(String.valueOf(costStr));

                    cost = MovementUtils.calculateMovementCostForArmy(startHexNo, md.getDir(), true, true);
                    tf = (JTextField)cavCosts.get(md);
                    costStr = (cost > 0 ? String.valueOf(cost) : "-");
                    tf.setText(String.valueOf(costStr));
                }
                return;
            }
            for (JTextField tf : (Collection<JTextField>)infCosts.values()) {
                tf.setText("");
            }
            for (JTextField tf : (Collection<JTextField>)cavCosts.values()) {
                tf.setText("");
            }
        }
    }
}
