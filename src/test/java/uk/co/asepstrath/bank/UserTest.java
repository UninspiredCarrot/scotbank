package uk.co.asepstrath.bank;

import java.util.ArrayList;

import io.jooby.test.JoobyTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@JoobyTest(App.class)
public class UserTest {
    @Test
    public void createUser(){
        User user = new User();
        assertNotNull(user);
    }

    @Test
    public void createUserFullConstructor(){
        User user = new User(
                1,
                "John",
                "password",
                new ArrayList<>()
        );
        assertEquals(1, user.getId());
        assertEquals("John", user.getName());
        assertEquals("password", user.getPassword());
        assertNotNull(user.getAccounts());
    }

    @Test
    public void testUserSettersGetters(){
        User user = new User();
        user.setId(1);
        user.setName("Tom");
        user.setPassword("Jerry");

        assertSame("Tom", user.getName());
        assertSame("Jerry", user.getPassword());
        assertSame(1, user.getId());

        ArrayList<Account> accounts = new ArrayList<>();
        accounts.add(new Account());
        accounts.add(new Account());

        assertTrue(user.getAccounts().isEmpty());
        user.setAccounts(accounts);
        assertFalse(user.getAccounts().isEmpty());
    }

    @Test
    public void addAccounts(){
        User user = new User();
        Account account1 = new Account();
        Account account2 = new Account();
        user.addAccount(account1);
        user.addAccount(account2);
        assertEquals(2, user.accountsLength());
    }

    @Test
    public void getAccounts(){
        User user = new User();
        Account account = new Account();
        user.addAccount(account);
        assertEquals(account, user.getAccounts().get(0));
    }

    @Test
    public void testAddAccount(){
        User user = new User();
        assertTrue(user.getAccounts().isEmpty());
        Account account = new Account();
        user.addAccount(account);
        assertFalse(user.getAccounts().isEmpty());
    }
    @Test
    public void testRemoveAccount(){
        User user = new User();
        Account account = new Account();
        user.addAccount(account);
        assertFalse(user.getAccounts().isEmpty());
        user.removeAccount(account);
        assertTrue(user.getAccounts().isEmpty());
    }

    @Test
    public void testGetAccountSize(){
        User user = new User();
        Account account1 = new Account();
        Account account2 = new Account();
        Account account3 = new Account();
        user.addAccount(account1);
        user.addAccount(account2);
        user.addAccount(account3);

        assertEquals(3, user.getAccounts().size());
    }

    @Test
    public void testToString(){
        User user = new User(
                1,
                "John",
                "password",
                new ArrayList<>()
        );
        assertEquals(
"{id: 1}\n{name: John}\n{password: password}\n----ACCOUNTS----\n",
        user.toString()
        );
    }
}
