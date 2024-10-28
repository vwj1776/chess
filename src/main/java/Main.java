import chess.*;
import dataaccess.MemoryDataAccess;
import server.Server;
import service.ChessService;

public class Main {
    public static void main(String[] args) {

        var server = new Server(new ChessService(new MemoryDataAccess()));
        var part = server.run(8000);
        System.out.println("♕ 240 Chess Server: " + part);
    }
}