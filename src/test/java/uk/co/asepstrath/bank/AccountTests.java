package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AccountTests {
    @Test
    public void createAccount(){
        Account a = new Account();
        assertTrue(a != null);
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
        assertTrue(a.getBalance() == 20);
        a.deposit(50);
        assertTrue(a.getBalance() == 70);
    }

    @Test
    public void withdrawMoney(){
        Account a = new Account();
        a.deposit(40);
        assertTrue(a.getBalance() == 40);
        a.withdraw(20);
        assertTrue(a.getBalance() == 20);
    }

    @Test
    public void overdraftTest() {
        Account a = new Account();
        a.deposit(30);
        assertTrue(a.getBalance() == 30);
        Assertions.assertThrows(ArithmeticException.class, () -> a.withdraw(100));
    }

    @Test
    public void SavingTest() {
        Account a = new Account();
        a.deposit(20);
        assertTrue(a.getBalance() == 20);
        for(int i = 0; i<5; i++) {
            a.deposit(10);
        }
        assertTrue(a.getBalance() == 70);
        for(int i = 0; i<3; i++) {
            a.withdraw(20);
        }
        assertTrue(a.getBalance() == 10);
    }

    @Test
    public void decimalTest() {
        Account a = new Account();
        a.deposit(5.45);
        assertTrue(a.getBalance() == 5.45);
        a.deposit(17.56);
        assertTrue(a.getBalance() == 23.01);
    }
}
