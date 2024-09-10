package org.txt2xml.cli;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BatchTest {

	final String OUTPUT_FILE =  "src/org/txt2xml/cli/sample.txt.xml";
	public static void setup() {
		
	}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		Files.deleteIfExists(Paths.get(OUTPUT_FILE));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMain() {
		final String EOL="\r\n";
		try {
			Batch.main(new String[]{"src/org/txt2xml/cli/sample.xml","src/org/txt2xml/cli/sample.txt"});
		byte[] encoded = Files.readAllBytes(Paths.get(OUTPUT_FILE));
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><txt2xml>"+EOL+
					"<line>"+EOL+
					"<number>"+EOL+
					"<type>1</type>"+EOL+
					"</number>"+EOL+
					"<number>"+EOL+
					"<type>56</type>"+EOL+
					"</number>"+EOL+
					"<number>"+EOL+
					"<type>8</type>"+EOL+
					"</number>"+EOL+
					"<field>8</field>"+EOL+
					"<field>2</field>"+EOL+
					"<field>3</field>"+EOL+
					"<field>4</field>"+EOL+
					"</line>"+EOL+
					"<line>"+EOL+
					"<field>a</field>"+EOL+
					"<field>b</field>"+EOL+
					"<field>c</field>"+EOL+
					"</line>"+EOL+
					"<line>"+EOL+
					"<field>x</field>"+EOL+
					"<field>y</field>"+EOL+
					"<field>z</field>"+EOL+
					"</line>"+EOL+
					"</txt2xml>" +EOL
					,new String(encoded, StandardCharsets.UTF_8));

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		fail("exception");
	}
	}
}
