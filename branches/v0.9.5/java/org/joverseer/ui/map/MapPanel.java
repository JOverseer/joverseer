package org.joverseer.ui.map;

import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.progress.BusyIndicator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.transferHandlers.HexNoTransferHandler;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.domain.*;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.support.Container;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;
import org.apache.log4j.Logger;
import org.joverseer.ui.map.renderers.DefaultHexRenderer;
import org.joverseer.ui.map.renderers.Renderer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.MouseInputListener;

import java.awt.*;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.util.Collection;
import java.util.ArrayList;


public class MapPanel extends JPanel implements MouseInputListener {
    protected javax.swing.event.EventListenerList listenerList =
            new javax.swing.event.EventListenerList();

    private static Logger logger = Logger.getLogger(MapPanel.class);
    private static MapPanel _instance = null;

    Polygon hex = new Polygon();
    int[] xPoints = new int[6];
    int[] yPoints = new int[6];
    Point location = new Point();

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
    
    boolean saveMap = false;

    public MapPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        this.setTransferHandler(new HexNoTransferHandler("hex"));
        _instance = this;
    }

    public static MapPanel instance() {
        return _instance;
    }

    private void setHexLocation(int x, int y) {
        location = getHexLocation(x, y);
    }
    
    protected MapMetadata getMetadata() {
    	if (metadata == null) {
    		metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata"); 
    	}
    	return metadata;
    }

    public Point getHexCenter(int hexNo) {
        Point p = getHexLocation(hexNo);
        MapMetadata metadata = getMetadata(); 
        p.translate(metadata.getHexSize() * metadata.getGridCellWidth() / 2,
                    metadata.getHexSize() * metadata.getGridCellHeight() / 2);
        return p;
    }

    public Point getHexLocation(int hexNo) {
        return getHexLocation(hexNo / 100, hexNo % 100);
    }

    public Point getHexLocation(int x, int y) {
        Point location = new Point();
        MapMetadata metadata = getMetadata();
        x = x - metadata.getMinMapColumn();
        y = y - metadata.getMinMapRow();
        if (y % 2 == 0) {
            location.setLocation(metadata.getHexSize() * metadata.getGridCellWidth() * x,
                                 metadata.getHexSize() * 3 / 4 * y * metadata.getGridCellHeight());
        } else {
            location.setLocation((x + .5) * metadata.getHexSize() * metadata.getGridCellWidth(),
                    metadata.getHexSize() * 3 / 4 * y * metadata.getGridCellHeight());
        }
        return location;
    }

    private void setPoints(int x, int y) {
    	MapMetadata metadata = getMetadata();
        xPoints[0] = metadata.getHexSize() / 2;
        xPoints[1] = metadata.getHexSize();
        xPoints[2] = metadata.getHexSize();
        xPoints[3] = metadata.getHexSize() / 2;
        xPoints[4] = 0;
        xPoints[5] = 0;

        yPoints[0] = 0;
        yPoints[1] = metadata.getHexSize() / 4;
        yPoints[2] = metadata.getHexSize() * 3 / 4;
        yPoints[3] = metadata.getHexSize();
        yPoints[4] = metadata.getHexSize() * 3 / 4;
        yPoints[5] = metadata.getHexSize() / 4;

        setHexLocation(x, y);

        for (int i=0; i<6; i++) {
            xPoints[i] = xPoints[i] * metadata.getGridCellWidth() + location.x;
            yPoints[i] = yPoints[i] * metadata.getGridCellHeight() + location.y;
        }


    }

    /**
     * Creates the basic map (the background layer with the hexes)
     * The hexes are retrieved from the Game Metadata and rendered with all the
     * valid available renderers found in the Map Metadata
     *
     */
    private void createMap() {
        MapMetadata metadata;
        try {
        	metadata = getMetadata();        
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }

        Game game = getGame();
        if (!Game.isInitialized(game)) return;

        GameMetadata gm = game.getMetadata();

        if (mapBack == null) {
            Dimension d = getMapDimension();
            mapBack = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
            this.setPreferredSize(d);
            this.setSize(d);
        }
        map = mapBack;

        Graphics2D g = map.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, map.getWidth(), map.getHeight());
        
  
        
