package core.gui;

import core.client.ClientGame;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    ClientGame game;
    public MainFrame() {
        game = new ClientGame();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Legyen Ön Is Milliomos");
        setResizable(false);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(new CardLayout());

        MainMenuPanel menuPanel = new MainMenuPanel(this);
        mainPanel.add(menuPanel, "MENU");

        HighScorePanel scorePanel = new HighScorePanel(this, game);
        mainPanel.add(scorePanel, "HIGHSCORES");

        GamePanel gamePanel = new GamePanel(this, game);
        mainPanel.add(gamePanel, "GAME");

        pack();
        setVisible(true);
    }

    @Override
    public void dispose() {
        game.closed();
        super.dispose();
    }
}
