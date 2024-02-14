package uk.co.asepstrath.bank;

import kong.unirest.core.GenericType;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.co.asepstrath.bank.example.ExampleController;
import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class App extends Jooby {

    {
        /*
        This section is used for setting up the Jooby Framework modules
         */
        install(new UniRestExtension());
        install(new HandlebarsModule());
        install(new HikariModule("mem"));

        /*
        This will host any files in src/main/resources/assets on <host>/assets
        For example in the dice template (dice.hbs) it references "assets/dice.png" which is in resources/assets folder
         */
        assets("/assets/*", "/assets");
        assets("/service_worker.js","/service_worker.js");

        /*
        Now we set up our controllers and their dependencies
         */
        DataSource ds = require(DataSource.class);
        Logger log = getLog();

        DatabaseUtil.createInstance(ds);

        mvc(new ExampleController(ds,log));
        mvc(new AppController(ds, log));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(() -> onStart());
        onStop(() -> onStop());
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public void onStart() {
        Logger log = getLog();
        log.info("Starting Up...");

        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Database util class
        DatabaseUtil db_util = DatabaseUtil.getInstance();

        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            
            //-----------------
            // CREATING TABLES-
            //-----------------
            //-----------------create the users table-----------------------------------------------
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
            "CREATE TABLE IF NOT EXISTS `users` (" +
                    "id VARCHAR(255) PRIMARY KEY," +
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

            //---------------------------
            // get information from api--
            //---------------------------
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
            //-----------------------------------------------------------------------------------------

            //--------------------
            //----Test Databases--
            //--------------------

            ArrayList<Account> accounts_check = db_util.getAllAccounts();
            ArrayList<Transaction> transactions_check = db_util.getAllTransactions();

            /*  "id":"5d85cff3-4792-43fe-9674-173bf7ef5c5c",
                "name":"Mr. Rickey Upton",
                "startingBalance":544.04,
                "roundUpEnabled":false} */
            System.out.println("-------------------------------------");
            Account account = db_util.getAccountByID(
    "25e9b894-c75b-498a-80c6-614942211594"
            );
            System.out.println(account);
            System.out.println("-------------------------------------");

            /*  <timestamp>2023-04-10 08:43</timestamp>
                <amount>150.00</amount>
                <from>25e9b894-c75b-498a-80c6-614942211594</from>
                <id>50567a98-9ffd-4d53-b75d-4848c4086416</id>
                <to>SAI</to>
                <type>PAYMENT</type>    */
            System.out.println("-------------------------------------");
            Transaction transaction = db_util.getTransactionByID(
                "50567a98-9ffd-4d53-b75d-4848c4086416"
            );
            System.out.println(transaction);
            System.out.println("-------------------------------------");
            //TODO: Test insertion and update features in db_util

            //TODO: Fix unwanted rounding in the database

            //TODO: Test User table once User class has been imported


        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
