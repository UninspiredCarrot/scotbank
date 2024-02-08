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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        // Open Connection to DB
        try (Connection connection = ds.getConnection()) {
            //-----------------create the users table-----------------------------------------------
            Statement stmt = connection.createStatement();
            String sql = (
                    "CREATE TABLE IF NOT EXISTS `users` (" +
                        "id VARCHAR(255) PRIMARY KEY," +
                        "username VARCHAR(255) NOT NULL," +
                        "password VARCHAR(255) NOT NULL" +
                    ")"
            );
            stmt.executeUpdate(sql);
            stmt.close();
            //--------------------------------------------------------------------------------------

            //--------------create transactions table and populate with data from api---------------
            sql = (
                            "CREATE TABLE IF NOT EXISTS`transactions` (" +
                            "id VARCHAR(255) PRIMARY KEY,"+
                            "timestamp VARCHAR(255) NOT NULL,"+
                            "`to` VARCHAR(255) NOT NULL," +
                            "amount DECIMAL NOT NULL,"+
                            "transaction_type VARCHAR(255) NOT NULL"+
                            ")"
            );
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();

            URL url = new URL("https://api.asep-strath.co.uk/api/transactions");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("results");
            ArrayList<Transaction> transactions = new ArrayList<>();
            for (int i = 0; i < nodeList.getLength(); i++) {

                Node child = nodeList.item(i).getFirstChild();

                String timestamp = child.getFirstChild().getNodeValue();
                child = child.getNextSibling();

                double amount = Double.parseDouble(child.getFirstChild().getNodeValue());
                child = child.getNextSibling();

                String id = child.getFirstChild().getNodeValue();
                child = child.getNextSibling();

                String to = child.getFirstChild().getNodeValue();
                child = child.getNextSibling();

                String type = child.getFirstChild().getNodeValue();

                sql = (
                        "INSERT INTO transactions (" +
                                "id, `to`, timestamp, amount, transaction_type" +
                                ")"+
                                "VALUES (?,?,?,?,?)"
                );
                PreparedStatement prep = connection.prepareStatement(sql);
                prep.setString(1, id);
                prep.setString(2, to);
                prep.setString(3, timestamp);
                prep.setDouble(4, amount);
                prep.setString(5, type);
                prep.executeUpdate();
                prep.close();
            }
            //---------------------------------------------------------------------------------------

            //------------get user information from api and save to a accounts table-----------------
            sql = (
                    "CREATE TABLE IF NOT EXISTS `accounts` (" +
                    "id VARCHAR(255) PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "balance DECIMAL NOT NULL," +
                    "round_up_enabled BIT NOT NULL)"
                    );
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();

            HttpResponse<List<Account>> accountResponse =
                    Unirest
                    .get("https://api.asep-strath.co.uk/api/accounts")
                    .asObject(new GenericType<>(){});

            for(Account account : accountResponse.getBody()){
                sql = (
                    "INSERT INTO accounts (" +
                    "id, name, balance, round_up_enabled" +
                    ")VALUES (?,?,?,?);"
                );
                PreparedStatement prep = connection.prepareStatement(sql);
                prep.setString(1, account.getId());
                prep.setString(2, account.getName());
                prep.setDouble(3, account.getBalance());
                prep.setBoolean(4, account.isRoundUpEnabled());
                prep.executeUpdate();
                prep.close();
            }
            //---------------------------------------------------------------------------------------

            //-------------------connect accounts and users tables-----------------------------------
            sql = (
                    "CREATE TABLE IF NOT EXISTS `user_accounts` (" +
                            "user_id VARCHAR(255)," +
                            "account_id VARCHAR(255)," +
                            "PRIMARY KEY (user_id, account_id)," +
                            "FOREIGN KEY (user_id) REFERENCES users(id)," +
                            "FOREIGN KEY (account_id) REFERENCES accounts(id)" +
                            ")"
            );
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            //---------------------------------------------------------------------------------------

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
