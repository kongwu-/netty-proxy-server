package cc.leevi.common.httpproxy;

import io.netty.buffer.ByteBuf;

public class HttpProxyRequestHead {
    private String host;
    private int port;
    private String proxyType;//tunnel or web
    private String protocolVersion;

    private ByteBuf byteBuf;

    public HttpProxyRequestHead(String host, int port, String proxyType, String protocolVersion, ByteBuf byteBuf) {
        this.host = host;
        this.port = port;
        this.proxyType = proxyType;
        this.protocolVersion = protocolVersion;
        this.byteBuf = byteBuf;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getProxyType() {
        return proxyType;
    }

    public void setProxyType(String proxyType) {
        this.proxyType = proxyType;
    }

    public ByteBuf getByteBuf() {
        return byteBuf;
    }

    public void setByteBuf(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
