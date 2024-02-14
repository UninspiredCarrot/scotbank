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

    // Create User Entity.

    // Create Account Entity.
    public Account createAccountEntity(Account account) throws SQLException{
        String sql = (
                "INSERT INTO accounts (" +
                        "id, name, balance, round_up_enabled" +
                        ")VALUES (?,?,?,?);"
        );
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(sql);

        prep.setString(1, account.getId());
        prep.setString(2, account.getName());
        prep.setDouble(3, account.getBalance());
        prep.setBoolean(4, account.isRoundUpEnabled());
        prep.executeUpdate();

        prep.close();
        con.close();

        return account;
    }

    // Create Multiple Account Entities
    public void createAccountEntitiesFromList(ArrayList<Account> accounts) throws SQLException{

        Connection con = ds.getConnection();

        for(Account account : accounts) {
            String sql = (
                    "INSERT INTO accounts (" +
                            "id, name, balance, round_up_enabled" +
                            ")VALUES (?,?,?,?);"
            );
            PreparedStatement prep = con.prepareStatement(sql);

            prep.setString(1, account.getId());
            prep.setString(2, account.getName());
            prep.setDouble(3, account.getBalance());
            prep.setBoolean(4, account.isRoundUpEnabled());
            prep.executeUpdate();

            prep.close();
        }
        con.close();
    }

    // Create User Account Entity
    public void createUserAccountEntity(String user_id, String account_id) throws SQLException{
        String sql = (
            "INSERT INTO user_accounts (" +
                "user_id, account_id" +
            ")VALUES (?,?)"
        );
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(sql);

        prep.setString(1, user_id);
        prep.setString(2, account_id);

        prep.executeUpdate();

        prep.close();
        con.close();
    }

    // Create Transaction Entity.
    public Transaction createTransactionEntity(Transaction transaction) throws SQLException{
        String sql = (
                 "INSERT INTO transactions (" +
                    "   id, `to`, timestamp, amount, transaction_type" +
                    ") VALUES (?,?,?,?,?)"
        );
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(sql);

        prep.setString(1, transaction.getId());
        prep.setString(2, transaction.getTo());
        prep.setString(3, transaction.getTimestamp());
        prep.setDouble(4, transaction.getAmount());
        prep.setString(5, transaction.getTransaction_type());

        prep.executeUpdate();

        prep.close();
        con.close();

        return transaction;
    }

    // Read User.
    // Read Users.

    // Read Account.

    public Account getAccountByID(String account_id) throws SQLException{
        String sql = (
            "SELECT * FROM accounts WHERE id = \'"+account_id+"\'"
        );
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        rs.next();

        Account account = new Account();

        account.setId(rs.getString("id"));
        account.setName(rs.getString("name"));
        account.setStartingBalance(rs.getDouble("balance"));
        account.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));

        stmt.close();
        rs.close();
        con.close();

        return account;
    }

    // Read Accounts.
    public ArrayList<Account> getAllAccounts() throws SQLException{
        ArrayList<Account> accounts = new ArrayList<>();

        String sql = (
                "SELECT * FROM accounts"
        );
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

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
        con.close();

        return accounts;
    }

    // Read Transaction.
    public Transaction getTransactionByID(String transaction_id) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery
        (
            "SELECT * FROM transactions " +
               "WHERE id = \'"+transaction_id+"\'"
        );

        rs.next();

        Transaction transaction = new Transaction();

        transaction.setId(rs.getString("id"));
        transaction.setTimestamp(rs.getString("timestamp"));
        transaction.setTo(rs.getString("to"));
        transaction.setTransaction_type(rs.getString("transaction_type"));
        transaction.setAmount(rs.getFloat("amount"));

        stmt.close();
        rs.close();
        con.close();

        return transaction;
    }

    // Read Transactions.
    public ArrayList<Transaction> getAllTransactions() throws SQLException{
        ArrayList<Transaction> transactions = new ArrayList<>();

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
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
        con.close();

        return transactions;
    }

    // Update User.
    public User updateUser(String user_id, User user) throws SQLException{
        //TODO: will work with the user class to be implemented
//        String sql = (
//            "UPDATE users " +
//            "SET " +
//                "username = \'?\'" +
//                "password = \'?\'" +
//            "WHERE id = \'?\'"
//        );
//        Connection con = ds.getConnection();
//        PreparedStatement prep = getConnection().prepareStatement(sql);
//        prep.setString(1, user.getName());
//        prep.setString(2, user.getPassword());
//        prep.setString(3, user_id);
//
//        prep.executeUpdate();
//
//        prep.close();
//        con.close()

        return user;
    }

    // Read User Account
    public ArrayList<Transaction> getAccountByUser(String user_id) throws SQLException{

        ArrayList<Transaction> transactions = new ArrayList<>();
        ArrayList<String> account_ids = new ArrayList<>();

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM user_accounts WHERE user_id \'"+user_id+"\'"
        );
        while(rs.next()){
            account_ids.add(rs.getString("account_id"));
        }

        stmt.close();
        rs.close();

        for(String id : account_ids){
            stmt = con.createStatement();
            rs = stmt.executeQuery(
                "SELECT * FROM accounts WHERE id =\' "+id+"\'"
            );

            while(rs.next()){
                Transaction transaction = new Transaction();
                transaction.setId(rs.getString("id"));
                transaction.setTimestamp(rs.getString("timestamp"));
                transaction.setTo(rs.getString("to"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransaction_type(rs.getString("transaction_type"));
                transactions.add(transaction);
            }

            stmt.close();
            rs.close();
            con.close();
        }
        return transactions;
    }

    // Update User.

    // Update Account.
    public Account updateAccount(String account_id, Account account) throws SQLException{
        String sql = (
                "UPDATE accounts " +
                "SET " +
                    "name = \'?\', balance = ?, round_up_enabled = ? " +
                "WHERE" +
                    "id = \'?\'"

        );
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(sql);

        prep.setString(1, account.getName());
        prep.setDouble(2, account.getBalance());
        prep.setBoolean(3, account.isRoundUpEnabled());
        prep.setString(4, account_id);

        prep.executeUpdate();

        prep.close();
        con.close();

        return account;
    }

    // Update Transaction


    // Update Transaction
    public Transaction updateTransaction(String transaction_id, Transaction transaction) throws SQLException{
        String sql = (
            "UPDATE transactions "+
            "SET " +
                "timestamp = \'?\'," +
                "`to` = \'?\'," +
                "amount = ?," +
                "transaction_type = \'?\' " +
            "WHERE id = \'?\'"
        );

        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(sql);

        prep.setString(1, transaction.getTimestamp());
        prep.setString(2, transaction.getTo());
        prep.setDouble(3, transaction.getAmount());
        prep.setString(4, transaction.getTransaction_type());
        prep.setString(5, transaction_id);

        prep.executeUpdate();

        prep.close();
        con.close();

        return transaction;
    }
    // Delete User.
    // Delete Account.
    // Delete Transaction
}
