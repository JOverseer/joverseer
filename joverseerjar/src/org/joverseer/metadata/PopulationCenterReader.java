package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.MetadataSource;

/**
 * Population metadata reader. Reads population center starting info from data
 * files.
 * 
 * @author Marios Skounakis
 * 
 */
public class PopulationCenterReader implements MetadataReader {
	String populationCenterFilename = "pcs";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<PopulationCenter> populationCenters = new Container<PopulationCenter>();
		try {
			BufferedReader reader = gm.getUTF8ResourceByGame(this.populationCenterFilename);

			String ln;
			ln = reader.readLine();
			while (ln != null) {
				String[] parts = ln.split(",");
				parts[0] = parts[0].replaceAll("\"", "");
				int no = Integer.parseInt(parts[0]);
				int x = no / 100;
				int y = no % 100;
				String size = parts[2];
				String fortification = parts[3];
				String harbor = parts[6];
				String nation = parts[4];
				String name = parts[7].replaceAll("\"", "");
				boolean capital = parts[9].equals("True");
				boolean hidden = parts[12].equals("True");
				PopulationCenterSizeEnum pcSize = PopulationCenterSizeEnum.ruins;
				if (size.equals("1")) {
					pcSize = PopulationCenterSizeEnum.camp;
				} else if (size.equals("2")) {
					pcSize = PopulationCenterSizeEnum.village;
				} else if (size.equals("3")) {
					pcSize = PopulationCenterSizeEnum.town;
				} else if (size.equals("4")) {
					pcSize = PopulationCenterSizeEnum.majorTown;
				} else if (size.equals("5")) {
					pcSize = PopulationCenterSizeEnum.city;
				}

				FortificationSizeEnum fortSize = FortificationSizeEnum.none;
				if (fortification.equals("1")) {
					fortSize = FortificationSizeEnum.tower;
				} else if (fortification.equals("2")) {
					fortSize = FortificationSizeEnum.fort;
				} else if (fortification.equals("3")) {
					fortSize = FortificationSizeEnum.castle;
				} else if (fortification.equals("4")) {
					fortSize = FortificationSizeEnum.keep;
				} else if (fortification.equals("5")) {
					fortSize = FortificationSizeEnum.citadel;
				}

				HarborSizeEnum harborSize = HarborSizeEnum.none;
				// hack, in the data files for 2950 the numbers are messed up
				if (gm.gameType.equals(GameTypeEnum.game2950) || gm.gameType.equals(GameTypeEnum.gameKS)) {
					if (harbor.equals("2")) {
						harborSize = HarborSizeEnum.port;
					} else if (harbor.equals("1")) {
						harborSize = HarborSizeEnum.harbor;
					}
				} else {
					if (harbor.equals("2")) {
						harborSize = HarborSizeEnum.harbor;
					} else if (harbor.equals("1")) {
						harborSize = HarborSizeEnum.port;
					}
				}

				int nationNo = Integer.parseInt(nation);

				PopulationCenter pc = new PopulationCenter();
				pc.setName(name);
				pc.setNationNo(new Integer(nationNo));
				pc.setSize(pcSize);
				pc.setFortification(fortSize);
				pc.setHarbor(harborSize);
				pc.setX(x);
				pc.setY(y);
				pc.setCapital(capital);
				pc.setHidden(hidden);

				pc.setInformationSource(InformationSourceEnum.detailed);
				pc.setInfoSource(new MetadataSource());

				populationCenters.addItem(pc);
				ln = reader.readLine();
			}
			gm.setPopulationCenters(populationCenters);
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading population center metadata.", exc);
		}
	}
}
