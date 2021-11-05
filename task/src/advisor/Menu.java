package advisor;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final String ERROR = "Please, provide access for application.";
    private String authorizationUrl;

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
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
        System.out.println("response:");
        System.out.println(response);
        server.stop();
        /*if (response.contains("access_token")) {*/
            System.out.println("---SUCCESS---");
            return true;
        /*}
        System.out.println("---FAILED---");
        return false;*/
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
