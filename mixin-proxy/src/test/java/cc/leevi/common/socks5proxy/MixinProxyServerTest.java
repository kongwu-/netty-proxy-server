package cc.leevi.common.socks5proxy;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class MixinProxyServerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void startServer() throws IOException {
        MixinProxyServer mixinProxyServer = new MixinProxyServer();
        mixinProxyServer.startServer();
        System.in.read();
    }
}