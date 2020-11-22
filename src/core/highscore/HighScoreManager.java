package core.highscore;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private List<HighScore> highScores;
    private static String highScoresFile = "highscores.txt";
    private static HighScoreManager instance;

    public static HighScoreManager getInstance() {
        if(instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    private HighScoreManager() {
        loadFile();
        if(highScores == null)
            highScores = new ArrayList<>();
    }

    public synchronized void add(HighScore score) {
        highScores.add(score);
        Collections.sort(highScores);
    }

    public synchronized List<HighScore> getHighScores() {
        return highScores;
    }

    private void loadFile() {
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(new FileInputStream(highScoresFile));
            highScores = (ArrayList<HighScore>)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if(objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(new FileOutputStream(highScoresFile));
            objectOutputStream.writeObject(highScores);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if(objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
