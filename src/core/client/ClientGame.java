package core.client;

import core.message.*;
import core.question.Question;
import core.message.MessageType;
import core.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientGame {
    private Socket socket;
    private MessageManager messageManager;
    public Question question;

    public boolean init(String ip, int port, String name) {
        try {
            socket = new Socket(ip, port);
            messageManager = new MessageManager(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
            messageManager.sendMessage(new Message(MessageType.NEW_GAME, name));
            Message msg = messageManager.receiveMessage();
            question = (Question)msg.getData();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Pair<Boolean, String> testSendAnswer(int index) {
        messageManager.sendMessage(new Message(MessageType.ANSWER, index));
        Message msg = messageManager.receiveMessage();
        if (msg.getMessageID() == MessageType.QUESTION) {
            question = (Question)msg.getData();
            return new Pair<>(true, null);
        } else {
            return new Pair<>(false, (String)msg.getData());
        }
    }

    public List<Integer> testCrowdHelpv2(boolean[] en) {
        messageManager.sendMessage(new Message(MessageType.CROWDHELP, en));
        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.CONFIRM) {
            return (List<Integer>) msg.getData();
        }
        else {
            return null;
        }
    }

    public boolean[] testSplitHelp() {
        messageManager.sendMessage(new Message(MessageType.SPLITHELP, null));
        Message msg = messageManager.receiveMessage();
        if(msg.getMessageID() == MessageType.CONFIRM) {
            return (boolean[])msg.getData();
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
            messageManager.close();
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
