package uk.co.asepstrath.bank;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

public class DatabaseUtil {

    private static DataSource ds;
    private static DatabaseUtil db_util;

    private DatabaseUtil() {

    }

    public static void createInstance(DataSource dataSource){
        if(db_util == null) db_util = new DatabaseUtil();
        ds=dataSource;
    }
    public static DatabaseUtil getInstance(){
        return db_util;
    }


    // Create User Entity.
/*
    public void createUserEntity(User user) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
            "INSERT INTO users (" +
                "id, username, password" +
            ")VALUES(?,?,?)"
        );

        prep.setString(1, user.getId());
        prep.setString(2, user.getName());
        prep.setString(3, user.getPassword());
        prep.executeUpdate();
        prep.close();
        con.close();
    }
*/
    // Create Account Entity.
    public void createAccountEntity(Account account, String user_id) throws SQLException {
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
        "INSERT INTO accounts (" +
                "id, name, balance, round_up_enabled" +
            ")VALUES (?,?,?,?);"
        );

        prep.setString(1, account.getId());
        prep.setString(2, account.getName());
        prep.setDouble(3, account.getBalance());
        prep.setBoolean(4, account.isRoundUpEnabled());
        prep.executeUpdate();

        prep.close();

        if(user_id != null){
            createUserAccountEntity(user_id, account.getId());
        }

        con.close();
    }

    // Create Multiple Account Entities
    public void createAccountEntitiesFromList(ArrayList<Account> accounts) throws SQLException{

        Connection con = ds.getConnection();

        for(Account account : accounts) {
            PreparedStatement prep = con.prepareStatement(
            "INSERT INTO accounts (" +
                    "id, name, balance, round_up_enabled" +
                ")VALUES (?,?,?,?);"
            );
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
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
        "INSERT INTO user_accounts (" +
                "user_id, account_id" +
            ")VALUES (?,?)"
        );

        prep.setString(1, user_id);
        prep.setString(2, account_id);

        prep.executeUpdate();

        prep.close();
        con.close();
    }

    // Create Transaction Entity.
    public void createTransactionEntity(Transaction transaction) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
        "INSERT INTO transactions (" +
                "   id, `to`, timestamp, amount, transaction_type" +
            ") VALUES (?,?,?,?,?)"
        );

        prep.setString(1, transaction.getId());
        prep.setString(2, transaction.getTo());
        prep.setString(3, transaction.getTimestamp());
        prep.setDouble(4, transaction.getAmount());
        prep.setString(5, transaction.getTransaction_type());

        prep.executeUpdate();

        prep.close();
        con.close();
    }

    public void createTransactionEntitiesFromList(ArrayList<Transaction> transactions) throws SQLException{
        Connection con = ds.getConnection();
        for(Transaction transaction : transactions){
            PreparedStatement prep = con.prepareStatement(
            "INSERT INTO `transactions` (" +
                    "id, `timestamp`, `to`, `from`, amount, transaction_type" +
                ") VALUES (?,?,?,?,?,?)"
            );

            prep.setString(1, transaction.getId());
            prep.setString(2, transaction.getTimestamp());
            prep.setString(3, transaction.getTo());
            prep.setString(4, transaction.getFrom());
            prep.setDouble(5, transaction.getAmount());
            prep.setString(6, transaction.getTransaction_type());

            prep.executeUpdate();

            prep.close();
        }
        con.close();
    }



    // Read User.
/*
    public User getUserByID(String user_id) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM users WHERE id = '"+user_id+"'"
        );

        rs.next();

        User user = new User();
        user.setId(rs.getString("id"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("username"));
        user.setAccounts(getAccountsByUser(user_id));

        rs.close();
        stmt.close();
        con.close();

        return user;
    }
*/

    // Read Users.
