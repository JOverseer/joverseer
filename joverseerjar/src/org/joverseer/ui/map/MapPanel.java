package org.joverseer.ui.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JViewport;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.event.MouseInputListener;

import org.apache.log4j.Logger;
import org.joverseer.JOApplication;
import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.Note;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.ShowCharacterFastStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterLongStrideRangeCommand;
import org.joverseer.ui.command.ShowCharacterMovementRangeCommand;
import org.joverseer.ui.command.ShowCharacterPathMasteryRangeCommand;
import org.joverseer.ui.command.range.ShowFedCavalryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowFedInfantryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowFedNavyCoastalRangeCommand;
import org.joverseer.ui.command.range.ShowFedNavyOpenSeasRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedCavalryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedInfantryArmyRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedNavyCoastalRangeCommand;
import org.joverseer.ui.command.range.ShowUnfedNavyOpenSeasRangeCommand;
import org.joverseer.ui.command.CreateCombatForHexCommand;
import org.joverseer.ui.domain.mapEditor.MapEditorOptionsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.map.renderers.Renderer;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dataFlavors.ArtifactDataFlavor;
import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;
import org.joverseer.ui.support.transferHandlers.HexNoTransferHandler;
import org.joverseer.ui.views.MapEditorView;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.progress.BusyIndicator;

/**
 * The basic control for displaying the map. It derives itself from a JPanel and
 * implements custom painting.
 *
 * Painting is done in a layered way in order to avoid having to always redraw
 * everything, which is slow The three layers are: 1. the map, which shows the
 * terrain and other static stuff for the game type 2. the map base items, which
 * shows fairly non-volatile objects for the current turn such as pop centers,
 * chars, armies, etc 3. the map items, which shows volatile objects such as
 * army ranges and orders Each layer is stored in a buffered image so that if
 * one of the higher layers is changed, the buffered image is used to redraw the
 * lower layer.
 *
 * The control uses renderers to paint the various layers.
 *
 * The control also implements mouse listener and mouse motion listener
 * interfaces to provide: - hex selection upon mouse click - scrolling upon
 * ctrl+mouse drag - drag & drop capability for dragging hex number from the map
 * to other controls
 *
 * TODO: make a real map panel interface for all the bits of code grabbing the currently selected hex.
 *
 * @author Marios Skounakis
 */
