package advisor.model;

public class Category implements Printable{
    String name;
    String id;

    public Category(String name, String id) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void print() {
        System.out.println(name + "\n");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
