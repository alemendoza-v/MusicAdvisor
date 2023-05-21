package advisor.model;

import java.util.ArrayList;
import java.util.List;

public class DB {
    private List<Album> albums = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private List<Playlist> playlists = new ArrayList<>();

    public DB() {
    }

    public List<Album> getAlbums() {
        return albums;
    }

    public void setAlbums(List<Album> albums) {
        this.albums = albums;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public List<? extends Printable> getList(String option) {
        switch (option) {
            case "new" -> {
                return this.albums;
            }
            case "featured", "playlists" -> {
                return this.playlists;
            }
            case "categories" -> {
                return this.categories;
            }
            default -> { return null; }
        }
    }
}
