package core.client;

import core.highscore.HighScore;
import core.message.Message;
import core.message.MessageManager;
import core.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EnumSet;
import java.util.List;

/**
 * Kliens program eredményeket kezelő logikája.
 */
public class ClientScoreboard {
    /**
     * Csatlakozik a szerverhez, hogy lekérje a korábbi felhasználok játékban elért eredményét.
     * @return Lista az eredményekről vagy <code>null</code>, ha hiba történt.
     */
    public static List<HighScore> getScoreboardData() {
        try {
            ClientConfig config = ClientConfig.read("client_config.json");
            Socket socket = new Socket(config.getServerIP(), config.getServerPort());
            MessageManager messageManager = new MessageManager(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.SCOREBOARD)));
            Message msg = messageManager.receiveMessage();
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.DISCONNECT)));
            return (List<HighScore>)msg.getData();
        } catch(IOException e) {
            return null;
        }
    }
}
