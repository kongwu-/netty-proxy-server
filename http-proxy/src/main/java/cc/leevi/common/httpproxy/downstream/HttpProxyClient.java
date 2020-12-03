package cc.leevi.common.httpproxy.downstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import com.google.common.net.HostAndPort;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class HttpProxyClient {

    private Logger logger = LoggerFactory.getLogger(HttpProxyClient.class);

    private Bootstrap clientBootstrap;

    private volatile int status = 0;

    private String host;

    private int port;

    private EventLoop upstreamEventLoop;

    private FullHttpRequest httpRequest;

    private HttpProxyChannelContainer httpProxyChannelContainer;

    private NioEventLoopGroup clientEventLoopGroup;

    public HttpProxyClient(EventLoop upstreamEventLoop, FullHttpRequest httpRequest, HttpProxyChannelContainer httpProxyChannelContainer) {
        this.upstreamEventLoop = upstreamEventLoop;
        this.httpRequest = httpRequest;
        this.httpProxyChannelContainer = httpProxyChannelContainer;
    }

    private void parseURI(FullHttpRequest httpRequest) throws URISyntaxException {
        String uriPlain = httpRequest.uri();
//        httpRequest.
        List<String> hosts = httpRequest.headers().getAll(HttpHeaderNames.HOST);
//        if (!hosts.isEmpty()) {
//            String hostAndPort = hosts.get(0);
//            HostAndPort parsedHostAndPort = HostAndPort.fromString(hostAndPort);
//            return parsedHostAndPort.getHost();
//        } else {
//            return null;
//        }
//
//        int port = uri.getPort();
//        if(port <= 0){
//            String scheme = uri.getScheme();
//            if("http".equalsIgnoreCase(scheme)){
//                port = 80;
//            }
//            if("https".equalsIgnoreCase(scheme)){
//                port = 443;
//            }
//        }
//        this.port = port;
//
//        URI uri = new URI(uriPlain);
//        this.host = uri.getHost();
//        resolvePort(uri);

    }

    private void resolvePort(URI uri) {
        int port = uri.getPort();
        if(port <= 0){
            String scheme = uri.getScheme();
            if("http".equalsIgnoreCase(scheme)){
                port = 80;
            }
            if("https".equalsIgnoreCase(scheme)){
                port = 443;
            }
        }
        this.port = port;
    }


    public HttpResponse connectAndWrite(){
        try {
            parseURI(httpRequest);

            clientEventLoopGroup = new NioEventLoopGroup(1);

            clientBootstrap = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .handler(new HttpProxyClientInitializer(httpProxyChannelContainer))
                    .group(clientEventLoopGroup);

            try {
                Channel channel = clientBootstrap.connect(host, port).sync().channel();
                ChannelFuture<HttpResponse> channelFuture = new ChannelFuture();
                httpProxyChannelContainer.addFuture(channel,channelFuture);
                channel.writeAndFlush(httpRequest.copy()).sync();
                HttpResponse httpResponse = channelFuture.get();
//                clientEventLoopGroup.shutdownGracefully();
                return httpResponse;
            } catch (Exception e) {
                logger.error("Channel get response failed!",e);
            }

        } catch (URISyntaxException e) {
            logger.error("URI syntax illegal: "+ httpRequest.uri(),e);
        }
        logger.info("Proxy Client starting...");

        return null;

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
