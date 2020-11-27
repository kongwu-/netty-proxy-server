package cc.leevi.common.httpproxy.downstream;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class HttpProxyClient {

    private Logger logger = LoggerFactory.getLogger(HttpProxyClient.class);

    private Bootstrap clientBootstrap;

    private volatile int status = 0;

    private String host;

    private int port;

    private EventLoop upstreamEventLoop;

    private FullHttpRequest httpRequest;

    public HttpProxyClient(EventLoop upstreamEventLoop, FullHttpRequest httpRequest) {
        this.upstreamEventLoop = upstreamEventLoop;
        this.httpRequest = httpRequest;
    }

    private void parseURI(FullHttpRequest httpRequest) throws URISyntaxException {
        String uriPlain = httpRequest.uri();
        URI uri = new URI(uriPlain);
        this.host = uri.getHost();
        this.port = uri.getPort();
    }


    public void connectAndWrite(){
        try {
            parseURI(httpRequest);

            clientBootstrap = new Bootstrap()
                    .channel(NioServerSocketChannel.class)
                    .handler(new HttpProxyClientInitializer())
                    .group(upstreamEventLoop);
            Channel channel = clientBootstrap.connect(host, port).syncUninterruptibly().channel();
            

        } catch (URISyntaxException e) {
            logger.error("URI syntax illegal: "+ httpRequest.uri(),e);
        }
        logger.info("Proxy Client starting...");



//        httpRequest.


//        channel.writeAndFlush()


    }

//    public void shutdown(){
//        logger.info("Proxy Client shutting down...");
//        acceptorChannel.close().syncUninterruptibly();
//        clientEventLoopGroup.shutdownGracefully().syncUninterruptibly();
//        logger.info("shutdown completed!");
//    }
}
