package org.joverseer.metadata;

import org.joverseer.metadata.domain.Artifact;
import org.joverseer.support.Container;
import org.springframework.richclient.application.Application;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 24 Οκτ 2006
 * Time: 11:34:51 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArtifactReader implements MetadataReader {
    String artifactFilename = "arties.csv";

    public String getArtifactFilename(GameMetadata gm) {
        return "file:///" + gm.getBasePath() + "/" + gm.getGameType().toString() + "." + artifactFilename;
    }

    public void load(GameMetadata gm) {
        gm.setArtifacts(loadArtifacts(gm));
    }

    private Container loadArtifacts(GameMetadata gm) {
        Container artifacts = new Container();

        try {
            Resource resource = Application.instance().getApplicationContext().getResource(getArtifactFilename(gm));

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));

            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(";");
                int no = Integer.parseInt(parts[0]);
                String name = parts[1];
                String power1 = parts[3];
                String bonus = parts[4];
                power1 += " " + bonus;
                String owner = (parts.length == 7 ? parts[6] : "");
                String alignment = parts[2];
                String power2 = (parts.length >= 6 ? parts[5] : "");
                Artifact artifact = new Artifact();
                artifact.setNo(no);
                artifact.setName(name);
                artifact.setOwner(owner);
                artifact.setAlignment(alignment);
                artifact.getPowers().add(power1);
                if (!power2.equals("")) {
                    artifact.getPowers().add(power2);
                }
                artifacts.addItem(artifact);
            }
        }
        catch (IOException exc) {
            // todo see
            // do nothing
            int a = 1;
        }
        return artifacts;
    }
}
