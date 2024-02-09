package uk.co.asepstrath.bank;

import org.h2.engine.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtil {
    
    private final DataSource ds;

    public DatabaseUtil(DataSource ds) {
        this.ds = ds;
    }

    // Get Connection
    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }


    // Create User.
    // Create Account.
    public Account createAccount(Account account) throws SQLException{

        String sql = (
                "INSERT INTO accounts (" +
                        "id, name, balance, round_up_enabled" +
                        ")VALUES (?,?,?,?);"
        );
        PreparedStatement prep = getConnection().prepareStatement(sql);
        prep.setString(1, account.getId());
        prep.setString(2, account.getName());
        prep.setDouble(3, account.getBalance());
        prep.setBoolean(4, account.isRoundUpEnabled());
        prep.executeUpdate();
        prep.close();

        return account;
    }
    // Create Transaction.
    public Transaction createTransaction(Transaction transaction) throws SQLException{

        String sql = (
                 "INSERT INTO transactions (" +
                    "   id, `to`, timestamp, amount, transaction_type" +
                    ") VALUES (?,?,?,?,?)"
        );
        PreparedStatement prep = getConnection().prepareStatement(sql);
        prep.setString(1, transaction.getId());
        prep.setString(2, transaction.getTo());
        prep.setString(3, transaction.getTimestamp());
        prep.setDouble(4, transaction.getAmount());
        prep.setString(5, transaction.getTransaction_type());
        prep.executeUpdate();
        prep.close();

        System.out.printf("Transaction %s entered%n", transaction.getId());

        return transaction;
    }

    // Read User.
    // Read Users.

    // Read Account.
    public Account readAccountByID(String account_id) throws SQLException{
        Account account = new Account();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM accounts " +
                   "WHERE id = " + account_id
        );
        rs.next();
        account.setId(rs.getString("id"));
        account.setName(rs.getString("name"));
        account.setStartingBalance(rs.getDouble("balance"));
        account.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));

        stmt.close();
        rs.close();

        return account;
    }

    // Read Accounts.
    public ArrayList<Account> readAllAccounts() throws SQLException{
        ArrayList<Account> accounts = new ArrayList<>();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM accounts"
        );
        while(rs.next()){
            Account account = new Account();
            account.setId(rs.getString("id"));
            account.setName(rs.getString("name"));
            account.setStartingBalance(rs.getDouble("balance"));
            account.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));
            accounts.add(account);
        }
        stmt.close();
        rs.close();

        return accounts;
    }

    // Read Transaction.
    public Transaction readTransactionByID(String transaction_id) throws SQLException{
        Transaction transaction = new Transaction();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery
        (
            "SELECT * FROM transactions " +
               "WHERE id = " + transaction_id
        );
        rs.next();
        transaction.setId(rs.getString("id"));
        transaction.setTimestamp(rs.getString("timestamp"));
        transaction.setTo(rs.getString("to"));
        transaction.setTransaction_type(rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));

        stmt.close();
        rs.close();

        return transaction;
    }

    // Read Transactions.
    public ArrayList<Transaction> getAllTransactions() throws SQLException{
        ArrayList<Transaction> transactions = new ArrayList<>();

        Statement stmt = getConnection().createStatement();
        ResultSet rs = stmt.executeQuery
        (
            "SELECT * FROM transactions"
        );
        while(rs.next())
        {
            Transaction transaction = new Transaction();

            transaction.setId(rs.getString("id"));
            transaction.setTimestamp(rs.getString("timestamp"));
            transaction.setTo(rs.getString("to"));
            transaction.setTransaction_type(rs.getString("transaction_type"));
            transaction.setAmount(rs.getDouble("amount"));

            transactions.add(transaction);
        }

        stmt.close();
        rs.close();

        return transactions;
    }

    // Update User.
    public User updateUser(String user_id, User user) throws SQLException{
        //TODO: will work with the user class to be implemented
//        String sql = (
//            "UPDATE users " +
//            "SET " +
//                "username = ?" +
//                "password = ?" +
//            "WHERE id = ?"
//        );
//        PreparedStatement prep = getConnection().prepareStatement(sql);
//        prep.setString(1, user.getName());
//        prep.setString(2, user.getPassword());
//        prep.setString(3, user_id);
//        prep.executeUpdate();
//        prep.close();
        return user;
    }
    // Update Account.
    public Account updateAccount(String account_id, Account account) throws SQLException{

        String sql = (
            "UPDATE accounts " +
            "SET "+
                "name = ?," +
                "balance = ?," +
                "round_up_enabled = ?" +
            " WHERE id = ?"
        );
        PreparedStatement prep = getConnection().prepareStatement(sql);
        prep.setString(1, account.getName());
        prep.setDouble(2, account.getBalance());
        prep.setBoolean(3, account.isRoundUpEnabled());
        prep.setString(4, account_id);
        prep.executeUpdate();
        prep.close();

        return account;
    }

    // Update Transaction
    public Transaction updateTransaction(String transaction_id, Transaction transaction) throws SQLException{
        String sql = (
            "UPDATE transactions "+
            "SET " +
                "timestamp = ?," +
                "`to` = ?," +
                "amount = ?," +
                "transaction_type = ? " +
            "WHERE id = ?"
        );
        PreparedStatement prep = getConnection().prepareStatement(sql);
        prep.setString(1, transaction.getTimestamp());
        prep.setString(2, transaction.getTo());
        prep.setDouble(3, transaction.getAmount());
        prep.setString(4, transaction.getTransaction_type());
        prep.setString(5, transaction_id);
        prep.executeUpdate();
        prep.close();
        return transaction;
    }
    // Delete User.
    // Delete Account.
    // Delete Transaction




}
