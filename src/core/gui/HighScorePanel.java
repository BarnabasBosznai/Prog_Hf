package core.gui;

import core.client.ClientScoreboard;
import core.highscore.HighScore;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A program felhasználóinak elért eredményét mutató JPanel.
 */
public class HighScorePanel extends JPanel {
    private JTable table;
    private TableModel model;

    /**
     * Csatlakozik a szerverhez és felépíti a panelt.
     * @param frame A program frame-je
     * @return Igazzal tér vissza, ha sikerült lekérni az adatokat és felépíteni a panelt, különben hamis
     */
    public boolean init(MainFrame frame) {
        List<HighScore> list = ClientScoreboard.getScoreboardData();
        if (list != null) {
            setupUI(frame);
            model.setModelData(list);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Felépíti a panelt.
     * @param frame A program frame-je
     */
    private void setupUI(MainFrame frame) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setEnabled(false);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton backButton = new JButton("Vissza");
        backButton.setPreferredSize(new Dimension(60, 25));
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 22));
        backButton.addActionListener((ActionListener) -> {
            CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
            cl.removeLayoutComponent(this);

            cl.show(frame.getContentPane(), "MENU");
        });

        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(70)
                                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 660, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(30)
                                                .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(70, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGap(30)
                                .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE)
                                .addGap(20)
                                .addComponent(backButton, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(30, Short.MAX_VALUE))
        );
        model = new TableModel();
        table = new JTable(model);
        table.setShowHorizontalLines(false);
        table.setShowGrid(false);
        table.setShowVerticalLines(false);
        table.setFont(new Font("Tahome", Font.PLAIN, 16));
        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setFont(new Font("Tahome", Font.PLAIN, 20));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(225);
        table.getColumnModel().getColumn(2).setPreferredWidth(225);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        table.setRowHeight(25);

        scrollPane.setViewportView(table);
        setLayout(groupLayout);
    }

    /**
     * Eredmények tábla szerkezete.
     */
    private class TableModel extends AbstractTableModel {

        private List<HighScore> scores = new ArrayList<>();

        /**
         * Beállítja a táblázat adatait.
         * @param highScores Eredmények lista
         */
        public void setModelData(List<HighScore> highScores) {
            scores = highScores;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return scores.size();
        }

        @Override
        public int getColumnCount() {
            return 4;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            HighScore score = scores.get(rowIndex);
            switch(columnIndex) {
                case 0: return rowIndex + 1;
                case 1: return score.getPlayerName();
                case 2: return (score.getPlayerScore() + " Ft");
                default: return score.getDate();
            }
        }

        @Override
        public String getColumnName(int column) {
            switch(column) {
                case 0: return " ";
                case 1: return "Játékos név";
                case 2: return "Nyeremény";
                default: return "Dátum";
            }
        }

    }

}
