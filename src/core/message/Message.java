package core.message;

import java.io.*;
import java.util.Base64;
import java.util.EnumSet;

/**
 * Üzenet, amit a kliens és szerver küld egymás között.
 */
public class Message implements Serializable {

    private EnumSet<MessageType> messageIDs;
    private byte[] data;

    /**
     * Új üzenet létrehozása adat plussz adat nélkül.
     * @param ids Üzenet flagjei
     */
    public Message(EnumSet<MessageType> ids) {
        messageIDs = ids;
        data = null;
    }

    /**
     * Új üzenet létrehozása adattal.
     * @param ids Üzenet flagjei
     * @param obj Mellékelt adat
     */
    public Message(EnumSet<MessageType> ids, Object obj) {
        messageIDs = ids;
        data = Base64.getEncoder().encode(serialize(obj));
    }

    /**
     * @return Üzenet flagjei
     */
    public EnumSet<MessageType> getMessageID() {
        return messageIDs;
    }

    /**
     * @return Üzenet adata, ha volt, különben <code>null</code>.
     */
    public Object getData() {
        if(data != null) {
            return deserialize(Base64.getDecoder().decode(data));
        } else {
            return null;
        }
    }

    /**
     * Adat szerializálása.
     * @param obj Mellékelt adat
     * @return Mellékelt adat byte[]-ba szerializálva
     */
    private byte[] serialize(Object obj) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(obj);
            return out.toByteArray();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mellékelt adat deszerializálása.
     * @param data Mellékelt szerializált adat byte[]-ben
     * @return Mellékelt adat
     */
    private Object deserialize(byte[] data) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(data); ObjectInputStream is = new ObjectInputStream(in)) {
            return is.readObject();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
