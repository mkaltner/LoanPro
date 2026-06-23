package net.kaltner.LoanPro;

//TODO: Update calculation based on order of operation or leave as is (like melvin's calc, windows calc) (add a setting to honor or not)
//TODO: Fix precision issue when doing things like 0.35% = (0.0035999999)
//TODO: Show math operations at the top of screen like windows 7 calc?
//TODO: Implement callbacks for updateScreen and when values are set in buttons (so that the main screen can show indicators, update itself, etc...)
//TODO: Consider BigDecimal for math operations

public class Calculator {
	private DoubleContainer _price = null;
	private DoubleContainer _downPayment = null;
	private DoubleContainer _loanAmount = null;
	private DoubleContainer _payment = null;
	private DoubleContainer _term = null;
	private DoubleContainer _interest = null;
	private DoubleContainer _tax = null;
	private DoubleContainer _insurance = null;
	private DoubleContainer _privateMortgageInsurance = null;

	private double storedValue = 0.0d;
	private Double screenValue = 0.0d;
	private double memoryValue = 0.0d;
	private final EntryBuffer entry = new EntryBuffer();

	private boolean newValue;
	private boolean clearScreen;
	private boolean performingCalculation;
	private boolean valueChanged;
	private boolean shiftEnabled;

	private int currentView;
	private int calculatorMode;
	private int currentAction;

	private int numberMode = Constants.MODE_INT;

	private MainActivity mainActivity = null;

	private int currentPrecision = 0;

	public Calculator(MainActivity main) {
		mainActivity = main;

		_price = new DoubleContainer(mainActivity, "price");
		_downPayment = new DoubleContainer(mainActivity, "downPayment");
		_loanAmount = new DoubleContainer(mainActivity, "loanAmount");
		_payment = new DoubleContainer(mainActivity, "payment");
		_term = new DoubleContainer(mainActivity, "term");
		_interest = new DoubleContainer(mainActivity, "interest");
		_tax = new DoubleContainer(mainActivity, "tax");
		_insurance = new DoubleContainer(mainActivity, "insurance");
		_privateMortgageInsurance = new DoubleContainer(mainActivity, "privateMortgageInsurance");
	}

	public DoubleContainer getPriceContainer() {
		return _price;
	}

	public DoubleContainer getDownPaymentContainer() {
		return _downPayment;
	}

	public DoubleContainer getLoanAmountContainer() {
		return _loanAmount;
	}

	public DoubleContainer getPaymentContainer() {
		return _payment;
	}

	public DoubleContainer getTermContainer() {
		return _term;
	}

	public DoubleContainer getInterestContainer() {
		return _interest;
	}

	public DoubleContainer getTaxContainer() {
		return _tax;
	}

	public DoubleContainer getInsuranceContainer() {
		return _insurance;
	}

	public DoubleContainer getPrivateMortgageInsuranceContainer() {
		return _privateMortgageInsurance;
	}

	public double getScreenValue() {
		return screenValue;
	}

	public int getCurrentView() {
		return currentView;
	}

	public int getNumberMode() {
		return entry.isActive() ? entry.getNumberMode() : numberMode;
	}

	public void shiftButtonClicked() {
		shiftEnabled = !shiftEnabled;
	}

	public boolean isShiftEnabled() {
		return shiftEnabled;
	}

	public void setShiftEnabled(boolean enabled) {
		shiftEnabled = false;
	}

    public int addNumberToScreen(int number) {
    	if (newValue == false || clearScreen || entry.isPercent()) {
    		entry.startWithDigit(number);
    	}
    	else if (entry.isEmpty()) {
    		entry.startWithDigit(number);
    	}
    	else {
    		entry.appendDigit(number);
    	}

    	syncEntryToScreen();

    	newValue = true;
    	clearScreen = false;

    	currentView = Constants.VIEW_NUMBERS;

    	return entry.getPrecision();
    }

    public boolean addPeriodToScreen() {
    	if (newValue == false || clearScreen) {
    		entry.startWithDecimal();
    	}
    	else if (!entry.appendDecimal()) {
    		return false;
    	}

    	syncEntryToScreen();
    	newValue = true;
    	clearScreen = false;

    	currentView = Constants.VIEW_NUMBERS;

    	return true;
    }

    public void removePeriodFromScreen() {
    	entry.backspace();
    	syncEntryToScreen();
    }

