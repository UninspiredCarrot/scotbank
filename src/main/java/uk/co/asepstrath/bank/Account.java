package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Account {

    private String  id,
                    name;
    private double startingBalance;
    private boolean roundUpEnabled;

    public Account () {
        startingBalance = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public boolean isRoundUpEnabled() {
        return roundUpEnabled;
    }

    public void setId(String id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartingBalance(double startingBalance) {
        this.startingBalance = startingBalance;
    }

    public void setRoundUpEnabled(boolean roundUpEnabled) {
        this.roundUpEnabled = roundUpEnabled;
    }

    public void deposit(double amount) {
        startingBalance += amount;
        startingBalance = round();
    }

    public void withdraw(double amount) throws ArithmeticException {
        if(startingBalance - amount < 0) {
            throw new ArithmeticException("Insufficient Funds");
        }

        startingBalance = startingBalance - amount;
        startingBalance = round();
    }

    public double getBalance() {
        return startingBalance;
    }

    public double round() {
        BigDecimal rn = new BigDecimal(Double.toString(startingBalance));
        rn = rn.setScale(2, RoundingMode.HALF_UP);
        return rn.doubleValue();
    }

    @Override
    public String toString(){
        return String
                .format(
                    "{id: %s}-{name: %s}-{starting balance: %.2f}-{is rounding up enabled: %b}",
                        this.id, this.name, this.round(), this.roundUpEnabled
                );
    }

}
