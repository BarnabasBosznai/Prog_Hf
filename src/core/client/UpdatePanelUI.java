package core.client;

import core.message.MessageType;

import java.util.EnumSet;

public interface UpdatePanelUI {
    void update(MessageType id, Object data);
}