public class MapPanel extends JPanel implements MouseInputListener, MouseWheelListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	protected javax.swing.event.EventListenerList listenerList1 = new javax.swing.event.EventListenerList();

	private static Logger logger = Logger.getLogger(MapPanel.class);
	private static MapPanel _instance = null;

	Polygon hex = new Polygon();
	int[] xPoints = new int[6];
	int[] yPoints = new int[6];
	Point location = new Point();
	boolean outOfMemoryErrorThrown = false;

	BufferedImage map = null;
	BufferedImage mapBaseItems = null;
	BufferedImage mapItems = null;
	BufferedImage mapBack = null;
	BufferedImage mapBaseItemsBack = null;
	BufferedImage mapItemsBack = null;

	MapMetadata metadata;

	Point selectedHex = null;

	private Game game = null;

	int xDiff, yDiff;

	boolean isDragging;

	java.awt.Container c;
	Color  backgroundColour = Color.white;
	boolean saveMap = false;

	//dependencies
	GameHolder gameHolder;

	public MapPanel(GameHolder gameHolder) {
		addMouseListener(this);
		addMouseWheelListener(this);
		addMouseMotionListener(this);
		this.setTransferHandler(new HexNoTransferHandler("hex")); //$NON-NLS-1$
		this.setDropTarget(new DropTarget(this, new MapPanelDropTargetAdapter()));
		_instance = this;
		this.gameHolder = gameHolder;
		this.backgroundColour = UIManager.getColor("Panel.background");
		
	}

	public static MapPanel instance() {
		return _instance;
	}

	private void setHexLocation(int x, int y) {
		this.location = getHexLocation(x, y);
	}

	protected MapMetadata getMetadata() {
		if (this.metadata == null) {
			this.metadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata"); //$NON-NLS-1$
		}
		return this.metadata;
	}

	public Point getHexCenter(int hexNo) {
		Point p = getHexLocation(hexNo);
		MapMetadata metadata1 = getMetadata();
		p.translate(metadata1.getHexSize() * metadata1.getGridCellWidth() / 2, metadata1.getHexSize() * metadata1.getGridCellHeight() / 2);
		return p;
	}

	public Point getHexLocation(int hexNo) {
		return getHexLocation(hexNo / 100, hexNo % 100);
	}

	public Point getHexLocation(int x, int y) {
		Point location1 = new Point();
		MapMetadata metadata1 = getMetadata();
		x = x - metadata1.getMinMapColumn();
		y = y - metadata1.getMinMapRow();
		if ((y + metadata1.getMinMapRow() + 1) % 2 == 0) {
			location1.setLocation(metadata1.getHexSize() * metadata1.getGridCellWidth() * x, metadata1.getHexSize() * 3 / 4 * y * metadata1.getGridCellHeight());
		} else {
			location1.setLocation((x + .5) * metadata1.getHexSize() * metadata1.getGridCellWidth(), metadata1.getHexSize() * 3 / 4 * y * metadata1.getGridCellHeight());
		}
		return location1;
	}

	private void setPoints(int x, int y) {
		MapMetadata metadata1 = getMetadata();
		this.xPoints[0] = metadata1.getHexSize() / 2;
		this.xPoints[1] = metadata1.getHexSize();
		this.xPoints[2] = metadata1.getHexSize();
		this.xPoints[3] = metadata1.getHexSize() / 2;
		this.xPoints[4] = 0;
		this.xPoints[5] = 0;

		this.yPoints[0] = 0;
		this.yPoints[1] = metadata1.getHexSize() / 4;
		this.yPoints[2] = metadata1.getHexSize() * 3 / 4;
		this.yPoints[3] = metadata1.getHexSize();
		this.yPoints[4] = metadata1.getHexSize() * 3 / 4;
		this.yPoints[5] = metadata1.getHexSize() / 4;

		setHexLocation(x, y);

		for (int i = 0; i < 6; i++) {
			this.xPoints[i] = this.xPoints[i] * metadata1.getGridCellWidth() + this.location.x;
			this.yPoints[i] = this.yPoints[i] * metadata1.getGridCellHeight() + this.location.y;
		}

	}

	/**
	 * Creates the basic map (the background layer with the hexes) The hexes are
	 * retrieved from the Game Metadata and rendered with all the valid
	 * available renderers found in the Map Metadata
	 *
	 */
	private void createMap() {
		MapMetadata metadata1;
		try {
			metadata1 = getMetadata();
		} catch (Exception exc) {
			// application is not ready
			return;
		}

		Game game1 = getGame();
		if (!Game.isInitialized(game1))
			return;

		GameMetadata gm = game1.getMetadata();

		if (this.mapBack == null) {
			Dimension d = getMapDimension();
			this.mapBack = new BufferedImage((int) d.getWidth(), (int) d.getHeight(), BufferedImage.TYPE_INT_ARGB);
			//this.setPreferredSize(d);
			//this.setSize(d);
		}
		this.map = this.mapBack;

		Graphics2D g = this.map.createGraphics();
		g.setColor(this.backgroundColour);
		g.fillRect(0, 0, this.map.getWidth(), this.map.getHeight());

		// try {
		// Resource r =
		// Application.instance().getApplicationContext().getResource("classpath:images/map/map.png");
		// BufferedImage mm = ImageIO.read(r.getInputStream());
		// g.drawImage(mm, 0, 0, null);
		// }
		// catch (Exception exc) {
		// exc.printStackTrace();
		// }

		refreshRendersConfig();

		for (Hex h : gm.getHexes()) {
			setHexLocation(h.getColumn(), h.getRow());
			for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
				if (r.appliesTo(h)) {
					r.render(h, g, this.location.x, this.location.y);
				}
			}
		}
