package core.gui;

import core.client.ClientGame;
import core.message.MessageType;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.List;

public class GamePanel extends JPanel {
    private JLabel questionLabel = new JLabel();
    private JButton crowdHelp;
    private JButton splitHelp;
    private JButton newQuestionHelp;
    private JButton[] ansButtons = new JButton[4];
    private CustomWindowListener windowListener;
    private ClientGame game;

    private void uiCallback(MessageType id, Object data) {
        MainFrame frame;
        CardLayout cl;
        switch(id) {
            case QUESTION:
                setupUIForNextQuestionTest();
                break;
            case CROWDHELP:crowdHelp.setEnabled(false);
                List<Integer> list = (List<Integer>)data;
                for(int i = 0; i < 4; i++) {
                    if(ansButtons[i].isEnabled()) {
                        ansButtons[i].setText(ansButtons[i].getText() + " " + list.get(i) + "%");
                    }
                }
                break;
            case SPLITHELP:
                splitHelp.setEnabled(false);
                boolean[] val = (boolean[])data;
                for(int i = 0; i < 4; i++) {
                    ansButtons[i].setEnabled(val[i]);
                }
                break;
            case SWAPQUESTIONHELP:
                newQuestionHelp.setEnabled(false);
                setupUIForNextQuestionTest();
                break;
            case WON:
                frame = (MainFrame)SwingUtilities.getWindowAncestor(this);
                JOptionPane.showMessageDialog(frame, "Gratulálunk, megnyerte a főnyereményt: " + data, "Winner Winner Chicken Dinner", JOptionPane.PLAIN_MESSAGE);
                frame.removeWindowListener(windowListener);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                cl = (CardLayout)frame.getContentPane().getLayout();
                cl.removeLayoutComponent(this);
                cl.show(frame.getContentPane(), "MENU");
                break;
            case WRONG_ANSWER:
                frame = (MainFrame)SwingUtilities.getWindowAncestor(this);
                frame.removeWindowListener(windowListener);
                JOptionPane.showMessageDialog(frame, "Rossz válasz! Nyereménye: " + data + " Ft", "Vége a játéknak!", JOptionPane.PLAIN_MESSAGE);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                cl = (CardLayout)frame.getContentPane().getLayout();
                cl.removeLayoutComponent(this);
                cl.show(frame.getContentPane(), "MENU");
                break;
            case DISCONNECT:
                frame = (MainFrame)SwingUtilities.getWindowAncestor(this);
                frame.removeWindowListener(windowListener);
                JOptionPane.showMessageDialog(frame, "Megszakadt a kapcsolat a szerverrel!", "Kapcsolat hiba", JOptionPane.ERROR_MESSAGE);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                cl = (CardLayout)frame.getContentPane().getLayout();
                cl.removeLayoutComponent(this);
                cl.show(frame.getContentPane(), "MENU");
                break;
            case CLOSED:
                frame = (MainFrame)SwingUtilities.getWindowAncestor(this);
                frame.removeWindowListener(windowListener);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                cl = (CardLayout)frame.getContentPane().getLayout();
                cl.removeLayoutComponent(this);
                cl.show(frame.getContentPane(), "MENU");
        }
    }

    public boolean init(MainFrame frame, String ip, int port, String name) {
        if(setupConnection(ip, port, name)) {
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            windowListener = new CustomWindowListener(this);
            frame.addWindowListener(windowListener);
            setupUI();
            return true;
        } else {
            return false;
        }
    }

    private boolean setupConnection(String ip, int port, String name) {
        game = new ClientGame();
        return game.init(ip, port, name, this::uiCallback);
    }

    private void setupUI() {
        crowdHelp = new JButton("Szavazás");
        crowdHelp.setPreferredSize(new Dimension(120, 60));
        crowdHelp.setFocusPainted(false);
        splitHelp = new JButton("Felezés");
        splitHelp.setPreferredSize(new Dimension(120, 60));
        splitHelp.setFocusPainted(false);
        newQuestionHelp = new JButton("Kérdés csere");
        newQuestionHelp.setPreferredSize(new Dimension(120, 60));
        newQuestionHelp.setFocusPainted(false);
        for(int i = 0; i < 4; i++) {
            ansButtons[i] = new JButton();
            ansButtons[i].setFocusPainted(false);
        }

        setupUIForNextQuestionTest();

        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap(110, Short.MAX_VALUE)
                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
                                                        .addComponent(questionLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                                        .addComponent(ansButtons[0], GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(ansButtons[2], GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE))
                                                                .addGap(110)
                                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                                        .addComponent(ansButtons[1], GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(ansButtons[3], GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE))))
                                                .addGap(110))
                                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                .addComponent(newQuestionHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(splitHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(crowdHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())))
        );

        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                        .addComponent(crowdHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(splitHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(newQuestionHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(100)
                                .addComponent(questionLabel, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(ansButtons[1], GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ansButtons[0], GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
                                .addGap(20)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(ansButtons[3], GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(ansButtons[2], GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
                                .addGap(40))
        );
        setLayout(groupLayout);

        for(int i = 0; i < 4; i++) {
            int finalI = i;
            ansButtons[i].addActionListener((ActionListener) -> {
                game.sendAnswer(finalI);
            });
        }

        splitHelp.addActionListener((ActionListener) -> {
            game.sendSplitHelp();
        });

        crowdHelp.addActionListener((ActionListener) -> {
            boolean[] en = new boolean[4];
            for(int i = 0; i < 4; i++) {
                en[i] = ansButtons[i].isEnabled();
            }
            game.sendCrowdHelp(en);
        });

        newQuestionHelp.addActionListener((ActionListener) -> {
            game.sendSwapQuestionHelp();
        });
    }

    private void setupUIForNextQuestionTest() {
        questionLabel.setText(game.question.getQuestion());
        String[] ans = game.question.getAnswers();
        for(int i = 0; i < 4; i++) {
            ansButtons[i].setText(((char)(65 + i)) + ") " + ans[i]);
            ansButtons[i].setEnabled(true);
        }
    }

    private class CustomWindowListener extends WindowAdapter {

        private GamePanel gamePanel;

        public CustomWindowListener(GamePanel panel) {
            gamePanel = panel;
        }

        @Override
        public void windowClosing(WindowEvent e) {
            Object[] options = {"Kilépés", "Vissza a menübe" , "Mégse"};
            MainFrame frame = (MainFrame)SwingUtilities.getWindowAncestor(gamePanel);
            for(WindowListener lis : frame.getWindowListeners()) {
                System.out.println(lis.toString());
            }
            int res = JOptionPane.showOptionDialog(frame, "Biztos kiakar lépni?", "Kilépés", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null ,options, options[0]);
            if(res == JOptionPane.YES_OPTION) {
                game.sendDisconnect();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                super.windowClosed(e);
            } else if(res == JOptionPane.NO_OPTION) {
                game.sendClosed();
            }
        }
    }
}
