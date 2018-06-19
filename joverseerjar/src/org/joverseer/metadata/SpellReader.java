package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.Container;

/**
 * Spell metadata reader. Reads spell metadata from data files.
 * 
 * @author Marios Skounakis
 * 
 */
public class SpellReader implements MetadataReader {
	String spellFilename = "spells.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<SpellInfo> spells = new Container<SpellInfo>();

		try {
			BufferedReader reader = gm.getUTF8Resource(this.spellFilename);
			String ln;
			while ((ln = reader.readLine()) != null) {
				String[] parts = ln.split(";");
				SpellInfo si = new SpellInfo();
				si.setName(parts[0]);
				si.setDifficulty(parts[1]);
				si.setOrderNumber(new Integer(Integer.parseInt(parts[2])));
				si.setNumber(new Integer(Integer.parseInt(parts[3])));
				si.setRequiredInfo(parts[4]);
				si.setRequirements(parts[5]);
				si.setDescription(parts[6]);
				si.setList(parts[7]);
				spells.addItem(si);
			}
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading spell metadata.", exc);
		}
		gm.setSpells(spells);
	}
}
