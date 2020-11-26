package cc.leevi.common.httpproxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyServer {

    private Logger logger = LoggerFactory.getLogger(HttpProxyServer.class);

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup eventLoopGroup;

    private volatile int status = 0;

    public void startServer(){
        logger.info("Proxy Server starting...");

        eventLoopGroup = new DefaultEventLoopGroup(4);

        serverBootstrap = new ServerBootstrap();
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.handler(new HttpProxyServerInitializer());
        serverBootstrap.group()

    }

    public void shutdown(){
        logger.info("Proxy Server shutting down...");

    }
}
