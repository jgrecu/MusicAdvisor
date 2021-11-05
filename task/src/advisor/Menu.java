package advisor;

import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final String ERROR = "Please, provide access for application.";

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
        String spotifyAPI = "https://accounts.spotify.com/authorize?client_id=2b90caa156094f3a91eac30f19349609";
        String request = spotifyAPI + "&redirect_uri=http://localhost:8080&response_type=code";
        System.out.println(request);
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
