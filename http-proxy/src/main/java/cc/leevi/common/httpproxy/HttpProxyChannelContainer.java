package cc.leevi.common.httpproxy;

import cc.leevi.common.httpproxy.downstream.ChannelFuture;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpProxyChannelContainer {
    private Map<Channel, ChannelFuture> channelFutures = new ConcurrentHashMap<>();

    public ChannelFuture getFuture(Channel channel){
        return channelFutures.get(channel);
    }

    public void received(Channel channel,Object response){
        ChannelFuture future = getFuture(channel);
        future.received(response);
    }

    public void addFuture(Channel channel, ChannelFuture channelFuture) {
        channelFutures.put(channel,channelFuture);
    }
}
