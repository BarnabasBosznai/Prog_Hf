package core.server;

import core.highscore.HighScore;
import core.highscore.HighScoreManager;
import core.message.*;
import core.question.Question;
import core.question.QuestionManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

public class ClientHandle implements Runnable {
    private Server serverRef;
    private Socket clientSocket;

    private QuestionManager qm;
    private HighScoreManager sm;

    private boolean running = true;
    private int qIndex = 0;
    private boolean crowdHelp = true;
    private boolean splitHelp = true;
    private boolean swapQuestionHelp = true;

    private Question currentQuestion;
    private String name;

    private MessageManager messageManager;

    public ClientHandle(Server server, Socket s, QuestionManager questionManager, HighScoreManager scoreManager) {
        serverRef = server;
        clientSocket = s;
        qm = questionManager;
        sm = scoreManager;

        try {
            messageManager = new MessageManager(new ObjectOutputStream(s.getOutputStream()),  new ObjectInputStream(s.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.SCOREBOARD) {
            messageManager.sendMessage(new Message(MessageType.CONFIRM, sm.getHighScores()));
        } else {
            currentQuestion = qm.getQuestionByDifficulty(++qIndex);
            messageManager.sendMessage(new Message(MessageType.QUESTION, currentQuestion));
            name = (String)msg.getData();
        }
    }

    private void answerResponse(Message msg) {
        int ansIdx = (int)msg.getData();
        if(ansIdx == currentQuestion.getAnswerIndex()) {
            qIndex++;
            if(qIndex < 16) {
                currentQuestion = qm.getQuestionByDifficulty(qIndex);
                messageManager.sendMessage(new Message(MessageType.QUESTION, currentQuestion));
            } else {
                messageManager.sendMessage(new Message(MessageType.WON, serverRef.getPrize(qIndex - 1)));
                sm.add(new HighScore(name, serverRef.getPrize(qIndex - 1), LocalDate.now()));
                cleanUp();
            }
        } else {
            if(qIndex - 1 != 0) {
                sm.add(new HighScore(name, serverRef.getPrize(qIndex - 1), LocalDate.now()));
            }
            messageManager.sendMessage(new Message(MessageType.ERROR, serverRef.getPrize(qIndex - 1)));
            cleanUp();
        }
    }

    private void swapQuestionHelpResponse() {
        if(swapQuestionHelp) {
            currentQuestion = qm.getQuestionByDifficulty(qIndex);
            messageManager.sendMessage(new Message(MessageType.CONFIRM, currentQuestion));
        } else {
            messageManager.sendMessage(new Message(MessageType.ERROR, "Already used"));
        }
    }

    private void splitHelpResponse() {
        if(splitHelp) {
            splitHelp = false;
            boolean arr[] = new boolean[4];
            for(int i = 0; i < 4; i++) {
                arr[i] = true;
            }
            int x = 0;
            int y = currentQuestion.getAnswerIndex();
            Random rnd = new Random();
            while (x != 2) {
                int temp = rnd.nextInt(4);
                if(temp != y && arr[temp]) {
                    arr[temp] = false;
                    x++;
                }
            }
            messageManager.sendMessage(new Message(MessageType.CONFIRM, arr));
        } else {
            messageManager.sendMessage(new Message(MessageType.ERROR, "Already used!"));
        }
    }

    private void crowdHelpResponse(Message msg) {
        if(crowdHelp) {
            boolean[] en = (boolean[])msg.getData();
            int cnt = 0;
            for(Boolean b : en) {
                if(b) {
                    cnt++;
                }
            }
            if(cnt == 2 || cnt == 4) {
                List<Integer> votes = n_random(100, 4);

                if(cnt == 2) {
                    Random rnd = new Random();
                    for(int i = 0; i < 4; i++) {
                        if(!en[i]) {
                            int temp = votes.get(i);
                            votes.set(i, 0);
                            int x = rnd.nextInt(4);
                            while (!en[x]) {
                                x = rnd.nextInt(4);
                            }
                            votes.set(x, votes.get(x) + temp);
                        }
                    }
                }
                Collections.swap(votes, currentQuestion.getAnswerIndex(), votes.indexOf(Collections.max(votes)));
                messageManager.sendMessage(new Message(MessageType.CONFIRM, votes));
            } else {
                messageManager.sendMessage(new Message(MessageType.ERROR, null));
            }
        } else {
            messageManager.sendMessage(new Message(MessageType.ERROR, null));
        }
    }

    private void disconnectResponse() {
        cleanUp();
    }

    @Override
    public void run() {
        while(running) {
            try {
                Message msg = messageManager.receiveMessage();

                switch(msg.getMessageID()) {
                    case ANSWER:
                        answerResponse(msg);
                        break;
                    case SWAPQUESTIONHELP:
                        swapQuestionHelpResponse();
                        break;
                    case SPLITHELP:
                        splitHelpResponse();
                        break;
                    case CROWDHELP:
                        crowdHelpResponse(msg);
                        break;
                    case DISCONNECT:
                        disconnectResponse();
                        break;
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<Integer> n_random(int targetSum, int numberOfDraws) {
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
            running = false;
            serverRef.removeConnection(clientSocket);
            messageManager.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}