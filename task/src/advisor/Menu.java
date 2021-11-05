package advisor;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final String ERROR = "Please, provide access for application.";
    private String URL;

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void start() {
        boolean isRunning = true;
        boolean isAuthorized = false;
        while (isRunning) {
            String choice = scanner.nextLine();
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
                case "playlists Mood":
                    if (isAuthorized) {
                        printMood();
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
        if (URL == null) {
            URL = "https://accounts.spotify.com";
        }
//        AtomicReference<String> queryFinal = new AtomicReference<>("");
//        try {
//            HttpServer server = HttpServer.create();
//            server.bind(new InetSocketAddress(8080), 0);
//            server.createContext("/",
//                    exchange -> {
//                        String query = exchange.getRequestURI().getQuery();
//                        String hello = "";
//                        if (query == null) {
//                            query = "";
//                            hello = "Authorization code not found. Try again.";
//                        } else if (query.contains("access_denied")) {
//                            hello = "Authorization code not found. Try again.";
//                        } else if (query.contains("code")){
//                            hello = "Got the code. Return back to your program.";
//                        }
//                        exchange.sendResponseHeaders(200, hello.length());
//                        exchange.getResponseBody().write(hello.getBytes());
//                        queryFinal.set(query);
//                        exchange.getResponseBody().close();
//                    }
//            );
//
//            server.start();
//            System.out.println("use this link to request the access code:");
//            String spotifyAPI = URL + "/authorize?client_id=2b90caa156094f3a91eac30f19349609";
//            String requestUri = spotifyAPI + "&redirect_uri=http://localhost:8080&response_type=code";
//            System.out.println(requestUri);
//            System.out.println("waiting for code...");
//
//            while (queryFinal.get().equals("")) {
//                Thread.sleep(1000);
//            }
//
//            System.out.println("code received");
//
//            if (!queryFinal.get().contains("code")) {
//                return false;
//            }
//
//            server.stop(1);
//
//            System.out.println("making http request for access_token...");
//
//            HttpClient client = HttpClient.newBuilder().build();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .uri(URI.create(URL + "/api/token"))
//                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code&" + queryFinal.get() + "&redirect_uri=http://localhost:8080"))
//                    .build();
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//            System.out.println("response:");
//            System.out.println(response.body());
//            System.out.println("---SUCCESS---");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        Server server = new Server();

        server.start();
        server.createContext();
        System.out.println("use this link to request the access code:");
        System.out.println(URL + "/authorize?client_id=2b90caa156094f3a91eac30f19349609&response_type=code&redirect_uri=http://localhost:8080");
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
        String response = server.getTokenData(server.getCode(), URL);
        System.out.println("response:");
        System.out.println(response);
        server.stop();
        System.out.println("---SUCCESS---");
        return true;
    }


    private void printNew() {
        System.out.println("---NEW RELEASES---\n" +
                "Mountains [Sia, Diplo, Labrinth]\n" +
                "Runaway [Lil Peep]\n" +
                "The Greatest Show [Panic! At The Disco]\n" +
                "All Out Life [Slipknot]");
    }

    private void printFeatured() {
        System.out.println("---FEATURED---\n" +
                "Mellow Morning\n" +
                "Wake Up and Smell the Coffee\n" +
                "Monday Motivation\n" +
                "Songs to Sing in the Shower");
    }

    private void printCategories() {
        System.out.println("---CATEGORIES---\n" +
                "Top Lists\n" +
                "Pop\n" +
                "Mood\n" +
                "Latin");
    }

    private void printMood() {
        System.out.println("---MOOD PLAYLISTS---\n" +
                "Walk Like A Badass  \n" +
                "Rage Beats  \n" +
                "Arab Mood Booster  \n" +
                "Sunday Stroll");
    }
}
