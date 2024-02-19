package uk.co.asepstrath.bank;

import io.jooby.Jooby;
import io.jooby.ModelAndView;
import io.jooby.StatusCode;
import io.jooby.test.JoobyTest;
import io.jooby.test.MockRouter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;

import java.io.IOException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest{
    /*
    Unit tests should be here
    Example can be found in example/UnitTest.java
     */

    MockRouter router = new MockRouter(new App());

    @Test
    public void welcome(){
        router.get("/bank", rsp -> {
            assertEquals("Welcome to Scotbank! ", rsp.value());
            assertEquals(StatusCode.OK, rsp.getStatusCode());
        });
    }

}
