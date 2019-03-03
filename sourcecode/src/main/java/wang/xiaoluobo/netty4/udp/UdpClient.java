package wang.xiaoluobo.netty4.udp;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import wang.xiaoluobo.netty4.MyMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/14 9:29
 */
public class UdpClient {

    public static void main(String[] args) throws InterruptedException, IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HelloClientInitializer());

            // 连接服务端
            Channel ch = bootstrap.connect("localhost", 9600).sync().channel();

            // 控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (; ; ) {
                String line = in.readLine();
                if (line == null) {
                    continue;
                }

                MyMessage myMessage = new MyMessage();
                myMessage.setMessage(line);
                myMessage.setTargetId("targetId");
                ch.writeAndFlush(JSON.toJSONString(myMessage) + "\r\n");
            }
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }


    public static class HelloClientInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder", new StringEncoder());

            // 客户端的逻辑
            pipeline.addLast("handler", new HelloClientHandler());
        }
    }


//    public static void main(String args[]) {
//        // Client服务启动器 3.x的ClientBootstrap 改为Bootstrap，且构造函数变化很大，这里用无参构造。
//        Bootstrap bootstrap = new Bootstrap();
//        // 指定channel类型
//        bootstrap.channel(NioSocketChannel.class);
//        // 指定Handler
//        bootstrap.handler(new HelloClientHandler());
//        // 指定EventLoopGroup
//        bootstrap.group(new NioEventLoopGroup());
//        // 连接到本地的9600端口的服务端
//        bootstrap.connect(new InetSocketAddress("127.0.0.1" , 9600));
//    }

    public static class HelloClientHandler extends ChannelInboundHandlerAdapter {

        /**
         * 当绑定到服务端的时候触发，打印"Hello world, I'm client."
         *
         * @alia OneCoder
         * @author lihzh
         * @date 2013年11月16日 上午12:50:47
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("Hello world, I'm client.");
        }
    }

}
