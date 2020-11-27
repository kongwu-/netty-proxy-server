package cc.leevi.common.httpproxy.upstream;

import cc.leevi.common.httpproxy.downstream.HttpProxyClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class HttpProxyServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Logger logger = LoggerFactory.getLogger(HttpProxyServerHandler.class);

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if(httpObject instanceof FullHttpMessage){
            FullHttpRequest httpRequest = (FullHttpRequest)httpObject;
            String uriPlain = httpRequest.uri();
            // TODO: 2020/11/26 解析url，dns之类，拿到host & port，然后连接，使用httpProxyClient
            EventLoop upstreamEventLoop = channelHandlerContext.channel().eventLoop();
            HttpProxyClient httpProxyClient = new HttpProxyClient(upstreamEventLoop, httpRequest);
            httpProxyClient.connectAndWrite();
        }
    }
}
