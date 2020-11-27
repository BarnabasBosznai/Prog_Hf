package core.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

/**
 * Üzenetek küldését és fogadását segítő osztály.
 */
public class MessageManager {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    /**
     *
     * @param objectOutputStream kimeneti stream
     * @param objectInputStream bemeneti stream
     */
    public MessageManager(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        oos = objectOutputStream;
        ois = objectInputStream;
    }

    /**
     * Új üzenet küldése.
     * @param msg Küldendő üzenet
     * @return Igazzal tér vissza, a sikeres volt a küldés, különben hamis
     */
    public boolean sendMessage(Message msg) {
        try {
            oos.writeObject(msg);
            return true;
        } catch(SocketException e) {
            return false;
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Üzenet fogadása.
     * @return Fogadott üzenet vagy <code>null</code>, ha hiba történt.
     */
    public Message receiveMessage() {
        try {
            return (Message)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Streamek bezárása.
     */
    public void close() {
        try {
            oos.close();
            ois.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
