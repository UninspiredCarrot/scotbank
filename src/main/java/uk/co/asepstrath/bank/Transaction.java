package uk.co.asepstrath.bank;

public class Transaction {
    private double amount;
    private String  id,
                    to,
                    transaction_type;

    public Transaction() {
    }

    public Transaction(double amount, String id, String to, String transaction_type) {
        this.amount = amount;
        this.id = id;
        this.to = to;
        this.transaction_type = transaction_type;
    }

    public double getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

}
