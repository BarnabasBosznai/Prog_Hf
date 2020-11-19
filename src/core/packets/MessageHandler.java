package core.packets;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageHandler {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public MessageHandler(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        oos = objectOutputStream;
        ois = objectInputStream;
    }

    public void sendMessage(Message msg) {
        try {
            oos.writeObject(msg);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Message receiveMessage() {
        try {
            return (Message)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
