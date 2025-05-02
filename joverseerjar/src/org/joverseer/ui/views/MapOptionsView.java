package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.ScalableAbstractView;
import org.joverseer.ui.command.EditPreferencesCommand;
import org.joverseer.ui.domain.mapOptions.MapOptionValuesEnum;
import org.joverseer.ui.domain.mapOptions.MapOptionsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.JLabelButton;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * View for changing the map options
 *
 * @author Marios Skounakis
 */
public class MapOptionsView extends ScalableAbstractView implements ApplicationListener {
	JComboBox cmbTurns;
	JComboBox cmbMaps;
	JComboBox zoom;
	JCheckBox drawOrders;
	JLabelButton moreMapOptions;
	/**
	 * Used internally to turn off propagating events when we know there are going to be a lot of them.
	 */
	boolean fireEvents = true;
	// injected dependencies
	GameHolder gameHolder;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}

	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	@Override
	protected JComponent createControl() {
		HashMap mapOptions = JOApplication.getMapOptions();
		mapOptions.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOn);
		TableLayoutBuilder lb = new TableLayoutBuilder();
		JLabel label;
		lb.cell(label = new JLabel(Messages.getString("MapOptionsView.TurnColon")), "colspec=left:130px"); //$NON-NLS-1$ //$NON-NLS-2$
		label.setPreferredSize(this.uiSizes.newDimension(100/16, this.uiSizes.getHeight4()));
		lb.cell(this.cmbTurns = new JComboBox(), "colspec=left:100px"); //$NON-NLS-1$
		lb.relatedGapRow();

		this.cmbTurns.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = MapOptionsView.this.cmbTurns.getSelectedItem();
				if (obj == null)
					return;
				int turnNo = (Integer) obj;

				Game g = MapOptionsView.this.gameHolder.getGame();
				if (g.getCurrentTurn() == turnNo)
					return;
				g.setCurrentTurn(turnNo);
				if (!MapOptionsView.this.fireEvents)
					return;

				JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, turnNo, this);
				if (MapPanel.instance().getSelectedHex() != null) {
					JOApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, MapPanel.instance().getSelectedHex(), this);
				}
			}
		});
		this.cmbTurns.setPreferredSize(this.uiSizes.newDimension(100/16, this.uiSizes.getHeight4()));
		lb.row();

		// lb.append(new JLabel("  "));
		lb.cell(label = new JLabel(Messages.getString("MapOptionsView.MapColon"))); //$NON-NLS-1$
		lb.cell(this.cmbMaps = new JComboBox(), "align=left"); //$NON-NLS-1$
		lb.relatedGapRow();
		this.cmbMaps.setPreferredSize(this.uiSizes.newDimension(100/16, this.uiSizes.getHeight4()));
		this.cmbMaps.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object obj = MapOptionsView.this.cmbMaps.getSelectedItem();
				if (obj == null)
					return;
				HashMap mapOptions1 = JOApplication.getMapOptions();
				Game g = MapOptionsView.this.gameHolder.getGame();
				String str = obj.toString();
				if (str.equals("Current")) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, null);
				} else if (str.equals(Messages.getString("MapOptionsView.10"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapDarkServants);
				} else if (str.equals(Messages.getString("MapOptionsView.11"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotDarkServants);
				} else if (str.equals(Messages.getString("MapOptionsView.12"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapFreePeople);
				} else if (str.equals(Messages.getString("MapOptionsView.13"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotFreePeople);
				} else if (str.equals(Messages.getString("MapOptionsView.14"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNeutrals);
				} else if (str.equals(Messages.getString("MapOptionsView.15"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNotNeutrals);
				} else if (str.equals(Messages.getString("MapOptionsView.16"))) { //$NON-NLS-1$
					mapOptions1.put(MapOptionsEnum.NationMap, MapOptionValuesEnum.NationMapNone);
				} else {
					int nationNo = g.getMetadata().getNationByName(str).getNumber();
					mapOptions1.put(MapOptionsEnum.NationMap, String.valueOf(nationNo));
				}
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;

				JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, turnNo, this);
			}

		});
		lb.row();

		// lb.append(new JLabel("  "));
		lb.cell(label = new JLabel(Messages.getString("MapOptionsView.DrawOrdersColon"))); //$NON-NLS-1$
		// label.setPreferredSize(new Dimension(100, 16));
		lb.cell(this.drawOrders = new JCheckBox(), "align=left"); //$NON-NLS-1$
		this.drawOrders.setSelected(mapOptions.get(MapOptionsEnum.DrawOrders) != null && mapOptions.get(MapOptionsEnum.DrawOrders) == MapOptionValuesEnum.DrawOrdersOn);
		lb.relatedGapRow();
		this.drawOrders.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				HashMap mapOptions1 = JOApplication.getMapOptions();
				if (MapOptionsView.this.drawOrders.getModel().isSelected()) {
					mapOptions1.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOn);
				} else {
					mapOptions1.put(MapOptionsEnum.DrawOrders, MapOptionValuesEnum.DrawOrdersOff);
				}
				Game g = MapOptionsView.this.gameHolder.getGame();
				if (!Game.isInitialized(g))
					return;
				int turnNo = g.getCurrentTurn();
				if (!MapOptionsView.this.fireEvents)
					return;
				JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, turnNo, this);

			}

		});
		lb.row();
		
		lb.cell(label = new JLabel(Messages.getString("MapOptionsView.ZoomLevelColon"))); //$NON-NLS-1$
		ZoomOption[] zoomOptions = new ZoomOption[] { new ZoomOption("s1", 7, 7), new ZoomOption("s2", 9, 9), new ZoomOption("s3", 11, 11), new ZoomOption("s4", 13, 13), new ZoomOption("1", 15, 15), new ZoomOption("2", 17, 17), new ZoomOption("3", 19, 19), new ZoomOption("4", 21, 21),//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
				new ZoomOption("5", 23, 23),new ZoomOption("6", 25, 25), new ZoomOption("7", 27, 27), new ZoomOption("8", 29, 29)
				}; 
