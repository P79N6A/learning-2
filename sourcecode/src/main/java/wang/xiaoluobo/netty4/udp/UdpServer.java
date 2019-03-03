package wang.xiaoluobo.netty4.udp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import wang.xiaoluobo.netty4.MyStringDecoder;
import wang.xiaoluobo.netty4.MyStringEncoder;

/**
 * @author WangYandong
 * @email wangyd1005sy@163.com
 * @date 2017/1/14 15:11
 */
@Component
public class UdpServer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(UdpServer.class);

    private ApplicationContext ctx;

    @Autowired
    private EventLoopGroup eventLoopGroup;

    private int port;

    public void init(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public void start(){
        try {
            logger.info("Prepare to start UdpServer.");
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline channelPipeline = channel.pipeline();
                            channelPipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            channelPipeline.addLast("encoder", new MyStringEncoder());
                            channelPipeline.addLast("decoder", new MyStringDecoder());
                            channelPipeline.addLast("handler", ctx.getBean("udpServerHandler", UdpServerHandler.class));
                        };
                    });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if (channelFuture.isSuccess()) {
                logger.info("UdpServer[port is {}] channelFuture is success.", port);
            }
            channelFuture.channel().closeFuture().sync();
            channelFuture.awaitUninterruptibly();
            logger.info("UdpServer[port is {}] start success.", port);
        } catch (Exception e) {
            logger.info("TcpServer[port is {}] start failed.", port);
            e.printStackTrace();
        }finally {
            if(eventLoopGroup != null){
                eventLoopGroup.shutdownGracefully();
            }
        }
    }

    public void destroy(){
        if(eventLoopGroup != null){
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
