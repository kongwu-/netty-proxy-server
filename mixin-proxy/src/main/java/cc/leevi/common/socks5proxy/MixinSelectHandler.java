package cc.leevi.common.socks5proxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import io.netty.handler.codec.socksx.SocksVersion;

public class MixinSelectHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        final int readerIndex = msg.readerIndex();
        if (msg.writerIndex() == readerIndex) {
            return;
        }

        ChannelPipeline p = ctx.pipeline();
        final byte versionVal = msg.getByte(readerIndex);

        SocksVersion version = SocksVersion.valueOf(versionVal);
        if(version.equals(SocksVersion.SOCKS4a) || version.equals(SocksVersion.SOCKS5)){
            //socks proxy
            p.addLast(new SocksPortUnificationServerHandler(),
                    SocksServerHandler.INSTANCE).remove(this);
        }else{
            //http/tunnel proxy
            p.addLast(new HttpServerHeadDecoder()).remove(this);
        }
        msg.retain();
        ctx.fireChannelRead(msg);
    }
}
