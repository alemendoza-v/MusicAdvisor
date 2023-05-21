package advisor.controller;

import advisor.model.Album;
import advisor.model.Category;
import advisor.model.Playlist;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class SpotifyClient {
    private final HttpClient client = HttpClient.newBuilder().build();
    private final String client_id = "0fe8adf0ea6340a2a36211b0357ea73b";
    private final String client_secret = "94cecadce7ff4ef39103a985b0fe3cf8";
    private final String redirect_uri = "http://localhost:8080/spotify";
    private final String uriToken;
    private final String uriAuth;
    private String accessToken;
    private final String apiServerPoint;

    public SpotifyClient(String accessServerPoint, String apiServerPoint) {
        this.uriToken = accessServerPoint + "/api/token";
        this.uriAuth = accessServerPoint + "/authorize?client_id=" +
                client_id + "&redirect_uri=" + redirect_uri + "&response_type=code";
        this.apiServerPoint = apiServerPoint;
    }

    public void getAccessToken(String code) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization",  "Basic " +
                        Base64.getEncoder().encodeToString((client_id + ":" + client_secret).getBytes()))
                .uri(URI.create(uriToken))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + code + "&redirect_uri=" + redirect_uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonObject responseJson = JsonParser.parseString(response.body()).getAsJsonObject();
        accessToken = responseJson.get("access_token").getAsString();
    }

    public String getAuthLink() {
        return uriAuth;
    }
    
    private JsonObject sendRequest(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization",  "Bearer " + accessToken)
                .uri(URI.create(apiServerPoint + uri + "?limit=18"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonParser.parseString(response.body()).getAsJsonObject();
    }

    public List<Album> getNewAlbums() throws IOException, InterruptedException {
        JsonObject response = sendRequest("/v1/browse/new-releases");

        if (response.has("error")) {
            System.out.println(response.getAsJsonObject("error").get("message").getAsString());
            return null;
        }

        List<JsonObject> albums = new ArrayList<>();
        response.getAsJsonObject("albums").getAsJsonArray("items")
                .forEach(item -> albums.add(item.getAsJsonObject()));

        List<Album> result = new ArrayList<>();

        for (JsonObject album : albums) {
            List<JsonObject> artists = new ArrayList<>();
            album.getAsJsonArray("artists")
                    .forEach(item -> artists.add(item.getAsJsonObject()));
            String name = album.get("name").getAsString();
            String url = album.getAsJsonObject("external_urls").get("spotify").getAsString();
            List<String> artistsString = new ArrayList<>();
            for (JsonObject artist : artists) {
                artistsString.add(artist.get("name").getAsString());
            }
            result.add(new Album(name, artistsString, url));
        }
        return result;
    }

    public List<Playlist> getFeaturedPlaylists() throws IOException, InterruptedException {
        JsonObject response = sendRequest("/v1/browse/featured-playlists");

        if (response.has("error")) {
            System.out.println(response.getAsJsonObject("error").get("message").getAsString());
            return null;
        }

        List<JsonObject> playlists = new ArrayList<>();
        response.getAsJsonObject("playlists").getAsJsonArray("items")
                .forEach(item -> playlists.add(item.getAsJsonObject()));

        List<Playlist> result = new ArrayList<>();
        for (JsonObject playlist : playlists) {
            String name = playlist.get("name").getAsString();
            String url = playlist.getAsJsonObject("external_urls").get("spotify").getAsString();
            result.add(new Playlist(name, url));
        }
        return result;
    }

    public List<Category> getCategories() throws IOException, InterruptedException {
        JsonObject response = sendRequest("/v1/browse/categories");

        if (response.has("error")) {
            System.out.println(response.getAsJsonObject("error").get("message").getAsString());
            return null;
        }

        List<JsonObject> categories = new ArrayList<>();
        response.getAsJsonObject("categories").getAsJsonArray("items")
                .forEach(item -> categories.add(item.getAsJsonObject()));

        List<Category> result = new ArrayList<>();
        for (JsonObject category : categories) {
            String id = category.get("id").getAsString();
            String name = category.get("name").getAsString();
            result.add(new Category(name, id));
        }
        return result;
    }

    public List<Playlist> getPlaylists(Category category) throws IOException, InterruptedException {
        JsonObject response = sendRequest("/v1/browse/categories/" + category.getId() + "/playlists");
        if (response.has("error")) {
            System.out.println(response.getAsJsonObject("error").get("message").getAsString());
            return null;
        }

        List<JsonObject> playlists = new ArrayList<>();
        response.getAsJsonObject("playlists").getAsJsonArray("items")
                .forEach(item -> playlists.add(item.getAsJsonObject()));

        List<Playlist> result = new ArrayList<>();
        for (JsonObject playlist : playlists) {
            String name = playlist.get("name").getAsString();
            String url = playlist.getAsJsonObject("external_urls").get("spotify").getAsString();
            result.add(new Playlist(name, url));
        }
        return result;
    }
}
