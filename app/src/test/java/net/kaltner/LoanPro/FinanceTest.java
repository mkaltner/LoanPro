package net.kaltner.LoanPro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FinanceTest {
	@Test
	public void calculatesMonthlyPayment() {
		assertEquals(1718.78d, Finance.calculateMonthlyPayment(265000.0d, 30.0d, 6.75d), 0.01d);
	}

	@Test
	public void calculatesLoanAmount() {
		assertEquals(208370.81d, Finance.calculateLoanAmount(1500.0d, 30.0d, 7.8d), 0.01d);
	}

	@Test
	public void calculatesTerm() {
		assertEquals(9.85d, Finance.calculateTerm(15000.0d, 200.0d, 10.0d), 0.01d);
	}

	@Test
	public void calculatesInterestRate() {
		assertEquals(13.78d, Finance.calculateInterest(98500.0d, 1150.0d, 30.0d), 0.01d);
	}

	@Test
	public void roundTripsThirtyYearSixPercentMortgage() {
		assertEquals(1798.65d, Finance.calculateMonthlyPayment(300000.0d, 30.0d, 6.0d), 0.01d);
		assertEquals(6.0d, Finance.calculateInterest(300000.0d, 1798.65d, 30.0d), 0.01d);
		assertEquals(30.0d, Finance.calculateTerm(300000.0d, 1798.65d, 6.0d), 0.01d);
	}

	@Test
	public void supportsZeroInterest() {
		assertEquals(1000.0d, Finance.calculateMonthlyPayment(12000.0d, 1.0d, 0.0d), 0.001d);
		assertEquals(12000.0d, Finance.calculateLoanAmount(1000.0d, 1.0d, 0.0d), 0.001d);
		assertEquals(1.0d, Finance.calculateTerm(12000.0d, 1000.0d, 0.0d), 0.001d);
		assertEquals(0.0d, Finance.calculateInterest(12000.0d, 1000.0d, 1.0d), 0.001d);
	}

	@Test
	public void rejectsImpossibleTermPayment() {
		assertTrue(Double.isNaN(Finance.calculateTerm(100000.0d, 100.0d, 10.0d)));
		assertTrue(Double.isNaN(Finance.calculateTerm(3000000.0d, 15000.0d, 6.0d)));
		assertTrue(Double.isNaN(Finance.calculateTerm(3000000.0d, 6000.0d, 6.0d)));
		assertTrue(Double.isNaN(Finance.calculateInterest(12000.0d, 500.0d, 1.0d)));
	}
}
