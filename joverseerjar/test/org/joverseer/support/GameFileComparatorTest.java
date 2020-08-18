package org.joverseer.support;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GameFileComparatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCompare() {
		GameFileComparator cut = new GameFileComparator();
		int actual = cut.compare(new File("g044n12t016.xml"), new File("g044n12t016.pdf"));
		assertEquals(-1, actual);
	}

}
