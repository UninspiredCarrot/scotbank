package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AccountTests {

    @Test
    public void createAccount(){
        Account a = new Account();
        Assertions.assertNotNull(a);
    }
    @Test
    public void AccountDeposit(){
        Account a = new Account();
        a.deposit(20);
        Assertions.assertTrue(a.getBalance() == 20);
        a.deposit(50);
        Assertions.assertTrue(a.getBalance() == 70);
    }

    @Test
    public void AccountWithdraw(){
        Account a = new Account();
        a.deposit(20);
        Assertions.assertTrue(a.getBalance() == 20);
        a.withdraw(20);
        Assertions.assertTrue(a.getBalance() == 0);
    }
}
