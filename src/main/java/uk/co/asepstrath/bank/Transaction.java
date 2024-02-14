package uk.co.asepstrath.bank;

public class Transaction {
    private double amount;
    private String  id,
                    timestamp,
                    to,
<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
=======
                    from,
>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79
                    transaction_type;

    public Transaction() {
    }

<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
    public Transaction(String timestamp, double amount, String id, String to, String transaction_type) {
=======
    public Transaction(String timestamp, double amount, String id, String to, String from, String transaction_type) {
>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79
        this.timestamp = timestamp;
        this.amount = amount;
        this.id = id;
        this.to = to;
<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
=======
        this.from = from;
>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79
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
<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
=======

    public String getFrom(){return from;}
>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79

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

<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
=======
    public void setFrom(String from){this.from = from;}

>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79
    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    @Override
    public String toString(){
<<<<<<< 530dfa0bbbb93cadf02aea78cbbc39e827947198
        return String.format("{%s}-{%s}-{%s}-{%.2f}-{%s}", this.id, this.timestamp, this.to, this.amount,this.transaction_type);
=======
        return String.format(
                "{id: %s}\n{timestamp: %s}\n{to: %s}\n{from: %s}-\n{amount: %.2f}-\n{transaction type: %s}",
                this.id, this.timestamp, this.to, this.from, this.amount,this.transaction_type
        );
>>>>>>> 2eb0654b6d549bc54e93338ff9b7c0418d564e79
    }

}
