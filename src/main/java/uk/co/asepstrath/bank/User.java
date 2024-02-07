package uk.co.asepstrath.bank;
import java.util.ArrayList;
public class User {
    private String name,password;
    private ArrayList<String> ids;
    public User () {
        ArrayList<String> ids = new ArrayList<String>();
        name = "";
        password = "";
    }

    public void addId(String id){
        ids.add(id);
    }
    public void removeId(String id){
        ids.remove(id);
    }

    public String getName(){
        return name;
    }
    public void SetName(String input){
        name = input;
    }

    public String getPassword(){
        return password;
    }
    public void SetPassword(String input){
        password = input;
    }
}
