package wang.xiaoluobo.netty4.udp;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/13 16:25
 */
@Component
@ChannelHandler.Sharable
public class UdpServerHandler extends SimpleChannelInboundHandler {

    private static final Logger logger = LoggerFactory.getLogger(UdpServerHandler.class);

    /**
     * 每当接到客户端消息时，就会执行此方法
     *
     * @param ctx
     * @param o
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        if(o == null){
            return;
        }

        logger.info("UdpServer recevie channelId[{}] message[{}].", ctx.channel().id().asLongText(), JSON.toJSONString(o));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // asLongText-->c03fd5fffef7ae0e-00001a84-00000001-d884ffc4e487708a-24d23545
        // asShortText-->24d23545
        logger.info("UdpServer channelId-->{}, RamoteAddress-->{}, HostName-->{} active", ctx.channel().id().asLongText(), ctx.channel().remoteAddress(), InetAddress.getLocalHost().getHostName());

        super.channelActive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        String channelId = ctx.channel().id().asLongText();
        logger.info("UdpServer channelId[{}] exception caught-->{}", channelId, cause.getMessage());

        cause.printStackTrace();
        ctx.close().addListener(ChannelFutureListener.CLOSE);
    }
}
