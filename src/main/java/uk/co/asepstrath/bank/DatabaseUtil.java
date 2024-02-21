package uk.co.asepstrath.bank;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
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
import java.io.StringReader;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                        "id IDENTITY PRIMARY KEY," +
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
                        "user_id DECIMAL," +
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
                        "`from` VARCHAR(255)," +
                        "amount DECIMAL NOT NULL,"+
                        "transaction_type VARCHAR(255) NOT NULL," +
                        "PRIMARY KEY (id)"+
                        ")"
        );
        stmt.close();
        //---------------------------------------------------------------------------------------

        //------------create businesses table----------------------------------------------------
        stmt = connection.createStatement();
        stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS`businesses` (" +
                        "id VARCHAR(255) PRIMARY KEY,"+
                        "`name` VARCHAR(255) NOT NULL,"+
                        "`category` VARCHAR(255) NOT NULL," +
                        "`sanctioned` VARCHAR(255) NOT NULL,"+
                        "PRIMARY KEY (id)"+
                        ")"
        );
        stmt.close();
        //---------------------------------------------------------------------------------------

    }

    public static void createInstance(DataSource dataSource){
        if(db_util == null) db_util = new DatabaseUtil();
        ds=dataSource;
    }
    public static void fetchDataFromAPI() throws SQLException, ParserConfigurationException, IOException, SAXException, CsvValidationException {
        //----------------read accounts from the account api-------------------------------------
        HttpResponse<List<Account>> accountResponse =
                Unirest
                        .get("https://api.asep-strath.co.uk/api/accounts")
                        .asObject(new GenericType<>(){});

        db_util.createAccountEntitiesFromList((ArrayList<Account>)accountResponse.getBody());
        //---------------------------------------------------------------------------------------

        //-----------------Get transaction information from api and save to data base------------

        URL url = new URL("https://api.asep-strath.co.uk/api/transactions?size=1000");
        DocumentBuilderFactory doc_builder_fact = DocumentBuilderFactory.newInstance();
        DocumentBuilder doc_builder = doc_builder_fact.newDocumentBuilder();
        Document doc = doc_builder.parse(new InputSource(url.openStream()));
        doc.getDocumentElement().normalize();

        NodeList nodeList = doc.getElementsByTagName("results");

        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Transaction transaction = new Transaction();
            Node node = nodeList.item(i).getFirstChild();
            while (node != null) {
                switch (node.getNodeName()) {
                    case "timestamp":
                        if(node.getFirstChild() != null)
                            transaction.setTimestamp(node.getFirstChild().getNodeValue());
                        break;
                    case "amount":
                        if(node.getFirstChild() != null)
                            transaction.setAmount(Double.parseDouble(node.getFirstChild().getNodeValue()));
                        break;
                    case "from":
                        if(node.getFirstChild() != null)
                            transaction.setFrom(node.getFirstChild().getNodeValue());
                        break;
                    case "id":
                        if(node.getFirstChild() != null)
                            transaction.setId(node.getFirstChild().getNodeValue());
                        break;
                    case "to":
                        if(node.getFirstChild() != null)
                            transaction.setTo(node.getFirstChild().getNodeValue());
                        break;
                    case "type":
                        if(node.getFirstChild() != null)
                            transaction.setTransaction_type(node.getFirstChild().getNodeValue());
                        break;
                }
                node = node.getNextSibling();
            }
            transactions.add(transaction);
        }
        db_util.createTransactionEntitiesFromList(transactions);

        /*int p = 1;
        while(true){
            String urlString = "https://api.asep-strath.co.uk/api/transactions?size=100&page="+p;
            URL url = new URL(urlString);
            DocumentBuilderFactory doc_builder_fact = DocumentBuilderFactory.newInstance();
            DocumentBuilder doc_builder = doc_builder_fact.newDocumentBuilder();
            Document doc = doc_builder.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("results");

            if(nodeList.getLength()<1) {
                break;
            }

            ArrayList<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Transaction transaction = new Transaction();
                Node node = nodeList.item(i).getFirstChild();
                while (node != null) {
                    switch (node.getNodeName()) {
                        case "timestamp":
                            if(node.getFirstChild() != null)
                                transaction.setTimestamp(node.getFirstChild().getNodeValue());
                            break;
                        case "amount":
                            if(node.getFirstChild() != null)
                                transaction.setAmount(Double.parseDouble(node.getFirstChild().getNodeValue()));
                            break;
                        case "from":
                            if(node.getFirstChild() != null)
                                transaction.setFrom(node.getFirstChild().getNodeValue());
                            break;
                        case "id":
                            if(node.getFirstChild() != null)
                                transaction.setId(node.getFirstChild().getNodeValue());
                            break;
                        case "to":
                            if(node.getFirstChild() != null)
                                transaction.setTo(node.getFirstChild().getNodeValue());
                            break;
                        case "type":
                            if(node.getFirstChild() != null)
                                transaction.setTransaction_type(node.getFirstChild().getNodeValue());
                            break;
                    }
                    node = node.getNextSibling();
                }
                transactions.add(transaction);
            }
            db_util.createTransactionEntitiesFromList(transactions);
            p++;
        }*/


        db_util.createBusinessEntitiesFromList(getBusinessesFromAPI());
    }
    public static DatabaseUtil getInstance(){
        return db_util;
    }

    public static ArrayList<Business> getBusinessesFromAPI() throws CsvValidationException, IOException {
        // Make a GET request to the API endpoint that returns a CSV file
        HttpResponse<String> response = Unirest.get("https://api.asep-strath.co.uk/api/businesses").asString();
        String csvContent = response.getBody();
//
//            // Parse the CSV content using OpenCSV
        CSVReader csvReader = new CSVReader(new StringReader(csvContent));
        String[] nextRecord;
        // Skip the header row
        csvReader.readNext();
        ArrayList<Business> businesses = new ArrayList<>();
        while ((nextRecord = csvReader.readNext()) != null) {
            // Process each CSV record
            String id = nextRecord[0];
            String name = nextRecord[1];
            String category = nextRecord[2];
            String sanctioned = nextRecord[3];

            businesses.add(new Business(id, name, category, sanctioned));
        }
        return businesses;
    }




    // Create User Entity.

    public void createUserEntity(User user) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
            "INSERT INTO users (" +
                "username, password" +
            ")VALUES(?,?)"
        );

