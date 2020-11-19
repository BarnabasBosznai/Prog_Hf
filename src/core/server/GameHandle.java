package core.server;

import core.highscore.HighScoreManager;
import core.packets.*;
import core.question.Question;
import core.question.QuestionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameHandle implements Runnable {
    private Server serverRef;
    private Socket clientSocket;
    private boolean playing = true;

    private QuestionManager qm;
    private HighScoreManager sm;

    private boolean running = true;
    private int qIndex = 0;
    private boolean crowdHelp = true;
    private boolean splitHelp = true;
    private boolean swapQuestionHelp = true;

    private Question currentQuestion;


    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    MessageHandler messageHandler;

    public GameHandle(Server server, Socket s, QuestionManager questionManager, HighScoreManager scoreManager) {
        serverRef = server;
        clientSocket = s;
        qm = questionManager;
        sm = scoreManager;

        try {
            objectInputStream = new ObjectInputStream(s.getInputStream());
            objectOutputStream = new ObjectOutputStream(s.getOutputStream());
            messageHandler = new MessageHandler(objectOutputStream, objectInputStream);
            currentQuestion = qm.getQuestionByDifficulty(++qIndex);
            messageHandler.sendMessage(new Message(MessageType.QUESTION, currentQuestion));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                Message msg = messageHandler.receiveMessage();
                switch(msg.getMessageID()) {
                    case ANSWER:
                        int ansIdx = (int)msg.getData();
                        if(ansIdx == currentQuestion.getAnswerIndex()) {
                            currentQuestion = qm.getQuestionByDifficulty(++qIndex);
                            messageHandler.sendMessage(new Message(MessageType.QUESTION, currentQuestion));
                        } else {
                            messageHandler.sendMessage(new Message(MessageType.ERROR, "Wrong answer!"));
                            cleanUp();
                        }
                        break;
                    case SWAPQUESTIONHELP:
                        if(swapQuestionHelp) {
                            currentQuestion = qm.getQuestionByDifficulty(qIndex);
                            messageHandler.sendMessage(new Message(MessageType.CONFIRM, currentQuestion));
                        } else {
                            messageHandler.sendMessage(new Message(MessageType.ERROR, "Already used"));
                        }
                        break;
                    case SPLITHELP:
                        if(splitHelp) {
                            splitHelp = false;
                            messageHandler.sendMessage(new Message(MessageType.CONFIRM, currentQuestion.getAnswerIndex()));
                        } else {
                            messageHandler.sendMessage(new Message(MessageType.ERROR, "Already used!"));
                        }
                        break;
                    case CROWDHELP:
                        if(crowdHelp) {
                            crowdHelp = false;
                            List<Integer> votes = n_random(100, (int)msg.getData());
                            int valIdx = votes.indexOf(Collections.max(votes));
                            Random rnd = new Random();
                            int swapIdx = rnd.nextInt(votes.size());
                            while(swapIdx == valIdx) {
                                swapIdx = rnd.nextInt(votes.size());
                            }
                            Collections.swap(votes, swapIdx, valIdx);
                            messageHandler.sendMessage(new Message(MessageType.CONFIRM, votes));
                        } else {
                            messageHandler.sendMessage(new Message(MessageType.ERROR, "Already used!"));
                        }
                        break;
                    case DISCONNECT:
                        cleanUp();
                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
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
