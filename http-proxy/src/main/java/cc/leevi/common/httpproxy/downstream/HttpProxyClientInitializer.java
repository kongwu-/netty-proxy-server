package cc.leevi.common.httpproxy.downstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

public class HttpProxyClientInitializer extends ChannelInitializer {

    private HttpProxyChannelContainer httpProxyChannelContainer;

    public HttpProxyClientInitializer(HttpProxyChannelContainer httpProxyChannelContainer) {
        this.httpProxyChannelContainer = httpProxyChannelContainer;
    }

    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline p = channel.pipeline();
        p.addLast(new HttpRequestEncoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseDecoder());
//         Remove the following line if you don't want automatic content compression.
//        p.addLast(new HttpContentCompressor());
        p.addLast(new HttpProxyClientHandler(httpProxyChannelContainer));
    }
}
