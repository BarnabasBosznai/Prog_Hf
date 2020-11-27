package core.gui;

import core.message.MessageType;

/**
 * Grafikus felületnek callback interfésze.
 */
public interface UpdatePanelUI {
    /**
     * Frissítő függvény.
     * @param id Üzenet típusa
     * @param data Opcionális adat
     */
    void update(MessageType id, Object data);
}
