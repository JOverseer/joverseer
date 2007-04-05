package org.joverseer.ui.map;

import java.util.ArrayList;
import java.util.Collection;


public class MapMetadata {
    int gridCellWidth;
    int gridCellHeight;
    int hexSize = 4;
    int maxMapColumn;
    int maxMapRow;
    int minMapColumn = 1;
    int minMapRow = 1;

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

    public int getMaxMapColumn() {
        return maxMapColumn;
    }

    public void setMaxMapColumn(int mapColumns) {
        this.maxMapColumn = mapColumns;
    }

    public int getMaxMapRow() {
        return maxMapRow;
    }

    public void setMaxMapRow(int mapRows) {
        this.maxMapRow = mapRows;
    }

    public int getMinMapColumn() {
        return minMapColumn;
    }

    
    public void setMinMapColumn(int minMapColumn) {
        this.minMapColumn = minMapColumn;
    }

    
    public int getMinMapRow() {
        return minMapRow;
    }

    
    public void setMinMapRow(int minMapRow) {
        this.minMapRow = minMapRow;
    }

    public void setRenderers(Collection renderers) {
        this.renderers.addAll(renderers);
    }

    public Collection getRenderers() {
        return renderers;
    }


}
