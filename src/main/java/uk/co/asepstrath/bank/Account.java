package uk.co.asepstrath.bank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class Account {

    private String  id,
                    name;
    private double startingBalance;
    private boolean roundUpEnabled;
    private ArrayList<Transaction> transactions;
    public Account () {
        startingBalance = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return startingBalance;
    }

    public boolean isRoundUpEnabled() {
        return roundUpEnabled;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
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

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
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


    public double round() {
        BigDecimal rn = new BigDecimal(Double.toString(startingBalance));
        rn = rn.setScale(2, RoundingMode.HALF_UP);
        return rn.doubleValue();
    }

    @Override
    public String toString(){
        return String
                .format(
<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
                    "{id: %s}-{name: %s}-{starting balance: %.2f}-{is rounding up enabled: %b}",
=======
                    "{id: %s}\n{name: %s}\n{starting balance: %.2f}\n{is rounding up enabled: %b}",
>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79
                        this.id, this.name, this.startingBalance, this.roundUpEnabled
                );
    }

}
