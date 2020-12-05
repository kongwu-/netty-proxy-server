package cc.leevi.common.httpproxy.upstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LoggingHandler;

public class HttpProxyServerInitializer extends ChannelInitializer {
    private final HttpProxyChannelContainer httpProxyChannelContainer;

    public HttpProxyServerInitializer(HttpProxyChannelContainer httpProxyChannelContainer) {
        this.httpProxyChannelContainer = httpProxyChannelContainer;
    }

    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new LoggingHandler());
        p.addLast(new HttpProxyServerConnectHandler());
    }
}
