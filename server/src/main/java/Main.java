import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.UserDataBaseAccess;
import server.Server;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        try {
            System.out.println("in try");

            int port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            DataAccess dataAccess = new MemoryDataAccess();
            if (args.length >= 2 && args[1].equalsIgnoreCase("sql")) {
                System.out.println("using Db");
                dataAccess = new UserDataBaseAccess();
            }

            var server = new Server();
            int portNumber = server.run(port);
            System.out.printf("Server started on port %d with %s%n", portNumber, dataAccess.getClass());

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }

        System.out.println("""
                chess Server:
                java ServerMain <port> [sql]
                """);
    }
}
