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
import java.util.Scanner;

public class MenuController {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String ERROR = "Please, provide access for application.";
    private static String authorizationUrl = "https://accounts.spotify.com";
    private static String apiServerPath = "https://api.spotify.com";
    private static Integer entriesPerPage = 5;
    private static final String newReleasesURL = apiServerPath + "/v1/browse/new-releases";
    private static final String featuredURL = apiServerPath + "/v1/browse/featured-playlists";
    private static final String categoriesURL = apiServerPath + "/v1/browse/categories";

    private static JsonObject authResponse;

    public MenuController() {
    }

    public static void setEntriesPerPage(String entries) {
        entriesPerPage = Integer.parseInt(entries);
    }

    public static void setResourceUrl(String ServerPath) {
        apiServerPath = ServerPath;
    }

    public static void setAuthorizationUrl(String authUrl) {
        authorizationUrl = authUrl;
    }

    public static void start() {
        boolean isRunning = true;
        boolean isAuthorized = false;
        String command = "";
        while (isRunning) {
            boolean isNext = true;
            boolean based = true;
            String option = scanner.nextLine().strip();
            if (!"next".equals(option) && !"prev".equals(option)) {
                command = option;
            } else if ("prev".equals(option)) {
                isNext = false;
                based = false;
            } else {
                isNext = true;
                based = false;
            }
            String choice = command.contains(" ") ? command.substring(0, command.indexOf(" ")) : command;
            switch (choice) {
                case "new":
                    if (isAuthorized) {
                        MenuViewer.printNewReleases(entriesPerPage, isNext, based);
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "featured":
                    if (isAuthorized) {
                        MenuViewer.printFeatures(entriesPerPage, isNext, based);
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "categories":
                    if (isAuthorized) {
                        MenuViewer.printCategoryNames(entriesPerPage, isNext, based);
                    } else {
                        System.out.println(ERROR);
                    }
                    break;
                case "playlists":
                    String category = option.substring(option.indexOf(" ") + 1);
                    if (isAuthorized) {
                        MenuViewer.printPlaylists(category, entriesPerPage, isNext, based);
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

    private static boolean getAuthorized() {

        OauthServer server = new OauthServer();

        server.start();
        server.createContext();
        MenuViewer.printAuthView(authorizationUrl);
        while (!server.isCodeReceived()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MenuViewer.printGetAccessToken();
        String response = server.getTokenData(server.getCode(), authorizationUrl);
        authResponse = JsonParser.parseString(response).getAsJsonObject();
        server.stop();
        if (authResponse.has("access_token")) {
            System.out.println("Success!");
            return true;
        }
        System.out.println("Failed!");
        return false;
    }

    public static JsonObject getNewReleases() {
        JsonObject response = getDataFromApi(newReleasesURL);
        return response.get("albums").getAsJsonObject();

    }

    public static JsonObject getCategoryNames() {
        JsonObject categories = getDataFromApi(categoriesURL);
        return categories.get("categories").getAsJsonObject();
    }

    public static JsonObject getFeatures() {
        JsonObject features = getDataFromApi(featuredURL);
        return features.get("playlists").getAsJsonObject();
    }

    public static JsonObject getPlaylists(String categoryName) {
        String categoryID = getCategoryIdByCategoryName(categoryName);
        if (categoryID == null) {
            System.out.println("Unknown category name.");
            return null;
        }
        final String playlistsURL = apiServerPath + "/v1/browse/categories/" + categoryID + "/playlists";
        JsonObject playlists = getDataFromApi(playlistsURL);
        if (playlists.has("error")) {
            System.out.println(playlists.get("error").getAsJsonObject().get("message").getAsString());
            return null;
        }
        return playlists;
    }

    private static JsonObject getDataFromApi(String url) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + MenuController.authResponse.get("access_token").getAsString())
                .uri(URI.create(url))
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

    private static String getCategoryIdByCategoryName(String categoryName)  {

        JsonObject categories = getDataFromApi(categoriesURL);
        JsonArray items = categories.get("categories").getAsJsonObject().get("items").getAsJsonArray();

        for(JsonElement item: items){
            String name = item.getAsJsonObject().get("name").getAsString();
            if(categoryName.equals(name)){
                return item.getAsJsonObject().get("id").getAsString();
            }
        }
        return null;
    }
}
