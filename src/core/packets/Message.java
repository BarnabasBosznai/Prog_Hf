package core.packets;

import core.server.MessageType;

import java.io.*;
import java.util.Base64;

public class Message implements Serializable {
    public Message(MessageType id, Object obj) {
        messageID = id;
        data = Base64.getEncoder().encodeToString(serialize(obj));
    }

    private MessageType messageID;
    private String data;


    public MessageType getMessageID() {
        return messageID;
    }

    public Object getData() {
        return deserialize(Base64.getDecoder().decode(data));
    }

    private byte[] serialize(Object obj) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(obj);
            return out.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object deserialize(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data); ObjectInputStream is = new ObjectInputStream(in)) {
            return is.readObject();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
