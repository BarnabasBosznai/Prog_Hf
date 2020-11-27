package core.question;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.json.*;

/**
 * Kérdéseket kezelő Singleton osztály.
 */
public class QuestionManager {
    private List<Question> questionList = new ArrayList<>();
    private static QuestionManager instance;

    /**
     * @return Kezelő példány vagy <code>null</code>, ha nem hoztuk létre <code>createInstance(String file)</code>-el
     */
    public static QuestionManager getInstance() {
        return instance;
    }

    /**
     * Kezelő példányáak létrehozás. <code>getInstance()</code> használta előtt mindig hívjuk meg.
     * @param file Kérdések fájl útvonala
     * @throws FileNotFoundException
     */
    public static void createInstance(String file) throws FileNotFoundException {
        if(instance == null) {
            synchronized (QuestionManager.class) {
                if(instance == null) {
                    instance = new QuestionManager(file);
                }
            }
        }
    }

    /**
     * Példányt létrehozó privát konstruktor.
     * @param filepath Kérdések fájl útvonala
     * @throws FileNotFoundException
     */
    private QuestionManager(String filepath) throws FileNotFoundException {
       JsonReader jsonReader = Json.createReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8));
       JsonObject jsonObject = jsonReader.readObject();
       JsonArray jsonArray = jsonObject.getJsonArray("questions");
       for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject q = jsonArray.getJsonObject(i);

            String question = q.getString("question");
            JsonArray answersArray = q.getJsonArray("choices");
            String[] answers = new String[4];
            for (int j = 0; j < answersArray.size(); j++)
                answers[j] = answersArray.getString(j);

            int diff = q.getInt("difficulty");
            int ansIdx = q.getInt("correct");
            String cat = q.getString("category");

            questionList.add(new Question(question, answers, diff, ansIdx, cat));
        }
    }

    /**
     * Véletlenszerűen visszaad egy kérdés a megadott nehézségi szintből.
     * @param diff Kérdés nehézsége
     * @return Egy kérdés
     */
    public synchronized Question getQuestionByDifficulty(int diff) {
        List<Question> avail = new ArrayList<>();
        for(Question q : questionList) {
            if (q.getDifficulty() == diff) {
                avail.add(q);
            }
        }
        return avail.get(new Random().nextInt(avail.size()));
    }
}
