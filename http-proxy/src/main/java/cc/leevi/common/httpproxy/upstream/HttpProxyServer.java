package cc.leevi.common.httpproxy.upstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyServer {

    private Logger logger = LoggerFactory.getLogger(HttpProxyServer.class);

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup serverEventLoopGroup;

    private volatile int status = 0;

    private Channel acceptorChannel;

    private HttpProxyChannelContainer httpProxyChannelContainer;

    public void startServer(){
        logger.info("Proxy Server starting...");

        httpProxyChannelContainer = new HttpProxyChannelContainer();

        serverEventLoopGroup = new NioEventLoopGroup(4);

        serverBootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpProxyServerInitializer(httpProxyChannelContainer))
                .group(serverEventLoopGroup);
        acceptorChannel = serverBootstrap.bind(8080).syncUninterruptibly().channel();
    }

    public void shutdown(){
        logger.info("Proxy Server shutting down...");
        acceptorChannel.close().syncUninterruptibly();
        serverEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        logger.info("shutdown completed!");
    }
}
