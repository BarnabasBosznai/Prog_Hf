package core.client;

import core.gui.UpdatePanelUI;
import core.message.*;
import core.question.Question;
import core.message.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EnumSet;

/**
 * Kliens program logikája
 */
public class ClientGame {
    private UpdatePanelUI callback;
    private Socket socket;
    private MessageManager messageManager;
    public Question question;
    private boolean running;

    /**
     * Inicializálja a kliens program logikáját, csatlakozik a szerverhez.
     * @param name Felhasználó neve
     * @param func Callback
     * @return Igazzal tér vissza, ha sikerült felépíteni a kapcsolatot a szerverrel, különben hamis
     */
    public boolean init(String name, UpdatePanelUI func) {
        try {
            ClientConfig config = ClientConfig.read("client_config.json");
            socket = new Socket(config.getServerIP(), config.getServerPort());
            messageManager = new MessageManager(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.NEW_GAME), name));
            Message msg = messageManager.receiveMessage();
            question = (Question)msg.getData();
            running = true;
            new Thread(this::processMessage).start();
            callback = func;
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Elküldi a szervernek a kérdésre adott válasz gomb indexét. Sikertelen küldés esetén véget ér a játék.
     * @param index Válasz gomb indexe
     */
    public void sendAnswer(int index) {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.ANSWER), index))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    /**
     * Elküldi a szervernek, hogy a felhasználó a szavazás segítséget venné igénybe. Sikertelen küldés esetén véget ér a játék.
     * @param en Aktív válasz gombok tömbje
     */
    public void sendCrowdHelp(boolean[] en) {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.CROWDHELP), en))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    /**
     *  Elküldi a szervernek, hogy a felhasználó a felezés segítséget venné igénybe. Sikertelen küldés esetén véget ér a játék.
     */
    public void sendSplitHelp() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.SPLITHELP)))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    /**
     * Elküldi a szervernek, hogy a felhasználó a kérdés csere segítséget venné igénybe. Sikertelen küldés esetén véget ér a játék.
     */
    public void sendSwapQuestionHelp() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.SWAPQUESTIONHELP)))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    /**
     * Elküldi a szervernek, hogy a kapcsolat bontását kezdeményezi a felhasználó.
     */
    public void sendDisconnect() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.DISCONNECT)))){
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    /**
     * Elküldi a szervernek, hogy a programot bezárták a játék folyamán.
     */
    public void sendClosed() {
        if(!messageManager.sendMessage(new Message(EnumSet.of(MessageType.CLOSED)))) {
            callback.update(MessageType.DISCONNECT, null);
        }
    }

    /**
     * Szerver válaszának feldolgozása, hogy helyes vagy helytelen választ jelőlt meg a felhasználó.
     * @param msg Beérkező üzenet a szervertől
     */
    private void answerReceived(Message msg) {
        EnumSet<MessageType> ids = msg.getMessageID();
        if(ids.contains(MessageType.QUESTION)) {
            question = (Question)msg.getData();
            callback.update(MessageType.QUESTION, null);
        } else if(ids.contains(MessageType.WRONG_ANSWER)){
            sendDisconnect();
            running = false;
            callback.update(MessageType.WRONG_ANSWER, msg.getData());
        } else if(ids.contains(MessageType.WON)) {
            sendDisconnect();
            running = false;
            callback.update(MessageType.WON, msg.getData());
        }
    }

    /**
     * Szerver válaszának feldolgozása, hogy engedélyezi-e a szavazás segítséget.
     * @param msg Beérkező üzenet a szervertől
     */
    private void crowdHelpReceived(Message msg) {
        EnumSet<MessageType> ids = msg.getMessageID();
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.CROWDHELP)) {
            callback.update(MessageType.CROWDHELP, msg.getData());
        }
    }

    /**
     * Szerver válaszának feldolgozása, hogy engedélyezi-e a felezés segítséget.
     * @param msg Beérkező üzenet a szervertől
     */
    private void splitHelpReceived(Message msg) {
        EnumSet<MessageType> ids = msg.getMessageID();
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.SPLITHELP)) {
            callback.update(MessageType.SPLITHELP, msg.getData());
        }
    }

    /**
     * Szerver válaszának feldolgozása, hogy engedélyezi-e a kérdés csere segítséget.
     * @param msg Beérkező üzenet a szervertől
     */
    private void swapQuestionHelpReceived(Message msg) {
        EnumSet<MessageType> ids = msg.getMessageID();
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.SWAPQUESTIONHELP)) {
            question = (Question)msg.getData();
            callback.update(MessageType.SWAPQUESTIONHELP, null);
        }
    }

    /**
     * Lezárja a kapcsolatot a szerverrel.
     */
    private void cleanUp() {
        messageManager.close();
        try {
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        running = false;
    }

    /**
     * Szervertől érkező üzenetek fogadása és továbbítása a megfelelő függvény(ek)nek, amíg a kapcsolat él.
     */
    private void processMessage() {
        while(running) {
            Message msg = messageManager.receiveMessage();
            if(msg != null) {
                EnumSet<MessageType> ids = msg.getMessageID();
                if(ids.contains(MessageType.QUESTION) || ids.contains(MessageType.WON) || ids.contains(MessageType.WRONG_ANSWER)) {
                    answerReceived(msg);
                } else if(ids.contains(MessageType.SWAPQUESTIONHELP)) {
                    swapQuestionHelpReceived(msg);
                } else if(ids.contains(MessageType.CROWDHELP)) {
                    crowdHelpReceived(msg);
                } else if(ids.contains(MessageType.SPLITHELP)) {
                    splitHelpReceived(msg);
                } else if(ids.contains(MessageType.DISCONNECT)) {
                    cleanUp();
                } else if(ids.contains(MessageType.CLOSED)) {
                    callback.update(MessageType.CLOSED, null);
                    cleanUp();
                }
            } else {
                callback.update(MessageType.DISCONNECT, null);
                cleanUp();
            }
        }
    }

}
