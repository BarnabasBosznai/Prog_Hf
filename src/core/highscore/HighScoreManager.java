package core.highscore;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Felhasználók eredményét kezelő Singleton osztály
 */
public class HighScoreManager {
    private List<HighScore> highScores;
    private String highScoresFile;
    private static HighScoreManager instance;

    /**
     * Kezelő példányáak létrehozás. <code>getInstance()</code> használta előtt mindig hívjuk meg.
     * @param file Eredmények fájl útvonala
     */
    public static void createInstance(String file) {
        if(instance == null) {
            synchronized (HighScoreManager.class) {
                if(instance == null) {
                    instance = new HighScoreManager(file);
                }
            }
        }
    }

    /**
     * @return Kezelő pélnánya vagy <code>null</code>, ha nem hoztuk létre <code>createInstance(String file)</code>-el
     */
    public static HighScoreManager getInstance() {
        return instance;
    }

    /**
     * Példányt létrehozó privát konstruktor.
     * @param file Eredmények fájl útvonala
     */
    private HighScoreManager(String file) {
        highScoresFile = file;
        loadFile(file);
    }

    /**
     * Új eredmény eltárolása.
     * @param score Felhasználói eredmény
     */
    public synchronized void add(HighScore score) {
        highScores.add(score);
        Collections.sort(highScores);
    }

    /**
     * @return Lista az eredményekről
     */
    public synchronized List<HighScore> getHighScores() {
        return highScores;
    }

    /**
     * Beolvasssa fájlból a korábbi eredményeket vagy üreset hoz létre, ha nincs ilyen.
     * @param file Eredmények fájl útvonala
     */
    private void loadFile(String file) {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            highScores = (ArrayList<HighScore>)ois.readObject();
        } catch(Exception e) {
            System.out.println(e.getMessage() + "! Empty list created!");
            highScores = new ArrayList<>();
        }
    }

    /**
     * Elmenti fájlba az eddig tárolt eredményeket.
     */
    public void save() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(highScoresFile))) {
            oos.writeObject(highScores);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}