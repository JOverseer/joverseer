package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import org.joverseer.JOApplication;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.PLaFHelper;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;

public class MapView extends AbstractView implements ApplicationListener {

	MapPanel mapPanel;
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
		this.scp.setPreferredSize(new Dimension(800, 500));
		MapMetadata mm = MapMetadata.instance();
		this.scp.getVerticalScrollBar().setUnitIncrement(mm.getGridCellHeight() * mm.getHexSize() * 2);
		this.scp.getHorizontalScrollBar().setUnitIncrement(mm.getGridCellWidth() * mm.getHexSize() * 2);
//		this.scp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// Add introduction image to explain to new players what to do
		ImageSource is = JOApplication.getImageSource();
		Image mapIntro;
		if(!PLaFHelper.isDarkMode()) mapIntro = is.getImage("map.intro");
		else mapIntro = is.getImage("map.dark.intro");
		if (mapIntro != null) {
			this.introLabel = new JLabel(new ImageIcon(mapIntro));
			this.mapPanel.add(this.introLabel);
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
			this.mapPanel.remove(this.introLabel);
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
			
			this.mapPanel.invalidateMapItems();
			this.mapPanel.updateUI();
			break;
		case OrderChangedEvent:
			this.mapPanel.invalidateMapItems();
			this.mapPanel.updateUI();
			break;
		case MapMetadataChangedEvent:
			MapMetadata mm = MapMetadata.instance();
			this.mapPanel.setPreferredSize(getRealMapDimension(mm));
			//this.mapPanel.setPreferredSize(new Dimension(w * (mm.getHexSize()) * (mm.getMaxMapColumn() + 1), mm.getGridCellHeight() * mm.getHexSize() * mm.getMaxMapRow()));
			this.scp.getVerticalScrollBar().setUnitIncrement(mm.getGridCellHeight() * mm.getHexSize() * 2);
			this.scp.getHorizontalScrollBar().setUnitIncrement(mm.getGridCellWidth() * mm.getHexSize() * 2);
			this.mapPanel.invalidateAndReset();
			this.mapPanel.updateUI();
			this.scp.updateUI();
			
			this.mapPanel.revalidate();
			this.scp.getVerticalScrollBar().setValue(this.scp.getVerticalScrollBar().getValue()-1);			

			break;
		}
	}
	
	private Dimension getRealMapDimension(MapMetadata mm) {
		Point p =this.mapPanel.getHexLocation((mm.getMaxMapColumn()), mm.getMaxMapRow());
	    double[] scales = this.mapPanel.getScaleTransformation();

	    int logicalX = (int) (p.x / scales[0]) + Math.round(mm.getGridCellWidth() * mm.getHexSize() * 3);
	    int logicalY = (int) (p.y / scales[1]) + Math.round((mm.getGridCellHeight() * mm.getHexSize()) * 2);
		//Point p = this.mapPanel.getHexFromPoint(p1);
		
		return new Dimension(logicalX, logicalY);
	}

}
