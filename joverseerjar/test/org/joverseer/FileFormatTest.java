package org.joverseer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import org.joverseer.game.Game;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileFormatTest {

	public final static String f ="c:\\users\\Dave\\Documents\\middleearth\\game637noroad.jov";
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private byte[] readInputStream(InputStream is) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len;
	    
	    while ((len = is.read(buffer)) != -1) {
	    	baos.write(buffer,0,len);
	    }
	    return baos.toByteArray();
	}
	@Test
	public void test() throws Exception {
	    Game g = Game.loadGame(new File(f));
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(f));
//		byte[] original = readInputStream(in);
//		byte[] latest;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(g);
//        latest = baos.toByteArray();

//        AssertObjectStreams aos = new AssertObjectStreams(new ByteArrayInputStream(original), new ByteArrayInputStream(latest));
//        aos.assertStream();
	    in.close();
        
	}

}
