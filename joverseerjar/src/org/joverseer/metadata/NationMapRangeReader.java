package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.support.Container;

/**
 * Reads the nation map ranges from data files.
 * 
 * @author Marios Skounakis
 * 
 */
public class NationMapRangeReader implements MetadataReader {
	String nationMapFilename = "maps.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<NationMapRange> mapRanges = new Container<NationMapRange>();

		try {
			BufferedReader reader = gm.getUTF8ResourceByGame(this.nationMapFilename,true);

			String ln;
			while ((ln = reader.readLine()) != null) {
				NationMapRange nmr = new NationMapRange();
				String[] parts = ln.split(";");
				
				int nationNo = Integer.parseInt(parts[0]);
				int x1 = Integer.parseInt(parts[1]);
				int y1 = Integer.parseInt(parts[2]);
				int x2 = Integer.parseInt(parts[3]);
				int y2 = Integer.parseInt(parts[4]);
				ArrayList<Integer> points2 = new ArrayList<Integer>();
				//Add additional hexes onto the main rectangle for FA games
				for (int i=5;i<parts.length;i++) {
					points2.add(Integer.parseInt(parts[i]));
				}
				
				mapRanges.addItem(nmr);
				nmr.setNationNo(nationNo);
				nmr.setTlX(x1);
				nmr.setTlY(y1);
				nmr.setBrX(x2);
				nmr.setBrY(y2);
				nmr.setPoints(points2);

			}
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading nation map metadata.", exc);
		}
		gm.setNationMapRanges(mapRanges);
	}
}
