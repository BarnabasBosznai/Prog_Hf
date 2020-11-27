package core.server;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ServerConfig {
    private String ip;
    private int port;
    private String questionData;
    private String scoresData;

    public ServerConfig(String ip, int port, String questionData, String scoresData) {
        this.ip = ip;
        this.port = port;
        this.questionData = questionData;
        this.scoresData = scoresData;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getQuestionData() {
        return questionData;
    }

    public String getScoresData() {
        return scoresData;
    }

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
