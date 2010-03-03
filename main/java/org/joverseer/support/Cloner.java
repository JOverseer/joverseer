package org.joverseer.support;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;



public class Cloner {
	public static Object clone(Object obj) {
		try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ObjectOutputStream os = new ObjectOutputStream(baos);
    		os.writeObject(obj);
    		os.close();
    		ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
    		Object ret = is.readObject();
    		return ret;
    	}
    	catch (Exception exc) {
    		throw new RuntimeException(exc);
    	}
	}
}