//		ZoomOption[] zoomOptions = new ZoomOption[] { new ZoomOption("s1", 6, 6), new ZoomOption("s2", 7, 7), new ZoomOption("s3", 9, 9), new ZoomOption("s4", 11, 11), new ZoomOption("1", 13, 13), new ZoomOption("2", 15, 15), new ZoomOption("3", 17, 17), new ZoomOption("4", 19, 19),//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
//				new ZoomOption("5", 21, 21),new ZoomOption("6", 23, 23),
//				}; 
		lb.cell(this.zoom = new JComboBox(zoomOptions), "align=left"); //$NON-NLS-1$
		lb.relatedGapRow();
		this.zoom.setPreferredSize(this.uiSizes.newDimension(100/16, this.uiSizes.getHeight4()));
		this.zoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ZoomOption opt = (ZoomOption) MapOptionsView.this.zoom.getSelectedItem();

				if (opt == null)
					return;
				MapMetadata metadata = MapMetadata.instance();
				metadata.setGridCellHeight(opt.getHeight());
				metadata.setGridCellWidth(opt.getWidth());
				
				if (!MapOptionsView.this.fireEvents)
					return;

				JOApplication.publishEvent(LifecycleEventsEnum.MapMetadataChangedEvent, this, this);
			}
		});
		String temp = PreferenceRegistry.instance().getPreferenceValue("map.defaultZoom");
		int defaultZoomIndex;
		try {
			defaultZoomIndex = Integer.parseInt(temp);
	
		}catch (NumberFormatException e) {
			defaultZoomIndex= 6; // zoom level 3
		}
		this.zoom.setSelectedIndex(defaultZoomIndex);
		lb.row();

		lb.cell(this.moreMapOptions = new JLabelButton("<html><font color='#3a79d1'><u>More Map Options...</u></u><html>"));
		this.moreMapOptions.addActionListener(new ActionListener() {
			
	          @Override
				public void actionPerformed(ActionEvent e) {
	        	  EditPreferencesCommand editPreferenceCmd = new EditPreferencesCommand();
	        	  editPreferenceCmd.setGroup("Display.Map");
	        	  editPreferenceCmd.execute();
	          }
			});

		resetGame();
		JPanel panel = lb.getPanel();
		panel.setPreferredSize(new Dimension(130, 100));
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));

		return new JScrollPane(panel);
	}

	public void resetGame() {
		this.fireEvents = false;
		this.cmbTurns.removeAllItems();
		this.cmbMaps.removeAllItems();
		Game g = this.gameHolder.getGame(); //$NON-NLS-1$
		if (g != null) {
			ActionListener[] als = this.cmbTurns.getActionListeners();
			for (ActionListener al : als) {
				this.cmbTurns.removeActionListener(al);
			}
			for (int i = 0; i <= g.getMaxTurn(); i++) {
				if (g.getTurn(i) != null) {
					this.cmbTurns.addItem(g.getTurn(i).getTurnNo());
				}
			}
			for (ActionListener al : als) {
				this.cmbTurns.addActionListener(al);
			}
			this.cmbTurns.setSelectedItem(g.getCurrentTurn());
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.Current")); //$NON-NLS-1$
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.FreePeople")); //$NON-NLS-1$
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.DS")); //$NON-NLS-1$
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.Neutral")); //$NON-NLS-1$
			for (NationMapRange nmr : g.getMetadata().getNationMapRanges().getItems()) {
				Nation n = g.getMetadata().getNationByNum(nmr.getNationNo());
				this.cmbMaps.addItem(n.getName());
			}
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.None")); //$NON-NLS-1$
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.NotFP")); //$NON-NLS-1$
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.NotDS")); //$NON-NLS-1$
			this.cmbMaps.addItem(Messages.getString("MapOptionsView.NotNeutral")); //$NON-NLS-1$
		}
		this.fireEvents = true;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case GameChangedEvent:
			this.fireEvents = false;
			resetGame();
			this.fireEvents = true;
			break;
		case SelectedTurnChangedEvent:
			this.fireEvents = false;
			Game g = this.gameHolder.getGame(); //$NON-NLS-1$
			if (Game.isInitialized(g)) {
				if (!this.cmbTurns.getSelectedItem().equals(g.getCurrentTurn())) {
					this.cmbTurns.setSelectedItem(g.getCurrentTurn());
				}
			}
			this.fireEvents = true;
			break;
		case SetPalantirMapStyleEvent:
			this.fireEvents = false;

			this.zoom.setSelectedIndex(2);
			PreferenceRegistry.instance().setPreferenceValue("map.nationColors", "nation"); //$NON-NLS-1$ //$NON-NLS-2$
			PreferenceRegistry.instance().setPreferenceValue("map.showClimate", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			PreferenceRegistry.instance().setPreferenceValue("map.terrainGraphics", "simple"); //$NON-NLS-1$ //$NON-NLS-2$
			PreferenceRegistry.instance().setPreferenceValue("map.fogOfWarStyle", "xs"); //$NON-NLS-1$ //$NON-NLS-2$
			PreferenceRegistry.instance().setPreferenceValue("map.charsAndArmies", "simplified"); //$NON-NLS-1$ //$NON-NLS-2$
			PreferenceRegistry.instance().setPreferenceValue("map.deadCharacters", "no"); //$NON-NLS-1$ //$NON-NLS-2$
			PreferenceRegistry.instance().setPreferenceValue("map.showArmyType", "no"); //$NON-NLS-1$ //$NON-NLS-2$

			this.fireEvents = true;
			JOApplication.publishEvent(LifecycleEventsEnum.MapMetadataChangedEvent, this, this);
			break;
		case ZoomIncreaseEvent:
			if (this.zoom.getSelectedIndex() < this.zoom.getItemCount() - 1) {
				this.zoom.setSelectedIndex(this.zoom.getSelectedIndex() + 1);
			}
			break;
		case ZoomDecreaseEvent:
			if (this.zoom.getSelectedIndex() > 0) {
				this.zoom.setSelectedIndex(this.zoom.getSelectedIndex() - 1);
			}
			break;
		}
	}

	class ZoomOption {
		String description;
		int width;
		int height;

		public ZoomOption(String description, int width, int height) {
			super();
			this.description = description;
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString() {
			return this.description;
		}

		public String getDescription() {
			return this.description;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

	}
}
