package org.joverseer.metadata;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.domain.*;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 27 ��� 2006
 * Time: 8:57:34 ��
 * To change this template use File | Settings | File Templates.
 */
public class PopulationCenterReader implements MetadataReader {
    String populationCenterFilename = "pcs";

    public String getPopulationCenterFilename(GameMetadata gm) {
        return "file:///" + gm.getBasePath() + "/" + gm.getGameType().toString() + "." + populationCenterFilename;
    }

    public void load(GameMetadata gm) {
        Container populationCenters = new Container();
        try {
            Resource resource = Application.instance().getApplicationContext().getResource(getPopulationCenterFilename(gm));
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String ln;
            while ((ln = reader.readLine()) != null) {
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
                    fortSize = FortificationSizeEnum.keep;
                } else if (fortification.equals("4")) {
                    fortSize = FortificationSizeEnum.castle;
                } else if (fortification.equals("5")) {
                    fortSize = FortificationSizeEnum.citadel;
                }

                HarborSizeEnum harborSize = HarborSizeEnum.none;
                if (harbor.equals("1")) {
                    harborSize = HarborSizeEnum.harbor;
                } else if (harbor.equals("2")) {
                    harborSize = HarborSizeEnum.port;
                }

                int nationNo = Integer.parseInt(nation);

                PopulationCenter pc = new PopulationCenter();
                pc.setName(name);
                pc.setNationNo(nationNo);
                pc.setSize(pcSize);
                pc.setFortification(fortSize);
                pc.setHarbor(harborSize);
                pc.setX(x);
                pc.setY(y);

                pc.setInformationSource(InformationSourceEnum.detailed);
                pc.setInfoSource(new MetadataSource());

                populationCenters.addItem(pc);
            }
            gm.setPopulationCenters(populationCenters);
        }
        catch (IOException exc) {
            // todo see
            // do nothing
            int a = 1;
        }
    }
}