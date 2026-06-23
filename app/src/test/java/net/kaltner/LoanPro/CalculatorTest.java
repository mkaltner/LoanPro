package net.kaltner.LoanPro;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
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

	@Test
	public void doesNotSolveCurrentBucketAgainAfterManualPaymentEntry() {
		assertFalse(Calculator.shouldSolvePayment(Constants.VIEW_PAYMENT_PI));
		assertFalse(Calculator.shouldSolvePayment(Constants.VIEW_PAYMENT_PITI));
		assertFalse(Calculator.shouldSolvePayment(Constants.VIEW_PAYMENT_IO));
		assertTrue(Calculator.shouldSolveTerm(Constants.VIEW_PAYMENT_PI));
		assertTrue(Calculator.shouldSolveInterest(Constants.VIEW_PAYMENT_PI));
	}

	@Test
	public void doesNotSolveCurrentBucketAgainAfterManualTermEntry() {
		assertFalse(Calculator.shouldSolveTerm(Constants.VIEW_TERM));
		assertFalse(Calculator.shouldSolveTerm(Constants.VIEW_TERM_MONTH));
		assertTrue(Calculator.shouldSolvePayment(Constants.VIEW_TERM));
		assertTrue(Calculator.shouldSolveInterest(Constants.VIEW_TERM));
	}

	@Test
	public void doesNotSolveCurrentBucketAgainAfterManualInterestEntry() {
		assertFalse(Calculator.shouldSolveInterest(Constants.VIEW_INTEREST));
		assertFalse(Calculator.shouldSolveInterest(Constants.VIEW_INTEREST_MONTH));
		assertTrue(Calculator.shouldSolvePayment(Constants.VIEW_INTEREST));
		assertTrue(Calculator.shouldSolveTerm(Constants.VIEW_INTEREST));
	}

	@Test
	public void doesNotSolveCurrentAmountBucketAgainAfterManualAmountEntry() {
		assertFalse(Calculator.shouldSolvePrice(Constants.VIEW_PRICE));
		assertFalse(Calculator.shouldSolvePrice(Constants.VIEW_AMOUNT));
		assertFalse(Calculator.shouldSolveLoanAmount(Constants.VIEW_PRICE));
		assertFalse(Calculator.shouldSolveLoanAmount(Constants.VIEW_AMOUNT));
		assertTrue(Calculator.shouldSolvePayment(Constants.VIEW_PRICE));
		assertTrue(Calculator.shouldSolveTerm(Constants.VIEW_AMOUNT));
	}

	@Test
	public void identifiesPaymentTooLowForTermSolve() {
		assertEquals(Calculator.ERROR_PAYMENT_TOO_LOW, Calculator.getInvalidTermError(3000000.0d, 6000.0d, 6.0d));
		assertEquals(Calculator.ERROR_PAYMENT_TOO_LOW, Calculator.getInvalidTermError(3000000.0d, 15000.0d, 6.0d));
	}

	@Test
	public void usesGenericErrorForOtherInvalidTermInputs() {
		assertEquals(Calculator.ERROR_GENERIC, Calculator.getInvalidTermError(0.0d, 6000.0d, 6.0d));
		assertEquals(Calculator.ERROR_GENERIC, Calculator.getInvalidTermError(3000000.0d, 0.0d, 6.0d));
	}
}
