package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.IOException;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;

/**
 * Order metadata reader. Reads order metadata from data files.
 * 
 * @author Marios Skounakis
 * 
 */
public class OrderReader implements MetadataReader {
	String orderFilename = "orders.csv";

	@Override
	public void load(GameMetadata gm) throws IOException, MetadataReaderException {
		Container<OrderMetadata> orders = new Container<OrderMetadata>();

		try {
			BufferedReader reader = gm.getUTF8Resource(this.orderFilename,true);
			String ln;
			//TODO: OrderInfoView also reads orders.csv.....refactor one of them!
			ln = reader.readLine(); // ignore the first line.
			while ((ln = reader.readLine()) != null) {
				String[] parts = ln.split(";");
				OrderMetadata om = new OrderMetadata();
				om.setName(parts[0]);
				om.setCode(parts[2]);
				om.setNumber(Integer.parseInt(parts[1]));
				om.setParameters(parts[3]);
				om.setDifficulty(parts[4]);
				om.setRequirement(parts[5]);
				om.setSkillRequirement(parts[6]);
				orders.addItem(om);
			}
		} catch (IOException exc) {
			throw exc;
		} catch (Exception exc) {
			throw new MetadataReaderException("Error reading order metadata.", exc);
		}
		gm.setOrders(orders);
	}
}
