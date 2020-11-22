package core.client;

import core.highscore.HighScore;
import core.message.Message;
import core.message.MessageManager;
import core.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientScoreboard {
    public static List<HighScore> getScoreboardData(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            MessageManager messageManager = new MessageManager(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
            messageManager.sendMessage(new Message(MessageType.SCOREBOARD, null));
            Message msg = messageManager.receiveMessage();
            messageManager.sendMessage(new Message(MessageType.DISCONNECT, null));
            return (List<HighScore>)msg.getData();
        } catch (IOException e) {
            return null;
        }
    }
}
