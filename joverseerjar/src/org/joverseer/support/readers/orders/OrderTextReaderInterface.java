package org.joverseer.support.readers.orders;

import java.io.BufferedReader;

public interface OrderTextReaderInterface
{
	void readOrders(BufferedReader reader) throws Exception;
	int getOrdersRead();
}
