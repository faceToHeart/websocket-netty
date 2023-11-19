package com.lwt.learn.websocketnetty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/10 0:23
 * 总体是这篇文章(https://blog.csdn.net/qq_41889508/article/details/120233844)中的思路，解决了文章中未提供的集群下的问题，毕竟redis不能序列化channel
 * 这里的init()方法，用 @PostConstruct 调用的话，websocket 连接能够建立，但是其他的bean注入不了，例如redis，rabbitMq类似的Configuration，
 * 所以最后采用继承CommandLineRunner接口的方式，spring 的bean创建顺序可以看 https://zhuanlan.zhihu.com/p/266126121
 */
@Slf4j
@Component
public class WebsocketServer implements CommandLineRunner {
    @Autowired
    private WebsocketChannelInitializer websocketChannelInitializer;

    @Value("${websocket.port}")
    private Integer port;

    public void init() throws InterruptedException {
        //bossGroup连接线程组，主要负责接受客户端连接，一般一个线程足矣
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //workerGroup工作线程组，主要负责网络IO读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            //启动辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //bootstrap绑定两个线程组
            serverBootstrap.group(bossGroup, workerGroup);
            //设置通道为NioChannel
            serverBootstrap.channel(NioServerSocketChannel.class);
            //可以对入站\出站事件进行日志记录，从而方便我们进行问题排查。
            serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));
            //设置自定义的通道初始化器，用于入站操作
            serverBootstrap.childHandler(websocketChannelInitializer);
            //启动服务器,本质是Java程序发起系统调用，然后内核底层起了一个处于监听状态的服务，生成一个文件描述符FD
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //异步
            channelFuture.channel().closeFuture().sync();
            log.info("netty启动完成。");

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        init();
    }
}