    public void addMathOperation(int action) {
    	newValue = true;
    	entry.clear();

    	if (performingCalculation) {
    		storedValue = performCalculation(storedValue, screenValue, currentAction);
    	}
    	else {
    		storedValue = screenValue;
    	}

    	screenValue = storedValue;

    	currentAction = action;
    	clearScreen = true;
    	performingCalculation = true;
    }

    public void completeCalculation() {
    	if (!performingCalculation) {
    		entry.clear();
    		clearScreen = true;
    		return;
    	}

    	screenValue = performCalculation(storedValue, screenValue, currentAction);
    	entry.clear();
    	clearScreen = true;
    	currentAction = Constants.ACTION_NONE;
    	performingCalculation = false;
    }

    public int back() {
    	int precision = 2;

    	if (canBackspace()) {
    		entry.backspace();

    		if (entry.isEmpty()) {
    			newValue = false;
    			clearScreen = true;
    			screenValue = 0.0d;
    			numberMode = Constants.MODE_INT;
    			currentPrecision = 0;
    			currentView = Constants.VIEW_CLEAR;
	        	precision = 2;
    		}
    		else {
    			syncEntryToScreen();
    			precision = currentPrecision;
    		}
    	}

    	return precision;
    }

    public boolean canBackspace() {
    	return isEnteringNumber() && entry.canBackspace();
    }

    public boolean canMakePercent() {
    	return performingCalculation || isEnteringNumber();
    }

    public int getEntryPrecision() {
    	return entry.getPrecision();
    }

    public boolean shouldShowEntryDecimal() {
    	return entry.shouldShowDecimal();
    }

    public double getEntryDisplayValue() {
    	return entry.getDisplayValue(screenValue);
    }

    public void clear() {
    	screenValue = 0.0d;
    	entry.clear();
    	numberMode = Constants.MODE_INT;
    	currentPrecision = 0;
    	newValue = false;
        clearScreen = true;
        currentView = Constants.VIEW_CLEAR;
        setShiftEnabled(false);
    }

    public void clearAll() {
    	newValue = false;
    	clearScreen = true;
    	entry.clear();
    	numberMode = Constants.MODE_INT;
    	currentPrecision = 0;
    	currentAction = Constants.ACTION_NONE;
    	performingCalculation = false;
    	valueChanged = false;
    	screenValue = 0.0d;
    	memoryValue = 0.0d;

    	_price.reset();
		_downPayment.reset();
		_tax.reset();
		_insurance.reset();
		_privateMortgageInsurance.reset();
		_loanAmount.reset();
		_payment.reset();
		_term.reset();
		_interest.reset();

		setShiftEnabled(false);

		currentView = Constants.VIEW_CLEAR;
    }

    public void addThousandToScreen() {
    	if (newValue && getNumberMode() == Constants.MODE_INT) {
    		entry.multiplyByThousand();
    		syncEntryToScreen();
    	}
    }

    public int makePercent() {
    	double displayValue;

    	if (performingCalculation) {
    		screenValue = calculatePercentValue(storedValue, screenValue, true);
    		displayValue = screenValue;
    		newValue = false;
    	}
    	else {
    		displayValue = screenValue;
    		screenValue = calculatePercentValue(storedValue, screenValue, false);
    		newValue = true;
    		clearScreen = false;
    		currentView = Constants.VIEW_NUMBERS;
    	}

    	numberMode = Constants.MODE_PERCENT;
    	entry.applyPercent(displayValue);
    	currentPrecision = 2;

    	return currentPrecision;
    }

    static double calculatePercentValue(double storedValue, double screenValue, boolean hasPendingMath) {
    	return hasPendingMath ? storedValue * (screenValue / 100.0d) : screenValue / 100.0d;
    }

    public void setPrice() {
	    	if (isEnteringNumber()) {
	    		_price.setUserValue(screenValue);
	    		newValue = false;
	    		entry.clear();

    		//_loanAmount.reset();

    		if (!_downPayment.isEmpty()) {
				_loanAmount.setValue(_price.getValue() - getDownPaymentAmount());
			}
			else {
				_loanAmount.setValue(_price.getValue());
			}

    		valueChanged = true;
    	}
    	else if (valueChanged && shouldSolvePrice(currentView)) {
    		setCalculatedValues();

    		if (_payment.hasValue() && _term.hasValue() && _interest.hasValue()) {
    			double loanAmount = Finance.calculateLoanAmount(_payment.getValue(), _term.getValue(), _interest.getValue());

    			_loanAmount.reset();
        		_loanAmount.setCalculatedValue(loanAmount);

        		if (_price.isLocked()) {
        			_downPayment.setValue(_price.getValue() - _loanAmount.getValue());
        		}
        		else {
        			if (!_downPayment.isEmpty()) {
            			_price.setValue(Finance.calculatePrice(_loanAmount.getValue(), _downPayment.getValue()));
            		}
            		else {
            			_price.setValue(_loanAmount.getValue());
            		}
        		}

        		valueChanged = false;
    		}
    	}

    	screenValue = getPriceAmount();

    	currentView = Constants.VIEW_PRICE;
    }

