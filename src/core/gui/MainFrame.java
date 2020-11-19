package core.gui;

import core.client.ClientGame;

import javax.swing.*;
import java.awt.*;
import java.util.function.Predicate;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private ClientGame game;
    public MainFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Legyen Ön Is Milliomos");
        setResizable(false);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(new CardLayout());

        MainMenuPanel menuPanel = new MainMenuPanel(this);
        mainPanel.add(menuPanel, "MENU");

        HighScorePanel scorePanel = new HighScorePanel(this);
        mainPanel.add(scorePanel, "HIGHSCORES");

        GamePanel gamePanel = new GamePanel(this);
        mainPanel.add(gamePanel, "GAME");

        pack();
        setVisible(true);
    }

    public void setGame(ClientGame clientGame) {
        game = clientGame;
    }

    @Override
    public void dispose() {
        if(game != null) {
            game.testSendDisconnect();
        }
        super.dispose();
    }
}
