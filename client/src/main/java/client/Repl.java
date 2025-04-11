package client;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
    }

    public void run() {
        System.out.println("♕ Welcome to CHESS! Sign in or register to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equalsIgnoreCase("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(BLUE + result + RESET);
            } catch (Throwable e) {
                System.out.print(RED + "Unexpected error: " + e.getMessage() + RESET);
            }
        }
        System.out.println("\nGoodbye!");
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
