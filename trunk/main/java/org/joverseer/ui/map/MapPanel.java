package org.joverseer.ui.map;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.progress.BusyIndicator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.support.JOverseerEvent;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Collection;
import java.util.ArrayList;


public class MapPanel extends JPanel implements MouseListener {
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
    
    
    Point selectedHex = null;

    private Game game = null;

    public MapPanel() {
        addMouseListener(this);
        _instance = this;
    }

    public static MapPanel instance() {
        return _instance;
    }

    private void setHexLocation(int x, int y) {
        location = getHexLocation(x, y);
    }

    public Point getHexCenter(int hexNo) {
        Point p = getHexLocation(hexNo);
        MapMetadata metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        p.translate(metadata.getHexSize() * metadata.getGridCellWidth() / 2,
                    metadata.getHexSize() * metadata.getGridCellHeight() / 2);
        return p;
    }

    public Point getHexLocation(int hexNo) {
        return getHexLocation(hexNo / 100, hexNo % 100);
    }

    public Point getHexLocation(int x, int y) {
        Point location = new Point();
        x--;
        y--;
        MapMetadata metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
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
        MapMetadata metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
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
            metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }

        Game game = getGame();
        if (!Game.isInitialized(game)) return;

        GameMetadata gm = game.getMetadata();

        if (mapBack == null) {
            int width = (metadata.getMapColumns() + 1) * metadata.getHexSize() * metadata.getGridCellWidth();
            int height = metadata.getMapRows() * metadata.getHexSize() * metadata.getGridCellHeight();
            mapBack = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }
        map = mapBack;

        Graphics2D g = map.createGraphics();

        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
        Image img = imgSource.getImage("memap");
        try {
        	wait(1000);
        }
        catch (Exception exc) {};
        g.drawImage(img, 0, 0, this);
        
//        for (Hex h : (Collection<Hex>)gm.getHexes()) {
//            setHexLocation(h.getColumn(), h.getRow());
//            for (org.joverseer.ui.map.renderers.Renderer r : (Collection<org.joverseer.ui.map.renderers.Renderer>)metadata.getRenderers()) {
//                if (r.appliesTo(h)) {
//                    r.render(h, g, location.x, location.y);
//                }
//            }
//        }
    }
    
    private void createMapItems() {
        MapMetadata metadata;
        try {
            metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }
        Game game = getGame();
        if (!Game.isInitialized(game)) return;

        BusyIndicator.showAt(this);
        if (mapItemsBack == null) {
            int width = (metadata.getMapColumns() + 1) * metadata.getHexSize() * metadata.getGridCellWidth();
            int height = metadata.getMapRows() * metadata.getHexSize() * metadata.getGridCellHeight();
            mapItemsBack = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
            metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        }
        catch (Exception exc) {
            // application is not ready
            return;
        }
        Game game = getGame();
        if (!Game.isInitialized(game)) return;

        BusyIndicator.showAt(this);
        if (mapBaseItemsBack == null) {
            int width = (metadata.getMapColumns() + 1) * metadata.getHexSize() * metadata.getGridCellWidth();
            int height = metadata.getMapRows() * metadata.getHexSize() * metadata.getGridCellHeight();
            mapBaseItemsBack = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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

    public void invalidateAll() {
        map = null;
        mapBaseItems = null;
        mapItems = null;
        setGame(((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame());
    }
    
    public void invalidateMapItems() {
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
     * Handles the mouse pressed event to change the current selected hex
     */
    public void mousePressed(MouseEvent e)
    {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (!GameHolder.hasInitializedGame()) return;
            Point p = e.getPoint();
//            MessageDialog dlg = new MessageDialog("test", p.x + "," + p.y);
//            dlg.getDialog().show();
            Point hex = getHexFromPoint(p);
            setSelectedHex(hex);
            this.updateUI();
        }
    }

    /**
     * Given a client point (eg from mouse click), it finds the containing hex
     * and returns it as a point (i.e. point.x = hex.column, point.y = hex.row)
     */
    private Point getHexFromPoint(Point p) {
        MapMetadata metadata = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
        int y = p.y / (metadata.getHexSize() * 3 / 4 * metadata.getGridCellHeight()) + 1;
        int x;
        if (y % 2 == 1) {
            x = p.x / (metadata.getHexSize() * metadata.getGridCellWidth()) + 1;
        } else {
            x = (p.x - metadata.getHexSize() / 2 * metadata.getGridCellWidth()) / (metadata.getHexSize() * metadata.getGridCellWidth()) + 1;
        }
        if (x > metadata.getMapColumns()) x = metadata.getMapColumns();
        if (y > metadata.getMapRows()) y = metadata.getMapRows();
        return new Point(x, y);
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
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

}
