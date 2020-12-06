package cc.leevi.common.httpproxy.downstream;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;

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
