package net.kaltner.LoanPro;

final class EntryBuffer {
	private String text;
	private boolean percent;

	void startWithDigit(int digit) {
		text = Integer.toString(digit);
		percent = false;
	}

	void startWithDecimal() {
		text = "0.";
		percent = false;
	}

	void appendDigit(int digit) {
		text += digit;
	}

	boolean appendDecimal() {
		if (text == null) {
			startWithDecimal();
			return true;
		}

		if (text.indexOf(".") >= 0) {
			return false;
		}

		text += ".";
		percent = false;
		return true;
	}

	void backspace() {
		if (text == null || text.length() == 0) {
			return;
		}

		text = text.substring(0, text.length() - 1);
		if (text.length() == 0) {
			clear();
		}
	}

	void multiplyByThousand() {
		if (text == null || getNumberMode() != Constants.MODE_INT) {
			return;
		}

		text = Long.toString((long)getValue() * 1000);
	}

	void applyPercent() {
		text = null;
		percent = true;
	}

	void clear() {
		text = null;
		percent = false;
	}

	boolean isActive() {
		return text != null || percent;
	}

	boolean canBackspace() {
		return text != null;
	}

	boolean isEmpty() {
		return text == null;
	}

	boolean isPercent() {
		return percent;
	}

	int getPrecision() {
		if (percent) {
			return 2;
		}

		if (text == null) {
			return 0;
		}

		int decimalIndex = text.indexOf(".");
		if (decimalIndex < 0) {
			return 0;
		}

		return text.length() - decimalIndex - 1;
	}

	int getNumberMode() {
		if (percent) {
			return Constants.MODE_PERCENT;
		}

		if (text != null && text.indexOf(".") >= 0) {
			return Constants.MODE_DOUBLE;
		}

		return Constants.MODE_INT;
	}

	boolean shouldShowDecimal() {
		return text != null && text.endsWith(".");
	}

	double getValue() {
		if (text == null || text.length() == 0) {
			return 0.0d;
		}

		String value = text.endsWith(".") ? text.substring(0, text.length() - 1) : text;
		if (value.length() == 0) {
			return 0.0d;
		}

		return Double.parseDouble(value);
	}
}
