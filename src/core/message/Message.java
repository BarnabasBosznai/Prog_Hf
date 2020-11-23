package core.message;

import java.io.*;
import java.util.Base64;
import java.util.EnumSet;

public class Message implements Serializable {

    private EnumSet<MessageType> messageIDs;
    private String data;

    public Message(EnumSet<MessageType> ids, Object obj) {
        messageIDs = ids;
        data = Base64.getEncoder().encodeToString(serialize(obj));
    }

    public EnumSet<MessageType> getMessageID() {
        return messageIDs;
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
