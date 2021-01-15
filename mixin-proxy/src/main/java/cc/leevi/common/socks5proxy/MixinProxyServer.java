package cc.leevi.common.socks5proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MixinProxyServer {
    private Logger logger = LoggerFactory.getLogger(MixinProxyServer.class);

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup serverEventLoopGroup;

    private Channel acceptorChannel;

    public void startServer(){
        logger.info("Proxy Server starting...");

        serverEventLoopGroup = new NioEventLoopGroup(4);

        serverBootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .childHandler(new MixinServerInitializer())
                .group(serverEventLoopGroup);
        acceptorChannel = serverBootstrap.bind(8065).syncUninterruptibly().channel();
    }

    public void shutdown(){
        logger.info("Proxy Server shutting down...");
        acceptorChannel.close().syncUninterruptibly();
        serverEventLoopGroup.shutdownGracefully().syncUninterruptibly();
        logger.info("shutdown completed!");
    }
}