    public void setDownPayment() {
	    	if (isEnteringNumber()) {
    		double value = screenValue;

    		if (getNumberMode() == Constants.MODE_PERCENT) {
    			if (value > 100) {
    				value /= 100;
    			}
    			else if (value < 1) {
    				value *= 100;
    			}
    		}

    		// If SHIFT, set down payment as LTV
    		if (isShiftEnabled() && value <= 100) {
    			value = 100.0d - value;
    		}

	    		_downPayment.setUserValue(value);
	    		newValue = false;
	    		entry.clear();

    		if (_price.isLocked()) {
    			_loanAmount.setValue(_price.getValue() - getDownPaymentAmount());
    		}
    		else if (_loanAmount.isLocked() || _loanAmount.isCalculated()) {
    			_price.setValue(Finance.calculatePrice(_loanAmount.getValue(), _downPayment.getValue()));
    		}

    		if (_price.hasValue() || _loanAmount.hasValue() || _loanAmount.isCalculated()) {
    			valueChanged = true;
    		}
    	}

    	int nextView = Constants.VIEW_CLEAR;

    	if (isShiftEnabled()) {
    		nextView = Constants.VIEW_LOAN_TO_VALUE;
		}
    	else if (currentView != Constants.VIEW_DOWN_PAYMENT && currentView != Constants.VIEW_DOWN_PAYMENT_PERCENT && currentView != Constants.VIEW_LOAN_TO_VALUE) {
    		if (_downPayment.getValue() < 100) {
    			nextView = Constants.VIEW_DOWN_PAYMENT_PERCENT;
    		}
    		else {
    			nextView = Constants.VIEW_DOWN_PAYMENT;
    		}
    	}
    	else {
    		if (currentView == Constants.VIEW_DOWN_PAYMENT) {
    			if (_downPayment.getValue() < 100) {
    				nextView = Constants.VIEW_LOAN_TO_VALUE;
    			}
    			else {
    				nextView = Constants.VIEW_DOWN_PAYMENT_PERCENT;
    			}
    		}
    		else if (currentView == Constants.VIEW_DOWN_PAYMENT_PERCENT) {
    			if (_downPayment.getValue() < 100) {
    				nextView = Constants.VIEW_DOWN_PAYMENT;
    			}
    			else {
    				nextView = Constants.VIEW_LOAN_TO_VALUE;
    			}
    		}
    		else if (currentView == Constants.VIEW_LOAN_TO_VALUE) {
    			if (_downPayment.getValue() < 100) {
    				nextView = Constants.VIEW_DOWN_PAYMENT_PERCENT;
    			}
    			else {
    				nextView = Constants.VIEW_DOWN_PAYMENT;
    			}
    		}
    	}

    	setShiftEnabled(false);

    	if (nextView == Constants.VIEW_DOWN_PAYMENT_PERCENT) {
    		if (_downPayment.getValue() < 100) {
    			screenValue = _downPayment.getValue();
    		}
    		else {
    			screenValue = _downPayment.getValue() / getPriceAmount() * 100;
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_DOWN_PAYMENT) {
    		if (_downPayment.getValue() < 100) {
    			screenValue = getPriceAmount() * (_downPayment.getValue() / 100);
    		}
    		else {
    			screenValue = _downPayment.getValue();
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_LOAN_TO_VALUE) {
    		if (_downPayment.getValue() < 100) {
    			screenValue = 100.0d - _downPayment.getValue();
    		}
    		else {
    			screenValue = 100.0d - ((getDownPaymentAmount() / getPriceAmount()) * 100);
    		}

    		currentView = nextView;
    	}
    }

    public void setTax() {
	    	if (isEnteringNumber()) {
    		double value = screenValue;

    		if (getNumberMode() == Constants.MODE_PERCENT) {
    			if (value > 100) {
    				value /= 100;
    			}
    			else if (value < 1) {
    				value *= 100;
    			}
    		}

	    		_tax.setUserValue(value);
	    		newValue = false;
	    		entry.clear();
    	}

    	if (currentView != Constants.VIEW_TAX_PERCENT && currentView != Constants.VIEW_TAX_ANNUAL && currentView != Constants.VIEW_TAX_MONTH) {
    		if (_tax.getValue() < 100) {
    			currentView = Constants.VIEW_TAX_MONTH;
    		}
    		else {
    			currentView = Constants.VIEW_TAX_PERCENT;
    		}
    	}

    	if (currentView == Constants.VIEW_TAX_MONTH) {
    		if (_tax.getValue() < 100) {
    			screenValue = _tax.getValue();
    		}
    		else {
    			screenValue = _tax.getValue() / getPriceAmount() * 100;
    		}

    		currentView = Constants.VIEW_TAX_PERCENT;

    	}
    	else if (currentView == Constants.VIEW_TAX_PERCENT) {
    		if (_tax.getValue() < 100) {
    			screenValue = getPriceAmount() * (_tax.getValue() / 100);
    		}
    		else {
    			screenValue = _tax.getValue();
    		}

    		currentView = Constants.VIEW_TAX_ANNUAL;
    	}
    	else if (currentView == Constants.VIEW_TAX_ANNUAL) {
    		if (_tax.getValue() < 100) {
    			screenValue = getPriceAmount() * (_tax.getValue() / 100) / 12;
    		}
    		else {
    			screenValue = _tax.getValue() / 12;
    		}

    		currentView = Constants.VIEW_TAX_MONTH;
    	}
    }

    public void setInsurance() {
	    	if (isEnteringNumber()) {
    		double value = screenValue;

    		if (getNumberMode() == Constants.MODE_PERCENT) {
    			if (value > 100) {
    				value /= 100;
    			}
    			else if (value < 1) {
    				value *= 100;
    			}
    		}

    		if (isShiftEnabled()) {
    			_privateMortgageInsurance.setUserValue(value);
    		}
    		else {
    			_insurance.setUserValue(value);
    		}

	    		newValue = false;
	    		entry.clear();
	    	}

    	int nextView = Constants.VIEW_CLEAR;

    	if (isShiftEnabled()) {
    		// If we're not already on a PMI view, set the default view
    		if (currentView != Constants.VIEW_PMI_PERCENT && currentView != Constants.VIEW_PMI_ANNUAL && currentView != Constants.VIEW_PMI_MONTH) {
	    		if (_privateMortgageInsurance.getValue() < 100) {
	    			nextView = Constants.VIEW_PMI_PERCENT;
	    		}
	    		else {
	    			nextView = Constants.VIEW_PMI_ANNUAL;
	    		}
    		}
    	}

    	if (nextView == Constants.VIEW_CLEAR) {
    		// If we're not already on an insurance view, set the default view
    		if (currentView != Constants.VIEW_INSURANCE_PERCENT && currentView != Constants.VIEW_INSURANCE_ANNUAL && currentView != Constants.VIEW_INSURANCE_MONTH &&
    			currentView != Constants.VIEW_PMI_PERCENT && currentView != Constants.VIEW_PMI_ANNUAL && currentView != Constants.VIEW_PMI_MONTH) {
    			if (_insurance.getValue() < 100) {
    				nextView = Constants.VIEW_INSURANCE_PERCENT;
	    		}
	    		else {
	    			nextView = Constants.VIEW_INSURANCE_ANNUAL;
	    		}
    		}
    		else {
    			if (currentView == Constants.VIEW_INSURANCE_PERCENT) {
    				nextView = Constants.VIEW_INSURANCE_ANNUAL;
    			}
    			else if (currentView == Constants.VIEW_INSURANCE_ANNUAL) {
    				nextView = Constants.VIEW_INSURANCE_MONTH;
    			}
    			else if (currentView == Constants.VIEW_INSURANCE_MONTH) {
    				nextView = Constants.VIEW_INSURANCE_PERCENT;
    			}
    			if (currentView == Constants.VIEW_PMI_PERCENT) {
    				nextView = Constants.VIEW_PMI_ANNUAL;
    			}
    			else if (currentView == Constants.VIEW_PMI_ANNUAL) {
    				nextView = Constants.VIEW_PMI_MONTH;
    			}
    			else if (currentView == Constants.VIEW_PMI_MONTH) {
    				nextView = Constants.VIEW_PMI_PERCENT;
    			}
    		}
    	}

    	setShiftEnabled(false);

    	if (nextView == Constants.VIEW_INSURANCE_PERCENT) {
    		if (_insurance.getValue() < 100) {
    			screenValue = _insurance.getValue();
    		}
    		else {
    			screenValue = _insurance.getValue() / getPriceAmount() * 100;
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_INSURANCE_ANNUAL) {
    		if (_insurance.getValue() < 100) {
    			screenValue = getPriceAmount() * (_insurance.getValue() / 100);
    		}
    		else {
    			screenValue = _insurance.getValue();
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_INSURANCE_MONTH) {
    		if (_insurance.getValue() < 100) {
    			screenValue = getPriceAmount() * (_insurance.getValue() / 100) / 12;
    		}
    		else {
    			screenValue = _insurance.getValue() / 12;
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_PMI_PERCENT) {
    		if (_privateMortgageInsurance.getValue() < 100) {
    			screenValue = _privateMortgageInsurance.getValue();
    		}
    		else {
    			screenValue = _privateMortgageInsurance.getValue() / getLoanAmount() * 100;
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_PMI_ANNUAL) {
    		if (_privateMortgageInsurance.getValue() < 100) {
    			screenValue = getLoanAmount() * (_privateMortgageInsurance.getValue() / 100);
    		}
    		else {
    			screenValue = _privateMortgageInsurance.getValue();
    		}

    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_PMI_MONTH) {
    		if (_privateMortgageInsurance.getValue() < 100) {
    			screenValue = getLoanAmount() * (_privateMortgageInsurance.getValue() / 100) / 12;
    		}
    		else {
    			screenValue = _privateMortgageInsurance.getValue() / 12;
    		}

    		currentView = nextView;
    	}
    }

    public void setLoanAmount() {
	    	if (isEnteringNumber()) {
	    		_loanAmount.setUserValue(screenValue);
	    		newValue = false;
	    		entry.clear();

    		//_price.reset();

    		if (_price.isLocked()) {
    			_downPayment.setValue(_price.getValue() - _loanAmount.getValue());
    		}
    		else {
    			if (!_downPayment.isEmpty()) {
    				_price.setValue(_loanAmount.getValue() + getDownPaymentAmount());
    			}
    			else {
    				_price.setValue(_loanAmount.getValue());
    			}
    		}

    		valueChanged = true;
    	}
    	else if (valueChanged && shouldSolveLoanAmount(currentView)) {
    		setCalculatedValues();

    		if (_payment.hasValue() && _term.hasValue() && _interest.hasValue()) {
    			double loanAmount = Finance.calculateLoanAmount(_payment.getValue(), _term.getValue(), _interest.getValue());

        		_loanAmount.reset();
        		_loanAmount.setCalculatedValue(loanAmount);

        		if (_price.isLocked()) {
        			_downPayment.setValue(_price.getValue() - _loanAmount.getValue());
        		}
        		else {
        			if (!_downPayment.isEmpty()) {
            			_price.setValue(Finance.calculatePrice(_loanAmount.getValue(), _downPayment.getValue()));
            		}
            		else {
            			_price.setValue(_loanAmount.getValue());
            		}
        		}

        		valueChanged = false;
    		}
    	}

    	screenValue = getLoanAmount();

    	currentView = Constants.VIEW_AMOUNT;
    }

    public void setPayment() {
	    	if (isEnteringNumber()) {
	    		_payment.setUserValue(screenValue);
	    		newValue = false;
	    		entry.clear();
    		valueChanged = true;
    	}
    	else if (valueChanged && shouldSolvePayment(currentView)) {
    		setCalculatedValues();

    		if ((_loanAmount.hasValue() || _price.hasValue()) && _term.hasValue() && _interest.hasValue()) {

        		double payment = Finance.calculateMonthlyPayment(getLoanAmount(), _term.getValue(), _interest.getValue());

        		_payment.reset();
        		_payment.setCalculatedValue(payment);

        		valueChanged = false;
    		}
    	}

    	int nextView = Constants.VIEW_CLEAR;

    	if (currentView != Constants.VIEW_PAYMENT_PI && currentView != Constants.VIEW_PAYMENT_PITI && currentView != Constants.VIEW_PAYMENT_IO) {
    		nextView = Constants.VIEW_PAYMENT_PI;
    	}
    	else {
    		if (currentView == Constants.VIEW_PAYMENT_PI) {
    			nextView = Constants.VIEW_PAYMENT_PITI;
    		}
    		else if (currentView == Constants.VIEW_PAYMENT_PITI) {
    			nextView = Constants.VIEW_PAYMENT_IO;
    		}
    		else if (currentView == Constants.VIEW_PAYMENT_IO) {
    			nextView = Constants.VIEW_PAYMENT_PI;
    		}
    	}

    	if (nextView == Constants.VIEW_PAYMENT_PI) {
    		screenValue = _payment.getValue();
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_PAYMENT_PITI) {
    		screenValue = _payment.getValue() + getTaxMonthly() + getInsuranceMonthly() + getPrivateMortgageInsuranceMonthly();
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_PAYMENT_IO) {
    		screenValue = getLoanAmount() * (((_interest.getValue() / 100) / 360) * 30);
    		currentView = nextView;
    	}
    }

    public void setTerm() {
	    	if (isEnteringNumber() && !isShiftEnabled()) {
	    		_term.setUserValue(screenValue);
	    		newValue = false;
	    		entry.clear();

    		valueChanged = true;
    	}
    	else if (valueChanged && shouldSolveTerm(currentView)) {
    		setCalculatedValues();

    		if ((_loanAmount.hasValue() || _price.hasValue()) && _payment.hasValue() && _interest.hasValue()) {
    			double term = Finance.calculateTerm(getLoanAmount(), _payment.getValue(), _interest.getValue());

        		_term.reset();
        		if (isValidCalculatedResult(term)) {
        			_term.setCalculatedValue(term);
        			valueChanged = false;
        		}
        		else {
        			screenValue = term;
        			currentView = Constants.VIEW_TERM;
        			return;
        		}
    		}
    	}

    	int nextView = Constants.VIEW_CLEAR;

    	if (isShiftEnabled()) {
    		// If we're not already on a PMI view, set the default view
    		if (currentView != Constants.VIEW_BI_TERM && currentView != Constants.VIEW_BI_TOTAL_INT_SAVINGS && currentView != Constants.VIEW_BI_TOTAL_INT && currentView != Constants.VIEW_BI_TOTAL_PRINCIPAL && currentView != Constants.VIEW_BI_TOTAL_PI) {
	    		nextView = Constants.VIEW_BI_TERM;
    		}
    	}

    	if (nextView == Constants.VIEW_CLEAR) {
	    	if (currentView != Constants.VIEW_TERM && currentView != Constants.VIEW_TERM_MONTH &&
	    		currentView != Constants.VIEW_BI_TERM && currentView != Constants.VIEW_BI_TOTAL_INT_SAVINGS && currentView != Constants.VIEW_BI_TOTAL_INT && currentView != Constants.VIEW_BI_TOTAL_PRINCIPAL && currentView != Constants.VIEW_BI_TOTAL_PI) {
	    		nextView = Constants.VIEW_TERM;
	    	}
	    	else {
	    		if (currentView == Constants.VIEW_TERM) {
	    			nextView = Constants.VIEW_TERM_MONTH;
	    		}
	    		else if (currentView == Constants.VIEW_TERM_MONTH) {
	    			nextView = Constants.VIEW_TERM;
	    		}
				else if (currentView == Constants.VIEW_BI_TERM) {
					nextView = Constants.VIEW_BI_TOTAL_INT_SAVINGS;
				}
				else if (currentView == Constants.VIEW_BI_TOTAL_INT_SAVINGS) {
					nextView = Constants.VIEW_BI_TOTAL_INT;
				}
				else if (currentView == Constants.VIEW_BI_TOTAL_INT) {
					nextView = Constants.VIEW_BI_TOTAL_PRINCIPAL;
				}
				else if (currentView == Constants.VIEW_BI_TOTAL_PRINCIPAL) {
					nextView = Constants.VIEW_BI_TOTAL_PI;
				}
				else if (currentView == Constants.VIEW_BI_TOTAL_PI) {
					nextView = Constants.VIEW_BI_TERM;
				}
	    	}
    	}

    	setShiftEnabled(false);

    	if (nextView == Constants.VIEW_TERM) {
    		screenValue = _term.getValue();
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_TERM_MONTH) {
    		screenValue = _term.getValue() * 12;
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_BI_TERM) {
    		screenValue = Finance.calculateTerm(getLoanAmount(), _payment.getValue() / 2, _interest.getValue(), 26);
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_BI_TOTAL_INT_SAVINGS) {
    		double termYears = Finance.calculateTerm(getLoanAmount(), _payment.getValue() / 2, _interest.getValue(), 26);
    		double biInterest = getTotalInterest(_payment.getValue() / 2, termYears, getLoanAmount(), 26);
    		double mInterest = getTotalInterest(_payment.getValue(), _term.getValue(), getLoanAmount(), 12);
    		screenValue = mInterest - biInterest;
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_BI_TOTAL_INT) {
    		double termYears = Finance.calculateTerm(getLoanAmount(), _payment.getValue() / 2, _interest.getValue(), 26);
    		screenValue = getTotalInterest(_payment.getValue() / 2, termYears, getLoanAmount(), 26);
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_BI_TOTAL_PRINCIPAL) {
    		screenValue = getLoanAmount();
    		currentView = nextView;
    	}
    	else if (nextView == Constants.VIEW_BI_TOTAL_PI) {
    		double termYears = Finance.calculateTerm(getLoanAmount(), _payment.getValue() / 2, _interest.getValue(), 26);
    		screenValue = (_payment.getValue() / 2) * (termYears * 26);
    		currentView = nextView;
    	}
    }

    public void setInterest() {
    	if (isEnteringNumber()) {
    		double value = screenValue;

    		if (value > 100) {
    			screenValue = Double.NaN;
    			return;
    		}

    		if (getNumberMode() == Constants.MODE_PERCENT) {
    			if (value > 100) {
    				value /= 100;
    			}
    			else if (value < 1) {
    				value *= 100;
    			}
    		}

	    		_interest.setUserValue(value);
	    		newValue = false;
	    		entry.clear();

    		valueChanged = true;
    	}
    	else if (valueChanged && shouldSolveInterest(currentView)) {
    		setCalculatedValues();

    		if ((_loanAmount.hasValue() || _price.hasValue()) && _payment.hasValue() && _term.hasValue()) {

    			double interestRate = Finance.calculateInterest(getLoanAmount(), _payment.getValue(), _term.getValue());

    			_interest.reset();
    			_interest.setCalculatedValue(interestRate);

        		valueChanged = false;
    		}
    	}

    	if (currentView != Constants.VIEW_INTEREST && currentView != Constants.VIEW_INTEREST_MONTH) {
    		currentView = Constants.VIEW_INTEREST_MONTH;
    	}

    	if (currentView == Constants.VIEW_INTEREST_MONTH) {
    		screenValue = _interest.getValue();
    		currentView = Constants.VIEW_INTEREST;
    	}
    	else if (currentView == Constants.VIEW_INTEREST) {
    		screenValue = _interest.getValue() / 12;
    		currentView = Constants.VIEW_INTEREST_MONTH;
    	}
    }

    private boolean isEnteringNumber() {
    	return newValue && currentView == Constants.VIEW_NUMBERS;
    }

    private void syncEntryToScreen() {
    	screenValue = entry.getValue();
    	numberMode = entry.getNumberMode();
    	currentPrecision = entry.getPrecision();
    }

    private double performCalculation(double value1, double value2, int action) {
    	switch(action) {
    	case Constants.ACTION_ADD:
    		return value1 + value2;
    	case Constants.ACTION_SUBTRACT:
    		return value1 - value2;
    	case Constants.ACTION_MULTIPLY:
    		return value1 * value2;
    	case Constants.ACTION_DIVIDE:
    		return value1 / value2;
    	default:
    		return Double.MIN_VALUE;
    	}
    }

    static boolean isValidCalculatedResult(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value);
    }

    static boolean shouldSolvePrice(int currentView) {
    	return currentView != Constants.VIEW_PRICE &&
    		currentView != Constants.VIEW_AMOUNT &&
    		currentView != Constants.VIEW_DOWN_PAYMENT &&
    		currentView != Constants.VIEW_DOWN_PAYMENT_PERCENT &&
    		currentView != Constants.VIEW_LOAN_TO_VALUE;
    }

    static boolean shouldSolveLoanAmount(int currentView) {
    	return currentView != Constants.VIEW_AMOUNT &&
    		currentView != Constants.VIEW_PRICE &&
    		currentView != Constants.VIEW_DOWN_PAYMENT &&
    		currentView != Constants.VIEW_DOWN_PAYMENT_PERCENT &&
    		currentView != Constants.VIEW_LOAN_TO_VALUE;
    }

    static boolean shouldSolvePayment(int currentView) {
    	return currentView != Constants.VIEW_PAYMENT_PI &&
    		currentView != Constants.VIEW_PAYMENT_PITI &&
    		currentView != Constants.VIEW_PAYMENT_IO;
    }

    static boolean shouldSolveTerm(int currentView) {
    	return currentView != Constants.VIEW_TERM &&
    		currentView != Constants.VIEW_TERM_MONTH &&
    		currentView != Constants.VIEW_BI_TERM &&
    		currentView != Constants.VIEW_BI_TOTAL_INT_SAVINGS &&
    		currentView != Constants.VIEW_BI_TOTAL_INT &&
    		currentView != Constants.VIEW_BI_TOTAL_PRINCIPAL &&
    		currentView != Constants.VIEW_BI_TOTAL_PI;
    }

    static boolean shouldSolveInterest(int currentView) {
    	return currentView != Constants.VIEW_INTEREST &&
    		currentView != Constants.VIEW_INTEREST_MONTH;
    }

    private double getDownPaymentAmount() {
    	double value = 0.0d;

    	if (!_price.isEmpty()) {
    		value = (_downPayment.getValue() < 100) ? _price.getValue() * (_downPayment.getValue() / 100) : _downPayment.getValue();
    	}
    	else if (!_loanAmount.isEmpty()) {
    		value = (_downPayment.getValue() < 100) ? ((_loanAmount.getValue() / (1 - (_downPayment.getValue()) / 100) - _loanAmount.getValue())) : _downPayment.getValue();
    	}
    	else {
    		value = 0.0d;
    	}

    	return value;
    }

    public double getPriceAmount() {
    	double value = 0.0d;
    	if (_price.hasValue() || _price.isCalculated()) {
    		value = _price.getValue();
    	}
    	else if (_loanAmount.hasValue() || _loanAmount.isCalculated()) {
    		value = _loanAmount.getValue() + getDownPaymentAmount();
    	}
    	else {
    		value = 0.0d;
    	}
    	return value;
    }

    public double getLoanAmount() {
    	double value = 0.0d;
    	if (_loanAmount.hasValue() || _loanAmount.isCalculated()) {
    		value = _loanAmount.getValue();
    	}
    	else if (_price.hasValue() || _price.isCalculated()) {
    		value = _price.getValue() - getDownPaymentAmount();
    	}
    	else {
    		value = 0.0d;
    	}
    	return value;
    }

    private double getTaxMonthly() {
    	double value = 0.0d;

    	if (_tax.getValue() < 100) {
			value = getPriceAmount() * (_tax.getValue() / 100) / 12;
		}
    	else {
    		value = _tax.getValue() / 12;
    	}

		return value;
    }

    private double getInsuranceMonthly() {
    	double value = 0.0d;

    	if (_insurance.getValue() < 100) {
			value = getPriceAmount() * (_insurance.getValue() / 100) / 12;
		}
    	else {
    		value = _insurance.getValue() / 12;
    	}

    	return value;
    }

    private double getPrivateMortgageInsuranceMonthly() {
    	double value = 0.0d;

    	if (_privateMortgageInsurance.getValue() < 100) {
			value = getLoanAmount() * (_privateMortgageInsurance.getValue() / 100) / 12;
		}
    	else {
    		value = _privateMortgageInsurance.getValue() / 12;
    	}

    	return value;
    }

    private double getTotalInterest(double payment, double termYears, double loanAmount, int paymentsPerYear) {
		return ((payment * (termYears * paymentsPerYear)) - loanAmount);
	}

    private void setCalculatedValues() {
    	double value = 0.0d;

    	// TODO how to handle locked values?

    	if (_price.isCalculated()) {
    		value = _price.getValue();
    		_price.reset();
    		_price.setValue(value);
    	}

    	if (_loanAmount.isCalculated()) {
    		value = _loanAmount.getValue();
    		_loanAmount.reset();
    		_loanAmount.setValue(value);
    	}

    	if (_payment.isCalculated()) {
    		value = _payment.getValue();
    		_payment.reset();
    		_payment.setValue(value);
    	}

    	if (_term.isCalculated()) {
    		value = _term.getValue();
    		_term.reset();
    		_term.setValue(value);
    	}

    	if (_interest.isCalculated()) {
    		value = _interest.getValue();
    		_interest.reset();
    		_interest.setValue(value);
    	}
    }

    public void addMemory() {
    	memoryValue = screenValue;
    	newValue = false;
    }

    public void recallMemory() {
    	screenValue = memoryValue;
    	newValue = false;
        clearScreen = true;
        currentView = Constants.VIEW_NONE;
    }
}
