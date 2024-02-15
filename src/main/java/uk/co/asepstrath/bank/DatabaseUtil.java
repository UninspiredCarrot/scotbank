package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import kong.unirest.core.GenericType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil extends Jooby {

    private static DataSource ds;
    private static DatabaseUtil db_util;

    private DatabaseUtil() {

    }

    public static void createDatabase() throws SQLException{
        Connection connection = ds.getConnection();
        //-----------------create the users table-----------------------------------------------
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `users` (" +
                        "id Decimal PRIMARY KEY," +
                        "username VARCHAR(255) NOT NULL," +
                        "password VARCHAR(255) NOT NULL" +
                        ")"
        );
        stmt.close();
        //--------------------------------------------------------------------------------------

        //------------create accounts table------------------------------------------------------

        stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `accounts` (" +
                        "id VARCHAR(255) PRIMARY KEY," +
                        "`name` VARCHAR(255) NOT NULL," +
                        "balance DECIMAL NOT NULL," +
                        "round_up_enabled BIT NOT NULL)"
        );

        //---------------------------------------------------------------------------------------

        //-------------------connect accounts and users tables-----------------------------------
        stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS `user_accounts` (" +
                        "user_id VARCHAR(255)," +
                        "account_id VARCHAR(255)," +
                        "PRIMARY KEY (user_id, account_id)," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)," +
                        "FOREIGN KEY (account_id) REFERENCES accounts(id)" +
                        ")"
        );
        stmt.close();
        //---------------------------------------------------------------------------------------

        //--------------create transactions table-----------------------------------------------
        stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS`transactions` (" +
                        "id VARCHAR(255) PRIMARY KEY,"+
                        "`timestamp` VARCHAR(255) NOT NULL,"+
                        "`to` VARCHAR(255) NOT NULL," +
                        "`from` VARCHAR(255) NOT NULL," +
                        "amount DECIMAL NOT NULL,"+
                        "transaction_type VARCHAR(255) NOT NULL"+
                        ")"
        );
        stmt.close();
        //---------------------------------------------------------------------------------------

    }

    public static void createInstance(DataSource dataSource){
        if(db_util == null) db_util = new DatabaseUtil();
        ds=dataSource;
    }
    public static void fetchDataFromAPI() throws SQLException, ParserConfigurationException, IOException, SAXException {
        //----------------read accounts from the account api-------------------------------------
        HttpResponse<List<Account>> accountResponse =
                Unirest
                        .get("https://api.asep-strath.co.uk/api/accounts")
                        .asObject(new GenericType<>(){});

        db_util.createAccountEntitiesFromList((ArrayList<Account>)accountResponse.getBody());
        //---------------------------------------------------------------------------------------

        //-----------------Get transaction information from api and save to data base------------

        URL url = new URL("https://api.asep-strath.co.uk/api/transactions");
        DocumentBuilderFactory doc_builder_fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder = doc_builder_fact.newDocumentBuilder();
        Document doc = doc_builder.parse(new InputSource(url.openStream()));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("results");

        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node child = nodeList.item(i).getFirstChild();

            String timestamp = child.getFirstChild().getNodeValue();
            child = child.getNextSibling();

            double amount = Double.parseDouble(child.getFirstChild().getNodeValue());
            child = child.getNextSibling();

            String from = child.getFirstChild().getNodeValue();
            child = child.getNextSibling();

            String id = child.getFirstChild().getNodeValue();
            child = child.getNextSibling();

            String to = child.getFirstChild().getNodeValue();
            child = child.getNextSibling();

            String type = child.getFirstChild().getNodeValue();

            Transaction transaction = new Transaction(
                    timestamp, amount, id, to, from, type
            );
            transactions.add(transaction);
        }
        db_util.createTransactionEntitiesFromList(transactions);
    }
    public static DatabaseUtil getInstance(){
        return db_util;
    }




    // Create User Entity.

    public void createUserEntity(User user) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
            "INSERT INTO users (" +
                "id, username, password" +
            ")VALUES(?,?,?)"
        );

        prep.setInt(1, user.getId());
        prep.setString(2, user.getName());
        prep.setString(3, user.getPassword());
        prep.executeUpdate();
        prep.close();
        con.close();
    }

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
    public User getUserByID(String user_id) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM users WHERE id = '"+user_id+"'"
        );

        rs.next();

        User user = new User();
        user.setId(rs.getInt("id"));
        user.setPassword(rs.getString("password"));
        user.setName(rs.getString("username"));
        user.setAccounts(getAccountsByUser(user.getId()));

        rs.close();
        stmt.close();
        con.close();

        return user;
    }


    // Read Users.
    public ArrayList<User> getAllUsers() throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM users"
        );

        ArrayList<User> users = new ArrayList<>();

        while(rs.next()){
            User user = new User();
            user.setId(rs.getInt("id"));
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
    public ArrayList<Account> getAccountsByUser(int user_id) throws SQLException{

        ArrayList<Account> accounts = new ArrayList<>();
        ArrayList<String> account_ids = new ArrayList<>();

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM user_accounts WHERE user_id "+user_id+""
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
