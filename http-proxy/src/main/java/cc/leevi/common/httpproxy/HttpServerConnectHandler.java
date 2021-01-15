/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package cc.leevi.common.httpproxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

@ChannelHandler.Sharable
public final class HttpServerConnectHandler extends SimpleChannelInboundHandler<HttpProxyRequestHead> {

    private final Bootstrap b = new Bootstrap();

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final HttpProxyRequestHead requestHead) throws Exception {

        Promise<Channel> promise = ctx.executor().newPromise();
        final Channel inboundChannel = ctx.channel();
        promise.addListener(
                new FutureListener<Channel>() {
                    @Override
                    public void operationComplete(final Future<Channel> future) throws Exception {
                        final Channel outboundChannel = future.getNow();
                        if (future.isSuccess()) {
                            ChannelFuture responseFuture;
                            if("TUNNEL".equals(requestHead.getProxyType())){
                                responseFuture = inboundChannel.writeAndFlush(Unpooled.wrappedBuffer((requestHead.getProtocolVersion() + " 200 Connection Established\r\n\r\n").getBytes()));
                            }else if("WEB".equals(requestHead.getProxyType())){
                                responseFuture = outboundChannel.writeAndFlush(requestHead.getByteBuf());
                            }else{
                                HttpServerUtils.closeOnFlush(inboundChannel);
                                return;
                            }
                            responseFuture.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture channelFuture) {
                                    ctx.pipeline().remove(HttpServerConnectHandler.this);
                                    outboundChannel.pipeline().addLast(new RelayHandler(inboundChannel));
                                    ctx.pipeline().addLast(new RelayHandler(outboundChannel));
                                }
                            });
                        } else {
                            HttpServerUtils.closeOnFlush(inboundChannel);
                        }
                    }
                });

        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));

        b.connect(requestHead.getHost(), requestHead.getPort()).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    // Connection established use handler provided results
                } else {
                    // Close the connection if the connection attempt has failed.
                    HttpServerUtils.closeOnFlush(inboundChannel);
                }
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        HttpServerUtils.closeOnFlush(ctx.channel());
    }
}
