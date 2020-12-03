package cc.leevi.common.httpproxy.upstream;

import cc.leevi.common.httpproxy.downstream.HttpProxyClient;
import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Logger logger = LoggerFactory.getLogger(HttpProxyServerHandler.class);

    private HttpProxyChannelContainer httpProxyChannelContainer;

    public HttpProxyServerHandler(HttpProxyChannelContainer httpProxyChannelContainer) {
        this.httpProxyChannelContainer = httpProxyChannelContainer;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if(httpObject instanceof FullHttpRequest){
            FullHttpRequest httpRequest = (FullHttpRequest)httpObject;
            String uriPlain = httpRequest.uri();

            // TODO: 2020/11/26 解析url，dns之类，拿到host & port，然后连接，使用httpProxyClient
            EventLoop upstreamEventLoop = channelHandlerContext.channel().eventLoop();
            HttpProxyClient httpProxyClient = new HttpProxyClient(upstreamEventLoop, httpRequest, httpProxyChannelContainer);
            HttpResponse httpResponse = httpProxyClient.connectAndWrite();
            channelHandlerContext.channel().writeAndFlush(httpResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
//        super.exceptionCaught(ctx, cause);
    }
}
