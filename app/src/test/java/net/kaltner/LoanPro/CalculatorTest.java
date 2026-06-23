package net.kaltner.LoanPro;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CalculatorTest {
	@Test
	public void acceptsOnlyFiniteCalculatedResults() {
		assertTrue(Calculator.isValidCalculatedResult(10.0d));
		assertFalse(Calculator.isValidCalculatedResult(Double.NaN));
		assertFalse(Calculator.isValidCalculatedResult(Double.POSITIVE_INFINITY));
		assertFalse(Calculator.isValidCalculatedResult(Double.NEGATIVE_INFINITY));
	}
}