/*
    public ArrayList<User> getAllUsers() throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM users"
        );

        ArrayList<User> users = new ArrayList<>();

        while(rs.next()){
            User user = new User();
            user.setId(rs.getString("id"));
            user.setPassword(rs.getString("password"));
            user.setName(rs.getString("username"));
            user.setAccounts(getAccountsByUser(user.getId()));
            users.add(user);
        }

        rs.close();
        stmt.close();
        con.close();

        return users;
    }
*/
    public boolean checkUsernameExists(String username) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM users WHERE username = ''"+username+"''"
        );
        if(rs.next()) {
            rs.close();
            stmt.close();
            rs.close();
            return true;
        }
        else{
            rs.close();
            stmt.close();
            con.close();
            return false;
        }
    }

    public boolean comparePassword(String username, String password) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT password FROM users WHERE username = \'"+username+"\'"
        );
        rs.next();
        if(rs.getString("password").equals(password))
        {
            rs.close();
            stmt.close();
            con.close();
            return true;
        }
        else
        {
            rs.close();
            stmt.close();
            con.close();
            return false;
        }
    }


    // Read Account.
    public Account getAccountByID(String account_id) throws SQLException {
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
        "SELECT * FROM accounts WHERE id = '"+account_id+"'"
        );

        rs.next();

        Account account = new Account();

        account.setId(rs.getString("id"));
        account.setName(rs.getString("name"));
        account.setStartingBalance(rs.getDouble("balance"));
        account.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));
        account.setTransactions(getTransactionsByAccount(account.getId()));

        stmt.close();
        rs.close();
        con.close();

        return account;
    }

    // Read User Account
    public ArrayList<Account> getAccountsByUser(String user_id) throws SQLException{

        ArrayList<Account> accounts = new ArrayList<>();
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

            rs.next();

            Account account = new Account();
            account.setId(rs.getString("id"));
            account.setName(rs.getString("name"));
            account.setStartingBalance(rs.getDouble("balance"));
            account.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));
            account.setTransactions(getTransactionsByAccount(account.getId()));
            accounts.add(account);


            stmt.close();
            rs.close();
            con.close();
        }
        return accounts;
    }

    // Read Accounts.
    public ArrayList<Account> getAllAccounts() throws SQLException{
        ArrayList<Account> accounts = new ArrayList<>();

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
        "SELECT * FROM accounts"
        );

        while(rs.next()){
            Account account = new Account();
            account.setId(rs.getString("id"));
            account.setName(rs.getString("name"));
            account.setStartingBalance(rs.getDouble("balance"));
            account.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));
            account.setTransactions(getTransactionsByAccount(account.getId()));
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
        ResultSet rs = stmt.executeQuery(
        "SELECT * FROM transactions WHERE id = \'"+transaction_id+"\'"
        );

        rs.next();

        Transaction transaction = new Transaction();

        transaction.setId(rs.getString("id"));
        transaction.setTimestamp(rs.getString("timestamp"));
        transaction.setTo(rs.getString("to"));
        transaction.setFrom(rs.getString("from"));
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
        ResultSet rs = stmt.executeQuery(
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

    public ArrayList<Transaction> getTransactionsByAccount(String account_id) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM `transactions` WHERE `from` = \'"+account_id+"\'"
        );
        ArrayList<Transaction> transactions = new ArrayList<>();
        while(rs.next())
        {
            transactions.add(getTransactionByID(rs.getString("id")));
        }
        rs.close();
        stmt.close();
        con.close();
        return transactions;
    }


// Update User.
/*
    public User updateUser(String user_id, User user) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
            "UPDATE users " +
            "SET " +
                "username = \'?\' password = \'?\'" +
            "WHERE id = \'?\'"
        );
        prep.setString(1, user.getName());
        prep.setString(2, user.getPassword());
        prep.setString(3, user_id);
        prep.executeUpdate();

        prep.close();
        con.close();

        return user;
    }
*/

    // Update Account.
    public Account updateAccount(String account_id, Account account) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
        "UPDATE accounts " +
            "SET " +
                "name = ?, balance = ?, round_up_enabled = ? " +
            "WHERE id = ? "
        );

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
    public Transaction updateTransaction(String transaction_id, Transaction transaction) throws SQLException{

        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
        "UPDATE transactions "+
            "SET " +
                "timestamp = ?, `to` = ?, amount = ?, transaction_type = ? " +
            "WHERE id = ?"
        );

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
}
