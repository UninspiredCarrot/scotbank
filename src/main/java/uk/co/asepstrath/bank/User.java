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

    public void addAccount(Account account){
        accounts.add(account);
    }

    public void removeAccount(Account account){
        accounts.remove(account);
    }

    public int accountsLength() {
        return accounts.size();
    }

    public ArrayList<Account> getAccounts(){
        ArrayList<Account> output = new ArrayList<Account>();
        for(int i = 0; i < accounts.size(); i++){
            output.add(accounts.get(i));
        }
        return output;
    }
    public String getName(){
        return name;
    }
    public void setName(String input){
        name = input;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String input){
        password = input;
    }
}
