package uk.co.asepstrath.bank;

import ch.qos.logback.core.model.Model;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.JsonNode;
import kong.unirest.core.Unirest;
import org.slf4j.Logger;

import javax.sql.DataSource;
import java.awt.print.Book;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Path("/bank")
public class AppController {
    private final Logger logger;
    private final DatabaseUtil db;
    /*
    This constructor can take in any dependencies the controller may need to respond to a request
     */
    public AppController(Logger log) {
        logger = log;
        db = DatabaseUtil.getInstance();
    }

    @GET
    public String welcome() { return "Welcome to Scotbank! "; }

    @GET("/overview")
    public ModelAndView overview(@QueryParam String id) {
        Map<String, Object> model = new HashMap<>();
        Account acc = new Account();

        try
        {
            acc = db.getAccountByID(id);

        } catch (SQLException e) {
            logger.error("Database Error Occurred",e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }

        model.put("id", acc.getId());
        model.put("name", acc.getName());
        model.put("balance", acc.getBalance());
        model.put("round_up", acc.isRoundUpEnabled());
        return new ModelAndView("overview.hbs", model);
    }

    @GET("/login")
    public ModelAndView login() {
        // we must create a model to pass to the "login" template

        return new ModelAndView("login.hbs", new HashMap<>());
    }

    @POST("/login")
    public ModelAndView loginPost(String username, String password) {
        // we must create a model to pass to the "login" template
        Encryption encryption = new Encryption();

        String hashed_password;

        try {
            hashed_password = encryption.encrypt(password).toString();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        User user = new User();
        user.setPassword(hashed_password);
        user.setName(username);

        return new ModelAndView("login.hbs", new HashMap<>());
    }

    @GET("/view_transaction")
    public ModelAndView view_transaction(@QueryParam String transaction_id){

        Map<String, Object> model = new HashMap<>();
        Transaction transaction;

        try
        {
            transaction = this.db.getTransactionByID(transaction_id);
        }
        catch (SQLException e)
        {

            logger.error("Database Error Occurred",e);

            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }

        // Object should be created here and the information should be passed to a transaction view
        model.put("ID", transaction.getId());
        model.put("amount", transaction.getAmount());
        model.put("timestamp", transaction.getTimestamp());
        model.put("to", transaction.getTo());
        model.put("transaction_type", transaction.getTransaction_type());

        return new ModelAndView("view_transaction.hbs", model);

    }
}