//        try {
//        	Resource r = Application.instance().getApplicationContext().getResource("classpath:images/map/map.png");
//        	BufferedImage mm = ImageIO.read(r.getInputStream());
//        	g.drawImage(mm, 0, 0, null);
//        }
//        catch (Exception exc) {
//        	exc.printStackTrace();
//        }
        

        for (Hex h : (Collection<Hex>)gm.getHexes()) {
            setHexLocation(h.getColumn(), h.getRow());
            for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                if (r.appliesTo(h)) {
                    r.render(h, g, location.x, location.y);
                }
            }
        }
        
        if (saveMap) {
        	File outputFile = new File("map.png");
        	try {
        		ImageIO.write(map, "PNG", outputFile);
        	}
        	catch (Exception exc) {};
        }
    }
    
    private Dimension getMapDimension() {
        int width = (int)((double)((double)metadata.getMaxMapColumn() + 2d - (double)metadata.getMinMapColumn() - .5) * (double)metadata.getHexSize() * (double)metadata.getGridCellWidth());
        int height = (int)((double)((double)metadata.getMaxMapRow() * .75d + .25) * (double)metadata.getHexSize() * (double)metadata.getGridCellHeight());
        return new Dimension(width, height);
    }
    
    private void createMapItems() {
        MapMetadata metadata;
        try {
        	metadata = getMetadata();
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }
        Game game = getGame();
        if (!Game.isInitialized(game)) return;

        BusyIndicator.showAt(this);
        if (mapItemsBack == null) {
            Dimension d = getMapDimension();
            mapItemsBack = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        mapItems = mapItemsBack;

        Graphics2D g = mapItems.createGraphics();

        if (mapBaseItems == null) {
            createMapBaseItems();
        }
        g.drawImage(mapBaseItems, 0, 0, this);
        Turn t = game.getTurn();
        Container mapItemsC = t.getContainer(TurnElementsEnum.MapItem);
        for (AbstractMapItem mi : (ArrayList<AbstractMapItem>)mapItemsC.items) {
            for (Renderer r : (Collection<Renderer>)metadata.getRenderers()) {
                if (r.appliesTo(mi)) {
                    r.render(mi, (Graphics2D)g, 0, 0);
                }
            }
        }
        
        try {
            ArrayList characters = getGame().getTurn().getContainer(TurnElementsEnum.Character).getItems();
            for (Character c : (ArrayList<Character>)characters) {
                for (Order o : c.getOrders()) {
                    for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                        if (r.appliesTo(o)) {
                            setHexLocation(c.getX(), c.getY());
                            try {
                                r.render(o, g, location.x, location.y);
                            }
                            catch (Exception exc) {
                                logger.error("Error rendering order " + o.getCharacter().getName() + " " + o.getOrderNo() + " " + exc.getMessage());
                            }
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering orders " + exc.getMessage());
        }
        
        BusyIndicator.clearAt(this);
    }

    /**
     * Draws the items on the map, i.e. everything that is in front
     * of the background (terrain)
     * todo update with renderers
     */
    private void createMapBaseItems() {
        MapMetadata metadata;
        try {
        	metadata = getMetadata();
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }
        Game game = getGame();
        if (!Game.isInitialized(game)) return;

        BusyIndicator.showAt(this);
        if (mapBaseItemsBack == null) {
            Dimension d = getMapDimension();
            mapBaseItemsBack = new BufferedImage((int)d.getWidth(), (int)d.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }
        mapBaseItems = mapBaseItemsBack;

        Graphics2D g = mapBaseItems.createGraphics();

        if (map == null) {
            createMap();
        }
        g.drawImage(map, 0, 0, this);

        try {
            ArrayList pcs = getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems();
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)pcs) {
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(pc)) {
                        setHexLocation(pc.getX(), pc.getY());
                        try {
                            r.render(pc, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error pc " + pc.getName() + " " + exc.getMessage());
                        }

                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering pop centers " + exc.getMessage());
        }

        try {
            ArrayList characters = getGame().getTurn().getContainer(TurnElementsEnum.Character).getItems();
            for (Character c : (ArrayList<Character>)characters) {
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(c)) {
                        setHexLocation(c.getX(), c.getY());
                        try {
                            r.render(c, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error rendering character " + c.getName() + " " + exc.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering pop centers " + exc.getMessage());
        }

        try {
            ArrayList armies = getGame().getTurn().getContainer(TurnElementsEnum.Army).getItems();
            for (Army army : (ArrayList<Army>)armies) {
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(army)) {
                        setHexLocation(army.getX(), army.getY());
                        try {
                            r.render(army, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error rendering army " + army.getCommanderName() + " " + exc.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering pop centers " + exc.getMessage());
        }

        try {
            ArrayList nationMessages = getGame().getTurn().getContainer(TurnElementsEnum.NationMessage).getItems();
            for (NationMessage nm : (ArrayList<NationMessage>)nationMessages) {
                if (nm.getX() <= 0 || nm.getY() <= 0) continue;
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(nm)) {
                        setHexLocation(nm.getX(), nm.getY());
                        try {
                            r.render(nm, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error rendering nation message " + nm.getMessage() + " " + exc.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering nation " + exc.getMessage());
        }

        
        
        try {
            ArrayList artifacts = getGame().getTurn().getContainer(TurnElementsEnum.Artifact).getItems();
            for (Artifact a : (ArrayList<Artifact>)artifacts) {
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(a)) {
                        setHexLocation(a.getX(), a.getY());
                        try {
                            r.render(a, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error rendering artifact " + a.getNumber() + " " + a.getName() + " " + exc.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering orders " + exc.getMessage());
        }
        
        try {
            ArrayList combats = getGame().getTurn().getContainer(TurnElementsEnum.Combat).getItems();
            for (Combat a : (ArrayList<Combat>)combats) {
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(a)) {
                        setHexLocation(a.getX(), a.getY());
                        try {
                            r.render(a, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error rendering combat " + a.getHexNo() + " " + exc.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering combats " + exc.getMessage());
        }
        
        try {
            ArrayList encounters = new ArrayList();
            encounters.addAll(getGame().getTurn().getContainer(TurnElementsEnum.Encounter).getItems());
            encounters.addAll(getGame().getTurn().getContainer(TurnElementsEnum.Challenge).getItems());
            for (Encounter a : (ArrayList<Encounter>)encounters) {
                for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
                    if (r.appliesTo(a)) {
                        setHexLocation(a.getX(), a.getY());
                        try {
                            r.render(a, g, location.x, location.y);
                        }
                        catch (Exception exc) {
                            logger.error("Error rendering encounter " + a.getHexNo() + " " + exc.getMessage());
                        }
                    }
                }
            }
        }
        catch (Exception exc) {
            logger.error("Error rendering encounters " + exc.getMessage());
        }

        BusyIndicator.clearAt(this);
    }


    public void invalidate() {
    }
    
    public void invalidateAndReset() {
    	metadata = null;
    	map = null;
        mapBack = null;
        mapItemsBack = null;
        mapBaseItemsBack = null;
        invalidateAll();
    }

    public void invalidateAll() {
    	metadata = null;
        map = null;
        mapBaseItems = null;
        mapItems = null;
        setGame(((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame());
    }
    
    public void invalidateMapItems() {
    	metadata = null;
        mapItems = null;
    }

    /**
     * Basic painting
     * todo update/cleanup
     * @param g
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isOpaque()) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        if (mapItems == null) {
            createMapItems();
        }
        if (mapItems == null) {
            // application is not ready
            return;
        }

        MapMetadata metadata;
        try {
            metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }


        //g.drawImage(map, 0, 0, this);
        g.drawImage(mapItems, 0, 0, this);

        if (getSelectedHex() != null)
        {
            Stroke s = ((Graphics2D)g).getStroke();
            Stroke r = new BasicStroke(2);
            setPoints(getSelectedHex().x, getSelectedHex().y);
            g.setColor(Color.YELLOW);
            ((Graphics2D)g).setStroke(r);
            g.drawPolygon(xPoints, yPoints, 6);
            ((Graphics2D)g).setStroke(s);
        }

    }

    public Point getSelectedHex() {
        return selectedHex;
    }

    public void setSelectedHex(Point selectedHex) {
        if (this.selectedHex == null || selectedHex.x != this.selectedHex.x || selectedHex.y != this.selectedHex.y) {
            this.selectedHex = selectedHex;
            //fireMyEvent(new SelectedHexChangedEvent(this));
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));
        }
    }

    public Rectangle getSelectedHexRectangle() {
        setHexLocation(getSelectedHex().x,  getSelectedHex().y);
        MapMetadata metadata;
        try {
            metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
            return new Rectangle(location.x, location.y, metadata.getHexSize() * metadata.getGridCellWidth(), metadata.getHexSize() * metadata.getGridCellHeight());
        }
        catch (Exception exc) {
            // application is not ready
        }
        return new Rectangle(location.x, location.y, 1, 1);
    }
    
    /**
     * Given a client point (eg from mouse click), it finds the containing hex
     * and returns it as a point (i.e. point.x = hex.column, point.y = hex.row)
     */
    private Point getHexFromPoint(Point p) {
        MapMetadata metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        int y = p.y / (metadata.getHexSize() * 3 / 4 * metadata.getGridCellHeight());
        int x;
        if (y % 2 == 0) {
            x = p.x / (metadata.getHexSize() * metadata.getGridCellWidth());
        } else {
            x = (p.x - metadata.getHexSize() / 2 * metadata.getGridCellWidth()) / (metadata.getHexSize() * metadata.getGridCellWidth());
        }
        x += metadata.getMinMapColumn();
        y += metadata.getMinMapRow();
        if (x > metadata.getMaxMapColumn()) x = metadata.getMaxMapColumn();
        if (y > metadata.getMaxMapRow()) y = metadata.getMaxMapRow();
        return new Point(x, y);
    }
    
    

    /**
     * Handles the mouse pressed event to change the current selected hex
     */
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1)
            xDiff = e.getX();
        	yDiff = e.getY();
            if ((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                
            }
            else {
            }
    }

    

    public void mouseReleased(MouseEvent e)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseClicked(MouseEvent e)
    {
    	Point p = e.getPoint();
        Point hex = getHexFromPoint(p);
        setSelectedHex(hex);
        this.updateUI();
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
    
    

    public void mouseDragged(MouseEvent e) {
        if ((e.getModifiers() & MouseEvent.CTRL_MASK) == MouseEvent.CTRL_MASK) {
            c = this.getParent();
            if (c instanceof JViewport) {
              JViewport jv = (JViewport) c;
              Point p = jv.getViewPosition();
              int newX = p.x - (e.getX() - xDiff);
              int newY = p.y - (e.getY() - yDiff);
        
              int maxX = this.getWidth()
                  - jv.getWidth();
              int maxY = this.getHeight()
                  - jv.getHeight();
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
        } else {
            if (!GameHolder.hasInitializedGame()) return;
            Point p = e.getPoint();
            int dx = Math.abs(e.getX() - xDiff);
            int dy = Math.abs(e.getY() - yDiff);
            if (dx > 5 || dy > 5) {
	            Point hex = getHexFromPoint(new Point(xDiff, yDiff));
	            TransferHandler handler = this.getTransferHandler();
	            handler.exportAsDrag(this, e, TransferHandler.COPY);
	            requestFocusInWindow();
            }

        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    public Game getGame() {
        if (game == null) {
            game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        }
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    public BufferedImage getMapImage() {
        return mapItems;
    }
    
    public String getHex() {
        Point p = getHexFromPoint(new Point(xDiff, yDiff));
        String h = String.valueOf(p.x * 100 + p.y);
        if (h.length() < 4) {
        	h = "0" + h;
        }
        return h;
    }
    
    public void setHex(String hex) {};
    

}
