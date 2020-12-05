package cc.leevi.common.httpproxy.downstream;

import cc.leevi.common.httpproxy.HttpProxyChannelContainer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpProxyClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private Logger logger = LoggerFactory.getLogger(HttpProxyClientHandler.class);

    private Channel upstreamChannel;

    public HttpProxyClientHandler(Channel upstreamChannel) {
        this.upstreamChannel = upstreamChannel;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        msg.retain();
        upstreamChannel.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
