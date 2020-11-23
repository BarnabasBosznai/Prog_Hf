package core.question;

import java.io.Serializable;

public class Question implements Serializable {
    private String question;
    private String[] answers;
    private int difficulty;
    private int answerIndex;
    private String category;

    public Question(String q, String[] ans, int diff, int ansIdx, String cat) {
        question = q;
        answers = ans;
        difficulty = diff;
        answerIndex = ansIdx;
        category = cat;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }

    public String getAnswer() {
        return answers[answerIndex];
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return "Question: " + question + "\n Answers: " + answers[0] + " " + answers[1] + " " + answers[2] + " " + answers[3] + "\n Correct answers: " + answers[answerIndex] +
                "\n Category: " + category + "\n Difficulty: " + difficulty;
    }
}
