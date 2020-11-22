package core.highscore;

import java.io.Serializable;
import java.time.LocalDate;

public class HighScore implements Serializable, Comparable<HighScore> {
    private String playerName;
    private String playerScore;
    private LocalDate gameDate;

    public HighScore(String name, String score, LocalDate date) {
        playerName = name;
        playerScore = score;
        gameDate = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerScore() {
        return playerScore;
    }

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
