package core.highscore;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Egy felhasználó eredményének tárolója.
 */
public class HighScore implements Serializable, Comparable<HighScore> {
    private String playerName;
    private String playerScore;
    private LocalDate gameDate;

    /**
     * Új felhasználói eredmény létrehozása
     * @param name Felhasználó neve
     * @param score Elért pontszám
     * @param date Dátum
     */
    public HighScore(String name, String score, LocalDate date) {
        playerName = name;
        playerScore = score;
        gameDate = date;
    }

    /**
     * @return Felhasználó neve
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return Felhasználó elért pontszáma
     */
    public String getPlayerScore() {
        return playerScore;
    }

    /**
     * @return Játék dátuma
     */
    public LocalDate getDate() {
        return gameDate;
    }

    @Override
    public int compareTo(HighScore o) {
        int oPlayer = Integer.parseInt((o.playerScore).replace(".", "").replace("Ft", ""));
        int player = Integer.parseInt((playerScore).replace(".", "").replace("Ft", ""));
        return Integer.compare(oPlayer, player);
    }
}
