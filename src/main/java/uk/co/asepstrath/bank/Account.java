package uk.co.asepstrath.bank;

public class Account {
    private int balance;

    public Account() {
        balance = 0;
    }

    public void deposit(int amount) {
        balance = balance + amount;
    }

    public void withdraw(int amount) throws ArithmeticException{
        if(balance - amount < 0) {
            throw new ArithmeticException("Not enough funds available.");
        }
        balance = balance - amount;
    }

    public int getBalance() {
        return balance;
    }
}
