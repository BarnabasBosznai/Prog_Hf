import core.message.Message;
import core.message.MessageManager;
import core.message.MessageType;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.EnumSet;

public class MessageTest {

    @Test
    public void testMessageSendingToFile() throws IOException {

        String messageData = "Test message";
        Message message = new Message(EnumSet.of(MessageType.QUESTION), messageData);
        MessageManager fromF = new MessageManager(new ObjectOutputStream(new FileOutputStream("messagetest.txt")), null);
        Assert.assertTrue(fromF.sendMessage(message));

        MessageManager toF = new MessageManager(null, new ObjectInputStream(new FileInputStream("messagetest.txt")));
        Message receivedMessage = toF.receiveMessage();
        Assert.assertNotNull(receivedMessage);
        Assert.assertEquals(receivedMessage.getData(), messageData);
    }
}
