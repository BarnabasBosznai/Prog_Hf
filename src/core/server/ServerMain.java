package core.server;

public class ServerMain {
    public static void main(String[] args) {
        if(args.length == 1) {
            Server server = new Server();
            if(server.init(args[0])) {
                server.start();
            }
        } else {
            System.out.println("No configuration file provided!");
        }
    }
}
