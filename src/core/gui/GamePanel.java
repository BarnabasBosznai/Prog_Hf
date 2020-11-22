package core.gui;

import core.client.ClientGame;
import core.util.Pair;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class GamePanel extends JPanel {
    private JLabel questionLabel = new JLabel("questionPlaceholder");
    private JButton crowdHelp;
    private JButton splitHelp;
    private JButton newQuestionHelp;

    private ClientGame game;

    private JButton[] ansButtons = new JButton[4];

    public boolean init(MainFrame frame, String ip, int port, String name) {
        if(setupConnection(ip, port, name)) {
            frame.setGame(game);
            setupUI(frame);
            return true;
        } else {
            JOptionPane.showMessageDialog(frame, "Nem sikerült csatlakozni a szerverhez. Próbáld később!", "Csatlakozási hiba", JOptionPane.WARNING_MESSAGE);
            CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
            cl.removeLayoutComponent(this);
            cl.show(frame.getContentPane(), "MENU");
            return false;
        }
    }

    private boolean setupConnection(String ip, int port, String name) {
        game = new ClientGame();
        return game.init(ip, port, name);
    }

    private void setupUI(MainFrame frame) {
        crowdHelp = new JButton("<html>Közönség<br>szavazás</html>");
        crowdHelp.setPreferredSize(new Dimension(120, 60));
        crowdHelp.setFocusPainted(false);
        splitHelp = new JButton("Felezés");
        splitHelp.setPreferredSize(new Dimension(120, 60));
        splitHelp.setFocusPainted(false);
        newQuestionHelp = new JButton("<html>Kérdés<br>csere</html>");
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
                answerButtonFunc(frame, finalI);
            });
        }

        splitHelp.addActionListener((ActionListener) -> {
            boolean[] arr = game.testSplitHelp();
            if(arr != null) {
                for(int i = 0; i < 4; i++) {
                    ansButtons[i].setEnabled(arr[i]);
                }
                splitHelp.setEnabled(false);
            }
        });

        crowdHelp.addActionListener((ActionListener) -> {
            boolean en[] = new boolean[4];
            for(int i = 0; i < 4; i++) {
                en[i] = ansButtons[i].isEnabled();
            }

            List<Integer> v2 = game.testCrowdHelpv2(en);
            if(v2 != null) {
                for(int i = 0; i < 4; i++) {
                    if(ansButtons[i].isEnabled()) {
                        ansButtons[i].setText(ansButtons[i].getText() + " " + v2.get(i) + "%");
                    }
                }
                crowdHelp.setEnabled(false);
            }
        });

        newQuestionHelp.addActionListener((ActionListener) -> {
            if(game.testSwapQuestionHelp()) {
                setupUIForNextQuestionTest();
            }
            newQuestionHelp.setEnabled(false);
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

    private void answerButtonFunc(MainFrame frame, int index) {
        Pair<Boolean, String> pair = game.testSendAnswer(index);
        if(pair.getFirst()) {
            setupUIForNextQuestionTest();
        } else {
            if(pair.getSecond() != null) {
                JOptionPane.showMessageDialog(frame, "Rossz válasz! Nyereménye: " + pair.getSecond() + " Ft", "Vége a játéknak!", JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Gratulálunk, megnyerte a főnyereményt: " + pair.getSecond() + " Ft", "Winner Winner Chicken Dinner", JOptionPane.PLAIN_MESSAGE);
            }
            game.testSendDisconnect();
            frame.setGame(null);
            CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
            cl.removeLayoutComponent(this);
            cl.show(frame.getContentPane(), "MENU");
        }
    }

    // ???
    private String wrapText(String string){
        //Return string initialized with opening html tag
        String returnString="<html>";

        //Get max width of text line
        int maxLineWidth = questionLabel.getWidth();

        //Create font metrics
        FontMetrics metrics = getFontMetrics(new Font("Tahoma", Font.PLAIN, 16));
        //Current line width
        int lineWidth = 0;

        //Iterate over string
        StringTokenizer tokenizer = new StringTokenizer(string," ");
        while (tokenizer.hasMoreElements()) {
            String word = (String) tokenizer.nextElement();
            int stringWidth = metrics.stringWidth(word);

            //If word will cause a spill over max line width
            if (stringWidth+lineWidth>=maxLineWidth) {

                //Add a new line, add a break tag and add the new word
                returnString=(returnString+"<br>"+word);

                //Reset line width
                lineWidth=0;
            } else {

                //No spill, so just add to current string
                returnString=(returnString + " " + word);
            }
            //Increase the width of the line
            lineWidth += stringWidth;
        }

        //Close html tag
        returnString = (returnString + "<html>");

        //Return the string
        return returnString;
    }
}
