package advisor.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Server {
        private HttpServer server;
        private BlockingDeque<String> codes = new LinkedBlockingDeque<>();

        public void start() throws IOException {
            server = HttpServer.create();
            server.bind(new InetSocketAddress(8080), 0);
            server.createContext("/spotify", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    try {
                        String query = exchange.getRequestURI().getQuery();
                        String message;
                        if (query != null && query.contains("code")) {
                            message = "Got the code. Return back to your program.";
                            String accessCode = query.substring(5);
                            codes.add(accessCode);
                        } else {
                            message = "Authorization code not found. Try again.";
                        }
                        exchange.sendResponseHeaders(200, message.length());
                        exchange.getResponseBody().write(message.getBytes());
                        exchange.getResponseBody().close();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            });
            codes.clear();
            server.start();
        }

        public void stop() {
            server.stop(1);
            codes.clear();
        }

        public String getAccessCode() throws InterruptedException {
            return codes.take();
        }

}
