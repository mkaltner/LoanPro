package net.kaltner.LoanPro;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CalculatorPercentTest {
	@Test
	public void standalonePercentConvertsToDecimal() {
		assertEquals(0.10d, Calculator.calculatePercentValue(0.0d, 10.0d, false), 0.0d);
	}

	@Test
	public void pendingMathPercentUsesStoredValue() {
		assertEquals(5.0d, Calculator.calculatePercentValue(50.0d, 10.0d, true), 0.0d);
		assertEquals(3.375d, Calculator.calculatePercentValue(50.0d, 6.75d, true), 0.0d);
	}
}
