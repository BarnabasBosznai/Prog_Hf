package core.server;

public class ServerMain {
    public static void main(String[] args) {
        //new Server("192.168.1.2",58901).start();
        new Server("localhost",58901).start();
    }
}
