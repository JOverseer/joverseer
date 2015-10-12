package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.joverseer.domain.Character;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.MetadataSource;
import org.springframework.core.io.Resource;

public class StartingCharacterReader implements MetadataReader {
	String characterFilename = "startchars";

	public String getCharacterFilename(GameMetadata gm) {
		return "file:///" + gm.getBasePath() + "/" + gm.getGameType().toString() + "." + this.characterFilename;
	}

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		gm.setStartDummyCharacters(loadCharacters(gm));
	}

	private Container<Character> loadCharacters(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<Character> characters = new Container<Character>();

		MetadataSource ms = new MetadataSource();

		try {
			// Resource resource =
			// Application.instance().getApplicationContext().getResource(getCharacterFilename(gm));
			Resource resource = gm.getResource(gm.getGameType().toString() + "." + this.characterFilename);

			BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

			String ln;
			while ((ln = reader.readLine()) != null) {
				if (ln.startsWith("#"))
					continue;
				String[] parts = ln.split(";");
				if (parts.length < 4)
					continue;
				int nationNo = Integer.parseInt(parts[3]);
				String charName = parts[1];
				String id = charName.toLowerCase().substring(0, Math.min(5, charName.length()));
				int hexNo = Integer.parseInt(parts[0]);
				Character c = new Character();
				c.setNationNo(new Integer(nationNo));
				c.setName(charName);
				c.setId(id);
				c.setHexNo(hexNo);
				c.setInfoSource(ms);
				c.setStartInfoDummy(true);
				characters.addItem(c);
			}
		} catch (IOException exc) {
			// ignore, not implemented for all game types
		} catch (NullPointerException exc) {
			// ignore, not implemented for all game types
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading character metadata.", exc);
		}
		return characters;
	}
}
