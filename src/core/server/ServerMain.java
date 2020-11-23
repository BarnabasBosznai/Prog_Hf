package core.server;

public class ServerMain {
    public static void main(String[] args) {
        //new Server("server_ip",58901).start();
        new Server("localhost",58901).start();
    }
}
