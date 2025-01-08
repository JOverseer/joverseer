package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.metadata.domain.ClimateModifier;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.TerrainModifier;
import org.joverseer.support.Container;


public class CombatModifierReader implements MetadataReader {
	String climateFilename = "climatemodifiers.csv";
	String terrainFilename = "terrainmodifiers.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<TerrainModifier> terrainModifiers = new Container<TerrainModifier>();
		try {

			BufferedReader reader = gm.getUTF8ResourceByGame(this.terrainFilename,true);

			String ln;
			while ((ln = reader.readLine()) != null) {
				ln = ln.replace("\"", "");
				String parts[] = ln.split(",");
				
				int NationNo = Integer.parseInt(parts[0]);
				int TerrainID = Integer.parseInt(parts[1]);
				Double Modifier = Double.parseDouble(parts[2]);
				
				TerrainModifier m = new TerrainModifier();
				
				m.setNationNo(NationNo);
				m.setModifier(Modifier);
				
				switch(TerrainID) {
				// The Terrain in the data file is different to the JO Enum
				case 0:
					m.setTerrain(HexTerrainEnum.ocean);
					break;

				case 1:
					m.setTerrain(HexTerrainEnum.sea);
					break;
					
				case 2:
					m.setTerrain(HexTerrainEnum.shore);
					break;

				case 3:
					m.setTerrain(HexTerrainEnum.plains);
					break;
				
				case 4:
					m.setTerrain(HexTerrainEnum.hillsNrough);
					break;
				
				case 5:
					m.setTerrain(HexTerrainEnum.forest);
					break;
					
				case 6:
					m.setTerrain(HexTerrainEnum.desert);
					break;
					
				case 7:
					m.setTerrain(HexTerrainEnum.swamp);
					break;
					
				case 8:
					m.setTerrain(HexTerrainEnum.mountains);
					break;
					
				}
								
				terrainModifiers.addItem(m);
			}
			
		} catch (IOException exc) {
			// Problems here will just lead to an empty collection and no modifier - could be new game tyoe or similar
		} catch (Exception exc) {
			// Problems here will just lead to an empty collection and no modifier - could be new game tyoe or similar
		}		
		gm.setTerrainModifiers(terrainModifiers);
	
		Container<ClimateModifier> climateModifiers = new Container<ClimateModifier>();
		
		try {
	
			BufferedReader reader = gm.getUTF8ResourceByGame(this.climateFilename,true);
	
			String ln;
			while ((ln = reader.readLine()) != null) {
				System.out.println(ln);
				ln = ln.replace("\"", "");
				String parts[] = ln.split(",");
				
				int NationNo = Integer.parseInt(parts[0]);
				int ClimateID = Integer.parseInt(parts[1]);
				Double Modifier = Double.parseDouble(parts[2]);
				
				ClimateModifier m = new ClimateModifier();
				
				m.setNationNo(NationNo);
				m.setModifier(Modifier);
				
				switch(ClimateID) {
				
				case 1:
					m.setClimate(ClimateEnum.Polar);
					break;
					
				case 2:
					m.setClimate(ClimateEnum.Severe);
					break;
	
				case 3:
					m.setClimate(ClimateEnum.Cold);
					break;
				
				case 4:
					m.setClimate(ClimateEnum.Cool);
					break;
				
				case 5:
					m.setClimate(ClimateEnum.Mild);
					break;
					
				case 6:
					m.setClimate(ClimateEnum.Warm);
					break;
					
				case 7:
					m.setClimate(ClimateEnum.Hot);
					break;
					
				}
								
				climateModifiers.addItem(m);
			}
			
		} catch (IOException exc) {
			// Problems here will just lead to an empty collection and no modifier - could be new game tyoe or similar
		} catch (Exception exc) {
			// Problems here will just lead to an empty collection and no modifier - could be new game tyoe or similar
		}	

		gm.setClimateModifiers(climateModifiers);
	}	
	
}
