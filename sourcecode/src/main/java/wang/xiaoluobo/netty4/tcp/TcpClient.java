package wang.xiaoluobo.netty4.tcp;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import wang.xiaoluobo.netty4.MyMessage;
import wang.xiaoluobo.netty4.MyStringDecoder;
import wang.xiaoluobo.netty4.MyStringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/14 9:29
 */
public class TcpClient {

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HelloClientInitializer());

            Channel ch = bootstrap.connect("localhost", 9600).sync().channel();

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String line = in.readLine();
                if (line == null) {
                    continue;
                }

                MyMessage myMessage = new MyMessage();
                myMessage.setMessage(line);
                myMessage.setTargetId("targetId");
                ChannelFuture channelFuture = ch.writeAndFlush(JSON.toJSONString(myMessage) + "\r\n");
                boolean isSuccess = channelFuture.isSuccess();
                boolean isDone = channelFuture.isDone();
            }
        } finally {
            group.shutdownGracefully();
        }
    }


    public static class HelloClientInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
            pipeline.addLast("decoder", new MyStringDecoder());
            pipeline.addLast("encoder", new MyStringEncoder());
            pipeline.addLast("handler", new HelloClientHandler());
        }
    }

    public static class HelloClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush("#\n");
        }
    }
}
