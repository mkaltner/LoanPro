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
		double payment = 0.0d;
		double monthlyInterest = (interestPercent / 100 / paymentsPerYear);
		double monthlyTerm = termYears * paymentsPerYear;
		// http://oakroadsystems.com/math/loan.htm#LoanPayment
		payment = (monthlyInterest * loanAmount) / (1 - Math.pow(1 + monthlyInterest, -monthlyTerm));
		return payment;
	}
	
	public static double calculateTerm(double loanAmount, double monthlyPayment, double interestPercent) {
		return calculateTerm(loanAmount, monthlyPayment, interestPercent, 12);
	}
	
	public static double calculateTerm(double loanAmount, double monthlyPayment, double interestPercent, int paymentsPerYear) {
		double term = 0.0d;
		double monthlyInterest = (interestPercent / 100 / paymentsPerYear);
		// http://oakroadsystems.com/math/loan.htm#Sample3
		term = -Math.log10(1 - ((monthlyInterest * loanAmount) / monthlyPayment)) / Math.log10(1 + monthlyInterest) / paymentsPerYear;
		return term;
	}
	
	public static double calculateLoanAmount(double monthlyPayment, double termYears, double interestPercent) {
		double amount = 0.0d;
		double monthlyInterest = (interestPercent / 100 / 12);
		double monthlyTerm = termYears * 12;
		// http://oakroadsystems.com/math/loan.htm#LoanAmount
		amount = (monthlyPayment / monthlyInterest) * (1 - Math.pow(1 + monthlyInterest, -monthlyTerm));
		return amount;
	}
	
	public static double calculateInterest(double loanAmount, double monthlyPayment, double termYears) {
		return calculateInterest(loanAmount, monthlyPayment, termYears, 12);
	}
	
	public static double calculateInterest(double loanAmount, double monthlyPayment, double termYears, int paymentsPerYear) {
		double rate = 0;
		double interestRate = 0;
		boolean breakOnLower = false;
		// http://oakroadsystems.com/math/loan.htm#LoanInterest
		while(rate < 100) {
			double calculatedPayment = Finance.calculateMonthlyPayment(loanAmount, termYears, rate, paymentsPerYear);
			double payment = monthlyPayment;
			
			if (calculatedPayment <= payment && breakOnLower) {
				interestRate = rate;
				break;
			}
			
			if (calculatedPayment > payment) {
				rate -= 0.0001;
				breakOnLower = true;
			}
			else {
				rate += 0.5;
			}
		}
		return interestRate;
	}
}
