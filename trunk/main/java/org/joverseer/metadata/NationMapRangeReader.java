package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.support.Container;
import org.springframework.core.io.Resource;

/**
 * Reads the nation map ranges from data files.
 * 
 * @author Marios Skounakis
 * 
 */
public class NationMapRangeReader implements MetadataReader {
	String nationMapFilename = "maps.csv";

	public String getNationMapFilename(GameMetadata gm) {
		return "file:///" + gm.getBasePath() + "/" + gm.getGameType().toString() + "." + nationMapFilename;
	}

	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<NationMapRange> mapRanges = new Container<NationMapRange>();

		try {
			// Resource resource =
			// Application.instance().getApplicationContext().getResource(getNationMapFilename(gm));
			Resource resource = gm.getResource(gm.getGameType().toString() + "." + nationMapFilename);

			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

			String ln;
			while ((ln = reader.readLine()) != null) {
				NationMapRange nmr = new NationMapRange();
				String[] parts = ln.split(";");
				int nationNo = Integer.parseInt(parts[0]);
				int x1 = Integer.parseInt(parts[1]);
				int y1 = Integer.parseInt(parts[2]);
				int x2 = Integer.parseInt(parts[3]);
				int y2 = Integer.parseInt(parts[4]);
				mapRanges.addItem(nmr);
				nmr.setNationNo(nationNo);
				nmr.setTlX(x1);
				nmr.setTlY(y1);
				nmr.setBrX(x2);
				nmr.setBrY(y2);
			}
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading nation map metadata.", exc);
		}
		gm.setNationMapRanges(mapRanges);
	}
}