//        prep.setInt(1, user.getId());
        prep.setString(1, user.getName());
        prep.setString(2, user.getPassword());
        prep.executeUpdate();
        prep.close();

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM users WHERE username = \'"+user.getName()+"\'"
        );

        rs.next();
        user.setId(rs.getInt("id"));

        for(Account account : user.getAccounts()){
            createAccountEntity(account, user.getId());
        }
    }

    public String createTransactionId() throws SQLException{
        String id = String.valueOf(UUID.randomUUID());
        boolean id_found = false;
        while(!id_found)
        {
            Connection con = ds.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT id FROM accounts WHERE id = \'"+id+"\'"
            );
            if(rs.next()){
                id = String.valueOf(UUID.randomUUID());
            }else{
                id_found = true;
            }
        }
        return id;
    }

    // Create Account Entity.
    public boolean createAccountEntity(Account account, int user_id) throws SQLException {
        Connection con = ds.getConnection();

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT id FROM accounts " +
                    "WHERE id = \'"+account.getId()+"\'"
        );
        if(rs.next()) {
            rs.close();
            stmt.close();
        }else{
            rs.close();
            stmt.close();

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

        if(user_id > 0){
            createUserAccountEntity(user_id, account.getId());
        }

        con.close();
        return true;
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
    public void createUserAccountEntity(int user_id, String account_id) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
        "INSERT INTO user_accounts (" +
                "user_id, account_id" +
            ")VALUES (?,?)"
        );

        prep.setInt(1, user_id);
        prep.setString(2, account_id);

        prep.executeUpdate();

        prep.close();
        con.close();
    }

    // Create Transaction Entity.
    public void createTransactionEntity(Transaction transaction) throws SQLException{
        Connection con = ds.getConnection();
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
    public void createBusinessEntitiesFromList(ArrayList<Business> businesses) throws SQLException{
        Connection con = ds.getConnection();
        for(Business business : businesses) {
            PreparedStatement prep = con.prepareStatement(
                    "INSERT INTO `businesses` (" +
                            "id, `name`, `category`, `sanctioned`" +
                            ") VALUES (?,?,?,?);"
            );

            prep.setString(1, business.getId());
            prep.setString(2, business.getName());
            prep.setString(3, business.getCategory());
            prep.setString(4, business.getSanctioned());

            prep.executeUpdate();

            prep.close();
        }
        con.close();
    }





    // Read User.
    public User getUserByID(int user_id) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM users WHERE id = "+user_id
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
    public User getUserByUsername(String username) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT * FROM users WHERE username = \'"+username+"\'"
        );
        User user = null;
        if(rs.next()){
            user = new User();
            user.setId(rs.getInt("id"));
            user.setPassword(rs.getString("password"));
            user.setName(rs.getString("username"));
            user.setAccounts(getAccountsByUser(user.getId()));
        }

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
                "SELECT * FROM users WHERE username = \'"+username+"\'"
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
                "SELECT * FROM user_accounts WHERE user_id = "+user_id
        );
        while(rs.next()){
            account_ids.add(rs.getString("account_id"));
        }

        stmt.close();
        rs.close();

        for(String id : account_ids){
            stmt = con.createStatement();
            rs = stmt.executeQuery(
                    "SELECT * FROM accounts WHERE id =\'"+id+"\'"
            );

            while(rs.next())
            {
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
        }
        con.close();
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

    public User updateUser(int user_id, User user) throws SQLException{
        Connection con = ds.getConnection();
        PreparedStatement prep = con.prepareStatement(
            "UPDATE users " +
            "SET " +
                "username = ?, password = ?" +
            "WHERE id = ?"
        );
        prep.setString(1, user.getName());
        prep.setString(2, user.getPassword());
        prep.setInt(3, user_id);
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

    public ArrayList<Business> getAllBusinesses() throws SQLException{
        ArrayList<Business> businesses = new ArrayList<>();

        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM businesses"
        );
        while(rs.next())
        {
            Business business = new Business();
            business.setId(rs.getString("id"));
            business.setCategory(rs.getString("category"));
            business.setName(rs.getString("name"));
            business.setSanctioned(rs.getString("sanctioned"));
            businesses.add(business);
        }

        stmt.close();
        rs.close();
        con.close();

        return businesses;
    }

    public Business getBusinessFromID(String id) throws SQLException{
        Connection con = ds.getConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
                "SELECT * FROM `businesses` WHERE `id` = \'"+id+"\'"
        );
        rs.next();
        Business business = new Business();
        business.setId(rs.getString("id"));
        business.setCategory(rs.getString("category"));
        business.setName(rs.getString("name"));
        business.setSanctioned(rs.getString("sanctioned"));
        return business;
    }
}
