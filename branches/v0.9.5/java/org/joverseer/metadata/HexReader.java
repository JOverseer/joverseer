package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 10:03:25 PM
 *
 * Loads hex information from the given files
 */
public class HexReader implements MetadataReader {
    String terrainFilename = "terrain";
    String trafficFilename = "traffic";

    public String getTerrainFilename(GameMetadata gm) {
        return "file:///" + gm.getBasePath() + "/" + gm.getGameType().toString() + "." + terrainFilename;
    }

    public String getTrafficFilename(GameMetadata gm) {
        return "file:///" + gm.getBasePath() + "/" + gm.getGameType().toString() + "." + trafficFilename;
    }

    public void load(GameMetadata gm) throws IOException, MetadataReaderException {
        HashMap hexes = loadHexes(gm);
        loadTraffic(hexes, gm);
        gm.setHexes(hexes.values());
    }

    private HashMap loadHexes(GameMetadata gm) throws IOException, MetadataReaderException {
        HashMap hexes = new HashMap();
        try {
            //Resource resource = Application.instance().getApplicationContext().getResource(getTerrainFilename(gm));
            Resource resource = gm.getResource(gm.getGameType().toString() + "." + terrainFilename);

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(",");
                parts[0] = parts[0].replaceAll("\"", "");
                int no = Integer.parseInt(parts[0]);
                int x = no / 100;
                int y = no % 100;
                Hex hex = new Hex();
                hex.setColumn(x);
                hex.setRow(y);
                int t = Integer.parseInt(parts[1]);
                hex.setTerrain(HexTerrainEnum.fromValue(t));
                hexes.put(no, hex);
            }
        }
        catch (IOException exc) {
            throw exc;
        }
        catch (Exception exc) {
            throw new MetadataReaderException("Error reading hex metadata.", exc);
        }
        return hexes;
    }

    private void loadTraffic(HashMap hexes, GameMetadata gm) throws IOException, MetadataReaderException {
        try {
            //Resource resource = Application.instance().getApplicationContext().getResource(getTrafficFilename(gm));
            Resource resource = gm.getResource(gm.getGameType().toString() + "." + trafficFilename);

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(",");
                parts[0] = parts[0].replaceAll("\"", "");
                int no = Integer.parseInt(parts[0]);
                Hex hex = (Hex)hexes.get(no);

                int iside = Integer.parseInt(parts[2]);
                int itype = Integer.parseInt(parts[3]);

                HexSideEnum side = null;
                if (iside == 1) side = HexSideEnum.TopRight;
                if (iside == 2) side = HexSideEnum.Right;
                if (iside == 3) side = HexSideEnum.BottomRight;
                if (iside == 4) side = HexSideEnum.BottomLeft;
                if (iside == 5) side = HexSideEnum.Left;
                if (iside == 6) side = HexSideEnum.TopLeft;
                HexSideElementEnum element = HexSideElementEnum.fromValue(itype);

                hex.addHexSideElement(side, element);
            }
        }
        catch (IOException exc) {
            throw exc;
        }
        catch (Exception exc) {
            throw new MetadataReaderException("Error reading traffic metadata.", exc);
        }
    }
}
