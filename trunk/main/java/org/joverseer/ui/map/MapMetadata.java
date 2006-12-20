package org.joverseer.ui.map;

import java.util.ArrayList;
import java.util.Collection;


public class MapMetadata {
    int gridCellWidth;
    int gridCellHeight;
    int hexSize = 4;
    int mapColumns;
    int mapRows;

    ArrayList renderers = new ArrayList();

    public int getGridCellHeight() {
        return gridCellHeight;
    }

    public void setGridCellHeight(int gridCellHeight) {
        this.gridCellHeight = gridCellHeight;
    }

    public int getGridCellWidth() {
        return gridCellWidth;
    }

    public void setGridCellWidth(int gridCellWidth) {
        this.gridCellWidth = gridCellWidth;
    }

    public int getHexSize() {
        return hexSize;
    }

    public void setHexSize(int hexSize) {
        this.hexSize = hexSize;
    }

    public int getMapColumns() {
        return mapColumns;
    }

    public void setMapColumns(int mapColumns) {
        this.mapColumns = mapColumns;
    }

    public int getMapRows() {
        return mapRows;
    }

    public void setMapRows(int mapRows) {
        this.mapRows = mapRows;
    }

    public void setRenderers(Collection renderers) {
        this.renderers.addAll(renderers);
    }

    public Collection getRenderers() {
        return renderers;
    }


}
