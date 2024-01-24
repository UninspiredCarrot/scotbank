package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account {

    private double balance = 0.0;
    private String name;

    public Account (double amount) {
        balance = amount;
    }

    public String getName() {
        return name;
    }
    public void setName(String n) {
        name = n;
    }

    public void deposit(double amount) {
        balance += amount;
        balance = round();
    }

    public double getBalance() {
        return balance;
    }

    public void withdraw(double amount) {

        if (amount > balance) throw new ArithmeticException();

        balance -= amount;
        balance = round();
    }

    public double round() {
        BigDecimal rn = new BigDecimal(Double.toString(balance));
        rn = rn.setScale(2, RoundingMode.HALF_UP);
        return rn.doubleValue();
    }

}
