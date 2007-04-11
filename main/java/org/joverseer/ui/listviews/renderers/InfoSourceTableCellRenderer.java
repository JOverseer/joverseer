package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JTable;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.PdfTurnInfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.springframework.richclient.table.BeanTableModel;


public class InfoSourceTableCellRenderer extends AllegianceColorCellRenderer {

    public InfoSourceTableCellRenderer(BeanTableModel tableModel) {
        super(tableModel);
    }

    private String getNationStr(int nationNo) {
        String nationName = String.valueOf(nationNo);
        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.showNationAs");
        if (pval == null || pval.equals("name")) {
            Nation n = GameHolder.instance().getGame().getMetadata().getNationByNum(nationNo);
            if (n != null) {
                nationName = n.getShortName();
            }
        }
        return nationName;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String strValue = "";
        if (InfoSource.class.isInstance(value)) {
            if (DerivedFromSpellInfoSource.class.isInstance(value)) {
                DerivedFromSpellInfoSource sis = (DerivedFromSpellInfoSource)value;
                strValue = sis.getSpell() + " - " + sis.getCasterName();
            } else if (DerivedFromArmyInfoSource.class.isInstance(value)) {
                strValue = "Army commander";
            } else if (DoubleAgentInfoSource.class.isInstance(value)) {
                DoubleAgentInfoSource dais = (DoubleAgentInfoSource)value;
                strValue = "Double agent for " + getNationStr(dais.getNationNo()); 
            } else if (MetadataSource.class.isInstance(value)) {
                strValue = "Starting info";
            } else if (PdfTurnInfoSource.class.isInstance(value)) {
                PdfTurnInfoSource pis = (PdfTurnInfoSource)value;
                strValue = "Pdf (" + getNationStr(pis.getNationNo()) + ")";
            } else if (XmlTurnInfoSource.class.isInstance(value)) {
                XmlTurnInfoSource xis = (XmlTurnInfoSource)value;
                strValue = "Xml (" + getNationStr(xis.getNationNo()) + ")";
            }
        }
        return super.getTableCellRendererComponent(table, strValue, isSelected, hasFocus, row, column);
    }
    
}
