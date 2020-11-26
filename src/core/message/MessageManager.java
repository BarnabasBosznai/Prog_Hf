package core.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;

public class MessageManager {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public MessageManager(ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream) {
        oos = objectOutputStream;
        ois = objectInputStream;
    }

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

    public Message receiveMessage() {
        try {
            return (Message)ois.readObject();
        } catch (SocketException e) {
            return null;
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
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
