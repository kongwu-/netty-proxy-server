package cc.leevi.common.httpproxy;

import com.google.common.net.HostAndPort;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.AppendableCharSequence;

import java.net.URL;

public class HttpServerHeadDecoder extends SimpleChannelInboundHandler<ByteBuf> {

    private HttpServerHeadDecoder.HeadLineByteProcessor headLineByteProcessor = new HttpServerHeadDecoder.HeadLineByteProcessor();

    private

    class HeadLineByteProcessor implements ByteProcessor{
        private AppendableCharSequence seq;

        public HeadLineByteProcessor() {
            this.seq = new AppendableCharSequence(4096);
        }

        public AppendableCharSequence parse(ByteBuf buffer) {
            seq.reset();
            int i = buffer.forEachByte(this);
            if (i == -1) {
                return null;
            }
            buffer.readerIndex(i + 1);
            return seq;
        }

        @Override
        public boolean process(byte value) throws Exception {
            char nextByte = (char) (value & 0xFF);
            if (nextByte == HttpConstants.LF) {
                int len = seq.length();
                if (len >= 1 && seq.charAtUnsafe(len - 1) == HttpConstants.CR) {
                    seq.append(nextByte);
                }
                return false;
            }
            //continue loop byte
            seq.append(nextByte);
            return true;
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        AppendableCharSequence seq = headLineByteProcessor.parse(in);
        if(seq.charAt(seq.length()-1) == HttpConstants.LF){
            HttpProxyRequestHead httpProxyRequestHead;
            String[] splitInitialLine = splitInitialLine(seq);
            String method = splitInitialLine[0];
            String uri = splitInitialLine[1];
            String protocolVersion = splitInitialLine[2];
            String host;
            int port;
            if(HttpMethod.CONNECT.name().equals(method)){
                //https tunnel proxy
                HostAndPort hostAndPort = HostAndPort.fromString(uri);
                host = hostAndPort.getHost();
                port = hostAndPort.getPort();

                httpProxyRequestHead = new HttpProxyRequestHead(host, port, "TUNNEL",protocolVersion,null);
            }else{
                //http proxy
                URL url = new URL(uri);
                host = url.getHost();
                port = url.getPort();
                if(port == -1){
                    port = 80;
                }

                httpProxyRequestHead = new HttpProxyRequestHead(host, port, protocolVersion,"WEB",in.resetReaderIndex());
            }
            ctx.pipeline().addLast(new HttpServerConnectHandler()).remove(this);
            ctx.fireChannelRead(httpProxyRequestHead);
        }
    }

    private static String[] splitInitialLine(AppendableCharSequence sb) {
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;
        int cStart;
        int cEnd;

        aStart = findNonSPLenient(sb, 0);
        aEnd = findSPLenient(sb, aStart);

        bStart = findNonSPLenient(sb, aEnd);
        bEnd = findSPLenient(sb, bStart);

        cStart = findNonSPLenient(sb, bEnd);
        cEnd = findEndOfString(sb);

        return new String[] {
                sb.subStringUnsafe(aStart, aEnd),
                sb.subStringUnsafe(bStart, bEnd),
                cStart < cEnd? sb.subStringUnsafe(cStart, cEnd) : "" };
    }

    private static int findNonSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            // See https://tools.ietf.org/html/rfc7230#section-3.5
            if (isSPLenient(c)) {
                continue;
            }
            if (Character.isWhitespace(c)) {
                // Any other whitespace delimiter is invalid
                throw new IllegalArgumentException("Invalid separator");
            }
            return result;
        }
        return sb.length();
    }

    private static int findSPLenient(AppendableCharSequence sb, int offset) {
        for (int result = offset; result < sb.length(); ++result) {
            if (isSPLenient(sb.charAtUnsafe(result))) {
                return result;
            }
        }
        return sb.length();
    }

    private static boolean isSPLenient(char c) {
        // See https://tools.ietf.org/html/rfc7230#section-3.5
        return c == ' ' || c == (char) 0x09 || c == (char) 0x0B || c == (char) 0x0C || c == (char) 0x0D;
    }

    private static int findNonWhitespace(AppendableCharSequence sb, int offset, boolean validateOWS) {
        for (int result = offset; result < sb.length(); ++result) {
            char c = sb.charAtUnsafe(result);
            if (!Character.isWhitespace(c)) {
                return result;
            } else if (validateOWS && !isOWS(c)) {
                // Only OWS is supported for whitespace
                throw new IllegalArgumentException("Invalid separator, only a single space or horizontal tab allowed," +
                        " but received a '" + c + "'");
            }
        }
        return sb.length();
    }

    private static int findEndOfString(AppendableCharSequence sb) {
        for (int result = sb.length() - 1; result > 0; --result) {
            if (!Character.isWhitespace(sb.charAtUnsafe(result))) {
                return result + 1;
            }
        }
        return 0;
    }

    private static boolean isOWS(char ch) {
        return ch == ' ' || ch == (char) 0x09;
    }

}
