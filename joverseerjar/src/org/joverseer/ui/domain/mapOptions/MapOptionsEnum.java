package org.joverseer.ui.domain.mapOptions;

/**
 * Enumeration for the various map options
 * 
 * The map options allow the customization of the map display in a dynamic manner. The are different
 * from preferences in that map options can be changed from a more easily accessible gui and more often. 
 * Moreover map option values are not stored anywhere and are re-initialized each time the program is started. 
 * 
 * @author Marios Skounakis
 */
public enum MapOptionsEnum {
    NationMap,          // which nation map to use when drawing visible/invisible hexes
    DrawOrders,         // whether the allow orders to be drawn or not
    DrawNamesOnOrders,  // whether to draw char names when drawing orders
    ShowClimate,        // show the climate or not
    NationColors,		// use color per nation or color per allegiance
    PopCenterNames,      // draw pop center names
    NationNames			// draw nation names
}
