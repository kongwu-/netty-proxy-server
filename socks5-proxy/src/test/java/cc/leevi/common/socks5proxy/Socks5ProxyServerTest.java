package cc.leevi.common.socks5proxy;

import com.google.common.net.HostAndPort;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class Socks5ProxyServerTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void startServer() throws IOException {
        Socks5ProxyServer socks5ProxyServer = new Socks5ProxyServer();
        socks5ProxyServer.startServer();
        System.in.read();
    }
}