package org.joverseer.ui.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.viewers.PopulationCenterViewer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class MapView extends AbstractView implements ApplicationListener {

	MapPanel mapPanel;
	PopulationCenterViewer pcViewer;
	JScrollPane scp;
	JLabel introLabel;

	//injected dependencies
	GameHolder gameHolder;

	public GameHolder getGameHolder() {
		return this.gameHolder;
	}

	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}

	/**
	 * Create the actual UI control for this view. It will be placed into the
	 * window according to the layout of the page holding this view.
	 */
	@Override
	protected JComponent createControl() {
		// In this view, we're just going to use standard Swing to place a
		// few controls.
		this.scp = new JScrollPane(this.mapPanel = new MapPanel(this.gameHolder));
		this.mapPanel.setFocusable(true);
		this.mapPanel.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_UP) {
					MapView.this.scp.getVerticalScrollBar().setValue(MapView.this.scp.getVerticalScrollBar().getValue() - MapView.this.scp.getVerticalScrollBar().getBlockIncrement());
				}
				if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
					MapView.this.scp.getVerticalScrollBar().setValue(MapView.this.scp.getVerticalScrollBar().getValue() + MapView.this.scp.getVerticalScrollBar().getBlockIncrement());
				}
				if (arg0.getKeyCode() == KeyEvent.VK_LEFT) {
					MapView.this.scp.getHorizontalScrollBar().setValue(MapView.this.scp.getHorizontalScrollBar().getValue() - MapView.this.scp.getHorizontalScrollBar().getBlockIncrement());
				}
				if (arg0.getKeyCode() == KeyEvent.VK_RIGHT) {
					MapView.this.scp.getHorizontalScrollBar().setValue(MapView.this.scp.getHorizontalScrollBar().getValue() + MapView.this.scp.getHorizontalScrollBar().getBlockIncrement());
				}

			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		this.mapPanel.setPreferredSize(new Dimension(1000, 2500));
		this.mapPanel.setBackground(Color.white);
		this.scp.setPreferredSize(new Dimension(800, 500));
		MapMetadata mm = MapMetadata.instance();
		this.scp.getVerticalScrollBar().setUnitIncrement(mm.getGridCellHeight() * mm.getHexSize() * 2);
		this.scp.getHorizontalScrollBar().setUnitIncrement(mm.getGridCellWidth() * mm.getHexSize() * 2);
		// Add introduction image to explain to new players what to do
		BufferedImage myImg;
		try {
			myImg = ImageIO.read(new File("resources/images/JOverseerIntro.png"));
			this.introLabel = new JLabel(new ImageIcon(myImg));
			
			this.mapPanel.add(this.introLabel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel(Messages.getString("MapView.NewGame"))); //$NON-NLS-1$
		return this.scp;
	}

	public MapPanel getMapPanel() {
		return this.mapPanel;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch(e.getType()) {
		case GameChangedEvent:
		case SelectedTurnChangedEvent:
		case RefreshTurnMapItems:
			this.mapPanel.invalidateAll();
			this.mapPanel.updateUI();
			break;
		case SelectedHexChangedEvent:
			if (e.getSender() != this.mapPanel) {
				Point p = (Point) e.getObject();
				this.mapPanel.setSelectedHex(p);
				Rectangle shr = this.mapPanel.getSelectedHexRectangle();
				this.mapPanel.updateUI();
				// expand shr
				Rectangle vr = this.mapPanel.getVisibleRect();

				vr.x = shr.x - (vr.width - shr.width) / 2;
				vr.y = shr.y - (vr.height - shr.height) / 2;
				this.mapPanel.scrollRectToVisible(vr);
			}
			break;
		case RefreshMapItems:
			// refreshAutoArmyRangeMapItems(null);
			this.mapPanel.remove(this.introLabel);
			this.mapPanel.invalidateMapItems();
			this.mapPanel.updateUI();
			break;
		case OrderChangedEvent:
			this.mapPanel.invalidateMapItems();
			this.mapPanel.updateUI();
			break;
		case MapMetadataChangedEvent:
			MapMetadata mm = MapMetadata.instance();
			this.mapPanel.setPreferredSize(new Dimension(mm.getGridCellWidth() * mm.getHexSize() * (mm.getMaxMapColumn() + 1), mm.getGridCellHeight() * mm.getHexSize() * mm.getMaxMapRow()));
			this.scp.getVerticalScrollBar().setUnitIncrement(mm.getGridCellHeight() * mm.getHexSize() * 2);
			this.scp.getHorizontalScrollBar().setUnitIncrement(mm.getGridCellWidth() * mm.getHexSize() * 2);
			this.mapPanel.invalidateAndReset();
			this.mapPanel.updateUI();
			this.scp.updateUI();
			break;
		}
	}

}
