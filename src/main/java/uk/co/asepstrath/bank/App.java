package uk.co.asepstrath.bank;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.xml.sax.SAXException;
import uk.co.asepstrath.bank.example.ExampleController;
import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

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
        assertNotNull(DatabaseUtil.getInstance());

        mvc(new ExampleController(ds,log));
        mvc(new AppController(log));

        /*
        Finally we register our application lifecycle methods
         */
        onStarted(this::onStart);
        onStop(this::onStop);
    }

    public static void main(final String[] args) {
        runApp(args, App::new);
    }

    /*
    This function will be called when the application starts up,
    it should be used to ensure that the DB is properly setup
     */
    public void onStart() throws CsvValidationException, SQLException, IOException {
        Logger log = getLog();
        log.info("Starting Up...");

        // Fetch DB Source
        DataSource ds = require(DataSource.class);
        // Database util class
        DatabaseUtil db_util = DatabaseUtil.getInstance();

        // CREATE TABLES AND GET API DATA
        try{
            DatabaseUtil.createDatabase();
            DatabaseUtil.fetchDataFromAPI();
        }catch (SQLException e) {
            log.error("Database Creation Error",e);
        } catch (ParserConfigurationException e) {
            log.error("Parser Configuration Error", e);
        } catch (IOException e){
            log.error("I/O Error", e);
        } catch (SAXException e){
            log.error("SAX Error", e);
        }

        try{

            //--------------------
            //----Test Databases--
            //--------------------

//            Account account = ;
            assertNotNull(db_util.getAccountByID("3558201f-3dc1-4898-92ac-7882806c9ab3"));

            assertNotNull(db_util.getTransactionByID("80712de9-8dfc-4cef-be2a-2a0e701c7fac"));

            Encryption encryption = new Encryption();
            ArrayList<Account> accounts = new ArrayList<>();

            accounts.add(db_util.getAccountByID("3558201f-3dc1-4898-92ac-7882806c9ab3"));

            User user = new User();
            user.setName("user");
            user.setAccounts(accounts);

            try {
                user.setPassword(new String(encryption.encrypt("Password!")));
            } catch (NoSuchAlgorithmException e) {
                log.error("No Such Algorithm Error : " + e);
            } catch (UnsupportedEncodingException e) {
                log.error("Unsupported Encoding Error " + e);
            }


            user.addAccount(new Account(db_util.createTransactionId(), "Jane Doe", 500, false));

            db_util.createUserEntity(new User("user1", "was", new ArrayList<>()));
            db_util.createUserEntity(new User("user2", "tes", new ArrayList<>()));


            if(!db_util.checkUsernameExists(user.getName()))
                db_util.createUserEntity(user);
            if(!db_util.checkUsernameExists(user.getName()))
                db_util.createUserEntity(user);

            user.setName("JaneDoe");
            db_util.updateUser(user.getId(), user);
            User user_check_id = db_util.getUserByID(user.getId());
            assertNotNull(user_check_id);

            ArrayList<Account> accounts_check = db_util.getAllAccounts();
            ArrayList<Transaction> transactions_check = db_util.getAllTransactions();
            ArrayList<User> users_check = db_util.getAllUsers();
            assertEquals(100, accounts_check.size());
            assertEquals(3047, transactions_check.size());
            assertEquals(100, users_check.size());

        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }


//        System.out.println(db_util.getAllBusinesses());
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
