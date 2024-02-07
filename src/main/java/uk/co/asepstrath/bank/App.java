package uk.co.asepstrath.bank;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import uk.co.asepstrath.bank.example.ExampleController;
import io.jooby.Jooby;
import io.jooby.handlebars.HandlebarsModule;
import io.jooby.helper.UniRestExtension;
import io.jooby.hikari.HikariModule;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
            //
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE `Example` (`Key` varchar(255),`Value` varchar(255))");
            stmt.executeUpdate("INSERT INTO Example " + "VALUES ('WelcomeMessage', 'Welcome to A Bank')");

            //---------------Testing transaction table plus data insertion--------------------------
            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS`transactions` (" +
                    "id integer PRIMARY KEY,"+
                    "`balance_before` decimal NOT NULL," +
                    "`balance_after` decimal NOT NULL,"+
                    "`transaction_amount` decimal NOT NULL,"+
                    "`transaction_type` varchar(255) NOT NULL"+
                    ")"
                );
            String sql = (
                    "INSERT INTO transactions (" +
                    "ID, balance_before, balance_after, transaction_amount, transaction_type" +
                    ")"+
                    "VALUES (?,?,?,?,?)"
            );
            PreparedStatement prep = connection.prepareStatement(sql);
            prep.setInt(1, 1);
            prep.setDouble(2, 100);
            prep.setDouble(3, 150);
            prep.setDouble(4, 50);
            prep.setString(5, "Deposit");
            prep.executeUpdate();
            //---------------------------------------------------------------------------------------

            //------------get user information from api and save to a users table-----------------
            sql = (
                    "CREATE TABLE IF NOT EXISTS `accounts` (" +
                    "id VARCHAR(255) PRIMARY KEY," +
                    "name VARCHAR(255) NOT NULL," +
                    "balance DECIMAL NOT NULL," +
                    "round_up_enabled BIT NOT NULL)"
                    );
            stmt.executeUpdate(sql);

            HttpResponse<Account[]> accountResponse =
                    Unirest
                    .get("https://api.asep-strath.co.uk/api/accounts")
                    .asObject(Account[].class);

            for(Account account : accountResponse.getBody()){
                sql = (
                    "INSERT INTO accounts (" +
                    "id, name, balance, round_up_enabled" +
                    ")VALUES (?,?,?,?);"
                );
                prep = connection.prepareStatement(sql);
                prep.setString(1, account.getId());
                prep.setString(2, account.getName());
                prep.setDouble(3, account.getBalance());
                prep.setBoolean(4, account.isRoundUpEnabled());
                prep.executeUpdate();
//                log.info(
//                        String.format(
//                                "{%s} - added to database", account
//                        )
//                );
            }
            //---------------------------------------------------------------------------------------
        } catch (SQLException e) {
            log.error("Database Creation Error",e);
        }
    }

    /*
    This function will be called when the application shuts down
     */
    public void onStop() {
        System.out.println("Shutting Down...");
    }

}
