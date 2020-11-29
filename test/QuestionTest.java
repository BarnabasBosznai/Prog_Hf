import core.question.Question;
import core.question.QuestionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.lang.reflect.Field;

public class QuestionTest {

    @Before
    public void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = QuestionManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testCreate() throws Exception {
        Assert.assertNull(QuestionManager.getInstance());
        QuestionManager.createInstance("questions.json");
        Assert.assertNotNull(QuestionManager.getInstance());
    }

    @Test(expected = FileNotFoundException.class)
    public void testFileNotFound() throws Exception {
        QuestionManager.createInstance("test.json");
    }

    @Test
    public void testGetQuestion() throws Exception {
        QuestionManager.createInstance("questions.json");
        for(int i = 1; i < 16; i++) {
            Question q = QuestionManager.getInstance().getQuestionByDifficulty(i);
            Assert.assertEquals(q.getDifficulty(), i);
        }
    }
}
