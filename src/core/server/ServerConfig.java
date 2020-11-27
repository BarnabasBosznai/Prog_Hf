package core.server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Szerver konfigurációs adatainak tárolása.
 */
public class ServerConfig {
    private String ip;
    private int port;
    private String questionData;
    private String scoresData;

    /**
     * Új konfiguráció létrehozása.
     * @param ip IP cím
     * @param port Port szám
     * @param questionData Kérdések fájl útvonala
     * @param scoresData Eredmények fájl útvonala
     */
    public ServerConfig(String ip, int port, String questionData, String scoresData) {
        this.ip = ip;
        this.port = port;
        this.questionData = questionData;
        this.scoresData = scoresData;
    }

    /**
     * @return IP cím
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return Port szám
     */
    public int getPort() {
        return port;
    }

    /**
     * @return Kérdések fájl útvonala
     */
    public String getQuestionData() {
        return questionData;
    }

    /**
     * @return Eredmények fájl útvonala
     */
    public String getScoresData() {
        return scoresData;
    }

    /**
     * Új konfiguráció betöltése.
     * @param file Konfigurációs fájl útvonala
     * @return Új konfigurációs adatok
     * @throws FileNotFoundException
     */
    public static ServerConfig read(String file) throws FileNotFoundException {
        JsonReader jsonReader = Json.createReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
        JsonObject jsonObject = jsonReader.readObject();

        String ip = jsonObject.getString("ip");
        int port = jsonObject.getInt("port");
        String questionData = jsonObject.getString("question_data_loc");
        String scoresData = jsonObject.getString("score_data_loc");

        return new ServerConfig(ip, port, questionData, scoresData);
    }
}
