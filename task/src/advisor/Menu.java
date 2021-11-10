package advisor;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final String ERROR = "Please, provide access for application.";
    private String authorizationUrl;
    private String apiServerPath;

    private JsonObject authResponse;
    private JsonObject queryResponse;

    public void setResourceUrl(String apiServerPath) {
        this.apiServerPath = apiServerPath;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public void start() {
        boolean isRunning = true;
        boolean isAuthorized = false;
        while (isRunning) {
            String option = scanner.nextLine().strip();
            String choice = option.contains(" ") ? option.substring(0, option.indexOf(" ")) : option;
            switch (choice) {
                case "new":
                    if (isAuthorized) {
                        printNew();
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "featured":
                    if (isAuthorized) {
                        printFeatured();
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "categories":
                    if (isAuthorized) {
                        printCategories();
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "playlists":
                    String category = option.substring(option.indexOf(" ") + 1);
                    if (isAuthorized) {
                        printCategoryID(category);
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "auth":
                    isAuthorized = getAuthorized();
                    break;
                case "exit":
                    System.out.println("---GOODBYE!---");
                    isRunning = false;
                    break;
                default:
                    break;
            }
        }
    }

    private boolean getAuthorized() {
        if (authorizationUrl == null) {
            authorizationUrl = "https://accounts.spotify.com";
        }

        Server server = new Server();

        server.start();
        server.createContext();
        System.out.println("use this link to request the access code:");
        System.out.println(authorizationUrl +
                            "/authorize?client_id=2b90caa156094f3a91eac30f19349609" +
                            "&response_type=code&redirect_uri=http://localhost:8080");
        System.out.println("waiting for code...");
        while (!server.isCodeReceived()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("code received");
        System.out.println("making http request for access_token...");
        String response = server.getTokenData(server.getCode(), authorizationUrl);
        authResponse = JsonParser.parseString(response).getAsJsonObject();
        server.stop();
        if (authResponse.has("access_token")) {
            //System.out.println("access_token: " + authResponse.get("access_token").getAsString());
            System.out.println("Success!");
            return true;
        }
        System.out.println("Failed!");
        return false;
    }


    private void printNew() {
        if (apiServerPath == null) {
            apiServerPath = "https://api.spotify.com";
        }
        final String newReleasesURL = apiServerPath + "/v1/browse/new-releases";
        queryResponse = getQueryResponse(newReleasesURL);

        JsonObject albums = queryResponse.get("albums").getAsJsonObject();
        albums.get("items").getAsJsonArray().forEach(item -> {
            JsonObject album = item.getAsJsonObject();
            String name = album.get("name").getAsString();
            String url = album.get("external_urls").getAsJsonObject().get("spotify").getAsString();
            List<String> artists = new ArrayList<>();
            album.get("artists").getAsJsonArray().forEach(artist -> {
                artists.add(artist.getAsJsonObject().get("name").getAsString());
                System.out.println(name);
                System.out.println(artists);
                System.out.println(url);
                System.out.println();
            });
        });
    }

    private void printFeatured() {
        if (apiServerPath == null) {
            apiServerPath = "https://api.spotify.com";
        }
        final String featuredURL = apiServerPath + "/v1/browse/featured-playlists";
        queryResponse = getQueryResponse(featuredURL);

        JsonArray items = queryResponse.get("playlists").getAsJsonObject().get("items").getAsJsonArray();
        items.forEach(item -> {
            JsonObject playlist = item.getAsJsonObject();
            System.out.println(playlist.get("name").getAsString());
            System.out.println(playlist.get("external_urls").getAsJsonObject().get("spotify").getAsString());
            System.out.println();
        });
    }

    private void printCategories() {
        if (apiServerPath == null) {
            apiServerPath = "https://api.spotify.com";
        }
        final String categoriesURL = apiServerPath + "/v1/browse/categories";
        queryResponse = getQueryResponse(categoriesURL);

        JsonArray categories = queryResponse.get("categories").getAsJsonObject().get("items").getAsJsonArray();
        categories.forEach(item -> {
            JsonObject category = item.getAsJsonObject();
            String categoryName = category.get("name").getAsString();
            System.out.println(categoryName);
        });
    }

    private void printCategoryID(String category) {
        String categoryID = getCategoryIdByCategoryName(category);
        if (categoryID == null) {
            System.out.println("Unknown category name.");
            return;
        }
        if (apiServerPath == null) {
            apiServerPath = "https://api.spotify.com";
        }
        final String playlistsURL = apiServerPath + "/v1/browse/categories/" + categoryID + "/playlists";
        queryResponse = getQueryResponse(playlistsURL);

        if (queryResponse.has("error")) {
            System.out.println("Specified id doesn't exist");
            return;
        }

        JsonArray items = queryResponse.get("playlists").getAsJsonObject().get("items").getAsJsonArray();
        items.forEach(item -> {
            JsonObject playlist = item.getAsJsonObject();
            System.out.println(playlist.get("name"));
            System.out.println(playlist.get("external_urls").getAsJsonObject().get("spotify").getAsString());
            System.out.println();
        });

    }

    private JsonObject getQueryResponse(String apiPath) {
        queryResponse = null;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + authResponse.get("access_token").getAsString())
                .uri(URI.create(apiPath))
                .GET()
                .build();
        try {
            HttpClient client = HttpClient.newBuilder().build();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return JsonParser.parseString(response.body()).getAsJsonObject();
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    private String getCategoryIdByCategoryName(String categoryName)  {
        if (apiServerPath == null) {
            apiServerPath = "https://api.spotify.com";
        }
        final String categoriesURL = apiServerPath + "/v1/browse/categories";
        queryResponse = getQueryResponse(categoriesURL);

        JsonArray items = queryResponse.get("categories").getAsJsonObject().get("items").getAsJsonArray();
        for(JsonElement item: items){
            String name = item.getAsJsonObject().get("name").getAsString();
            if(categoryName.equals(name)){
                return item.getAsJsonObject().get("id").getAsString();
            }
        }
        return null;
    }
}
