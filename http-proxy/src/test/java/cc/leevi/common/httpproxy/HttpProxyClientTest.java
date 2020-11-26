package cc.leevi.common.httpproxy;

import cc.leevi.common.httpproxy.upstream.HttpProxyServer;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HttpProxyClientTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void startServer() throws IOException {
        HttpProxyServer httpProxyServer = new HttpProxyServer();
        httpProxyServer.startServer();
        System.in.read();
    }
}
