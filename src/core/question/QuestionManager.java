package core.question;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.json.*;

public class QuestionManager {
    private List<Question> questionList = new ArrayList<>();
    private static QuestionManager instance;

    public static QuestionManager getInstance(String filepath) {
        if(instance == null) {
            instance = new QuestionManager(filepath);
        }

        return instance;
    }

    private QuestionManager(String filepath) {
        try(JsonReader jsonReader = Json.createReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8)))
        {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized Question getQuestionByDifficulty(int diff) {
        List<Question> avail = new ArrayList<>();
        for(Question q : questionList) {
            if (q.getDifficulty() == diff) {
                avail.add(q);
            }
        }
        return avail.get(new Random().nextInt(avail.size()));
    }

    public synchronized Question getQuestionByCategory(String cat) {
        List<Question> avail = new ArrayList<>();
        for(Question q : questionList) {
            if (q.getCategory().equals(cat)) {
                avail.add(q);
            }
        }
        return avail.get(new Random().nextInt(avail.size()));
    }
}
