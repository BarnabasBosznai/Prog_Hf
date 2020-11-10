package core.packets;

import java.util.EnumSet;

public class ResponsePacket extends Packet {
    private EnumSet<AnswerFlag> flags;
    public ResponsePacket(EnumSet<AnswerFlag> flags) {
        this.flags = flags;
    }

    public EnumSet<AnswerFlag> getFlags() {
        return flags;
    }
}
