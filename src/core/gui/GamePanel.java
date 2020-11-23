package core.gui;

import core.client.ClientGame;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;

public class GamePanel extends JPanel {
    private JLabel questionLabel = new JLabel();
    private JButton crowdHelp;
    private JButton splitHelp;
    private JButton newQuestionHelp;
    private JButton[] ansButtons = new JButton[4];

    private ClientGame game;

    public boolean init(MainFrame frame, String ip, int port, String name) {
        if(setupConnection(ip, port, name)) {
            frame.setGame(game);
            setupUI();
            return true;
        } else {
            return false;
        }
    }

    private boolean setupConnection(String ip, int port, String name) {
        game = new ClientGame();
        return game.init(ip, port, this,  name);
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
            boolean en[] = new boolean[4];
            for(int i = 0; i < 4; i++) {
                en[i] = ansButtons[i].isEnabled();
            }
            game.sendCrowdHelp(en);
        });

        newQuestionHelp.addActionListener((ActionListener) -> {
            game.sendSwapQuestionHelp();
        });
    }


    public void setupUIForNextQuestionTest() {
        questionLabel.setText(game.question.getQuestion());
        String[] ans = game.question.getAnswers();
        for(int i = 0; i < 4; i++) {
            ansButtons[i].setText(((char)(65 + i)) + ") " + ans[i]);
            ansButtons[i].setEnabled(true);
        }
    }

    public JButton getCrowdHelp() {
        return crowdHelp;
    }

    public JButton getSplitHelp() {
        return splitHelp;
    }

    public JButton getNewQuestionHelp() {
        return newQuestionHelp;
    }

    public JButton[] getAnsButtons() {
        return ansButtons;
    }
}
