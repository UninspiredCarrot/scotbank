package uk.co.asepstrath.bank;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account(0);
        assertTrue(a != null);
        Assertions.assertTrue(a.getBalance() == 0);
    }

    @Test
    public void accountDeposits(){
        Account a = new Account(20);
        a.deposit(50);
        assertTrue(a.getBalance() == 70);

    }

    @Test
    public void accountWithdrawls() {
        Account a = new Account(40);
        a.withdraw(20);
        assertTrue(a.getBalance() == 20);

        Account b = new Account(30);
        assertThrows(ArithmeticException.class,() -> b.withdraw(100));
    }

    @Test
    public void superSaving() {
        Account a = new Account(20);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);
        a.deposit(10);

        a.withdraw(20);
        a.withdraw(20);
        a.withdraw(20);

        assertTrue(a.getBalance() == 10);

    }

    @Test
    public void pennies() {
        Account a = new Account(5.45);
        a.deposit(17.56);
        assertEquals(23.01, a.getBalance());
    }

}
