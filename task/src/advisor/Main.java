package advisor;

public class Main {
    public static void main(String[] args) {
        Menu menu = new Menu();
        if (args.length > 0 && args[0].equals("-access")) {
            menu.setURL(args[1]);
        }
        menu.start();
    }
}
