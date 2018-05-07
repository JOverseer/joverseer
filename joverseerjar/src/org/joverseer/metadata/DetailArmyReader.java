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
 * This is the "detailed" reader, ie it reads army details such as training,
 * weapon/armor/training ranks, etc
 * 
 * @author Marios Skounakis
 * 
 */
public class DetailArmyReader implements MetadataReader {
	String armyFilename = "startarmies";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<Army> armies = new Container<Army>();
		try {
			BufferedReader reader = gm.getUTF8ResourceByGame(this.armyFilename);

			String ln;
			while ((ln = reader.readLine()) != null) {
				if (ln.startsWith(("#")) || ln.equals(""))
					continue;
				try {
					String parts[] = ln.split(";");
					String hexNo = parts[0];
					String commander = parts[1];
					NationAllegianceEnum allegiance = null;
					if (parts[2].equals("1")) {
						allegiance = NationAllegianceEnum.FreePeople;
					} else if (parts[2].equals("2")) {
						allegiance = NationAllegianceEnum.DarkServants;
					} else {
						allegiance = NationAllegianceEnum.Neutral;
					}
					int nationNo = Integer.parseInt(parts[3]);
					boolean navy = parts[4].equals("1");
					ArmySizeEnum size = ArmySizeEnum.unknown;
					int morale = Integer.parseInt(parts[7]);

					Army army = new Army();
					army.setHexNo(hexNo);
					army.setCommanderName(commander);
					army.setCommanderTitle("");
					army.setNationAllegiance(allegiance);
					army.setNationNo(new Integer(nationNo));
					army.setMorale(morale);
					army.setSize(size);
					army.setNavy(navy);
					int i = 0;
					for (ArmyElementType aet : ArmyElementType.values()) {
						int si = 8 + i * 5;
						i++;
						if (si + 1 >= parts.length)
							continue;
						String no = parts[si + 1];
						if (!no.equals("")) {
							ArmyElement ae = new ArmyElement(aet, Integer.parseInt(no));
							String training = parts[si + 2];
							if (!training.equals("")) {
								ae.setTraining(Integer.parseInt(training));
							}
							String we = parts[si + 3];
							if (!we.equals("")) {
								ae.setWeapons(Integer.parseInt(we));
							}
							String ar = parts[si + 4];
							if (!ar.equals("")) {
								ae.setArmor(Integer.parseInt(ar));
							}
							army.getElements().add(ae);
						}
					}
					army.setCavalry(army.computeCavalry());
					army.setInformationSource(InformationSourceEnum.detailed);
					army.setInfoSource(new MetadataSource());
					armies.addItem(army);
				} catch (Exception exc) {
					System.out.println(ln);
					throw exc;
				}
			}
			gm.setArmies(armies);
		} catch (IOException exc) {
			exc.printStackTrace();
			throw exc;
		} catch (Exception exc) {
			exc.printStackTrace();
			throw new MetadataReaderException("Error reading army metadata.", exc);
		}
	}

}
