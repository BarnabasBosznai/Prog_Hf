package core.client;

import core.gui.GamePanel;
import core.gui.MainFrame;
import core.message.*;
import core.question.Question;
import core.message.MessageType;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.EnumSet;
import java.util.List;

public class ClientGame {
    private Socket socket;
    private MessageManager messageManager;
    private Thread t;
    public Question question;
    private GamePanel gamePanel;
    private boolean run;

    public boolean init(String ip, int port, GamePanel gp, String name) {
        try {
            socket = new Socket(ip, port);
            messageManager = new MessageManager(new ObjectOutputStream(socket.getOutputStream()), new ObjectInputStream(socket.getInputStream()));
            messageManager.sendMessage(new Message(EnumSet.of(MessageType.NEW_GAME), name));
            Message msg = messageManager.receiveMessage();
            question = (Question)msg.getData();
            gamePanel = gp;
            run = true;
            t = new Thread(this::processMessage);
            t.start();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendAnswer(int index) {
        messageManager.sendMessage(new Message(EnumSet.of(MessageType.ANSWER), index));
    }

    public void sendCrowdHelp(boolean[] en) {
        messageManager.sendMessage(new Message(EnumSet.of(MessageType.CROWDHELP), en));
    }

    public void sendSplitHelp() {
        messageManager.sendMessage(new Message(EnumSet.of(MessageType.SPLITHELP), null));
    }

    public void sendSwapQuestionHelp() {
        messageManager.sendMessage(new Message(EnumSet.of(MessageType.SWAPQUESTIONHELP), null));
    }

    private void answerReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.QUESTION)) {
            question = (Question)data;
            gamePanel.setupUIForNextQuestionTest();
        } else if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.WON)) {
            MainFrame frame = (MainFrame)SwingUtilities.getWindowAncestor(gamePanel);
            JOptionPane.showMessageDialog(frame, "Gratulálunk, megnyerte a főnyereményt: " + data + " Ft", "Winner Winner Chicken Dinner", JOptionPane.PLAIN_MESSAGE);
            testSendDisconnect();
            run = false;
            frame.setGame(null);
            CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
            cl.removeLayoutComponent(gamePanel);
            cl.show(frame.getContentPane(), "MENU");
        } else {
            MainFrame frame = (MainFrame)SwingUtilities.getWindowAncestor(gamePanel);
            JOptionPane.showMessageDialog(frame, "Rossz válasz! Nyereménye: " + data + " Ft", "Vége a játéknak!", JOptionPane.PLAIN_MESSAGE);
            testSendDisconnect();
            run = false;
            frame.setGame(null);
            CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
            cl.removeLayoutComponent(gamePanel);
            cl.show(frame.getContentPane(), "MENU");
        }
    }

    private void crowdHelpReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.CROWDHELP)) {

            List<Integer> v2 = (List<Integer>)data;
            if(v2 != null) {
                JButton[] btns = gamePanel.getAnsButtons();
                for(int i = 0; i < 4; i++) {
                    if(btns[i].isEnabled()) {
                        btns[i].setText(btns[i].getText() + " " + v2.get(i) + "%");
                    }
                }
                gamePanel.getCrowdHelp().setEnabled(false);
            }
        }
    }

    private void splitHelpReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.SPLITHELP)) {
            boolean[] val = (boolean[])data;
            if(val != null) {
                JButton[] btns = gamePanel.getAnsButtons();
                for(int i = 0; i < 4; i++) {
                    btns[i].setEnabled(val[i]);
                }
                gamePanel.getSplitHelp().setEnabled(false);
            }
        }
    }

    private void swapQuestionHelpReceived(EnumSet<MessageType> ids, Object data) {
        if(ids.contains(MessageType.CONFIRM) && ids.contains(MessageType.SWAPQUESTIONHELP)) {
            question = (Question)data;
            gamePanel.setupUIForNextQuestionTest();
            gamePanel.getNewQuestionHelp().setEnabled(true);
        }
    }

    public void testSendDisconnect() {
        messageManager.sendMessage(new Message(EnumSet.of(MessageType.DISCONNECT), null));
        try {
            run = false;
            messageManager.close();
            socket.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void processMessage() {
        while(run) {
            Message msg = messageManager.receiveMessage();
            EnumSet<MessageType> ids = msg.getMessageID();
            if(ids.contains(MessageType.CONFIRM) ||  ids.contains(MessageType.ERROR)) {
                if(ids.contains(MessageType.QUESTION) || ids.contains(MessageType.ANSWER) || ids.contains(MessageType.WON)) {
                    answerReceived(ids, msg.getData());
                } else if (ids.contains(MessageType.SWAPQUESTIONHELP)) {
                    swapQuestionHelpReceived(ids, msg.getData());
                } else if(ids.contains(MessageType.CROWDHELP)) {
                    crowdHelpReceived(ids, msg.getData());
                } else if(ids.contains(MessageType.SPLITHELP)) {
                    splitHelpReceived(ids, msg.getData());
                }
            }
        }

    }

}
