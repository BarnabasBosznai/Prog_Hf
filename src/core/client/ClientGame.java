package core.client;

import core.message.*;
import core.question.Question;
import core.server.MessageType;
import core.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientGame {
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    MessageManager messageManager;
    public Question question;

    public ClientGame() {
        try {
            socket = new Socket("localhost", 58901);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            messageManager = new MessageManager(oos, ois);
            Message msg = messageManager.receiveMessage();
            question = (Question)msg.getData();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /*public List<HighScore> getScores() {
        try {
            oos.writeObject(MessageType.SEND_SCORES);
            return (List<HighScore>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }*/


    public boolean testSendAnswer(int index) {
        messageManager.sendMessage(new Message(MessageType.ANSWER, index));
        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.ERROR) {
            return false;
        } else {
            question = (Question)msg.getData();
            return true;
        }
    }

    public List<Integer> testCrowdHelp(int currentlyActiveButtons) {
        messageManager.sendMessage(new Message(MessageType.CROWDHELP, currentlyActiveButtons));
        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.CONFIRM) {
            return (List<Integer>) msg.getData();
        }
        else {
            return null;
        }
    }

    public Pair<Boolean, Integer> testSplitHelp() {
        messageManager.sendMessage(new Message(MessageType.SPLITHELP, null));
        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.CONFIRM) {
            return new Pair<>(true, (int)msg.getData());
        } else {
            return null;
        }
    }

    public boolean testSwapQuestionHelp() {
        messageManager.sendMessage(new Message(MessageType.SWAPQUESTIONHELP, null));
        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.CONFIRM) {
            question = (Question)msg.getData();
            return true;
        }
        return false;
    }

    public void testSendDisconnect() {
        messageManager.sendMessage(new Message(MessageType.DISCONNECT, null));
        try {
            oos.close();
            ois.close();
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
