package core.gui;

import javax.swing.*;
import java.awt.*;

/**
 * A program kerete (frame-je)
 */
public class MainFrame extends JFrame {

    private JPanel mainPanel;

    /**
     * A program keretének inicializálása.
     */
    public MainFrame() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Legyen Ön Is Milliomos!");
        setResizable(false);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(new CardLayout());

        MainMenuPanel menuPanel = new MainMenuPanel(this);
        mainPanel.add(menuPanel, "MENU");

        pack();
        setVisible(true);
    }
}
