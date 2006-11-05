package org.joverseer.ui.viewers;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.image.ImageSource;
import org.springframework.binding.form.FormModel;
import org.joverseer.domain.Character;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Artifact;
import org.joverseer.ui.listviews.ArtifactTableModel;
import org.joverseer.ui.listviews.ItemTableModel;
import org.joverseer.ui.support.TableUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 19 Σεπ 2006
 * Time: 11:45:02 μμ
 * To change this template use File | Settings | File Templates.
 */
public class CharacterViewer extends AbstractForm implements ActionListener {
    public static final String FORM_PAGE = "CharacterViewer";

    JTextField statsTextBox;
    JTextField nationTextBox;

    JTable artifactsTable;
    JTable spellsTable;

    JToggleButton btnArtifacts;
    JToggleButton btnSpells;

    public CharacterViewer(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    public void setFormObject(Object object) {
        if (object != getFormObject()) {
            btnArtifacts.getModel().setSelected(false);
            btnSpells.getModel().setSelected(false);
        }
        super.setFormObject(object);
        if (statsTextBox != null) {
            Character c = (Character)object;
            String txt = "";
            txt += getStatText("C", c.getCommand(), c.getCommandTotal());
            txt += getStatText("A", c.getAgent(), c.getAgentTotal());
            txt += getStatText("E", c.getEmmisary(), c.getEmmisaryTotal());
            txt += getStatText("M", c.getMage(), c.getMageTotal());
            txt += getStatText("S", c.getStealth(), c.getStealthTotal());
            txt += getStatText("Cr", c.getChallenge(), c.getChallenge());
            txt += getStatText("H", c.getHealth(), c.getHealth());
            statsTextBox.setText(txt);
            statsTextBox.setCaretPosition(0);

            GameMetadata gm = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
            nationTextBox.setText(gm.getNationByNum(c.getNationNo()).getShortName());

            ArrayList artis = new ArrayList();
            btnArtifacts.setEnabled(c.getArtifacts().size() > 0);
            if (btnArtifacts.getModel().isSelected()) {
                for (Integer no : c.getArtifacts()) {
                    Artifact arti = (Artifact)gm.getArtifacts().findFirstByProperty("no", no);
                    if (arti == null) {
                        arti = new Artifact();
                        arti.setNo(no);
                        arti.setName("---");
                    }
                    artis.add(arti);
                }
            }
            ((BeanTableModel)artifactsTable.getModel()).setRows(artis);
            artifactsTable.setPreferredSize(new Dimension(artifactsTable.getWidth(), 16 * artis.size()));

            ArrayList spells = new ArrayList();
            btnSpells.setEnabled(c.getSpells().size() > 0);
            if (btnSpells.getModel().isSelected()) {
                spells.addAll(c.getSpells());
            }
            ((BeanTableModel)spellsTable.getModel()).setRows(spells);
            spellsTable.setPreferredSize(new Dimension(spellsTable.getWidth(), 16 * spells.size()));
        }
        
    }

    private String getStatText(String prefix, int skill, int skillTotal) {
        if (skillTotal == 0) return "";
        return prefix + skill + (skillTotal != skill ? "(" + skillTotal + ")" : "") + " ";
    }



    protected JComponent createFormControl() {
        getFormModel().setValidating(false);
        BindingFactory bf = getBindingFactory();
        GridBagLayoutBuilder glb = new GridBagLayoutBuilder();
        glb.setDefaultInsets(new Insets(0, 0, 0, 5));

        JComponent c;

        glb.append(c = new JTextField());
        c.setBorder(null);
        c.setFont(new Font(c.getFont().getName(), Font.BOLD, c.getFont().getSize()));
        c.setPreferredSize(new Dimension(100, 12));
        bf.bindControl(c, "name");
        glb.append(c = new JTextField());
        c.setBorder(null);
        nationTextBox = (JTextField)c;
        //bf.bindControl(c, "nationNo");
        c.setPreferredSize(new Dimension(50, 12));

        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");

        btnArtifacts = new JToggleButton();
        Icon ico = new ImageIcon(imgSource.getImage("artifact.image"));
        btnArtifacts.setIcon(ico);
        btnArtifacts.setPreferredSize(new Dimension(16,16));
        btnArtifacts.addActionListener(this);
        glb.append(btnArtifacts);

        btnSpells = new JToggleButton();
        ico = new ImageIcon(imgSource.getImage("spell.image"));
        btnSpells.setIcon(ico);
        btnSpells .setPreferredSize(new Dimension(16,16));
        btnSpells .addActionListener(this);
        glb.append(btnSpells);

        glb.nextLine();

        glb.append(statsTextBox = new JTextField(), 4, 1);
        statsTextBox.setBorder(null);
        statsTextBox.setPreferredSize(new Dimension(100, 12));
        glb.nextLine();

        glb.append(artifactsTable = new JTable(), 4, 1);
        artifactsTable.setPreferredSize(new Dimension(150, 20));
        ArtifactTableModel tableModel =
            new ArtifactTableModel(this.getMessageSource()) {
                protected String[] createColumnPropertyNames() {
                    return new String[]{"no", "name"};
                }

                protected Class[] createColumnClasses() {
                    return new Class[]{String.class, String.class};
                }
            };
        tableModel.setRowNumbers(false);
        artifactsTable.setModel(tableModel);
        // todo think about this
        TableUtils.setTableColumnWidths(artifactsTable, new int[]{30, 120});
        artifactsTable.setBorder(null);

        glb.nextLine();

        glb.append(spellsTable = new JTable(), 2, 1);
        spellsTable.setPreferredSize(new Dimension(150, 12));
        ItemTableModel spellModel = new ItemTableModel(SpellProficiency.class, this.getMessageSource()) {
            protected String[] createColumnPropertyNames() {
                return new String[]{"spellId", "name", "proficiency"};
            }

            protected Class[] createColumnClasses() {
                return new Class[]{Integer.class, String.class, String.class};
            }
        };
        spellModel.setRowNumbers(false);
        spellsTable.setModel(spellModel);
        TableUtils.setTableColumnWidths(spellsTable, new int[]{30, 90, 30});
        spellsTable.setBorder(null);
        glb.nextLine();

        JPanel panel = glb.getPanel();
        panel.setBackground(Color.white);
        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        setFormObject(getFormObject());
    }
}

