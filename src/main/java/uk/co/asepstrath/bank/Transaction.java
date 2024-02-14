package uk.co.asepstrath.bank;

public class Transaction {
    private double amount;
    private String  id,
                    timestamp,
                    to,
                    from,
                    transaction_type;

    public Transaction() {
    }

    public Transaction(String timestamp, double amount, String id, String to, String from, String transaction_type) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.id = id;
        this.to = to;
        this.from = from;
        this.transaction_type = transaction_type;
    }

    public double getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTo() {
        return to;
    }

    public String getFrom(){return from;}

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setFrom(String from){this.from = from;}

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    @Override
    public String toString(){

        return String.format(
                "{id: %s}\n{timestamp: %s}\n{to: %s}\n{from: %s}-\n{amount: %.2f}-\n{transaction type: %s}",
                this.id, this.timestamp, this.to, this.from, this.amount,this.transaction_type
        );
    }

}
