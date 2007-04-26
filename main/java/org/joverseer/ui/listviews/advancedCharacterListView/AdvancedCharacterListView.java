package org.joverseer.ui.listviews.advancedCharacterListView;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.joverseer.ui.domain.ArtifactWrapper;
import org.joverseer.ui.listviews.BaseItemListView;
import org.joverseer.ui.support.controls.JLabelButton;
import org.joverseer.ui.support.controls.PopupMenuActionListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;

public class AdvancedCharacterListView extends BaseItemListView {

	public AdvancedCharacterListView() {
		super(AdvancedCharacterTableModel.class);
	}

	protected int[] columnWidths() {
		return new int[]{96, 48, 48, 
                                    48, 48, 48, 
                                    48, 48, 48,
                                    48,
                                    32, 32, 32, 32, 32, 32, 
                                    96, 48};
	}

	protected void setItems() {
		tableModel.setRows(AdvancedCharacterWrapper.getWrappers());
	}

	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		table.setDefaultRenderer(CharacterAttributeWrapper.class, new CharacterAttributeWrapperTableCellRenderer(tableModel));
                table.setDefaultRenderer(ArtifactWrapper.class, new ArtifactWrapperTableCellRenderer(tableModel));
		return c;
	}
	
	
        protected JComponent[] getButtons() {
	    ArrayList<JComponent> comps = new ArrayList<JComponent>();
            comps.addAll(Arrays.asList(super.getButtons()));
            JLabelButton popupMenu = new JLabelButton();
            ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
            Icon ico = new ImageIcon(imgSource.getImage("menu.icon"));
            popupMenu.setIcon(ico);
            popupMenu.addActionListener(new PopupMenuActionListener() {
                public JPopupMenu getPopupMenu() {
                    //TODO continue here
                    return new JPopupMenu();
                }
            });
            comps.add(popupMenu);
            return comps.toArray(new JComponent[]{});
        }

}
