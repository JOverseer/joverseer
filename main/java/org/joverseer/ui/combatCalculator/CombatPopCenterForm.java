package org.joverseer.ui.combatCalculator;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class CombatPopCenterForm extends AbstractForm {
    public static String FORM_ID = "combatPopCenterForm";

    public CombatPopCenterForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    protected JComponent createFormControl() {
        SwingBindingFactory sbf = (SwingBindingFactory)getBindingFactory();
        
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("Name :"), "colspec=left:120px");
        tlb.gapCol();
        tlb.cell(sbf.createBoundTextField("name").getControl(), "align=left");
        tlb.relatedGapRow();
        
        ArrayList nations = new ArrayList();
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            nations.addAll(gm.getNations());
        }
            
        ComboBoxBinding b = (ComboBoxBinding)sbf.createBoundComboBox("nation", new ValueHolder(nations), "name");
        b.setComparator(new PropertyComparator("number", true, true));
        
        tlb.cell(new JLabel("Nation :"), "align=left");
        tlb.gapCol();
        tlb.cell(b.getControl(), "align=left");
        tlb.relatedGapRow();
        
        tlb.cell(new JLabel("Loyalty :"), "colspec=left:120px");
        tlb.gapCol();
        tlb.cell(sbf.createBoundTextField("loyalty").getControl(), "align=left");
        tlb.relatedGapRow();
        
        b = (ComboBoxBinding)sbf.createBoundComboBox("size", new ValueHolder(PopulationCenterSizeEnum.values()));
        b.setComparator(new PropertyComparator("number", true, true));
        
        tlb.cell(new JLabel("Size :"), "align=left");
        tlb.gapCol();
        tlb.cell(b.getControl(), "align=left");
        tlb.relatedGapRow();

        b = (ComboBoxBinding)sbf.createBoundComboBox("fort", new ValueHolder(FortificationSizeEnum.values()));
        b.setComparator(new PropertyComparator("number", true, true));
        
        tlb.cell(new JLabel("Fort :"), "align=left");
        tlb.gapCol();
        tlb.cell(b.getControl(), "align=left");
        tlb.relatedGapRow();

        return tlb.getPanel();
    }
}