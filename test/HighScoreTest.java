import core.highscore.HighScore;
import core.highscore.HighScoreManager;
import core.question.QuestionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class HighScoreTest {

    @Before
    public void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = QuestionManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testCreate() {
        Assert.assertNull(HighScoreManager.getInstance());
        HighScoreManager.createInstance("test.txt");
        Assert.assertNotNull(HighScoreManager.getInstance());
    }

    @Test
    public void testCompare() {
        HighScore h1 = new HighScore("h1", "40.000.000", LocalDate.now());
        HighScore h2 = new HighScore("h2", "20.000.000", LocalDate.now());
        Assert.assertTrue(h1.compareTo(h2) < 1);
    }

    @Test
    public void testInsert() {
        HighScoreManager.createInstance("test.txt");
        int before = HighScoreManager.getInstance().getHighScores().size();
        HighScoreManager.getInstance().add(new HighScore("Test Elek", "5.000", LocalDate.now()));
        int after = HighScoreManager.getInstance().getHighScores().size();
        Assert.assertTrue(before < after);
    }
}
