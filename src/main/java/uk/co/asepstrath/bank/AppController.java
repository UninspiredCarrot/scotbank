package uk.co.asepstrath.bank;

import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.annotation.*;
import io.jooby.exception.StatusCodeException;
import org.slf4j.Logger;

import javax.sql.DataSource;
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
    public ModelAndView overview(@QueryParam String uuid) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", uuid);

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
