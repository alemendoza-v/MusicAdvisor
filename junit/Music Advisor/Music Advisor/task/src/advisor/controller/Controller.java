package advisor.controller;

import advisor.model.*;
import advisor.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class Controller {
    SpotifyClient spotifyClient;
    View view = new View();
    DB db = new DB();
    boolean isAuthorized = false;
    int currentPage = 1;
    String lastCommand = "";
    int pageSize;
    int maxPages;
    
    public Controller(SpotifyClient spotifyClient, int pageSize) {
        this.spotifyClient = spotifyClient;
        this.pageSize = pageSize;
    }
    
    private String getCode(SpotifyClient spotifyClient) throws IOException, InterruptedException {
        Server server = new Server();
        try {
            server.start();
            view.printMessages(List.of(
                    "use this link to request the access code:",
                    spotifyClient.getAuthLink(),
                    "waiting for code..."));
            return server.getAccessCode();
        } finally {
            server.stop();
        }
    }

    private int getStartIndex(int currentPage) {
       return currentPage * pageSize - pageSize;
    }

    private int getEndIndex(int size, int currentPage) {
        return Math.min(currentPage * pageSize, size);
    }
    
    public void readCommand(String command) throws IOException, InterruptedException {
        switch (command.split(" ")[0]) {
            case "auth" -> {
                String code = getCode(spotifyClient);
                view.printMessages(List.of("code received", "making http request for access_token..."));
                spotifyClient.getAccessToken(code);
                isAuthorized = true;
                view.printMessages(List.of("---SUCCESS---"));
                lastCommand = "auth";
            }

            case "new" -> {
                if (isAuthorized) {
                    List<Album> albums = spotifyClient.getNewAlbums();
                    db.setAlbums(albums);
                    if (albums != null) {
                        currentPage = 1;
                        maxPages = (int) Math.ceil((double) albums.size() / (double) pageSize);
                        int startIndex = getStartIndex(currentPage);
                        int endIndex = getEndIndex(albums.size(), currentPage);
                        view.printObjects(albums.subList(startIndex, endIndex));
                        view.printMessages(List.of("---PAGE " + currentPage + " OF " + maxPages + "---"));
                    }
                } else {
                    view.printUnauthorized();
                }
                lastCommand = "new";
            }

            case "featured" -> {
                if (isAuthorized) {
                    List<Playlist> playlists = spotifyClient.getFeaturedPlaylists();
                    db.setPlaylists(playlists);
                    if (playlists != null) {
                        currentPage = 1;
                        maxPages = (int) Math.ceil((double) playlists.size() / (double) pageSize);
                        int startIndex = getStartIndex(currentPage);
                        int endIndex = getEndIndex(playlists.size(), currentPage);
                        view.printObjects(playlists.subList(startIndex, endIndex));
                        view.printMessages(List.of("---PAGE " + currentPage + " OF " + maxPages + "---"));
                    }
                } else {
                    view.printUnauthorized();
                }
                lastCommand = "featured";
            }

            case "categories" -> {
                if (isAuthorized) {
                    List<Category> categories = spotifyClient.getCategories();
                    db.setCategories(categories);
                    if (categories != null) {
                        currentPage = 1;
                        maxPages = (int) Math.ceil((double) categories.size() / (double) pageSize);
                        int startIndex = getStartIndex(currentPage);
                        int endIndex = getEndIndex(categories.size(), currentPage);
                        view.printObjects(categories.subList(startIndex, endIndex));
                        view.printMessages(List.of("---PAGE " + currentPage + " OF " + maxPages + "---"));
                    }
                } else {
                    view.printUnauthorized();
                }
                lastCommand = "categories";
            }

            case "playlists" -> {
                if (isAuthorized) {
                    StringBuilder optionStringBuilder = new StringBuilder();
                    for (String word : command.split(" ")) {
                        if (!word.equals("playlists")) {
                            optionStringBuilder.append(" ").append(word);
                        }
                    }
                    String option = optionStringBuilder.toString().trim();
                    List<Category> categories = spotifyClient.getCategories();
                    db.setCategories(categories);
                    if (categories != null) {
                        Optional<Category> category = categories.stream()
                                .filter(c -> c.getName().equalsIgnoreCase(option))
                                .findFirst();

                        if (category.isPresent()) {
                            List<Playlist> playlists = spotifyClient.getPlaylists(category.get());
                            db.setPlaylists(playlists);
                            currentPage = 1;
                            maxPages = (int) Math.ceil((double) playlists.size() / (double) pageSize);
                            int startIndex = getStartIndex(currentPage);
                            int endIndex = getEndIndex(playlists.size(), currentPage);
                            view.printObjects(playlists.subList(startIndex, endIndex));
                            view.printMessages(List.of("---PAGE " + currentPage + " OF " + maxPages + "---"));
                        } else {
                            System.out.println("Unknown category name.");
                        }
                    }

                } else {
                    view.printUnauthorized();
                }
                lastCommand = "playlists";
            }

            case "next" -> {
                if (currentPage < maxPages) {
                    currentPage++;
                    List<? extends Printable> list = db.getList(lastCommand);
                    int startIndex = getStartIndex(currentPage);
                    int endIndex = getEndIndex(list.size(), currentPage);
                    view.printObjects(list.subList(startIndex, endIndex));
                    view.printMessages(List.of("---PAGE " + currentPage + " OF " + maxPages + "---"));
                } else {
                    view.printMessages(List.of("No more pages."));
                }
            }

            case "prev" -> {
                if (currentPage != 1) {
                    currentPage--;
                    List<? extends Printable> list = db.getList(lastCommand);
                    int startIndex = getStartIndex(currentPage);
                    int endIndex = getEndIndex(list.size(), currentPage);
                    view.printObjects(list.subList(startIndex, endIndex));
                    view.printMessages(List.of("---PAGE " + currentPage + " OF " + maxPages + "---"));
                } else {
                    view.printMessages(List.of("No more pages."));
                }
            }

            case "exit" -> {
                view.printMessages(List.of("---GOODBYE!---"));
            }
        }
    }
}
