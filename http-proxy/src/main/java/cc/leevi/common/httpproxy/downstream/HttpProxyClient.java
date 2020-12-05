package cc.leevi.common.httpproxy.downstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyClient {

    private Logger logger = LoggerFactory.getLogger(HttpProxyClient.class);

    private Bootstrap clientBootstrap;

    private volatile int status = 0;

    private String host;

    private int port;

    private String protocolVersion;

    private EventLoop upstreamEventLoop;

    private HttpProxyChannelContainer httpProxyChannelContainer;

    private NioEventLoopGroup clientEventLoopGroup;

    private Channel channel;
    private Channel upstreamChannel;

    public HttpProxyClient(String host, int port, String protocolVersion) {
        this.host = host;
        this.port = port;
        this.protocolVersion = protocolVersion;
    }

    public void prepareProxyClient(Channel upstreamChannel) throws InterruptedException {
        clientEventLoopGroup = new NioEventLoopGroup(1);
        this.upstreamChannel = upstreamChannel;
        clientBootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .handler(new HttpProxyClientInitializer(upstreamChannel))
                .group(clientEventLoopGroup);
        this.channel = clientBootstrap.connect(host, port).sync().channel();
    }

    public void write(ByteBuf msg) throws InterruptedException {
        if(channel.isActive()){
            ReferenceCountUtil.retain(msg);
            this.channel.writeAndFlush(msg).sync();
        }
    }

    public void connectTunnel() {
        logger.info("Proxy Client starting...");
        upstreamChannel.writeAndFlush(Unpooled.wrappedBuffer("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes()));
    }
}
