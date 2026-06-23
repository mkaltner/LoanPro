package net.kaltner.LoanPro;

import java.io.InputStream;
import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView screen;
	private TextView screenType;
	private TextView screenMode;
	private TextView screenView;
	private TextView screenFrequency;

	private Button buttonClear;

	private Button buttonPrice;
	private Button buttonDownPayment;
	private Button buttonTax;
	private Button buttonInsurance;
	private Button buttonBack;

	private Button buttonLoanAmount;
	private Button buttonPayment;
	private Button buttonTerm;
	private Button buttonInterest;
	private Button buttonPercent;

	private Button buttonShift;
	private Button buttonSeven;
	private Button buttonEight;
	private Button buttonNine;
	private Button buttonDivide;

	private Button buttonRecall;
	private Button buttonFour;
	private Button buttonFive;
	private Button buttonSix;
	private Button buttonMultiply;

	private Button buttonMemory;
	private Button buttonOne;
	private Button buttonTwo;
	private Button buttonThree;
	private Button buttonSubtract;

	private Button buttonThousand;
	private Button buttonZero;
	private Button buttonPeriod;
	private Button buttonEquals;
	private Button buttonAdd;

	private Calculator calc = null;
	private int currentPrecision = 2;

    @Override
    public void onStart() {
        super.onStart();
        Utils.showChangeLog(this);
    }

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        applySystemBarInsets();

        calc = new Calculator(this);

        screen = (TextView)findViewById(R.id.screen);
        screenType = (TextView)findViewById(R.id.screenType);
        screenMode = (TextView)findViewById(R.id.screenMode);
        screenView = (TextView)findViewById(R.id.screenView);
        screenFrequency = (TextView)findViewById(R.id.screenFrequency);

        this.setTitle(R.string.app_title);

        initializeButtons();

        updateScreen();

        boolean initialHelp = Utils.getPreferenceBoolean(this, "initialHelp");

		if (initialHelp == false) {
			showHelp();
			Utils.savePreferenceBoolean(this, "initialHelp", true);
		}
    }

    private void applySystemBarInsets() {
        final View content = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
        content.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars());
                    view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
                } else {
                    applyLegacySystemBarInsets(view, windowInsets);
                }

                return windowInsets;
            }
        });
        content.requestApplyInsets();
    }

    @SuppressWarnings("deprecation")
    private void applyLegacySystemBarInsets(View view, WindowInsets windowInsets) {
        view.setPadding(
            windowInsets.getSystemWindowInsetLeft(),
            windowInsets.getSystemWindowInsetTop(),
            windowInsets.getSystemWindowInsetRight(),
            windowInsets.getSystemWindowInsetBottom());
    }

    private void showHelp() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		Resources res = getResources();
		InputStream stream = res.openRawResource(R.raw.quick_start_guide);
		String data = Utils.readTextFile(stream);
		data = Uri.encode(data);

		alert.setTitle("Quick Start Guide");
		// Set an EditText view to get user input
		ScrollView sv = new ScrollView(this);
		WebView wv = new WebView(this);
		wv.setPadding(5,5,5,5);
		sv.addView(wv);
		wv.loadData(data, "text/html", "utf-8");
		alert.setView(sv);

		alert.setPositiveButton("OK", null);

		alert.show();
	}

    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();

        if (itemId == R.id.help) {
        	showHelp();
            return true;
        }

        return false;
    }

    protected String getCurrentScreenText() {
    	return screen.getText().toString();
    }

    protected double getDoubleFromScreen() {
    	return Double.parseDouble(getCurrentScreenText());
    }

    public void number_onClick(int number) {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	int precision = calc.addNumberToScreen(number);
    	updateScreen(precision, true);
    }

    public void buttonPeriod_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	if (calc.addPeriodToScreen()) {
    		updateScreen(0, true);
    	}
    }

    public void math_onClick(int action) {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.addMathOperation(action);
    	updateScreen(false);
    }

    public void buttonEquals_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.completeCalculation();
    	updateScreen(false);
    }

    public void buttonClear_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.clear();
    	updateScreen();
    }

    public void buttonBack_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	boolean showDecimal = false;
    	int precision = 0;

		precision = calc.back();
		int numberMode = calc.getNumberMode();
		showDecimal = (numberMode != Constants.MODE_INT && precision == 0);

    	updateScreen(precision, showDecimal);
    }

    public void buttonMemory_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.addMemory();
    	updateScreen();
    }

    public void buttonRecall_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.recallMemory();
    	updateScreen();
    }

    public boolean buttonClear_onLongClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return true;
    	}

    	calc.clearAll();
    	updateScreen();
    	return true;
    }

    public void buttonThousand_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.addThousandToScreen();
    	updateScreen();
    }

    public void buttonPercent_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	int precision = calc.makePercent();
    	updateScreen(precision, true);
    }

    public void buttonPrice_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.setPrice();
   		updateScreen();
    }

    public void buttonDownPayment_onClick() {
    	calc.setDownPayment();
    	updateScreen();
    }

    public void buttonTax_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.setTax();
    	updateScreen();
    }

    public void buttonInsurance_onClick() {
    	calc.setInsurance();
    	updateScreen();
    }

    public void buttonLoanAmount_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.setLoanAmount();
    	updateScreen();
    }

    public void buttonPayment_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.setPayment();
    	updateScreen();
    }

    public void buttonTerm_onClick() {
    	calc.setTerm();
    	updateScreen();
    }

    public void buttonInterest_onClick() {
    	if (calc.isShiftEnabled()) {
    		calc.setShiftEnabled(false);
    		updateScreen();
    		return;
    	}

    	calc.setInterest();
    	updateScreen();
    }

    protected void buttonShift_onClick() {
    	calc.shiftButtonClicked();
    	updateScreen();
    }

    protected void updateScreen() {
    	updateScreen(currentPrecision, true, false);
    }

    protected void updateScreen(boolean usePrecision) {
    	updateScreen(currentPrecision, usePrecision, false);
    }

    protected void updateScreen(int precision, boolean usePrecision) {
    	updateScreen(precision, usePrecision, false);
    }

    protected void updateScreen(int precision, boolean usePrecision, boolean showDecimal) {
    	screen.setText(formatScreenValue(calc.getScreenValue(), precision, usePrecision, showDecimal));

    	/*buttonPrice.setTextColor((calc.getPriceContainer().isCalculated()) ? Color.GREEN : (calc.getPriceContainer().hasValue()) ? Color.BLUE : Color.WHITE);
    	buttonDownPayment.setTextColor((calc.getDownPaymentContainer().isCalculated()) ? Color.GREEN : (calc.getDownPaymentContainer().hasValue()) ? Color.BLUE : Color.WHITE);
    	buttonTax.setTextColor((calc.getTaxContainer().isCalculated()) ? Color.GREEN : (calc.getTaxContainer().hasValue()) ? Color.BLUE : Color.WHITE);
    	buttonInsurance.setTextColor((calc.getInsuranceContainer().isCalculated()) ? Color.GREEN : (calc.getInsuranceContainer().hasValue()) ? Color.BLUE : Color.WHITE);

    	buttonLoanAmount.setTextColor((calc.getLoanAmountContainer().isCalculated()) ? Color.GREEN : (calc.getLoanAmountContainer().hasValue()) ? Color.BLUE : Color.WHITE);
    	buttonPayment.setTextColor((calc.getPaymentContainer().isCalculated()) ? Color.GREEN : (calc.getPaymentContainer().hasValue()) ? Color.BLUE : Color.WHITE);
    	buttonTerm.setTextColor((calc.getTermContainer().isCalculated()) ? Color.GREEN : (calc.getTermContainer().hasValue()) ? Color.BLUE : Color.WHITE);
    	buttonInterest.setTextColor((calc.getInterestContainer().isCalculated()) ? Color.GREEN : (calc.getInterestContainer().hasValue()) ? Color.BLUE : Color.WHITE);*/

    	buttonDownPayment.setTextColor((calc.isShiftEnabled()) ? Color.parseColor("#007B79") : Color.WHITE);
    	buttonDownPayment.setText((calc.isShiftEnabled()) ? "LTV" : "Down");

    	buttonInsurance.setTextColor((calc.isShiftEnabled()) ? Color.parseColor("#007B79") : Color.WHITE);
    	buttonInsurance.setText((calc.isShiftEnabled()) ? "Mtg Ins" : "Ins");

    	buttonTerm.setTextColor((calc.isShiftEnabled()) ? Color.parseColor("#007B79") : Color.WHITE);
    	buttonTerm.setText((calc.isShiftEnabled()) ? "Bi-Wkly" : "Term");

    	buttonShift.setBackgroundResource((calc.isShiftEnabled()) ? R.drawable.button_green_pressed : R.drawable.button_green);

    	switch(calc.getCurrentView()) {
    	case Constants.VIEW_NONE:
    	case Constants.VIEW_CLEAR:
    	case Constants.VIEW_NUMBERS:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("");
    		screenView.setText("");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_AMOUNT:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("");
    		screenView.setText("Loan Amount");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_PAYMENT_PI:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "P+I");
    		screenFrequency.setText("");
    		screenView.setText("Payment");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_PAYMENT_PITI:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "PITI");
    		screenFrequency.setText("");
    		screenView.setText("Payment");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_PAYMENT_IO:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "I/O");
    		screenFrequency.setText("");
    		screenView.setText("Payment");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_TERM:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Years");
    		screenView.setText("Term");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_TERM_MONTH:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Months");
    		screenView.setText("Term");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_INTEREST:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Interest");
    		screenMode.setText("%");
    		break;
    	case Constants.VIEW_INTEREST_MONTH:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Per Month");
    		screenView.setText("Interest");
    		screenMode.setText("%");
    		break;

    	case Constants.VIEW_DOWN_PAYMENT:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("");
    		screenView.setText("Down Payment");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_DOWN_PAYMENT_PERCENT:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("");
    		screenView.setText("Down Payment");
    		screenMode.setText("%");
    		break;
    	case Constants.VIEW_LOAN_TO_VALUE:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "LTV");
    		screenFrequency.setText("");
    		screenView.setText("Loan to Value");
    		screenMode.setText("%");
    		break;

    	case Constants.VIEW_PRICE:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("");
    		screenView.setText("Price");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_TAX_PERCENT:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Tax");
    		screenMode.setText("%");
    		break;
    	case Constants.VIEW_TAX_ANNUAL:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Tax");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_TAX_MONTH:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Per Month");
    		screenView.setText("Tax");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_INSURANCE_PERCENT:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Insurance");
    		screenMode.setText("%");
    		break;
    	case Constants.VIEW_INSURANCE_ANNUAL:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Insurance");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_INSURANCE_MONTH:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Per Month");
    		screenView.setText("Insurance");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_PMI_PERCENT:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Mortgage Insurance");
    		screenMode.setText("%");
    		break;
    	case Constants.VIEW_PMI_ANNUAL:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Annual");
    		screenView.setText("Mortgage Insurance");
    		screenMode.setText("");
    		break;
    	case Constants.VIEW_PMI_MONTH:
    		screenType.setText((calc.isShiftEnabled()) ? "SHIFT" : "");
    		screenFrequency.setText("Per Month");
    		screenView.setText("Mortgage Insurance");
    		screenMode.setText("");
    		break;

    	case Constants.VIEW_BI_TERM:
    		screenType.setText("");
    		screenFrequency.setText("Annual");
    		screenView.setText("Term");
    		screenMode.setText("Bi-Wkly");
    		break;
    	case Constants.VIEW_BI_TOTAL_INT_SAVINGS:
    		screenType.setText("SVG");
    		screenFrequency.setText("Total");
    		screenView.setText("Interest");
    		screenMode.setText("Bi-Wkly");
    		break;
    	case Constants.VIEW_BI_TOTAL_INT:
    		screenType.setText("");
    		screenFrequency.setText("Total");
    		screenView.setText("Interest");
    		screenMode.setText("Bi-Wkly");
    		break;
    	case Constants.VIEW_BI_TOTAL_PRINCIPAL:
    		screenType.setText("");
    		screenFrequency.setText("Total");
    		screenView.setText("Principal");
    		screenMode.setText("Bi-Wkly");
    		break;
    	case Constants.VIEW_BI_TOTAL_PI:
    		screenType.setText("");
    		screenFrequency.setText("Total");
    		screenView.setText("P+I");
    		screenMode.setText("Bi-Wkly");
    		break;
    	}
    }

    private void initializeButtons() {
        buttonClear = (Button)findViewById(R.id.buttonClear);

        buttonPrice = (Button)findViewById(R.id.buttonPrice);
        buttonDownPayment = (Button)findViewById(R.id.buttonDownPayment);
        buttonTax = (Button)findViewById(R.id.buttonTax);
        buttonInsurance = (Button)findViewById(R.id.buttonInsurance);
        buttonBack = (Button)findViewById(R.id.buttonBack);

        buttonLoanAmount = (Button)findViewById(R.id.buttonLoanAmount);
        buttonPayment = (Button)findViewById(R.id.buttonPayment);
        buttonTerm = (Button)findViewById(R.id.buttonTerm);
        buttonInterest = (Button)findViewById(R.id.buttonInterest);
        buttonPercent = (Button)findViewById(R.id.buttonPercent);

        buttonShift = (Button)findViewById(R.id.buttonShift);
    	buttonSeven = (Button)findViewById(R.id.buttonSeven);
    	buttonEight = (Button)findViewById(R.id.buttonEight);
    	buttonNine = (Button)findViewById(R.id.buttonNine);
    	buttonDivide = (Button)findViewById(R.id.buttonDivide);

    	buttonRecall = (Button)findViewById(R.id.buttonRecall);
    	buttonFour = (Button)findViewById(R.id.buttonFour);
    	buttonFive = (Button)findViewById(R.id.buttonFive);
    	buttonSix = (Button)findViewById(R.id.buttonSix);
    	buttonMultiply = (Button)findViewById(R.id.buttonMultiply);

    	buttonMemory = (Button)findViewById(R.id.buttonMemory);
    	buttonOne = (Button)findViewById(R.id.buttonOne);
    	buttonTwo = (Button)findViewById(R.id.buttonTwo);
    	buttonThree = (Button)findViewById(R.id.buttonThree);
    	buttonSubtract = (Button)findViewById(R.id.buttonSubtract);

    	buttonThousand = (Button)findViewById(R.id.buttonThousand);
    	buttonZero = (Button)findViewById(R.id.buttonZero);
    	buttonPeriod = (Button)findViewById(R.id.buttonPeriod);
    	buttonEquals = (Button)findViewById(R.id.buttonEquals);
    	buttonAdd = (Button)findViewById(R.id.buttonAdd);

    	buttonRecall.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonRecall_onClick();}});
    	buttonMemory.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonMemory_onClick();}});

    	buttonShift.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonShift_onClick();}});

    	buttonPrice.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonPrice_onClick();}});
    	buttonDownPayment.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonDownPayment_onClick();}});
    	buttonTax.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonTax_onClick();}});
    	buttonInsurance.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonInsurance_onClick();}});
    	buttonBack.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonBack_onClick();}});

    	buttonAdd.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){math_onClick(Constants.ACTION_ADD);}});
    	buttonSubtract.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){math_onClick(Constants.ACTION_SUBTRACT);}});
    	buttonMultiply.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){math_onClick(Constants.ACTION_MULTIPLY);}});
    	buttonDivide.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){math_onClick(Constants.ACTION_DIVIDE);}});
    	buttonPercent.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonPercent_onClick();}});

    	buttonEquals.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonEquals_onClick();}});

    	buttonClear.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonClear_onClick();}});
    	buttonClear.setOnLongClickListener(new Button.OnLongClickListener(){public boolean onLongClick(View v){return buttonClear_onLongClick();}});

    	buttonThousand.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonThousand_onClick();}});
    	buttonLoanAmount.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonLoanAmount_onClick();}});
        buttonPayment.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonPayment_onClick();}});
        buttonTerm.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonTerm_onClick();}});
        buttonInterest.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonInterest_onClick();}});
        buttonPeriod.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){buttonPeriod_onClick();}});

    	buttonOne.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(1);}});
    	buttonTwo.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(2);}});
    	buttonThree.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(3);}});
    	buttonFour.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(4);}});
    	buttonFive.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(5);}});
    	buttonSix.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(6);}});
    	buttonSeven.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(7);}});
    	buttonEight.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(8);}});
    	buttonNine.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(9);}});
    	buttonZero.setOnClickListener(new Button.OnClickListener(){public void onClick(View v){number_onClick(0);}});
    }

    public String doubleToString(Double inValue, int precision, boolean usePrecision, boolean showDecimal) {
    	if (inValue.isNaN() || inValue.isInfinite()) {
    		return "Error";
    	}

    	DecimalFormat formatter = new DecimalFormat();
    	if (usePrecision) {
    		formatter.setMinimumFractionDigits(precision);
    		formatter.setMaximumFractionDigits(precision);
    	}
    	else {
    		formatter.setMinimumFractionDigits(currentPrecision);
    		formatter.setMaximumFractionDigits(9);
    	}
    	formatter.setGroupingUsed(true);
    	formatter.setDecimalSeparatorAlwaysShown(showDecimal);
    	return formatter.format(inValue);
    }

	private String formatScreenValue(Double value, int precision, boolean usePrecision, boolean showDecimal) {
		if (value.isNaN() || value.isInfinite()) {
			return "Error";
		}

		if (calc.getCurrentView() == Constants.VIEW_NUMBERS || showDecimal) {
			return doubleToString(value, precision, true, showDecimal);
		}

		if (!usePrecision) {
			return formatNumber(value, currentPrecision, 6, false);
		}

		switch(calc.getCurrentView()) {
		case Constants.VIEW_INTEREST:
		case Constants.VIEW_INTEREST_MONTH:
		case Constants.VIEW_DOWN_PAYMENT_PERCENT:
		case Constants.VIEW_LOAN_TO_VALUE:
		case Constants.VIEW_TAX_PERCENT:
		case Constants.VIEW_INSURANCE_PERCENT:
		case Constants.VIEW_PMI_PERCENT:
			return formatNumber(value, 2, 2, false);

		case Constants.VIEW_TERM:
		case Constants.VIEW_TERM_MONTH:
		case Constants.VIEW_BI_TERM:
			return formatNumber(value, 0, 2, false);

		case Constants.VIEW_CLEAR:
		case Constants.VIEW_NONE:
			return doubleToString(value, precision, true, showDecimal);

		default:
			return formatNumber(value, 2, 2, false);
		}
	}

	private String formatNumber(Double value, int minPrecision, int maxPrecision, boolean showDecimal) {
		DecimalFormat formatter = new DecimalFormat();
		formatter.setMinimumFractionDigits(minPrecision);
		formatter.setMaximumFractionDigits(maxPrecision);
		formatter.setGroupingUsed(true);
		formatter.setDecimalSeparatorAlwaysShown(showDecimal);
		return formatter.format(value);
	}
}
