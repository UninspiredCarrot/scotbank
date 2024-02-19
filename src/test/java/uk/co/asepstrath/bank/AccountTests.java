package uk.co.asepstrath.bank;

import static org.junit.jupiter.api.Assertions.*;

import io.jooby.test.JoobyTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

@JoobyTest(App.class)
public class AccountTests {

    Account account;

    @BeforeEach
    public void setUp(){
        account = new Account("5d85cff3-4792-43fe-9674-173bf7ef5c5c", "Mr. Rickey Upton", 544.04, false);
    }

    @Test
    public void createAccount(){
        Account a = new Account();
        assertNotNull(a);
    }

    @Test
    public void testGetters(){
        assertEquals("5d85cff3-4792-43fe-9674-173bf7ef5c5c", account.getId());
        assertEquals("Mr. Rickey Upton", account.getName());
        assertEquals(544.04, account.getBalance());
        assertFalse(account.isRoundUpEnabled());
    }

    @Test
    public void testSetters(){
        account.setId("5d85cff3-4792-43fe-9674-124n6j38shf");
        assertEquals("5d85cff3-4792-43fe-9674-124n6j38shf", account.getId());
        account.setName("Mrs. Jane Doe");
        assertEquals("Mrs. Jane Doe", account.getName());
        account.setStartingBalance(42.42);
        assertEquals(42.42, account.getBalance());
        account.setRoundUpEnabled(true);
        assertTrue(account.isRoundUpEnabled());
    }

    @Test
    public void testWrongDataTypeSetters(){

    }

    @Test
    public void checkNewBalance(){
        Account a = new Account();
        assertEquals(0, a.getBalance());
    }

    @Test
    public void depositMoney(){
        double before = account.getBalance();
        account.deposit(50);
        assertEquals((before+50), account.getBalance());
    }

    @Test
    public void withdrawMoney(){
        double before = account.getBalance();
        account.withdraw(20);
        assertEquals((before-20), account.getBalance());
    }

    @Test
    public void overdraftTest() {
        Account a = new Account();
        a.setStartingBalance(20);
        assertEquals(20, a.getBalance());
        assertThrows(ArithmeticException.class, () -> a.withdraw(100));
    }

    @Test
    public void pence() {
        Account a = new Account();
        a.setStartingBalance(5.45);
        a.deposit(17.56);
        assertEquals(23.01, a.getBalance());
    }

    @Test
    public void testToString(){
        assertEquals(
                "{id: 5d85cff3-4792-43fe-9674-173bf7ef5c5c}" +
                "\n{name: Mr. Rickey Upton}" +
                "\n{starting balance: 544.04}" +
                "\n{is rounding up enabled: false}",
                account.toString());
    }

    @Test
    public void testSetTransactions(){
        account.setTransactions(null);
        assertNull(account.getTransactions());
    }

    @Test public void testGetTransactions(){
        assertNotNull(account.getTransactions());
    }
}
