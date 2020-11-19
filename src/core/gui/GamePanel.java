package core.gui;

import core.client.ClientGame;
import core.util.Pair;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import java.awt.*;
import java.awt.event.*;
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

    public GamePanel(MainFrame frame) {
        crowdHelp = new JButton("<html>Közönség<br>szavazás</html>");
        crowdHelp.setPreferredSize(new Dimension(120, 55));
        crowdHelp.setFocusPainted(false);
        splitHelp = new JButton("Felezés");
        splitHelp.setPreferredSize(new Dimension(120, 55));
        splitHelp.setFocusPainted(false);
        newQuestionHelp = new JButton("<html>Kérdés<br>csere</html>");
        newQuestionHelp.setPreferredSize(new Dimension(120, 55));
        newQuestionHelp.setFocusPainted(false);
        for(int i = 0; i < 4; i++) {
            ansButtons[i] = new JButton();
            ansButtons[i].setFocusPainted(false);
        }
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap(110, Short.MAX_VALUE)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
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

        questionLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(crowdHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                                .addComponent(splitHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(newQuestionHelp, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)))
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
            Pair<Boolean, Integer> pair = game.testSplitHelp();
            if(pair.getFirst()) {
                Random random = new Random();
                int idx = pair.getSecond();
                int count = 0;
                while(count != 2) {
                    int temp = random.nextInt(4);
                    if(temp != idx && ansButtons[temp].isEnabled()) {
                        ansButtons[temp].setEnabled(false);
                        count++;
                    }
                }
                splitHelp.setEnabled(false);
            }
        });

        crowdHelp.addActionListener((ActionListener) -> {
            int count = 0;
            for(int i = 0; i < 4; i++) {
                if(ansButtons[i].isEnabled()) {
                    count++;
                }
            }

            List<Integer> v = game.testCrowdHelp(count);
            if(v != null) {
                int idx = 0;
                for(int i = 0; i < 4; i++) {
                    if(ansButtons[i].isEnabled()) {
                        ansButtons[i].setText(ansButtons[i].getText() + " " + v.get(idx++) + "%");
                    }
                }
                crowdHelp.setEnabled(false);
            }
        });

        newQuestionHelp.addActionListener((ActionListener) -> {
            if(game.testSwapQuestionHelp()) {
                setupUINextQuestionTest();
            }
            newQuestionHelp.setEnabled(false);
        });
        addComponentListener(new GamePanelComponentListener(frame));
    }

    private void setupUINextQuestionTest() {
        questionLabel.setText(wrapText(game.question.getQuestion()));
        String[] ans = game.question.getAnswers();
        for(int i = 0; i < 4; i++) {
            ansButtons[i].setText(((char)(65 + i)) + ") " + ans[i]);
            ansButtons[i].setEnabled(true);
        }
    }

    private void answerButtonFunc(MainFrame frame, int index) {
        if(game.testSendAnswer(index)) {
            setupUINextQuestionTest();
        } else {
            game.testSendDisconnect();
            frame.setGame(null);
            CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
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

    private void onConnect(MainFrame frame) {
        game = new ClientGame();
        frame.setGame(game);
    }

    private class GamePanelComponentListener implements ComponentListener {

        private MainFrame frame;
        public GamePanelComponentListener(MainFrame mainFrame) {
            frame = mainFrame;
        }

        @Override
        public void componentResized(ComponentEvent e) {

        }

        @Override
        public void componentMoved(ComponentEvent e) {

        }

        @Override
        public void componentShown(ComponentEvent e) {
            System.out.println("ComponenetShown got called!");
            onConnect(frame);
            newQuestionHelp.setEnabled(true);
            crowdHelp.setEnabled(true);
            splitHelp.setEnabled(true);
            setupUINextQuestionTest();
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
    }
}
