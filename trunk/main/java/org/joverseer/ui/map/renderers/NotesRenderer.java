package org.joverseer.ui.map.renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import org.apache.log4j.Logger;
import org.joverseer.domain.Note;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.richclient.application.Application;

public class NotesRenderer implements Renderer {
	MapMetadata mapMetadata = null;

	static Logger logger = Logger.getLogger(PopulationCenterRenderer.class);

	public boolean appliesTo(Object obj) {
		return Note.class.isInstance(obj);
	}

	private void init() {
		mapMetadata = (MapMetadata) Application.instance().getApplicationContext().getBean("mapMetadata");
	}

	public void render(Object obj, Graphics2D g, int x, int y) {
		if (mapMetadata == null)
			init();

		// Note note = (Note)obj;

		int w = mapMetadata.getGridCellWidth() / 3;
		int h = mapMetadata.getGridCellHeight() / 3;
		int dx = mapMetadata.getGridCellWidth() * mapMetadata.getHexSize() * 1 / 2 - w / 2 + w + 1;
		int dy = mapMetadata.getGridCellHeight() * mapMetadata.getHexSize() * 1 / 9;

		Color color1 = ColorPicker.getInstance().getColor("noteFG");
		Color color2 = ColorPicker.getInstance().getColor("noteBorder");
		g.setColor(color1);

		g.setStroke(GraphicUtils.getBasicStroke(1));
		RoundRectangle2D.Float e = new RoundRectangle2D.Float(x + dx, y + dy, w, h, w / 5 * 2, h / 5 * 2);
		g.fill(e);
		// g.fillRect(x + dx, y + dy, w, h);
		g.setColor(color2);

		g.draw(e);
	}

}
