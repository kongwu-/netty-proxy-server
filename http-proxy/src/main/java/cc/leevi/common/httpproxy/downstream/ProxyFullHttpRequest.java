package cc.leevi.common.httpproxy.downstream;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;

public class ProxyFullHttpRequest extends DefaultHttpRequest {

    private String scheme;
    private String host;
    private int port;
    private ByteBuf content;

    private DefaultFullHttpRequest nativeRequest;

    public ProxyFullHttpRequest(HttpVersion httpVersion, HttpMethod method, String uri) {
        super(httpVersion, method, uri);
    }



}
