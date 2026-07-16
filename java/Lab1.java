// Solution: Inheritance — A Loan Portfolio System
// All classes in one file. Only the file name class (LoanLab) is public.
// Compile and run:  javac LoanLab.java  &&  java LoanLab

// ------------------------------------------------------------
// Part 1 + Part 4: The superclass, now abstract
// ------------------------------------------------------------
abstract class Loan {

    private String customer;
    private double principal;
    private int termMonths;

    public Loan(String customer, double principal, int termMonths) {
        this.customer = customer;
        this.principal = principal;
        this.termMonths = termMonths;
    }

    public String getCustomer() {
        return customer;
    }

    public double getPrincipal() {
        return principal;
    }

    public int getTermMonths() {
        return termMonths;
    }

    // Part 4: abstract method - no body, subclasses must implement it
    public abstract double monthlyInstallment();

    // Never changed - but calls the overridden method of the subclass!
    public double totalCost() {
        return monthlyInstallment() * termMonths;
    }

    // Part 3: overriding toString() from Object
    @Override
    public String toString() {
        return "Loan[customer=" + customer
                + ", principal=" + principal
                + ", term=" + termMonths + "]";
    }
}

// ------------------------------------------------------------
// Part 2 + Part 3: The subclass PersonalLoan
// ------------------------------------------------------------
class PersonalLoan extends Loan {

    private double annualRate;

    public PersonalLoan(String customer, double principal, int termMonths, double annualRate) {
        super(customer, principal, termMonths);   // must be the first line
        this.annualRate = annualRate;
    }

    // Part 3: implement the calculation with interest
    @Override
    public double monthlyInstallment() {
        double totalInterest = getPrincipal() * annualRate * (getTermMonths() / 12.0);
        return (getPrincipal() + totalInterest) / getTermMonths();
    }

    @Override
    public String toString() {
        return super.toString() + " PersonalLoan[rate=" + annualRate + "]";
    }
}

// ------------------------------------------------------------
// Part 4: A second concrete subclass
// ------------------------------------------------------------
class Mortgage extends Loan {

    private double propertyValue;

    public Mortgage(String customer, double principal, int termMonths, double propertyValue) {
        super(customer, principal, termMonths);
        this.propertyValue = propertyValue;
    }

    // Fixed rate 3.5% plus 15.0 insurance per month
    @Override
    public double monthlyInstallment() {
        double totalInterest = getPrincipal() * 0.035 * (getTermMonths() / 12.0);
        return (getPrincipal() + totalInterest) / getTermMonths() + 15.0;
    }

    @Override
    public String toString() {
        return super.toString() + " Mortgage[propertyValue=" + propertyValue + "]";
    }
}

// ------------------------------------------------------------
// Part 5: Polymorphism with an array
// ------------------------------------------------------------
public class Lab1 {

    public static void main(String[] args) {

        // Loan l = new Loan("Meyer", 12000, 48);   // NOT ALLOWED - Loan is abstract

        Loan[] portfolio = {
            new PersonalLoan("Meyer", 12000, 48, 0.06),
            new Mortgage("Schmidt", 250000, 360, 320000),
            new PersonalLoan("Yilmaz", 5000, 24, 0.08)
        };

        double sum = 0;

        // for-each loop: declared type is Loan, runtime type decides
        // which monthlyInstallment() and toString() run -> polymorphism
        for (Loan loan : portfolio) {
            System.out.println(loan);   // calls toString() automatically
            System.out.println("  monthly installment: " + loan.monthlyInstallment());
            System.out.println("  total cost:          " + loan.totalCost());
            sum = sum + loan.monthlyInstallment();
        }

        System.out.println("Sum of all monthly installments: " + sum);

        // Part 5, question 3:
        Loan l = new Mortgage("Kaya", 180000, 300, 210000);      // OK - a Mortgage is a Loan
        // Mortgage m = new PersonalLoan("Braun", 3000, 12, 0.07); // ERROR - a PersonalLoan is not a Mortgage
        Object o = new PersonalLoan("Braun", 3000, 12, 0.07);    // OK - every class is an Object
    }
}