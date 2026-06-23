package net.kaltner.LoanPro;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EntryBufferTest {
	@Test
	public void preservesTypedDecimalState() {
		EntryBuffer entry = new EntryBuffer();

		entry.startWithDigit(3);
		entry.appendDigit(0);
		assertEquals(30.0d, entry.getValue(), 0.0d);
		assertEquals(Constants.MODE_INT, entry.getNumberMode());
		assertFalse(entry.shouldShowDecimal());

		assertTrue(entry.appendDecimal());
		assertEquals(30.0d, entry.getValue(), 0.0d);
		assertEquals(Constants.MODE_DOUBLE, entry.getNumberMode());
		assertEquals(0, entry.getPrecision());
		assertTrue(entry.shouldShowDecimal());

		entry.appendDigit(5);
		assertEquals(30.5d, entry.getValue(), 0.0d);
		assertEquals(1, entry.getPrecision());
		assertFalse(entry.shouldShowDecimal());
	}

	@Test
	public void backspaceClearsOnlyActiveText() {
		EntryBuffer entry = new EntryBuffer();

		entry.startWithDigit(1);
		entry.appendDigit(2);
		entry.appendDecimal();
		entry.appendDigit(3);

		entry.backspace();
		assertEquals(12.0d, entry.getValue(), 0.0d);
		assertTrue(entry.shouldShowDecimal());

		entry.backspace();
		entry.backspace();
		entry.backspace();
		assertFalse(entry.isActive());
		assertFalse(entry.canBackspace());
	}

	@Test
	public void percentModeIsTerminalForEditing() {
		EntryBuffer entry = new EntryBuffer();

		entry.startWithDigit(6);
		entry.appendDecimal();
		entry.appendDigit(7);
		entry.appendDigit(5);
		entry.applyPercent();

		assertTrue(entry.isActive());
		assertTrue(entry.isPercent());
		assertFalse(entry.canBackspace());
		assertEquals(Constants.MODE_PERCENT, entry.getNumberMode());
		assertEquals(2, entry.getPrecision());
	}
}
