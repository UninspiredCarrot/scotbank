package uk.co.asepstrath.bank;

import ch.qos.logback.core.model.Model;
import io.jooby.Context;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.*;

@Path("/bank")
public class AppController {

    private final Logger logger;
    private final DatabaseUtil db;
    User ACTIVEUSER = null;



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
    public ModelAndView login(Context context) {

        if(ACTIVEUSER!=null)
            context.sendRedirect("/bank/logout");

        return new ModelAndView("login.hbs", new HashMap<>());
    }

    @POST("/login")
    public void loginPost(Context ctx) {
        // we must create a model to pass to the "login" template
        Encryption encryption = new Encryption();
        DatabaseUtil connection = DatabaseUtil.getInstance();
        boolean username_match = false, password_match = false;
        User user = null;
        String username = ctx.form("username").value();
        String password = ctx.form("password").value();

        try {
            username_match = connection.checkUsernameExists(username);
            if(username_match){
                String hashed_password = new String(encryption.encrypt(password));
                password_match = connection.comparePassword(
                        username,
                        new String(encryption.encrypt(password))
                );
            }
            if(username_match && password_match){
                user = connection.getUserByUsername(username);
            }

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("Encryption Error :" + e);
        } catch (SQLException e){
            logger.error("SQL Exception :" + e);
        }

        if(user == null){
            ctx.sendRedirect("/bank/login");
        }
        else {
            ACTIVEUSER = user;
            ctx.sendRedirect("/bank");

        }
    }

    @GET("/logout")
    public ModelAndView logout(Context context){

        return new ModelAndView("logout.hbs", new HashMap<>());

    }

    @POST("/logout")
    public void logoutPost(Context context){
        this.ACTIVEUSER = null;
        context.sendRedirect("/bank/login");
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

    
    @GET("/view_all_transactions")
    public ModelAndView view_all_transactions() {
        Map<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;

        try {
            transactions = this.db.getAllTransactions();
        } catch (SQLException e) {
            logger.error("Database Error Occurred",e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Database Error Occurred");
        }

        model.put("Transaction", transactions);

        return new ModelAndView("view_all_transaction.hbs", model);
    }

    @GET("/summary")
    public ModelAndView summary(@QueryParam String id) throws SQLException {
        Map<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;

        try {
            transactions = db.getTransactionsByAccount(id);
        } catch (SQLException e) {
            logger.error("Transactions Database Error Occurred",e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Transactions Database Error Occurred");
        }

        HashMap<String, Double> categoriesToTotals = new HashMap<>();
        for (Transaction transaction:transactions){
            if (Objects.equals(transaction.getTransaction_type(), "PAYMENT")) {
                List<String> categoryList = Arrays.asList(db.getBusinessFromID(transaction.getTo()).getCategory().split(" "));
                categoryList.remove("&");
                String category = String.join("", categoryList);
                double amount = transaction.getAmount();;
                if (categoriesToTotals.containsKey(category)) {
                    amount += categoriesToTotals.get(category);
                }
                categoriesToTotals.put(category, amount);
            }
        }

        for (String key: categoriesToTotals.keySet()) {
            model.put(key + "Total", categoriesToTotals.get(key));
        }
        return new ModelAndView("summary.hbs", model);
    }
}
