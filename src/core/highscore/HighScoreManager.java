package core.highscore;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighScoreManager {
    private List<HighScore> highScores;
    private String highScoresFile;
    private static HighScoreManager instance;

    public static void createInstance(String file) {
        if(instance == null) {
            synchronized (HighScoreManager.class) {
                if(instance == null) {
                    instance = new HighScoreManager(file);
                }
            }
        }
    }

    public static HighScoreManager getInstance() {
        return instance;
    }

    private HighScoreManager(String file) {
        highScoresFile = file;
        loadFile(file);
    }

    public synchronized void add(HighScore score) {
        highScores.add(score);
        Collections.sort(highScores);
    }

    public synchronized List<HighScore> getHighScores() {
        return highScores;
    }

    private void loadFile(String file) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highScores = (ArrayList<HighScore>)ois.readObject();
        } catch(Exception e) {
            highScores = new ArrayList<>();
        }
    }

    public void close() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(highScoresFile))) {
            oos.writeObject(highScores);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}