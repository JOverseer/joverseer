package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;


public class SpellReader  implements MetadataReader {
    String spellFilename = "spells.csv";

    public String getSpellFilename(GameMetadata gm) {
        return "file:///" + gm.getBasePath() + "/" + spellFilename;
    }

    public void load(GameMetadata gm) throws IOException, MetadataReaderException {
        Container spells = new Container();

        try {
            //Resource resource = Application.instance().getApplicationContext().getResource(getSpellFilename(gm));
            Resource resource = gm.getResource(spellFilename);

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(";");
                SpellInfo si = new SpellInfo();
                si.setName(parts[0]);
                si.setDifficulty(parts[1]);
                si.setOrderNumber(Integer.parseInt(parts[2]));
                si.setNumber(Integer.parseInt(parts[3]));
                si.setRequiredInfo(parts[4]);
                si.setRequirements(parts[5]);
                si.setDescription(parts[6]);
                si.setList(parts[7]);
                spells.addItem(si);
            }
        }
        catch (IOException exc) {
            throw exc;
         }
         catch (Exception exc) {
             throw new MetadataReaderException("Error reading spell metadata.", exc);
         }
        gm.setSpells(spells);
    }
}
