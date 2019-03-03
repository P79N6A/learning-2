package wang.xiaoluobo.netty4;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/14 11:26
 */
public class MyStringEncoder extends MessageToMessageEncoder<CharSequence> {
    private final Charset charset;

    public MyStringEncoder() {
        this(Charset.forName("UTF-8"));
    }

    public MyStringEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        } else {
            this.charset = charset;
        }
    }

    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if (msg.length() != 0) {
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), this.charset));
        }
    }

}
