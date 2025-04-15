package ResponsesAndExceptions;

import model.GameData;

import java.util.List;

public class GameListResponse {
    private List<GameData> games;

    public List<GameData> getGames() {
        return games;
    }

    public void setGames(List<GameData> games) {
        this.games = games;
    }
}
