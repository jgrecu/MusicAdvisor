package advisor;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OauthServer {
    private String code;
    private HttpServer httpServer;

    public void start() {
        try {
            this.httpServer = HttpServer.create();
            this.httpServer.bind(new InetSocketAddress(8080), 0);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createContext() {
        httpServer.createContext("/", exchange -> {
            String msg = "Authorization code not found. Try again.";
            String query = exchange.getRequestURI().getQuery();
            if (query != null && query.matches("^code=.+")) {
                msg = "Got the code. Return back to your program.";
                this.code = query.replaceFirst("^code=", "");
            }
            exchange.sendResponseHeaders(200, msg.length());
            exchange.getResponseBody().write(msg.getBytes());
            exchange.getResponseBody().close();
        });
    }

    public void stop() {
        httpServer.stop(1);
    }

    public String getTokenData(String code, String url) {
        HttpRequest request = HttpRequest
                .newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&code=" + code + "&redirect_uri=http://localhost:8080"))
                .headers("Authorization", "Basic /* put the bearer token*/")
                .uri(URI.create(url + "/api/token"))
                .build();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            this.code = null;
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getCode() {
        if (isCodeReceived()) {
            return code;
        } else {
            throw new IllegalStateException("Code isn't received");
        }
    }

    public boolean isCodeReceived() {
        if (code != null) {
            return true;
        } else {
            return false;
        }
    }
}
