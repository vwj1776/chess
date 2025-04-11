package client;

public class ChessClient {

    private final ServerFacade server;
    private String authToken = null;
    private UIClient currentClient;

    public ChessClient(ServerFacade server, Repl repl) {
        this.server = server;
        this.currentClient = new PreLoginClient(server, this);
    }

    public String eval(String input) {
        return currentClient.eval(input);
    }

    public String help() {
        return currentClient.help();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void promoteToPostLogin() {
        this.currentClient = new PostLoginClient(server, this, authToken);
    }

    public void logout() {
        this.authToken = null;
        this.currentClient = new PreLoginClient(server, this);
    }
}
