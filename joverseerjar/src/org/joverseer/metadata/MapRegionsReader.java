package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.support.Container;

public class MapRegionsReader implements MetadataReader {
	String nationMapFilename = "mapregions.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<MapRegion> mapRegions = new Container<MapRegion>();

		try {
			BufferedReader reader = gm.getUTF8ResourceByGame(this.nationMapFilename,true);
			String ln;
			while ((ln = reader.readLine()) != null) {
				MapRegion nmr = new MapRegion();
				String[] parts = ln.split(",");
				
				int regionNo = Integer.parseInt(parts[0]);
				String regionName = parts[1];
				
				System.out.println(regionName);
				
				mapRegions.addItem(nmr);
				nmr.setRegionNo(regionNo);
				nmr.setRegionName(regionName);
				
			}
		} catch (IOException exc) {
			gm.setMapRegions(mapRegions);
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading map region metadata.", exc);
		}
		System.out.println("Finished and setting regions");
		gm.setMapRegions(mapRegions);
	}
}
