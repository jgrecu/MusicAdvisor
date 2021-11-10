package advisor;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> argumentsMap = new HashMap<>();

        if (args.length > 0 ) {
            for (int i = 0; i < args.length; i +=2) {
                if (args[i].startsWith("-")) {
                    argumentsMap.put(args[i], args[i + 1]);
                }
            }
            MenuController.setAuthorizationUrl(argumentsMap.get("-access"));
            MenuController.setResourceUrl(argumentsMap.get("-resource"));
            MenuController.setEntriesPerPage(argumentsMap.get("-page"));
        }

        MenuController.start();
    }
}
