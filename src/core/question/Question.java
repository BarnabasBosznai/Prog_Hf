package core.question;

import java.io.Serializable;

/**
 * Egy kérdés információit tároló osztály.
 */
public class Question implements Serializable {
    private String question;
    private String[] answers;
    private int difficulty;
    private int answerIndex;
    private String category;

    /**
     * Új kérdés létrehozása.
     * @param q Kérdés
     * @param ans Válaszlehetőségek
     * @param diff Kérdés nehézsége
     * @param ansIdx Helyes válasz indexe
     * @param cat Kérdés kategoriája
     */
    public Question(String q, String[] ans, int diff, int ansIdx, String cat) {
        question = q;
        answers = ans;
        difficulty = diff;
        answerIndex = ansIdx;
        category = cat;
    }

    /**
     * @return Kérdés szövege
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @return Válaszlehetőségek
     */
    public String[] getAnswers() {
        return answers;
    }

    /**
     * @return Kérdés nehézsége
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * @return Helyes válasz indexe
     */
    public int getAnswerIndex() {
        return answerIndex;
    }

    /**
     * @return Kérdés kategoriája
     */
    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Question: " + question + "\n Answers: " + answers[0] + " " + answers[1] + " " + answers[2] + " " + answers[3] + "\n Correct answers: " + answers[answerIndex] +
                "\n Category: " + category + "\n Difficulty: " + difficulty;
    }
}
