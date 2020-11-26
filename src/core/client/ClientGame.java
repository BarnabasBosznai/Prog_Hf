package core.client;

import core.message.*;
import core.question.Question;
import core.message.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EnumSet;

public class ClientGame {
    private UpdatePanelUI callback;
    private Socket socket;
    private MessageManager messageManager;
    public Question question;
    private boolean running;

    public boolean init(String ip, int port, String name, UpdatePanelUI func) {
        try {
            socket = new Socket(ip, port);
            messageManager = new MessageManager(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.NEW_GAME), name));
            Message msg = messageManager.receiveMessage();
            question = (Question)msg.getData();
            running = true;
            new Thread(this::processMessage).start();
            callback = func;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void sendAnswer(int index) {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.ANSWER), index))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    public void sendCrowdHelp(boolean[] en) {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.CROWDHELP), en))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    public void sendSplitHelp() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.SPLITHELP)))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    public void sendSwapQuestionHelp() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.SWAPQUESTIONHELP)))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    public void sendDisconnect() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.DISCONNECT)))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    private void answerReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.QUESTION)) {
            question = (Question)data;
            callback.update(MessageType.QUESTION, null);
        } else if(ids.contains(MessageType.WRONG_ANSWER)){
            sendDisconnect();
            running = false;
            callback.update(MessageType.WRONG_ANSWER, data);
        } else if(ids.contains(MessageType.WON)) {
            sendDisconnect();
            running = false;
            callback.update(MessageType.WON, data);
        }
    }

    private void crowdHelpReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.CROWDHELP)) {
            callback.update(MessageType.CROWDHELP, data);
        }
    }

    private void splitHelpReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.SPLITHELP)) {
            callback.update(MessageType.SPLITHELP, data);
        }
    }

    private void swapQuestionHelpReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.SWAPQUESTIONHELP)) {
            question = (Question)data;
            callback.update(MessageType.SWAPQUESTIONHELP, null);
        }
    }

    private void disconnectReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.DISCONNECT)) {
            cleanUp();
        }
    }

    private void cleanUp() {
        messageManager.close();
        try {
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        running = false;
    }

    private void processMessage() {
        while(running) {
            Message msg = messageManager.receiveMessage();
            if(msg != null) {
                EnumSet<MessageType> ids = msg.getMessageID();
                if(ids.contains(MessageType.QUESTION) || ids.contains(MessageType.WON) || ids.contains(MessageType.WRONG_ANSWER)) {
                    answerReceived(ids, msg.getData());
                } else if(ids.contains(MessageType.SWAPQUESTIONHELP)) {
                    swapQuestionHelpReceived(ids, msg.getData());
                } else if(ids.contains(MessageType.CROWDHELP)) {
                    crowdHelpReceived(ids, msg.getData());
                } else if(ids.contains(MessageType.SPLITHELP)) {
                    splitHelpReceived(ids, msg.getData());
                } else if(ids.contains(MessageType.DISCONNECT)) {
                    disconnectReceived(ids, msg.getData());
                }
            } else {
                callback.update(MessageType.DISCONNECT, null);
                cleanUp();
            }
        }
    }

}
