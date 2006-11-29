package org.joverseer.metadata;

import org.joverseer.support.Container;
import org.joverseer.metadata.domain.Artifact;
import org.joverseer.metadata.orders.OrderMetadata;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 10 Íןו 2006
 * Time: 10:40:05 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderReader implements MetadataReader {
    String orderFilename = "orders.csv";

    public String getOrderFilename(GameMetadata gm) {
        return "file:///" + gm.getBasePath() + orderFilename;
    }

    public void load(GameMetadata gm) {
        Container orders = new Container();

        try {
            Resource resource = Application.instance().getApplicationContext().getResource(getOrderFilename(gm));

            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String ln;
            while ((ln = reader.readLine()) != null) {
                String[] parts = ln.split(";");
                OrderMetadata om = new OrderMetadata();
                om.setName(parts[0]);
                om.setCode(parts[2]);
                om.setNumber(Integer.parseInt(parts[1]));
                om.setParameters(parts[3]);
                om.setDifficulty(parts[4]);
                om.setRequirement(parts[5]);
                orders.addItem(om);
            }
        }
        catch (IOException exc) {
            // todo see
            // do nothing
            int a = 1;
        }
        gm.setOrders(orders);
    }
}
