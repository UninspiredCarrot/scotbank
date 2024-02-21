package uk.co.asepstrath.bank;

import ch.qos.logback.core.model.Model;
import io.jooby.ModelAndView;
import io.jooby.Router;
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
import java.util.*;

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
    public String loginPost(String username, String password) {
        // we must create a model to pass to the "login" template
        Encryption encryption = new Encryption();
        DatabaseUtil connection = DatabaseUtil.getInstance();
        boolean username_match = false, password_match = false;
        User user = null;

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
        if(user == null)
            return "LOGIN FAIL";
        else
            return "LOGIN SUCCESS";
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

    @GET("/summary")
    public ModelAndView summary(@QueryParam String id) throws SQLException {
        Map<String, Object> model = new HashMap<>();
        ArrayList<Transaction> transactions;

        try
        {
            transactions = db.getTransactionsByAccount(id);

        } catch (SQLException e) {
            logger.error("Transactions Database Error Occurred",e);
            throw new StatusCodeException(StatusCode.SERVER_ERROR, "Transactions Database Error Occurred");
        }

        double EntertainmentTotal = 0;
        double ClothingTotal = 0;
        double GiftsTotal = 0;
        double CoffeeTotal = 0;
        double OnlineRetailerTotal = 0;
        double OtherTotal = 0;
        double FurnitureTotal = 0;
        double HealthBeautyTotal = 0;
        double OpticiansTotal = 0;
        double GroceriesTotal = 0;
        double EatingOutTotal = 0;
        double JewleryTotal = 0;
        double UtilitiesTotal = 0;
        double DepartmentStoreTotal = 0;
        double BooksTotal = 0;


        for (Transaction transaction:transactions){
            if (Objects.equals(transaction.getTransaction_type(), "PAYMENT")) {
                String category = db.getBusinessFromID(transaction.getTo()).getCategory();
                if (Objects.equals(category, "Entertainment")) {
                    EntertainmentTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Clothing")) {
                    ClothingTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Gifts")) {
                    GiftsTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Coffee")) {
                    CoffeeTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Online Retailer")) {
                    OnlineRetailerTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Other")) {
                    OtherTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Furniture")) {
                    FurnitureTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Health & Beauty")) {
                    HealthBeautyTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Opticians")) {
                    OpticiansTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Groceries")) {
                    GroceriesTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Eating Out")) {
                    EatingOutTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Jewlery")) {
                    JewleryTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Utilities")) {
                    UtilitiesTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Department Store")) {
                    DepartmentStoreTotal += transaction.getAmount();
                } else if (Objects.equals(category, "Books")) {
                    BooksTotal += transaction.getAmount();
                }

            }
        }

        model.put("EntertainmentTotal", EntertainmentTotal);
        model.put("ClothingTotal", ClothingTotal);
        model.put("GiftsTotal", GiftsTotal);
        model.put("CoffeeTotal", CoffeeTotal);
        model.put("OnlineRetailerTotal", OnlineRetailerTotal);
        model.put("OtherTotal", OtherTotal);
        model.put("FurnitureTotal", FurnitureTotal);
        model.put("HealthBeautyTotal", HealthBeautyTotal);
        model.put("OpticiansTotal", OpticiansTotal);
        model.put("GroceriesTotal", GroceriesTotal);
        model.put("EatingOutTotal", EatingOutTotal);
        model.put("JewleryTotal", JewleryTotal);
        model.put("UtilitiesTotal", UtilitiesTotal);
        model.put("DepartmentStoreTotal", DepartmentStoreTotal);
        model.put("BooksTotal", BooksTotal);
        return new ModelAndView("summary.hbs", model);
    }
}
