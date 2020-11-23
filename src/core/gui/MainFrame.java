package core.gui;

import core.client.ClientGame;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private ClientGame game;
    public MainFrame(String ip, int port) {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Legyen Ön Is Milliomos!");
        setResizable(false);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(new CardLayout());

        MainMenuPanel menuPanel = new MainMenuPanel(ip, port, this);
        mainPanel.add(menuPanel, "MENU");

        pack();
        setVisible(true);
    }

    public void setGame(ClientGame clientGame) {
        game = clientGame;
    }

    @Override
    public void dispose() {
        if(game != null) {
            game.sendDisconnect();
        }
        super.dispose();
    }
}
