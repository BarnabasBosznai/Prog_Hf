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

/**
 * Kliens kapcsolatot kezelő osztály.
 */
public class ClientHandle implements Runnable {
    private Server serverRef;
    private Socket clientSocket;

    private boolean running = true;
    private int questionIndex = 0;
    private boolean crowdHelp = true;
    private boolean splitHelp = true;
    private boolean swapQuestionHelp = true;

    private Question currentQuestion;
    private String name;
    private MessageManager messageManager;

    String[] prizes = { "0", "5.000", "10.000","25.000", "50.000", "100.000", "200.000", "300.000", "500.000", "800.000",
                        "1.500.000", "3.000.000", "5.000.000", "10.000.000", "20.000.000", "40.000.000" };

    /**
     * Új kezelő létrehozása.
     * @param server Szerver
     * @param s Socket
     */
    public ClientHandle(Server server, Socket s) {
        serverRef = server;
        clientSocket = s;
        try {
            messageManager = new MessageManager(new ObjectOutputStream(s.getOutputStream()),  new ObjectInputStream(s.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Új játék indítása kérés feldolgozása.
     * @param msg Üzenet
     */
    private void newGameResponse(Message msg) {
        currentQuestion = QuestionManager.getInstance().getQuestionByDifficulty(++questionIndex);
        messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.QUESTION), currentQuestion));
        name = (String)msg.getData();
    }

    /**
     * Egy kérdésre adott válasz feldolgozása.
     * @param msg Üzenet
     */
    private void  answerResponse(Message msg) {
        int ansIdx = (int)msg.getData();
        if(ansIdx == currentQuestion.getAnswerIndex()) {
            questionIndex++;
            if(questionIndex < 16) {
                currentQuestion = QuestionManager.getInstance().getQuestionByDifficulty(questionIndex);
                messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.QUESTION), currentQuestion));
            } else {
                messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.WON), prizes[questionIndex - 1]));
                HighScoreManager.getInstance().add(new HighScore(name, prizes[questionIndex - 1], LocalDate.now()));
                cleanUp();
            }
        } else {
            if(questionIndex - 1 != 0) {
                HighScoreManager.getInstance().add(new HighScore(name, prizes[questionIndex - 1], LocalDate.now()));
            }
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.WRONG_ANSWER), prizes[questionIndex - 1]));
            cleanUp();
        }
    }

    /**
     * Kérdés csere segítség feldolgozása.
     */
    private void swapQuestionHelpResponse() {
        if(swapQuestionHelp) {
            currentQuestion = QuestionManager.getInstance().getQuestionByDifficulty(questionIndex);
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.SWAPQUESTIONHELP), currentQuestion));
        }
    }

    /**
     * Felezés segítség feldolgozása.
     */
    private void splitHelpResponse() {
        if(splitHelp) {
            splitHelp = false;
            boolean[] arr = new boolean[4];
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
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.SPLITHELP), arr));
        }
    }

    /**
     * Szavazás segítésg feldolgozása.
     * @param msg Üzenet
     */
    private void crowdHelpResponse(Message msg) {
        if(crowdHelp) {
            boolean[] en = (boolean[])msg.getData();
            int cnt = 0;
            for(int i = 0; i < 4; i++) {
                if(en[i]) {
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
                messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.CROWDHELP), votes));
            }
        }
    }

    /**
     * Érkező üzenetek felismerése és továbbítása.
     */
    @Override
    public void run() {
        while(running) {
            try {
                Message msg = messageManager.receiveMessage();
                if(msg != null) {
                    EnumSet<MessageType> ids = msg.getMessageID();
                    if(ids.contains(MessageType.ANSWER)) {
                        answerResponse(msg);
                    } else if(ids.contains(MessageType.SWAPQUESTIONHELP)) {
                        swapQuestionHelpResponse();
                    } else if(ids.contains(MessageType.SPLITHELP)) {
                        splitHelpResponse();
                    } else if(ids.contains(MessageType.CROWDHELP)) {
                        crowdHelpResponse(msg);
                    } else if(ids.contains(MessageType.DISCONNECT)) {
                        messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.DISCONNECT)));
                        cleanUp();
                        System.out.println("Disconnecting: " + clientSocket.toString());
                    } else if(ids.contains(MessageType.CLOSED)) {
                        messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.CLOSED)));
                        cleanUp();
                    } else if(ids.contains(MessageType.NEW_GAME)) {
                        newGameResponse(msg);
                    } else if(ids.contains(MessageType.SCOREBOARD)) {
                        messageManager.sendMessage(new Message(EnumSet.of(MessageType.CONFIRM, MessageType.SCOREBOARD), HighScoreManager.getInstance().getHighScores()));
                    }
                } else {
                    System.out.println("Lost connection to: " + clientSocket.toString());
                    cleanUp();
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param targetSum Cél összeg
     * @param numberOfDraws Hány db
     * @return <code>numberOfDraws</code> darabszámú <code>targetSum</code> összegű véletlen számot generál.
     */
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

    /**
     * Kapcsolat bontása.
     */
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
