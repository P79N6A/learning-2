package wang.xiaoluobo.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.Charset;
import java.util.List;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/14 11:32
 */
public class MyStringDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final Charset charset;

    public MyStringDecoder() {
        this(Charset.forName("UTF-8"));
    }

    public MyStringDecoder(Charset charset) {
        if(charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        out.add(msg.toString(this.charset));
    }
}
