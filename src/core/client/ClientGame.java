package core.client;

import core.highscore.HighScore;
import core.packets.AnswerFlag;
import core.packets.QuestionPacket;
import core.packets.ResponsePacket;
import core.question.Question;
import core.server.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ClientGame {
    private Socket socket;
    private Scanner scanner = new Scanner(System.in);
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private boolean playing;

    public Question question;

    public ClientGame() {
        try {
            socket = new Socket("localhost", 58901);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            playing = true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public void run() {
        while(playing) {
            try {
                QuestionPacket qp = (QuestionPacket) ois.readObject();
                //Question q = (Question)ois.readObject();

                System.out.println("Kérdés: " + qp.getQuestion());
                //System.out.println("Kérdés: " + q.getQuestion());
                System.out.print("Lehetséges válaszok: ");
                String[] ans = qp.getAnswers();
                //String[] ans = q.getAnswers();
                for (int i = 0; i < 4; i++)
                    System.out.print((i + 1) + ". " + ans[i] + " ");
                System.out.println();

                int idx = scanner.nextInt();
                oos.writeObject(idx);
                ResponsePacket rp = (ResponsePacket) ois.readObject();
                if (rp.getFlags().contains(AnswerFlag.WRONG)) {
                    System.out.println("Hibás válasz! Vége a játéknak!");
                    playing = false;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public List<Integer> n_random(int targetSum, int numberOfDraws) {
        Random r = new Random();
        List<Integer> list = new ArrayList<>();

        int sum = 0;
        for(int i = 0; i < numberOfDraws; i++) {
            int next = r.nextInt(targetSum) + 1;
            list.add(next);
            sum+=next;
        }

        double scale = 1d * targetSum / sum;
        sum = 0;
        for(int i = 0; i < numberOfDraws; i++) {
            list.set(i, (int)(list.get(i) * scale));
            sum+=list.get(i);
        }

        while(sum++ < targetSum) {
            int i = r.nextInt(numberOfDraws);
            list.set(i, list.get(i) + 1);
        }
        return list;
    }

    public List<HighScore> getScores() {
        try {
            oos.writeObject(MessageType.SEND_SCORES);
            return (List<HighScore>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getQuestion() {
        try {
            oos.writeObject(MessageType.SEND_QUESTION);
            question = (Question)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void getNextQuestion() {
        try {
            oos.writeObject(MessageType.NEXT_QUESTION);
            question = (Question)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void gameOver() {
        try {
            oos.writeObject(MessageType.GAMEOVER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closed() {
        try {
            oos.writeObject(MessageType.DISCONNECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
