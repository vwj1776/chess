package client;

import com.google.gson.Gson;
import ui.EscapeSequences;

import java.util.Map;

public interface UIClient {
    String eval(String input);
    String help();
    default String formatError(String jsonMessage) {
        try {
            var errorMap = new Gson().fromJson(jsonMessage, Map.class);
            var msg = (String) errorMap.get("message");

            if (msg != null && msg.toLowerCase().startsWith("error:")) {
                msg = msg.substring(6).trim();
            }

            return humanReadable(msg);

        } catch (Exception e) {
            // Fall back to plain string
            return humanReadable(jsonMessage);
        }
    }

    private String humanReadable(String msg) {
        if (msg == null) return EscapeSequences.RED + "Unknown error occurred." + EscapeSequences.RESET;

        if (msg.contains("Username already")) return EscapeSequences.RED + "That username is taken. Try a different one." + EscapeSequences.RESET;
        if (msg.contains("Invalid auth token")) return EscapeSequences.RED + "You're not logged in. Please login first." + EscapeSequences.RESET;
        if (msg.contains("Invalid login")) return EscapeSequences.RED + "Incorrect username or password." + EscapeSequences.RESET;
        if (msg.contains("bad request")) return EscapeSequences.RED + "Something's off with your input. Check the command format." + EscapeSequences.RESET;

        return EscapeSequences.RED + "Error: " + msg + EscapeSequences.RESET;
    }
}

