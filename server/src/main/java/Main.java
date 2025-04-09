import chess.*;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import server.Server;
import dataaccess.UserDataBaseAccess;

public class Main {
    public static void main(String[] args) {
        // var server = new Server();
       //  var part = server.run(8000);
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        try {
            var port = 8080;
            if (args.length >= 1) {
                port = Integer.parseInt(args[0]);
            }

            DataAccess dataAccess = new MemoryDataAccess();
            if (args.length >= 2 && args[1].equals("sql")) {
                dataAccess = new UserDataBaseAccess();
            }

           //  var service = new ChessService(dataAccess);
            var server = new Server().run(port);
            port = server;
            System.out.printf("Server started on port %d with %s%n", port, dataAccess.getClass());
            return;
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
        System.out.println("""
                Pet Server:
                java ServerMain <port> [sql]
                """);
    }



}



