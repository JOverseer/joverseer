package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.joverseer.domain.Character;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.MetadataSource;
import org.springframework.util.StringUtils;

/**
 * Reads starting character information
 *
 * @author Marios Skounakis
 *
 */
public class CharacterReader implements MetadataReader {
	String characterFilename = "chars.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		gm.setCharacters(loadCharacters(gm));
	}

	private Container<Character> loadCharacters(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<Character> characters = new Container<Character>();

		MetadataSource ms = new MetadataSource();

		String ln=""; // outside the try/catch so that the exception can report something useful
		try {
			BufferedReader reader = gm.getUTF8ResourceByGame(this.characterFilename,true);

			while ((ln = reader.readLine()) != null) {
				String[] parts = ln.split(";");
				int nationNo = Integer.parseInt(parts[0]);
				String charName = parts[1];
				String id = org.joverseer.support.StringUtils.toCharacterId(charName);
				int command = Integer.parseInt(parts[2]);
				int agent = Integer.parseInt(parts[3]);
				int emmisary = Integer.parseInt(parts[4]);
				int mage = Integer.parseInt(parts[5]);
				int stealth = Integer.parseInt(parts[6]);
				int challenge = Integer.parseInt(parts[7]);
				int numberOfOrders = 2;
				if (parts.length > 9) {
					numberOfOrders = Integer.parseInt(parts[9]);
				}
				Character c = new Character();
				c.setNationNo(Integer.valueOf(nationNo));
				c.setName(charName);
				c.setId(id);
				c.setCommand(command);
				c.setAgent(agent);
				c.setEmmisary(emmisary);
				c.setMage(mage);
				c.setStealth(stealth);
				c.setChallenge(challenge);
				c.setNumberOfOrders(numberOfOrders);
				c.setInfoSource(ms);
				characters.addItem(c);
			}
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			if (ln == null) {
				ln = "null";
			}
			Logger.getRootLogger().error("parse error in for character meta data in file:"+this.characterFilename+System.lineSeparator()+ln);
			throw new MetadataReaderException("Error reading character metadata.", exc);
		}
		return characters;
	}
}
