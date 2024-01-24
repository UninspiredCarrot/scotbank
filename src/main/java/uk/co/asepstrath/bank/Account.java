package uk.co.asepstrath.bank;

public class Account {
    private int balancce;
    public Account() {
        balancce = 0;
    }
    public void deposit(int amount) {
        balancce = balancce + amount;
    }

    public int getBalance() {
        return balancce;
    }

    public void withdraw(int amount) {
        balancce = balancce - amount;
    }

}
