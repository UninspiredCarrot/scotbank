package uk.co.asepstrath.bank;

import org.junit.jupiter.api.Assertions;
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
        a.addId("test");
        a.addId("test2");
        assertTrue(a.idsLength() == 2);
    }

    @Test
    public void getIds(){
        ArrayList<String> temp = new ArrayList<>();
        temp.add("test");
        temp.add("test2");
        User a = new User();
        a.addId("test");
        a.addId("test2");
        assertTrue(temp.equals(a.getIds()));


    }
}
