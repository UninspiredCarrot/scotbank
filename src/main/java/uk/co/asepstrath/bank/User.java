package uk.co.asepstrath.bank;
import java.util.ArrayList;
public class User {
    private int id;
    private String name,password;
    private ArrayList<Account> accounts;
    public User () {
        this.accounts = new ArrayList<>();
        this.name = "";
        this.password = "";
    }

    public User(int id, String name, String password, ArrayList<Account> accounts) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.accounts = accounts;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }

    public void removeAccount(Account account){
        accounts.remove(account);
    }

    public int accountsLength() {
        return accounts.size();
    }

    public int getId(){return id;}

    public ArrayList<Account> getAccounts(){
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts){this.accounts = accounts;}

    public String getName(){
        return name;
    }
    public void setName(String input){
        name = input;
    }

    public void setId(int id) {this.id = id;}

    public String getPassword(){
        return password;
    }
    public void setPassword(String input){
        password = input;
    }
}
