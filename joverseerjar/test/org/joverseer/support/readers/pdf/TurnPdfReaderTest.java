package org.joverseer.support.readers.pdf;

import junit.framework.TestCase;
import java.io.File;

public class TurnPdfReaderTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testReadFile() {
		TurnPdfReader r = new TurnPdfReader(null, "C:\\Users\\Dave\\Documents\\GitHub\\joverseer\\joverseerjar\\test\\org\\joverseer\\support\\readers\\pdf\\test1.pdf");
		r.pdfTextFile = new File(r.filename + ".txt");
		try {
			r.parsePdf();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("exception");
		}
		assertTrue("no output file",r.pdfTextFile.exists());
		r = new TurnPdfReader(null, "C:\\Users\\Dave\\Documents\\GitHub\\joverseer\\joverseerjar\\test\\org\\joverseer\\support\\readers\\pdf\\test2.pdf");
		r.pdfTextFile = new File(r.filename + ".txt");
		try {
			r.parsePdf();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("exception");
		}
		assertTrue("no output file",r.pdfTextFile.exists());
		
	}

}
