package core.highscore;

import java.io.Serializable;
import java.time.LocalDate;

public class HighScore implements Serializable, Comparable<HighScore> {
    private String playerName;
    private int playerScore;
    private LocalDate gameDate;

    public HighScore(String name, int score, LocalDate date) {
        playerName = name;
        playerScore = score;
        gameDate = date;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public LocalDate getDate() {
        return gameDate;
    }

    @Override
    public int compareTo(HighScore o) {
        return Integer.compare(o.playerScore, playerScore);
    }
}
