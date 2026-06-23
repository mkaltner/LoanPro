package net.kaltner.LoanPro;


public class Finance {
	public static double calculatePrice(double loanAmount, double downPayment) {
		double downPaymentPercent = (downPayment < 100) ? downPayment / 100 : ((downPayment / loanAmount));
		return (loanAmount / (1 - downPaymentPercent));
	}

	public static double calculateMonthlyPayment(double loanAmount, double termYears, double interestPercent) {
		return calculateMonthlyPayment(loanAmount, termYears, interestPercent, 12);
	}

	public static double calculateMonthlyPayment(double loanAmount, double termYears, double interestPercent, int paymentsPerYear) {
		double paymentCount = termYears * paymentsPerYear;

		if (!hasPositiveLoanInputs(loanAmount, paymentCount, paymentsPerYear)) {
			return Double.NaN;
		}

		if (isZero(interestPercent)) {
			return loanAmount / paymentCount;
		}

		double monthlyInterest = (interestPercent / 100 / paymentsPerYear);
		// http://oakroadsystems.com/math/loan.htm#LoanPayment
		return (monthlyInterest * loanAmount) / (1 - Math.pow(1 + monthlyInterest, -paymentCount));
	}

	public static double calculateTerm(double loanAmount, double monthlyPayment, double interestPercent) {
		return calculateTerm(loanAmount, monthlyPayment, interestPercent, 12);
	}

	public static double calculateTerm(double loanAmount, double monthlyPayment, double interestPercent, int paymentsPerYear) {
		if (!hasPositiveLoanInputs(loanAmount, monthlyPayment, paymentsPerYear)) {
			return Double.NaN;
		}

		if (isZero(interestPercent)) {
			return loanAmount / monthlyPayment / paymentsPerYear;
		}

		double monthlyInterest = (interestPercent / 100 / paymentsPerYear);
		if (monthlyPayment <= monthlyInterest * loanAmount) {
			return Double.NaN;
		}

		// http://oakroadsystems.com/math/loan.htm#Sample3
		return -Math.log(1 - ((monthlyInterest * loanAmount) / monthlyPayment)) / Math.log(1 + monthlyInterest) / paymentsPerYear;
	}

	public static double calculateLoanAmount(double monthlyPayment, double termYears, double interestPercent) {
		double paymentCount = termYears * 12;

		if (!hasPositiveLoanInputs(monthlyPayment, paymentCount, 12)) {
			return Double.NaN;
		}

		if (isZero(interestPercent)) {
			return monthlyPayment * paymentCount;
		}

		double monthlyInterest = (interestPercent / 100 / 12);
		// http://oakroadsystems.com/math/loan.htm#LoanAmount
		return (monthlyPayment / monthlyInterest) * (1 - Math.pow(1 + monthlyInterest, -paymentCount));
	}

	public static double calculateInterest(double loanAmount, double monthlyPayment, double termYears) {
		return calculateInterest(loanAmount, monthlyPayment, termYears, 12);
	}

	public static double calculateInterest(double loanAmount, double monthlyPayment, double termYears, int paymentsPerYear) {
		double paymentCount = termYears * paymentsPerYear;
		if (!hasPositiveLoanInputs(loanAmount, monthlyPayment, paymentsPerYear) || paymentCount <= 0) {
			return Double.NaN;
		}

		// http://oakroadsystems.com/math/loan.htm#LoanInterest
		double zeroInterestPayment = loanAmount / paymentCount;
		if (monthlyPayment < zeroInterestPayment && !isClose(monthlyPayment, zeroInterestPayment)) {
			return Double.NaN;
		}
		if (isClose(monthlyPayment, zeroInterestPayment)) {
			return 0.0d;
		}

		double low = 0.0d;
		double high = 100.0d;
		double highPayment = calculateMonthlyPayment(loanAmount, termYears, high, paymentsPerYear);

		if (Double.isNaN(highPayment) || monthlyPayment > highPayment) {
			return Double.NaN;
		}

		for (int i = 0; i < 100; i++) {
			double mid = (low + high) / 2.0d;
			double calculatedPayment = calculateMonthlyPayment(loanAmount, termYears, mid, paymentsPerYear);

			if (isClose(calculatedPayment, monthlyPayment)) {
				return mid;
			}

			if (calculatedPayment > monthlyPayment) {
				high = mid;
			}
			else {
				low = mid;
			}
		}

		return (low + high) / 2.0d;
	}

	private static boolean hasPositiveLoanInputs(double amount, double paymentOrPeriods, int paymentsPerYear) {
		return amount >= 0 && paymentOrPeriods > 0 && paymentsPerYear > 0;
	}

	private static boolean isZero(double value) {
		return Math.abs(value) < 0.0000000001d;
	}

	private static boolean isClose(double value1, double value2) {
		return Math.abs(value1 - value2) < 0.0000001d;
	}
}
