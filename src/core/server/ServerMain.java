package core.server;

/**
 * Szerver program main-je
 */
public class ServerMain {
    /**
     * Szerver program belépési pontja.
     * @param args Parancssori argument a konfigurációs fájlhoz.
     */
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
