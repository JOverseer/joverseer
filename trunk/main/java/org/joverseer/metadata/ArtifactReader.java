package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.Container;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;


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
                ArtifactInfo artifact = new ArtifactInfo();
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