package cc.leevi.common.httpproxy.upstream;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LoggingHandler;

public class HttpProxyServerInitializer extends ChannelInitializer {

    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new LoggingHandler());
        p.addLast(new HttpProxyServerConnectHandler());
    }
}
