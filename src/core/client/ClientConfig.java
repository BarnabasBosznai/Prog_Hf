package core.client;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Kliens konfigurációjának tárolása.
 */
public class ClientConfig {
    private String serverIP;
    private int serverPort;

    public ClientConfig(String ip, int port) {
        serverIP = ip;
        serverPort = port;
    }

    /**
     * @return Szerver IP címe
     */
    public String getServerIP() {
        return serverIP;
    }

    /**
     * @return Szerver portja
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Beolvassa annak a szervernek az adatait, amihez csatlakozik a kliens program.
     * @param file Útvonal a konfigurációs fájlhoz
     * @return Beolvasott konfigurációs adatok
     * @throws FileNotFoundException
     */
    public static ClientConfig read(String file) throws FileNotFoundException {
        JsonReader jsonReader = Json.createReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        JsonObject jsonObject = jsonReader.readObject();

        String ip = jsonObject.getString("server_ip");
        int port = jsonObject.getInt("server_port");

        return new ClientConfig(ip, port);
    }
}
