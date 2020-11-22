package core.message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MessageManager {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public MessageManager(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
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

    public void close() {
        try {
            oos.close();
            ois.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
