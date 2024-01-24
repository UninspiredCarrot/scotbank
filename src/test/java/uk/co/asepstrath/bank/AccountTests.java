package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTests {
    @Test
    public void createAccount(){
        Account a = new Account();
        Assertions.assertTrue(a != null);
    }

    @Test
    public void checkNewBalance(){
        Account a = new Account();
        Assertions.assertTrue(a.getBalance() == 0);
    }

    @Test
    public void depositMoney(){
        Account a = new Account();
        a.deposit(20);
        Assertions.assertTrue(a.getBalance() == 20);
        a.deposit(50);
        Assertions.assertTrue(a.getBalance() == 70);
    }

    @Test
    public void withdrawMoney(){
        Account a = new Account();
        a.deposit(40);
        Assertions.assertTrue(a.getBalance() == 40);
        a.withdraw(20);
        Assertions.assertTrue(a.getBalance() == 20);
    }

    @Test
    public void overdraftTest() {
        Account a = new Account();
        a.deposit(30);
        Assertions.assertTrue(a.getBalance() == 30);
        Assertions.assertThrows(ArithmeticException.class, () -> a.withdraw(100));
    }
}
