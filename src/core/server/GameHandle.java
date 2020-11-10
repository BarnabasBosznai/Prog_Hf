package core.server;

import core.highscore.HighScoreManager;
import core.packets.AnswerFlag;
import core.packets.ResponsePacket;
import core.question.Question;
import core.question.QuestionManager;
import core.packets.QuestionPacket;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EnumSet;

public class GameHandle implements Runnable {
    private Server serverRef;
    private Socket clientSocket;
    private boolean playing = true;

    private QuestionManager qm;
    private HighScoreManager sm;

    private boolean running = true;
    private int qIndex = 0;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public GameHandle(Server server, Socket s, QuestionManager questionManager, HighScoreManager scoreManager) {
        serverRef = server;
        clientSocket = s;
        qm = questionManager;
        sm = scoreManager;

        try {
            objectInputStream = new ObjectInputStream(s.getInputStream());
            objectOutputStream = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void run() {
        Question question;
        int index = 1;
        while(playing) {
            //question = questionManager.getQuestionByDifficulty(index++);
            question = qm.getQuestionByDifficulty(index++);

            QuestionPacket qp = new QuestionPacket(question.getQuestion(), question.getAnswers());

            System.out.println(question.toString());

            try {
                objectOutputStream.writeObject(qp);
                //objectOutputStream.writeObject(question);

                int clientAnswer = (int)objectInputStream.readObject();
                EnumSet<AnswerFlag> flag;
                ResponsePacket responsePacket;
                if(!((clientAnswer - 1) == question.getAnswerIndex())) {
                    flag = EnumSet.of(AnswerFlag.WRONG);
                    responsePacket = new ResponsePacket(flag);
                    objectOutputStream.writeObject(responsePacket);
                    cleanUp();
                } else {
                    flag = EnumSet.of(AnswerFlag.CORRECT);
                    responsePacket = new ResponsePacket(flag);
                    objectOutputStream.writeObject(responsePacket);
                }
            } catch (IOException | ClassNotFoundException e) {
                //System.out.println(e.getMessage());
                e.printStackTrace();
                cleanUp();
            }
        }
        System.out.println(clientSocket + " játéka véget ért.");

    }*/

    @Override
    public void run() {
        MessageType type;
        while(running) {
            try {
                type = (MessageType)objectInputStream.readObject();
                switch(type) {
                    case SEND_SCORES:
                        objectOutputStream.writeObject(sm.getHighScores());
                        break;
                    case SEND_QUESTION:
                        objectOutputStream.writeObject(qm.getQuestionByDifficulty(qIndex));
                        break;
                    case NEXT_QUESTION:
                        objectOutputStream.writeObject(qm.getQuestionByDifficulty(++qIndex));
                        break;
                    case GAMEOVER:
                        qIndex = 0;
                        break;
                    case DISCONNECT:
                        cleanUp();
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    } //Func end

    private void cleanUp() {
        try {
            playing = false;
            running = false;
            objectInputStream.close();
            objectOutputStream.close();
            serverRef.removeConnection(clientSocket);
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
