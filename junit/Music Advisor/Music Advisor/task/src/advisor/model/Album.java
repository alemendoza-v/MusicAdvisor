package advisor.model;

import java.util.ArrayList;
import java.util.List;

public class Album implements Printable {
    private final String name;
    private final List<String> artists;
    private final String url;

    public Album(String name, List<String> artists, String url) {
        this.name = name;
        this.artists = artists;
        this.url = url;
    }

    public String getArtists() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (int i = 0; i < artists.size(); i++) {
            result.append(artists.get(i));
            if (i < artists.size() - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }

    @Override
    public void print() {
        System.out.println(name + "\n" + getArtists() + "\n" + url + "\n");
    }
}
