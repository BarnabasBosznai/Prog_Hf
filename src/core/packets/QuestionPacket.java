package core.packets;

public class QuestionPacket extends Packet {
    private String question;
    private String[] answers;
    public QuestionPacket(String q, String[] ans) {
        question = q;
        answers = ans;
    }

    public String getQuestion() {
        return question;
    }

    public String[] getAnswers() {
        return answers;
    }
}
