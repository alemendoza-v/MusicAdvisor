package advisor;

import advisor.controller.Controller;
import advisor.controller.SpotifyClient;

import java.io.IOException;

import static advisor.controller.UserInput.readUserInput;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String accessServerPoint = "https://accounts.spotify.com";
        String apiServerPoint = "https://api.spotify.com";
        int pageSize = 5;

        // Read args
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-access") && i < args.length - 1) {
                accessServerPoint = args[i + 1];
            }
            if (args[i].equals("-resource") && i < args.length - 1) {
                apiServerPoint = args[i + 1];
            }
            if (args[i].equals("-page") && i < args.length - 1) {
                pageSize = Integer.parseInt(args[i + 1]);
            }
        }

        SpotifyClient spotifyClient = new SpotifyClient(accessServerPoint, apiServerPoint);
        Controller controller = new Controller(spotifyClient, pageSize);

        while (true) {
            String command = readUserInput();
            controller.readCommand(command);
        }
    }
}
