package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.MetadataSource;

/**
 * Reads starting army information.
 * 
 * @author Marios Skounakis
 * 
 */
public class ArmyReader implements MetadataReader {
	String armyFilename = "armies";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<Army> armies = new Container<Army>();
		try {

			BufferedReader reader = gm.getUTF8ResourceByGame(this.armyFilename);

			String ln;
			while ((ln = reader.readLine()) != null) {
				if (ln.equals(""))
					continue;
				ln = ln.replace("\"", "");
				String parts[] = ln.split(",");

				int allegiance = Integer.parseInt(parts[5]);
				int size = Integer.parseInt(parts[6]);
				String navy = parts[9];

				Army a = new Army();
				a.setNavy(navy.equals("True"));

				switch (size) {
				case 2:
					a.setSize(ArmySizeEnum.small);
					break;
				case 3:
					a.setSize(ArmySizeEnum.army);
					break;
				case 4:
					a.setSize(ArmySizeEnum.large);
					break;
				case 5:
					a.setSize(ArmySizeEnum.huge);
					break;
				}
				switch (allegiance) {
				case 1:
					a.setNationAllegiance(NationAllegianceEnum.FreePeople);
					break;
				case 2:
					a.setNationAllegiance(NationAllegianceEnum.DarkServants);
					break;
				case 3:
					a.setNationAllegiance(NationAllegianceEnum.Neutral);
					break;
				}

				a.setHexNo(parts[0]);
				a.setNationNo(new Integer(Integer.parseInt(parts[2])));
				a.setCommanderName(parts[3]);
				a.setCommanderTitle(parts[4]);
				String[] regiments = parts[8].split(" ");
				for (int i = 0; i < regiments.length; i++) {
					String type = regiments[i].substring(regiments[i].length() - 2, regiments[i].length());
					String number = regiments[i].substring(0, regiments[i].length() - 2);
					int no = Integer.parseInt(number);
					if (type.equals("HC")) {
						a.getElements().add(new ArmyElement(ArmyElementType.HeavyCavalry, no));
					} else if (type.equals("LC")) {
						a.getElements().add(new ArmyElement(ArmyElementType.LightCavalry, no));
					} else if (type.equals("HI")) {
						a.getElements().add(new ArmyElement(ArmyElementType.HeavyInfantry, no));
					} else if (type.equals("LI")) {
						a.getElements().add(new ArmyElement(ArmyElementType.LightInfantry, no));
					} else if (type.equals("AR")) {
						a.getElements().add(new ArmyElement(ArmyElementType.Archers, no));
					} else if (type.equals("MA")) {
						a.getElements().add(new ArmyElement(ArmyElementType.MenAtArms, no));
					}
				}
				if (parts.length > 11) {
					String withArmy = parts[11];
					String[] chars = withArmy.split("-");
					if (chars.length > 0) {
						for (String c : chars) {
							a.getCharacters().add(c.trim());
						}
					}
				}

				a.setInformationSource(InformationSourceEnum.detailed);
				a.setInfoSource(new MetadataSource());
				armies.addItem(a);
			}
			gm.setArmies(armies);
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading army metadata.", exc);
		}
	}

}
