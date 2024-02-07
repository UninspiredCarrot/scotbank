package uk.co.asepstrath.bank;
import java.util.ArrayList;
public class User {
    private String name,password;
    private ArrayList<String> ids;
    public User () {
        this.ids = new ArrayList<>();
        this.name = "";
        this.password = "";
    }

    public void addId(String id){
        ids.add(id);
    }
    public void removeId(String id){
        ids.remove(id);
    }

    public int idsLength() {
        return ids.size();
    }

    public ArrayList<String> getIds(){
        ArrayList<String> output = new ArrayList<String>();
        for(int i = 0; i < ids.size();i++){
            output.add(ids.get(i));
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
