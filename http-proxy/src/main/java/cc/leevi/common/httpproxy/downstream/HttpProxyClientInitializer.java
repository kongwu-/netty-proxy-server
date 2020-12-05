package cc.leevi.common.httpproxy.downstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

public class HttpProxyClientInitializer extends ChannelInitializer {

    private Channel upstreamChannel;

    public HttpProxyClientInitializer(Channel upstreamChannel) {
        this.upstreamChannel = upstreamChannel;
    }

    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new HttpProxyClientHandler(upstreamChannel));
    }
}
