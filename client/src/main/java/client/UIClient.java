package client;

import com.google.gson.Gson;
import ui.EscapeSequences;

import java.util.Map;

import static ui.UiUtils.humanReadable;

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
            return humanReadable(jsonMessage);
        }
    }


}

