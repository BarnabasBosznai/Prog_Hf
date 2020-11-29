import core.server.Server;
import org.junit.Assert;
import org.junit.Test;

public class ServerTest {

    @Test
    public void testNoFileFound() {
        Server server = new Server();
        Assert.assertFalse(server.init("nofile.json"));
    }

    @Test
    public void testStartServer() {
        Server server = new Server();
        Assert.assertTrue(server.init("server_config.json"));
    }
}
