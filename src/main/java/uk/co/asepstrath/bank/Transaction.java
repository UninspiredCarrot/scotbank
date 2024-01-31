package uk.co.asepstrath.bank;

public class Transaction {
    private double balance_before,
                   transaction_amount,
                   balance_after;
    private String transaction_type;

    public Transaction(double balance_before, double transaction_amount, String transaction_type){
        this.balance_before = balance_before;
        this.transaction_amount = transaction_amount;
        this.balance_after = balance_before - transaction_amount;
        this.transaction_type = transaction_type;
    }

    public Transaction(double balance_before, double balance_after, double transaction_amount, String transaction_type){
        this.balance_before = balance_before;
        this.transaction_amount = transaction_amount;
        this.balance_after = balance_after;
        this.transaction_type = transaction_type;
    }

    public Transaction(){}

    public double getBalance_before() {
        return balance_before;
    }

    public void setBalance_before(double balance_before) {
        this.balance_before = balance_before;
    }

    public double getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(double transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public double getBalance_after() {
        return balance_after;
    }

    public void setBalance_after(double balance_after) {
        this.balance_after = balance_after;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }
}
