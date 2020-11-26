package cc.leevi.common.httpproxy.downstream;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyClient {

    private Logger logger = LoggerFactory.getLogger(HttpProxyClient.class);

    private Bootstrap clientBootstrap;

    private EventLoopGroup clientEventLoopGroup;

    private volatile int status = 0;

    private Channel acceptorChannel;


    public void startServer(){
        logger.info("Proxy Client starting...");

        clientEventLoopGroup = new NioEventLoopGroup(4);

        clientBootstrap = new Bootstrap()
                .channel(NioServerSocketChannel.class)
                .handler(new HttpProxyClientInitializer())
                .group(clientEventLoopGroup);
        acceptorChannel = clientBootstrap.connect();
    }

    public void shutdown(){
        logger.info("Proxy Client shutting down...");
        acceptorChannel.close().syncUninterruptibly();
        clientEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        logger.info("shutdown completed!");
    }
}
