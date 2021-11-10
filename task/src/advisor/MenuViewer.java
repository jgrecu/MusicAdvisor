package advisor;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MenuViewer {

    private static int currentNewReleasesPageIdx = 0;
    private static int currentCategoryNamesPageIdx = 0;
    private static int currentPlaylistsPageIdx = 0;
    private static int currentFeaturedPageIdx = 0;

    public static void printAuthView(String authUrl) {
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=%s&response_type=code", authUrl, SpotifyData.CLIENT_ID, SpotifyData.REDIRECT_URL);
        System.out.println();
        System.out.println("waiting for code...");
    }

    public static void printGetAccessToken() {
        System.out.println("code received");
        System.out.println("making http request for access_token...");
    }

    public static void printNewReleases(int limit, boolean isNext, boolean based) {
        JsonObject albums = MenuController.getNewReleases();
        int size = albums.get("items").getAsJsonArray().size();
        // if this variable has the value "false", meaning the current command is "prev"
        if (!isNext) {
            // minus limit will give us the first index of the current page, 2*limit gives us the previous page,
            // if it less than 0, then there is no page left.
            if (currentNewReleasesPageIdx - (2 * limit) < 0) {
                System.out.println("No more pages.");
                return;
            }
            // because the command is "prev", then we minus the index before printing the list out.
            currentNewReleasesPageIdx -= limit;
        }
        if (based) {
            // if the "based" is true, meaning this is the "new" command, then every time the "new" command is
            // pressed we reset the index to 0.
            currentNewReleasesPageIdx = 0;
        }
        if (currentNewReleasesPageIdx >= size) {
            // if it exceeds the size, then return.
            System.out.println("No more pages.");
            return;
        }
        if (isNext) {
            // check if this is the "next" command, if it is, increase the index.
            currentNewReleasesPageIdx += limit;
        }
        for (int i = currentNewReleasesPageIdx - limit; i < (Math.min(currentNewReleasesPageIdx, size)); i++) {
            JsonObject album = albums.get("items").getAsJsonArray().get(i).getAsJsonObject();
            String name = album.get("name").getAsString();
            String url = album.get("external_urls").getAsJsonObject().get("spotify").getAsString();
            List<String> artists = new ArrayList<>();
            album.get("artists").getAsJsonArray().forEach(artist -> {
                artists.add(artist.getAsJsonObject().get("name").getAsString());
            });
            System.out.println(name);
            System.out.println(artists);
            System.out.println(url);
            System.out.println();
        }
        System.out.println("---PAGE " + (based ? 1 : (currentNewReleasesPageIdx) / limit) + " OF " + (int) Math.ceil((double) size / limit) + "---");
    }

    public static void printCategoryNames(int limit, boolean isNext, boolean based) {
        JsonObject categories = MenuController.getCategoryNames();
        int size = categories.get("items").getAsJsonArray().size();

        if (!isNext) {
            if (currentCategoryNamesPageIdx - (2 * limit) < 0) {
                System.out.println("No more pages.");
                return;
            }
            currentCategoryNamesPageIdx -= limit;
        }
        if (based) {
            currentCategoryNamesPageIdx = 0;
        }
        if (currentCategoryNamesPageIdx >= size) {
            System.out.println("No more pages.");
            return;
        }
        if (isNext) {
            currentCategoryNamesPageIdx += limit;
        }
        for (int i = currentCategoryNamesPageIdx - limit; i < (Math.min(currentCategoryNamesPageIdx, size)); i++) {
            JsonObject category = categories.get("items").getAsJsonArray().get(i).getAsJsonObject();
            String categoryName = category.get("name").getAsString();
            System.out.println(categoryName);
        }
        System.out.println("---PAGE " + (based ? 1 : currentCategoryNamesPageIdx / limit) + " OF " + (int) Math.ceil((double) size / limit) + "---");
    }

    public static void printFeatures(int limit, boolean isNext, boolean based) {
        JsonObject features = MenuController.getFeatures();
        int size = features.get("items").getAsJsonArray().size();
        if (!isNext) {
            if (currentFeaturedPageIdx - (2 * limit) < 0) {
                System.out.println("No more pages.");
                return;
            }
            currentFeaturedPageIdx -= limit;
        }
        if (based) {
            currentFeaturedPageIdx = 0;
        }
        if (currentFeaturedPageIdx >= size) {
            System.out.println("No more pages.");
            return;
        }
        if (isNext) {
            currentFeaturedPageIdx += limit;
        }

        for (int i = currentFeaturedPageIdx - limit; i < (Math.min(currentFeaturedPageIdx, size)); i++) {
            JsonObject item = features.get("items").getAsJsonArray().get(i).getAsJsonObject();
            System.out.println(item.get("name").getAsString());
            System.out.println(item.get("owner").getAsJsonObject().get("external_urls").getAsJsonObject().get("spotify").getAsString());
            System.out.println();
        }
        System.out.println("---PAGE " + (based ? 1 : currentFeaturedPageIdx / limit) + " OF " + (int) Math.ceil((double) size / limit) + "---");
    }

    public static void printPlaylists(String categoryName, int limit, boolean isNext, boolean based) {
        JsonObject playlists = MenuController.getPlaylists(categoryName);
        if (playlists == null) {
            return;
        }
        int size = playlists.get("playlists").getAsJsonObject().get("items").getAsJsonArray().size();
        if (!isNext) {
            if (currentPlaylistsPageIdx - (2 * limit) < 0) {
                System.out.println("No more pages.");
                return;
            }
            currentPlaylistsPageIdx -= limit;
        }
        if (based) {
            currentPlaylistsPageIdx = 0;
        }
        if (currentPlaylistsPageIdx >= size) {
            System.out.println("No more pages.");
            return;
        }
        if (isNext) {
            currentPlaylistsPageIdx += limit;
        }
        for (int i = currentPlaylistsPageIdx - limit; i < (Math.min(currentPlaylistsPageIdx, size)); i++) {
            JsonObject item = playlists.get("playlists").getAsJsonObject().get("items").getAsJsonArray().get(i).getAsJsonObject();
            System.out.println(item.get("name").getAsString());
            System.out.println(item.get("external_urls").getAsJsonObject().get("spotify").getAsString());
            System.out.println();
        }
        System.out.println("---PAGE " + (based ? 1 : currentPlaylistsPageIdx / limit) + " OF " + (int) Math.ceil((double) size / limit) + "---");
    }
}
