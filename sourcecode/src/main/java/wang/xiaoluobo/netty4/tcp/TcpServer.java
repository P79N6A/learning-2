package wang.xiaoluobo.netty4.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
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
 * @date 2017/1/13 16:09
 */
@Component
public final class TcpServer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    private ApplicationContext ctx;

    @Autowired
    private EventLoopGroup bossEventLoopGroup;

    @Autowired
    private EventLoopGroup workerEventLoopGroup;

    private int port;

    public void init() {
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

    public void start() {
        try {
            logger.info("Prepare to start TcpServer.");
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline channelPipeline = channel.pipeline();
                            channelPipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            channelPipeline.addLast("encoder", new MyStringEncoder());
                            channelPipeline.addLast("decoder", new MyStringDecoder());
                            channelPipeline.addLast("handler", ctx.getBean("tcpServerHandler", TcpServerHandler.class));
                            channelPipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                            channelPipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                            // 心跳监测 读超时为20s，写超时为20s 全部空闲时间100s
                            channelPipeline.addLast("ping", new IdleStateHandler(20, 20, 100));
                        }

                        ;
                    });

            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
            channelFuture.awaitUninterruptibly();
            if (channelFuture.isSuccess()) {
                logger.info("TcpServer[port is {}] start success.", port);
            }
        } catch (Exception e) {
            logger.info("TcpServer[port is {}] start failed.", port);
            e.printStackTrace();
        } finally {
            if (bossEventLoopGroup != null) {
                bossEventLoopGroup.shutdownGracefully();
            }

            if (workerEventLoopGroup != null) {
                workerEventLoopGroup.shutdownGracefully();
            }
        }
    }

    public void destroy() {
        if (bossEventLoopGroup != null) {
            bossEventLoopGroup.shutdownGracefully();
        }

        if (workerEventLoopGroup != null) {
            workerEventLoopGroup.shutdownGracefully();
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
