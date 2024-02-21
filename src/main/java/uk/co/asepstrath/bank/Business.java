package uk.co.asepstrath.bank;

public class Business {
    private String  id,
            name,
            category,
            sanctioned;

    public Business() {
    }

    public Business(String id, String name, String category, String sanctioned) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.sanctioned = sanctioned;
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSanctioned(){return sanctioned;}

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSanctioned(String sanctioned) {
        this.sanctioned = sanctioned;
    }

    @Override
    public String toString(){

        return String.format(
                "{id: %s}\n{name: %s}\n{category: %s}\n{sanctioned: %s}",
                this.id, this.name, this.category, this.sanctioned
        );
    }

}
