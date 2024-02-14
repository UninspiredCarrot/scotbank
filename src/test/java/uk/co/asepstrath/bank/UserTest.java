package uk.co.asepstrath.bank;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    public void createUser(){
        User a = new User();
        assertTrue(a != null);
    }
    @Test
    public void getUser(){
        User a = new User();
        assertTrue(a.getName() != null);
        assertTrue(a.getPassword() != null);
    }

    @Test
    public void setUser(){
        User a = new User();
        a.setName("Tom");
        a.setPassword("Jerry");
        assertTrue(a.getName() == "Tom");
        assertTrue(a.getPassword() == "Jerry");
    }

    @Test
    public void addIds(){
        User a = new User();
        Account b = new Account();
        Account c = new Account();
        a.addAccount(b);
        a.addAccount(c);
        assertTrue(a.accountsLength() == 2);
    }

    @Test
    public void getAccounts(){
        User a = new User();
        Account b = new Account();
        a.addAccount(b);
        assertTrue(b.equals(a.getAccounts().get(0)));


    }
}