//		Container<Hex> hexOverrides = gm.getHexOverrides(game1.getCurrentTurn());
//		for (Hex h : hexOverrides) {
//			setHexLocation(h.getColumn(), h.getRow());
//			for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
//				if (r.appliesTo(h)) {
//					r.render(h, g, this.location.x, this.location.y);
//				}
//			}
//		}

		if (this.saveMap) {
			File outputFile = new File("map.png"); //$NON-NLS-1$
			try {
				ImageIO.write(this.map, "PNG", outputFile); //$NON-NLS-1$
			} catch (Exception exc) {
			}
			;
		}
	}

	public Dimension getMapDimension() {
		int width = (int) ((this.metadata.getMaxMapColumn() + 2d - this.metadata.getMinMapColumn() - .5) * this.metadata.getHexSize() * this.metadata.getGridCellWidth());
		int height = (int) (((this.metadata.getMaxMapRow() + 1 -this.metadata.getMinMapRow()) * .75d + .25) * this.metadata.getHexSize() * this.metadata.getGridCellHeight());
		return new Dimension(width, height);
	}

	private void createMapItems() {
		MapMetadata metadata1;
		try {
			metadata1 = getMetadata();
		} catch (Exception exc) {
			// application is not ready
			return;
		}
		Game game1 = getGame();
		if (!Game.isInitialized(game1))
			return;

		Graphics2D g = null;
		BusyIndicator.showAt(this);
		try {
			refreshRendersConfig();
			if (this.mapItemsBack == null) {
				Dimension d = getMapDimension();
				this.mapItemsBack = new BufferedImage((int) d.getWidth(), (int) d.getHeight(), BufferedImage.TYPE_INT_ARGB);
			}
			this.mapItems = this.mapItemsBack;

			g = this.mapItems.createGraphics();

			if (this.mapBaseItems == null) {
				createMapBaseItems();
			}
			g.drawImage(this.mapBaseItems, 0, 0, this);
		} catch (OutOfMemoryError e) {
			if (!this.outOfMemoryErrorThrown) {
				this.outOfMemoryErrorThrown = true;
				throw e;
			}
		}

		try {
			for (Character c1 : getGame().getTurn().getCharacters()) {
				for (Order o : c1.getOrders()) {
					for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
						if (r.appliesTo(o)) {
							setHexLocation(c1.getX(), c1.getY());
							try {
								r.render(o, g, this.location.x, this.location.y);
							} catch (Exception exc) {
									logger.error("Error rendering order " + o.getCharacter().getName() + " " + o.getOrderNo() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							}
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering orders " + exc.getMessage()); //$NON-NLS-1$
		}

		for (AbstractMapItem mi : game1.getTurn().getMapItems()) {
			for (Renderer r : metadata1.getRenderers()) {
				if (r.appliesTo(mi)) {
					r.render(mi, g, 0, 0);
				}
			}
		}

		try {
			for (Note n : getGame().getTurn().getNotes()) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(n)) {
						setHexLocation(n.getX(), n.getY());
						try {
							r.render(n, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering note " + n.getHexNo() + " " + n.getText() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering notes " + exc.getMessage()); //$NON-NLS-1$
		}
		BusyIndicator.clearAt(this);
	}

	/**
	 * Draws the items on the map, i.e. everything that is in front of the
	 * background (terrain) todo update with renderers
	 */
	private void createMapBaseItems() {
		MapMetadata metadata1;
		try {
			metadata1 = getMetadata();
		} catch (Exception exc) {
			// application is not ready
			return;
		}
		Game game1 = getGame();
		if (!Game.isInitialized(game1))
			return;

		MapTooltipHolder.instance().reset();

		BusyIndicator.showAt(this);
		refreshRendersConfig();
		if (this.mapBaseItemsBack == null) {
			Dimension d = getMapDimension();
			this.mapBaseItemsBack = new BufferedImage((int) d.getWidth(), (int) d.getHeight(), BufferedImage.TYPE_INT_ARGB);
		}
		this.mapBaseItems = this.mapBaseItemsBack;

		Graphics2D g = this.mapBaseItems.createGraphics();

		if (this.map == null) {
			createMap();
		}
		g.drawImage(this.map, 0, 0, this);

		try {
			for (PopulationCenter pc : getGame().getTurn().getPopulationCenters()) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(pc)) {
						setHexLocation(pc.getX(), pc.getY());
						try {
							r.render(pc, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error pc " + pc.getName() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}

					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering pop centers " + exc.getMessage()); //$NON-NLS-1$
		}

		try {
			for (Character c1 : getGame().getTurn().getCharacters()) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(c1)) {
						setHexLocation(c1.getX(), c1.getY());
						try {
							r.render(c1, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering character " + c1.getName() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering pop centers " + exc.getMessage()); //$NON-NLS-1$
		}

		try {
			for (Army army : getGame().getTurn().getArmies()) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(army)) {
						setHexLocation(army.getX(), army.getY());
						try {
							r.render(army, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering army " + army.getCommanderName() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering pop centers " + exc.getMessage()); //$NON-NLS-1$
		}

		try {
			for (NationMessage nm : getGame().getTurn().getNationMessages()) {
				if (nm.getX() <= 0 || nm.getY() <= 0)
					continue;
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(nm)) {
						setHexLocation(nm.getX(), nm.getY());
						try {
							r.render(nm, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering nation message " + nm.getMessage() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering nation " + exc.getMessage()); //$NON-NLS-1$
		}

		try {
			for (Artifact a : getGame().getTurn().getArtifacts()) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(a)) {
						setHexLocation(a.getX(), a.getY());
						try {
							r.render(a, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering artifact " + a.getNumber() + " " + a.getName() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering orders " + exc.getMessage()); //$NON-NLS-1$
		}

		try {
			for (Combat a : getGame().getTurn().getCombats()) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(a)) {
						setHexLocation(a.getX(), a.getY());
						try {
							r.render(a, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering combat " + a.getHexNo() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering combats " + exc.getMessage()); //$NON-NLS-1$
		}

		try {
			ArrayList<Encounter> encounters = new ArrayList<Encounter>();
			encounters.addAll(getGame().getTurn().getEncounters().getItems());
			encounters.addAll(getGame().getTurn().getChallenges().getItems());
			for (Encounter a : encounters) {
				for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
					if (r.appliesTo(a)) {
						setHexLocation(a.getX(), a.getY());
						try {
							r.render(a, g, this.location.x, this.location.y);
						} catch (Exception exc) {
							logger.error("Error rendering encounter " + a.getHexNo() + " " + exc.getMessage()); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		} catch (Exception exc) {
			logger.error("Error rendering encounters " + exc.getMessage()); //$NON-NLS-1$
		}

		BusyIndicator.clearAt(this);
	}

	@Override
	public void invalidate() {
	}

	public void invalidateAndReset() {
		this.metadata = null;
		this.map = null;
		this.mapBack = null;
		this.mapItemsBack = null;
		this.mapBaseItemsBack = null;
		invalidateAll();
	}

	public void invalidateAll() {
		this.metadata = null;
		this.map = null;
		this.mapBaseItems = null;
		this.mapItems = null;
		setGame(this.gameHolder.getGame());
	}

	public void invalidateMapItems() {
		this.metadata = null;
		this.mapItems = null;
	}

	public void refreshRendersConfig() {
		MapMetadata metadata1;
		metadata1 = getMetadata();
		if (metadata1 != null ) {
			for (org.joverseer.ui.map.renderers.Renderer r : metadata1.getRenderers()) {
				r.refreshConfig();
			}
		}

	}

	/**
	 * Basic painting todo update/cleanup
	 *
	 * @param g
	 */
	@Override
	protected void paintComponent(Graphics gcc) {
		super.paintComponent(gcc);
		Graphics2D g2d = (Graphics2D) gcc.create();
		
		double[] scales = this.getScaleTransformation();

	    // Inverse scale to neutralize system DPI scaling
	    g2d.scale(1 / scales[0], 1 / scales[1]);
	    
		if (isOpaque()) {
			g2d.setColor(this.backgroundColour);
			g2d.fillRect(0, 0, getWidth(), getHeight());
		}

		if (this.mapItems == null) {
			createMapItems();
		}
		if (this.mapItems == null) {
			// application is not ready
			return;
		}

		try {
			MapMetadata.instance();
		} catch (Exception exc) {
			// application is not ready
			return;
		}

		g2d.drawImage(this.mapItems, 0, 0, this);

		if (getSelectedHex() != null) {
			Stroke s = g2d.getStroke();
			Stroke r = new BasicStroke(2);
			setPoints(getSelectedHex().x, getSelectedHex().y);
			g2d.setColor(Color.YELLOW);
			g2d.setStroke(r);
			g2d.drawPolygon(this.xPoints, this.yPoints, 6);
			g2d.setStroke(s);
		}
		g2d.dispose();
		//this.setSize(this.getWidth()-300, this.getHeight()-100);
	}

	public Point getSelectedHex() {
		return this.selectedHex;
	}

	public void setSelectedHex(Point selectedHex) {
		if (selectedHex == null || (selectedHex.x == 0 && selectedHex.y == 0))
			return;
		if (this.selectedHex == null || selectedHex.x != this.selectedHex.x || selectedHex.y != this.selectedHex.y) {
			this.selectedHex = selectedHex;
			// fireMyEvent(new SelectedHexChangedEvent(this));
			JOApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, selectedHex, this);
		}
	}

	public Rectangle getSelectedHexRectangle() {
		setHexLocation(getSelectedHex().x, getSelectedHex().y);
		MapMetadata metadata1;
		try {
	        metadata1 = MapMetadata.instance();
	        int logicalX = this.location.x;
	        int logicalY = this.location.y;
	        int logicalW = metadata1.getHexSize() * metadata1.getGridCellWidth();
	        int logicalH = metadata1.getHexSize() * metadata1.getGridCellHeight();

	        // Apply DPI scaling
	        double[] scales = this.getScaleTransformation();
	        
	        return new Rectangle(
	            (int)(logicalX / scales[0]),
	            (int)(logicalY / scales[1]),
	            (int)(logicalW / scales[0]),
	            (int)(logicalH / scales[1])
	        );
		} catch (Exception exc) {
			// application is not ready
		}
		return new Rectangle(this.location.x, this.location.y, 1, 1);
	}

	/**
	 * Given a client point (eg from mouse click), it finds the containing hex
	 * and returns it as a point (i.e. point.x = hex.column, point.y = hex.row)
	 */
	public Point getHexFromPoint(Point p) {
	    // Correct mouse input from physical → logical space
		double[] scales = this.getScaleTransformation();

	    int logicalX = (int) (p.x * scales[0]);
	    int logicalY = (int) (p.y * scales[1]);

	    MapMetadata metadata1 = MapMetadata.instance();
	    int y = logicalY / (metadata1.getHexSize() * 3 / 4 * metadata1.getGridCellHeight());
	    int x;
	    if ((y + metadata1.getMinMapRow() + 1) % 2 == 0) {
	        x = logicalX / (metadata1.getHexSize() * metadata1.getGridCellWidth());
	    } else {
	        x = (logicalX - metadata1.getHexSize() / 2 * metadata1.getGridCellWidth()) / (metadata1.getHexSize() * metadata1.getGridCellWidth());
	    }
	    x += metadata1.getMinMapColumn();
	    y += metadata1.getMinMapRow();
	    if (x > metadata1.getMaxMapColumn()) x = metadata1.getMaxMapColumn();
	    if (y > metadata1.getMaxMapRow()) y = metadata1.getMaxMapRow();
	    return new Point(x, y);
	}

	/**
	 * Handles the mouse pressed event to change the current selected hex
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			this.xDiff = e.getX();
			this.yDiff = e.getY();
			if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
			} else {
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			Point h = getHexFromPoint(e.getPoint());
			int hexNo = h.x * 100 + h.y;
			Game g = this.gameHolder.getGame();
			if (g == null) {  // eg before game loaded.
				return;
			}
			Hex hex1 = g.getMetadata().getHex(hexNo);
			ArrayList<Object> commands = new ArrayList<Object>(Arrays.asList(new ShowCharacterMovementRangeCommand(hexNo, 12), new ShowCharacterLongStrideRangeCommand(hexNo), new ShowCharacterFastStrideRangeCommand(hexNo), new ShowCharacterPathMasteryRangeCommand(hexNo)));

			HexTerrainEnum terrain = hex1.getTerrain();
			if (terrain.isLand()) {
				commands.add("separator"); //$NON-NLS-1$
				commands.add(new ShowFedInfantryArmyRangeCommand(hexNo));
				commands.add(new ShowUnfedInfantryArmyRangeCommand(hexNo));
				commands.add(new ShowFedCavalryArmyRangeCommand(hexNo));
				commands.add(new ShowUnfedCavalryArmyRangeCommand(hexNo));
			}
			if (MovementUtils.calculateNavyRangeHexes(hexNo, false, true).size() > 0 || terrain.isOpenSea()) {
				commands.add("separator"); //$NON-NLS-1$
				commands.add(new ShowFedNavyCoastalRangeCommand(hexNo));
				commands.add(new ShowUnfedNavyCoastalRangeCommand(hexNo));
				commands.add(new ShowFedNavyOpenSeasRangeCommand(hexNo));
				commands.add(new ShowUnfedNavyOpenSeasRangeCommand(hexNo));
			}
			
			commands.add("separator");
			commands.add(new CreateCombatForHexCommand(hexNo, this.gameHolder));

			CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("MapPanelContextMenu", commands.toArray()); //$NON-NLS-1$
			JPopupMenu popup = group.createPopupMenu();
			popup.show(this, e.getPoint().x, e.getPoint().y);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Point p = e.getPoint();
			Point hex1 = getHexFromPoint(p);
			setSelectedHex(hex1);
			this.updateUI();
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			HashMap<MapEditorOptionsEnum, Object> mapEditorOptions = (HashMap<MapEditorOptionsEnum, Object>) Application.instance().getApplicationContext().getBean("mapEditorOptions"); //$NON-NLS-1$
			Boolean active = (Boolean) mapEditorOptions.get(MapEditorOptionsEnum.active);
			if (active == null || !active)
				return;
			Object brush = mapEditorOptions.get(MapEditorOptionsEnum.brush);
			if (HexTerrainEnum.class.isInstance(brush)) {
				Point p = e.getPoint();
				Point h = getHexFromPoint(p);
				int hexNo = h.x * 100 + h.y;
				Hex hex1 = this.gameHolder.getGame().getMetadata().getHex(hexNo);
				hex1.setTerrain((HexTerrainEnum) brush);
				MapEditorView.instance.log(""); //$NON-NLS-1$
				MapEditorView.instance.log(hexNo + " terrain " + brush.toString());
				invalidateAll();
				this.updateUI();
			} else if (HexSideElementEnum.class.isInstance(brush)) {
				Point p = e.getPoint();
				Point h = getHexFromPoint(p);
				int hexNo = h.x * 100 + h.y;
				Hex hex1 = this.gameHolder.getGame().getMetadata().getHex(hexNo);

				Point hp = getHexLocation(hexNo);
				int hexHalfWidth = this.metadata.getGridCellWidth() * this.metadata.getHexSize() / 2;
				int hexOneThirdHeight = this.metadata.getGridCellHeight() * this.metadata.getHexSize() / 3;

				HexSideEnum hexSide = null;
				HexSideEnum otherHexSide = null;
				int otherHexNo = 0;
				hexSide = HexSideEnum.classifyPoint(p, hp, hexHalfWidth, hexOneThirdHeight);
				otherHexSide = hexSide.getOppositeSide();
				otherHexNo = hexSide.getHexNoAtSide(hexNo);
				Hex otherHex = null;
				if (otherHexNo > 0) {
					otherHex = this.gameHolder.getGame().getMetadata().getHex(otherHexNo);
				}

				HexSideElementEnum element = (HexSideElementEnum) brush;
				ArrayList<HexSideElementEnum> toRemove = new ArrayList<HexSideElementEnum>();
				ArrayList<HexSideElementEnum> toAdd = new ArrayList<HexSideElementEnum>();

				if (hex1.getHexSideElements(hexSide).contains(element)) {
					// remove element
					// if element is a river, then remove the bridge as well
					if (element.equals(HexSideElementEnum.MajorRiver)) {
						// remove major river
						// remove bridge
						// remove ford
						toRemove.add(HexSideElementEnum.MajorRiver);
						toRemove.add(HexSideElementEnum.Bridge);
						toRemove.add(HexSideElementEnum.Ford);
					} else if (element.equals(HexSideElementEnum.MinorRiver)) {
						// remove minor river
						// remove bridge
						// remove ford
						toRemove.add(HexSideElementEnum.MinorRiver);
						toRemove.add(HexSideElementEnum.Bridge);
						toRemove.add(HexSideElementEnum.Ford);
					} else {
						// just remove the element
						toRemove.add(element);
					}

				} else {
					// adding new element
					if (element.equals(HexSideElementEnum.MajorRiver)) {
						// if new is major river, remove minor river
						toRemove.add(HexSideElementEnum.MinorRiver);
					} else if (element.equals(HexSideElementEnum.MinorRiver)) {
						// if new is minor river, remove major river
						toRemove.add(HexSideElementEnum.MajorRiver);
					} else if (element.equals(HexSideElementEnum.Bridge)) {
						// if new is bridge, remove ford
						toRemove.add(HexSideElementEnum.Ford);
					} else if (element.equals(HexSideElementEnum.Ford)) {
						// if new is ford, remove bridge and road
						toRemove.add(HexSideElementEnum.Bridge);
					}
					// add appropriate elements
					if (element.equals(HexSideElementEnum.Bridge)) {
						// add a bridge only if river exists
						if (hex1.getHexSideElements(hexSide).contains(HexSideElementEnum.MinorRiver) || hex1.getHexSideElements(hexSide).contains(HexSideElementEnum.MajorRiver)) {
							toAdd.add(HexSideElementEnum.Bridge);
						}
					} else {
						toAdd.add(element);
					}

				}
				if (toRemove.size() + toAdd.size() > 0) {
					MapEditorView.instance.log(""); //$NON-NLS-1$
				}
				// remove what you must
				for (HexSideElementEnum el : toRemove) {
					if (hex1.getHexSideElements(hexSide).contains(el)) {
						hex1.getHexSideElements(hexSide).remove(el);
						MapEditorView.instance.log(hex1.getHexNo() + " " + hexSide.toString() + " remove " + el.toString()); //$NON-NLS-1$
					}
					if (otherHex != null) {
						if (otherHex.getHexSideElements(otherHexSide).contains(el)) {
							otherHex.getHexSideElements(otherHexSide).remove(el);
							MapEditorView.instance.log(otherHex.getHexNo() + " " + otherHexSide.toString() + " remove " + el.toString()); //$NON-NLS-1$
						}
					}
				}
				// add what you must
				for (HexSideElementEnum el : toAdd) {
					if (!hex1.getHexSideElements(hexSide).contains(el)) {
						MapEditorView.instance.log(hex1.getHexNo() + " " + hexSide.toString() + " add " + el.toString()); //$NON-NLS-1$
						hex1.getHexSideElements(hexSide).add(el);
					}
					if (otherHex != null && !otherHex.getHexSideElements(otherHexSide).contains(el)) {
						MapEditorView.instance.log(otherHex.getHexNo() + " " + otherHexSide.toString() + " add " + el.toString()); //$NON-NLS-1$
						otherHex.getHexSideElements(otherHexSide).add(el);
					}
				}
				invalidateAll();
				this.updateUI();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
			if (!GameHolder.hasInitializedGame())
				return;
			int dx = Math.abs(e.getX() - this.xDiff);
			int dy = Math.abs(e.getY() - this.yDiff);
			if (dx > 5 || dy > 5) {
				TransferHandler handler = this.getTransferHandler();
				handler.exportAsDrag(this, e, TransferHandler.COPY);
				requestFocusInWindow();
			}
		} else {
			this.c = this.getParent();
			if (this.c instanceof JViewport) {
				JViewport jv = (JViewport) this.c;
				Point p = jv.getViewPosition();
				int newX = p.x - (e.getX() - this.xDiff);
				int newY = p.y - (e.getY() - this.yDiff);

				int maxX = this.getWidth() - jv.getWidth();
				int maxY = this.getHeight() - jv.getHeight();
				if (newX < 0)
					newX = 0;
				if (newX > maxX)
					newX = maxX;
				if (newY < 0)
					newY = 0;
				if (newY > maxY)
					newY = maxY;

				jv.setViewPosition(new Point(newX, newY));
			}

		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		MapTooltipHolder tooltipHolder = MapTooltipHolder.instance();
		String pval = PreferenceRegistry.instance().getPreferenceValue("map.tooltips"); //$NON-NLS-1$
		
		double[] scales = this.getScaleTransformation();

	    int logicalX = (int) (e.getPoint().getX() * scales[0]);
	    int logicalY = (int) (e.getPoint().y * scales[1]);
	    Point p = new Point(logicalX, logicalY);
		
		if (pval != null && pval.equals("yes")) //$NON-NLS-1$
			
			
			tooltipHolder.showTooltip(p, e.getPoint());
	}
	
	public double[] getScaleTransformation() {
	    GraphicsConfiguration gc = getGraphicsConfiguration();
	    AffineTransform tx = gc.getDefaultTransform();
	    double scaleX = tx.getScaleX();
	    double scaleY = tx.getScaleY();
	    
	    return new double[]{scaleX, scaleY};
	}

	public Game getGame() {
		if (this.game == null) {
			if (this.gameHolder!=null) {
				this.game = this.gameHolder.getGame();
			}
		}
		return this.game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public BufferedImage getMapImage() {
		return this.mapItems;
	}

	public String getHex() {
		Point p = getHexFromPoint(new Point(this.xDiff, this.yDiff));
		String h = String.valueOf(p.x * 100 + p.y);
		if (h.length() < 4) {
			h = "0" + h; //$NON-NLS-1$
		}
		return h;
	}

	public void setHex(String hex) {
	};

	public BufferedImage getMap() {
		return this.mapItems;
	}

	class MapPanelDropTargetAdapter extends DropTargetAdapter {

		@Override
		public void drop(DropTargetDropEvent e) {
			Transferable t = e.getTransferable();
			Object obj = null;
			try {
				if (t.isDataFlavorSupported(new CharacterDataFlavor())) {
					obj = t.getTransferData(new CharacterDataFlavor());
				} else if (t.isDataFlavorSupported(new ArtifactDataFlavor())) {
					obj = t.getTransferData(new ArtifactDataFlavor());
				} else if (t.isDataFlavorSupported(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName()))) { //$NON-NLS-1$
					obj = t.getTransferData(new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName())); //$NON-NLS-1$
				}
				;
				Point p = MapPanel.instance().getHexFromPoint(e.getLocation());
				final int hexNo = p.x * 100 + p.y;
				if (obj != null) {
					final Turn turn = MapPanel.this.gameHolder.getGame().getTurn();
					final Object target = obj;
					ConfirmationDialog dlg = new ConfirmationDialog(Messages.getString("MapPanel.MoveConfirmation.title"),
							Messages.getString("MapPanel.MoveConfirmation.text", new Object[] { hexNo })) {
						@Override
						protected void onConfirm() {
							if (Character.class.isInstance(target)) {
								((Character) target).setHexNo(hexNo);
								turn.getCharacters().refreshItem((Character) target);
							} else if (Army.class.isInstance(target)) {
								((Army) target).setHexNo(String.valueOf(hexNo));
								turn.getArmies().refreshItem((Army) target);
							} else if (Artifact.class.isInstance(target)) {
								((Artifact) target).setHexNo(hexNo);
								turn.getArtifacts().refreshItem((Artifact) target);
							}
							JOApplication.publishEvent(LifecycleEventsEnum.SelectedTurnChangedEvent, this, this);
						}
					};
					dlg.showDialog();
				}
			} catch (Exception exc) {
			}
			;
		}

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
			if (e.getUnitsToScroll() < 0) {
				JOApplication.publishEvent(LifecycleEventsEnum.ZoomIncreaseEvent, this, this);
			} else if (e.getUnitsToScroll() > 0) {
				JOApplication.publishEvent(LifecycleEventsEnum.ZoomDecreaseEvent, this, this);
			}
		} else {
			// get the JScrollPane for this container
			if (getParent() != null && getParent().getParent() != null) {
				for (MouseWheelListener mwl : getParent().getParent().getMouseWheelListeners()) {
					mwl.mouseWheelMoved(e);
				}
			}
		}
	}

}
