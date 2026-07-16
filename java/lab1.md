# Exercise: Inheritance — A Loan Portfolio System

**Estimated time:** 60–75 minutes
**Topics covered:** `extends`, `super`, constructors, method overriding, `@Override`, `toString()`, abstract classes, polymorphism, arrays of object references

Your bank offers different kinds of loans. They all share common data (customer, principal, term), but each type calculates its cost differently. You will model this as a class hierarchy.

---

## Part 1 — The Superclass (10 min)

Create a class `Loan` with:

- Three **private** instance variables: `String customer`, `double principal` (the amount borrowed), and `int termMonths`
- A constructor `Loan(String customer, double principal, int termMonths)`
- Getters for all three variables
- A method `double monthlyInstallment()` that for now returns a simple interest-free installment: `principal / termMonths`
- A method `double totalCost()` that returns `monthlyInstallment() * termMonths`

Write a `main` method that creates a `Loan("Meyer", 12000, 48)` and prints its monthly installment and total cost.

---

## Part 2 — The Subclass (15 min)

The bank's standard product is a **personal loan** with a fixed annual interest rate.

Create a class `PersonalLoan` that **extends** `Loan` and adds:

- A private instance variable `double annualRate` (e.g. `0.06` for 6%)
- A constructor `PersonalLoan(String customer, double principal, int termMonths, double annualRate)`

**Questions to answer before you run the code:**

1. Can your `PersonalLoan` constructor write `this.principal = principal;` directly? Why or why not?
2. What must the **first line** of your subclass constructor be, and what happens if you leave it out entirely?
3. Which members did `PersonalLoan` inherit from `Loan` — and what did it *not* inherit?

---

## Part 3 — Overriding and toString() (15 min)

1. **Override** `monthlyInstallment()` in `PersonalLoan` using simplified interest:

   ```
   totalInterest = principal * annualRate * (termMonths / 12.0)
   installment   = (principal + totalInterest) / termMonths
   ```

   You will need access to `principal` and `termMonths` — use the getters. Afterwards, discuss: what would change if these fields were `protected` instead of `private`? What is the downside?

2. Add the `@Override` annotation. Then deliberately misspell the method once (e.g. `monthlyInstalment` with one "l"). What error do you get? What would silently happen **without** the annotation?

3. Override `toString()` in `Loan` to return something like
   `Loan[customer=Meyer, principal=12000.0, term=48]`
   and again in `PersonalLoan` to add the rate. Use `super.toString()` in the subclass version instead of rebuilding the whole string.

4. **Test:** `System.out.println(myLoan);` — why does this call `toString()` even though you never wrote it?

5. Notice that `totalCost()` in `Loan` was never touched — yet it now returns the correct (higher) value for a `PersonalLoan`. Explain why.

---

## Part 4 — Abstract Classes (10 min)

The bank decides there is no such thing as "just a loan" — every real loan is of a specific type.

1. Make `Loan` **abstract** and make `monthlyInstallment()` an **abstract method** (declaration only, no body). What must you now delete from `Loan`?
2. What happens if you try `new Loan("Meyer", 12000, 48)`? Read the compiler error.
3. Create a second concrete subclass `Mortgage extends Loan` with:
   - An extra field `double propertyValue`
   - Its own `monthlyInstallment()`: same formula as the personal loan, but the rate is fixed at `0.035`, **plus** a monthly building-insurance surcharge of `15.0`
4. Which class is now *forced* to implement `monthlyInstallment()`, and which class *may* leave it unimplemented?

---

## Part 5 — Polymorphism with Arrays (15 min)

The bank wants a portfolio report across all loan types.

```java
Loan[] portfolio = {
    new PersonalLoan("Meyer", 12000, 48, 0.06),
    new Mortgage("Schmidt", 250000, 360, 320000),
    new PersonalLoan("Yilmaz", 5000, 24, 0.08)
};
```

1. Loop over the array with a **for-each loop** and print each loan. Which `toString()` runs for each element?
2. In the same loop, sum up all `monthlyInstallment()` values. The declared type of each element is `Loan` — which implementation actually runs, and what is this behavior called?
3. Predict which of these lines compile, **then** verify:
   ```java
   Loan l = new Mortgage("Kaya", 180000, 300, 210000);   // ?
   Mortgage m = new PersonalLoan("Braun", 3000, 12, 0.07); // ?
   Object o = new PersonalLoan("Braun", 3000, 12, 0.07);   // ?
   ```

---

## Bonus Challenges

- Mark `totalCost()` in `Loan` as `final`, then try to override it in `Mortgage`. What happens? Why might the bank's auditors *want* this method to be final?
- Validate input: the constructor should throw `IllegalArgumentException` if `principal <= 0` or `termMonths <= 0`. Where should this check live so that **all** subclasses benefit from it?
- Write a static method `double portfolioInstallments(Loan... loans)` using varargs that returns the sum of all monthly installments. Call it once with individual loans and once with your array.
- Add a `StudentLoan` where the first 12 months are repayment-free: `monthlyInstallment()` returns `0.0` while a counter of elapsed months is below 12. What extra state does this class need?

---

## Checklist — you should now be able to explain:

- [ ] What is inherited and what is not (constructors!)
- [ ] Why `super(...)` must be the first line in a subclass constructor
- [ ] The difference between `private` and `protected` for subclasses
- [ ] What `@Override` protects you from
- [ ] Why an abstract class cannot be instantiated and what abstract methods force
- [ ] Declared type vs. runtime type, and which one decides the method that runs
