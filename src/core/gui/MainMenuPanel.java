package core.gui;

import javax.swing.*;
import java.awt.*;

public class MainMenuPanel extends JPanel {

    public MainMenuPanel(String ip, int port, MainFrame frame) {
        JButton startGame = new JButton("Új Játék");
        startGame.setPreferredSize(new Dimension(60, 25));
        startGame.setFont(new Font("Tahoma", Font.PLAIN, 22));
        startGame.setFocusPainted(false);
        startGame.addActionListener((ActionListener) -> {
            String name = (String)JOptionPane.showInputDialog(frame, "Enter your name!", "Enter name", JOptionPane.PLAIN_MESSAGE, null, null, "Anonymous");
            GamePanel gamePanel = new GamePanel();
            if(gamePanel.init(frame, ip, port, name)) {
                frame.getContentPane().add(gamePanel, "GAME");
                CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
                cl.show(frame.getContentPane(), "GAME");
            } else {
                JOptionPane.showMessageDialog(frame, "Nem sikerült csatlakozni a szerverhez. Próbáld később!", "Csatlakozási hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton highScores = new JButton("Dicsőséglista");
        highScores.setPreferredSize(new Dimension(60, 25));
        highScores.setFont(new Font("Tahoma", Font.PLAIN, 22));
        highScores.setFocusPainted(false);
        highScores.addActionListener((ActionListener) -> {
            HighScorePanel highScorePanel = new HighScorePanel();
            if(highScorePanel.init(frame, ip, port)) {
                frame.getContentPane().add(highScorePanel, "HIGHSCORES");
                CardLayout cl = (CardLayout)frame.getContentPane().getLayout();
                cl.show(frame.getContentPane(), "HIGHSCORES");
            } else {
                JOptionPane.showMessageDialog(frame, "Nem sikerült csatlakozni a szerverhez! Próbáld később!", "Csatlakozási hiba", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton exit = new JButton("Kilépés");
        exit.setPreferredSize(new Dimension(60, 25));
        exit.setFont(new Font("Tahoma", Font.PLAIN, 22));
        exit.setFocusPainted(false);
        exit.addActionListener((ActionListener) -> frame.dispose());

        JLabel title = new JLabel("Legyen Ön Is Milliomos!");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.PLAIN, 35));
        title.setPreferredSize(new Dimension(400, 45));
        System.out.println(title.getPreferredSize());
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(200)
                                .addComponent(title, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(200, Short.MAX_VALUE))
                        .addGroup(GroupLayout.Alignment.TRAILING, groupLayout.createSequentialGroup()
                                .addGap(285)
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(exit, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                        .addComponent(highScores, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                                        .addComponent(startGame, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
                                .addGap(285))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addContainerGap(110, Short.MAX_VALUE)
                                .addComponent(title, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addGap(170)
                                .addComponent(startGame, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(highScores, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(exit, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addGap(100))
        );
        setLayout(groupLayout);
    }
}
