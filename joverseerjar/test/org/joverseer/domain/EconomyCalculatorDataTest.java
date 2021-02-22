package org.joverseer.domain;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EconomyCalculatorDataTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		EconomyCalculatorData ecd = new EconomyCalculatorData();
		ProductPrice pp = new ProductPrice();
		pp.product = ProductEnum.Steel;
		pp.buyPrice = 3;
		pp.marketTotal = 10000;
		ecd.setBidUnits(ProductEnum.Steel,10);
		assertEquals(0, ecd.ordersCost);
	}

}
