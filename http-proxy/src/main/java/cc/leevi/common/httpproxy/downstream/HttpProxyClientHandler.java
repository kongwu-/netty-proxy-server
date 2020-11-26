package cc.leevi.common.httpproxy.downstream;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

public class HttpProxyClientHandler extends SimpleChannelInboundHandler<HttpObject> {

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        if(httpObject instanceof HttpResponse){
            HttpRequest httpRequest = (HttpRequest)httpObject;
            String uri = httpRequest.uri();
            System.out.println(uri);
            //收到数据后输出至httpProxyServer Channel中，考虑下这里怎么绑定，new的形式或者其他，keepAlive如何处理？
        }
    }
}
