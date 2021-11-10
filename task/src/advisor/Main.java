package advisor;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> argumentsMap = new HashMap<>();
        Menu menu = new Menu();
        if (args.length > 0 ) {
            for (int i = 0; i < args.length; i +=2) {
                if (args[i].startsWith("-")) {
                    argumentsMap.put(args[i], args[i + 1]);
                }
            }
            menu.setAuthorizationUrl(argumentsMap.get("-access"));
            menu.setResourceUrl(argumentsMap.get("-resource"));
        } else {
            menu.setAuthorizationUrl("https://accounts.spotify.com");
            menu.setResourceUrl("https://api.spotify.com");
        }
        menu.start();
    }
}
