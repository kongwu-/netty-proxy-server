package cc.leevi.common.httpproxy.downstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    private Logger logger = LoggerFactory.getLogger(HttpProxyClientHandler.class);

    private HttpProxyChannelContainer httpProxyChannelContainer;

    private DefaultFullHttpResponse httpResponse;

    public HttpProxyClientHandler(HttpProxyChannelContainer httpProxyChannelContainer) {
        this.httpProxyChannelContainer = httpProxyChannelContainer;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httoObject) throws Exception {
        if(httoObject instanceof HttpResponse){
            this.httpResponse = (HttpResponse) httoObject;
        }
        if(httoObject instanceof LastHttpContent){
            logger.info("last content"+((LastHttpContent)httoObject).content().capacity());
            DefaultFullHttpResponse defaultFullHttpResponse =
                    new DefaultFullHttpResponse(httpResponse.protocolVersion(),httpResponse.status(),
                            ((LastHttpContent)httoObject).content().copy());
            defaultFullHttpResponse.headers().set(httpResponse.headers());
            httpProxyChannelContainer.received(channelHandlerContext.channel(),defaultFullHttpResponse);
        }

//            httpProxyChannelContainer.received(channelHandlerContext.channel(),httpResponse);

            //收到数据后输出至httpProxyServer Channel中，考虑下这里怎么绑定，new的形式或者其他，keepAlive如何处理？
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
