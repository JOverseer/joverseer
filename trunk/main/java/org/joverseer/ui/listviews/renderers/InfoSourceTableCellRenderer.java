package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JTable;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.HostageInfoSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;
import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.PdfTurnInfoSource;
import org.joverseer.support.infoSources.RumorInfoSource;
import org.joverseer.support.infoSources.UserInfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.ui.support.Messages;
import org.springframework.richclient.table.BeanTableModel;

/**
 * Cell renderer for the InfoSource class
 * @author Marios Skounakis
 */
public class InfoSourceTableCellRenderer extends AllegianceColorCellRenderer {

    public InfoSourceTableCellRenderer(BeanTableModel tableModel) {
        super(tableModel);
    }

    private static String getNationStr(int nationNo) {
        String nationName = String.valueOf(nationNo);
        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.showNationAs"); //$NON-NLS-1$
        if (pval == null || pval.equals("name")) { //$NON-NLS-1$
            Nation n = GameHolder.instance().getGame().getMetadata().getNationByNum(nationNo);
            if (n != null) {
                nationName = n.getShortName();
            }
        }
        return nationName;
    }
    
    public static String getInfoSourceDescription(InfoSource value) {
        String strValue = ""; //$NON-NLS-1$
        if (InfoSource.class.isInstance(value)) {
            if (DerivedFromSpellInfoSource.class.isInstance(value)) {
                DerivedFromSpellInfoSource sis = (DerivedFromSpellInfoSource)value;
                strValue = sis.getSpell() + " - " + sis.getCasterName(); //$NON-NLS-1$
                for (InfoSource is : sis.getOtherInfoSources()) {
                    strValue += ", " + getInfoSourceDescription(is); //$NON-NLS-1$
                }
            } else if (DerivedFromArmyInfoSource.class.isInstance(value)) {
                strValue = Messages.getString("InfoSourceTableCellRenderer.Commander"); //$NON-NLS-1$
            } else if (DoubleAgentInfoSource.class.isInstance(value)) {
                DoubleAgentInfoSource dais = (DoubleAgentInfoSource)value;
                strValue = Messages.getString("InfoSourceTableCellRenderer.DoubleAgent", new String[] {getNationStr(dais.getNationNo())});  //$NON-NLS-1$
            } else if (HostageInfoSource.class.isInstance(value)) {
                HostageInfoSource dais = (HostageInfoSource)value;
                strValue = Messages.getString("InfoSourceTableCellRenderer.Hostage", new String[] {getNationStr(dais.getNationNo())});  //$NON-NLS-1$
            } else if (MetadataSource.class.isInstance(value)) {
                strValue = Messages.getString("InfoSourceTableCellRenderer.StartingInfo"); //$NON-NLS-1$
            } else if (PdfTurnInfoSource.class.isInstance(value)) {
                PdfTurnInfoSource pis = (PdfTurnInfoSource)value;
                strValue = Messages.getString("InfoSourceTableCellRenderer.PDF", new String[] {getNationStr(pis.getNationNo())});
            } else if (XmlTurnInfoSource.class.isInstance(value)) {
                XmlTurnInfoSource xis = (XmlTurnInfoSource)value;
                strValue = Messages.getString("InfoSourceTableCellRenderer.XML", new String[] {getNationStr(xis.getNationNo())});
            } else if (DerivedFromTitleInfoSource.class.isInstance(value)) {
            	DerivedFromTitleInfoSource dtis = (DerivedFromTitleInfoSource)value;
            	strValue = dtis.getTitle();
            } else if (RumorInfoSource.class.isInstance(value)) {
            	strValue = Messages.getString("InfoSourceTableCellRenderer.Rumour"); //$NON-NLS-1$
            } else if (RumorActionInfoSource.class.isInstance(value)) {
            	RumorActionInfoSource aais = (RumorActionInfoSource)value;
            	strValue = aais.getReports();
            } else if (UserInfoSource.class.isInstance(value)) {
            	UserInfoSource uis = (UserInfoSource)value;
            	strValue = Messages.getString("InfoSourceTableCellRenderer.UserT", new Object[] { uis.getTurnNo()});; //$NON-NLS-1$
            }
        }
        return strValue;
    }
    
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String strValue = getInfoSourceDescription((InfoSource)value);
        return super.getTableCellRendererComponent(table, strValue, isSelected, hasFocus, row, column);
    }
    
}
