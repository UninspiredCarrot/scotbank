package uk.co.asepstrath.bank;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

@Path("/bank")
public class AppController {
    private final DataSource dataSource;
    private final Logger logger;
    private final DatabaseUtil db;
    /*
    This constructor can take in any dependencies the controller may need to respond to a request
     */
    public AppController(DataSource ds, Logger log) {
        dataSource = ds;
        logger = log;
        this.db = new DatabaseUtil(ds);
    }

    @GET
    public String welcome() { return "Welcome to Scotbank! "; }

    @GET("/overview")
    public ModelAndView overview(@QueryParam String id) {
        Map<String, Object> model = new HashMap<>();
        Account acc = new Account();

        try {
            Statement stmt = this.dataSource.getConnection().createStatement();

            String sql = "SELECT * FROM accounts WHERE id = " + id;

            ResultSet rs = stmt.executeQuery(sql);

            rs.next();
            acc = new Account();
            acc.setId(rs.getString("id"));
            acc.setName(rs.getString("name"));
            acc.deposit(rs.getDouble("balance"));
            acc.setRoundUpEnabled(rs.getBoolean("round_up_enabled"));

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

    @GET("/view_transaction")
    public ModelAndView view_transaction(@QueryParam String transaction_id){

        Map<String, Object> model = new HashMap<>();
        Transaction transaction;

        try
        {
            transaction = this.db.readTransactionByID(transaction_id);
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
