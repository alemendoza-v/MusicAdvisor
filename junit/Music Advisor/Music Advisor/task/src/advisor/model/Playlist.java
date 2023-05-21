package advisor.model;

public class Playlist implements Printable {
    String name;
    String url;

    public Playlist(String name, String url) {
        this.name = name;
        this.url = url;
    }

    @Override
    public void print() {
        System.out.println(name + "\n" + url + "\n");
    }
}
