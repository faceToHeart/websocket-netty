package com.lwt.learn.websocketnetty.websocket.handler;

import com.lwt.learn.websocketnetty.msg.MessageRequest;
import com.lwt.learn.websocketnetty.producer.RabbitMqProducer;
import com.lwt.learn.websocketnetty.service.CacheService;
import com.lwt.learn.websocketnetty.util.JsonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/10 0:27
 */
@Slf4j
@Component
@ChannelHandler.Sharable//保证处理器，在整个生命周期中就是以单例的形式存在，方便统计客户端的在线数量
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    @Autowired
    private RabbitMqProducer rabbitMqProducer;

    @Autowired
    private CacheService cacheService;

    /**
     * 客户端发送给服务端的消息
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        try {
            //接受客户端发送的消息
            MessageRequest messageRequest = JsonUtil.parseObject(msg.text(), MessageRequest.class);

            Channel channel = ctx.channel();
            //每个channel都有id，asLongText是全局channel唯一id
            String channelId = channel.id().asLongText();

            String userId = messageRequest.getUserId();
            //存储userId和channelId
            cacheService.getChannelUserMap().put(userId, channelId);
            log.info("接受客户端的消息......" + ctx.channel().remoteAddress() + "-参数[" + userId + "]");

            if (!cacheService.getUserChnnelMap().containsKey(userId)) {
                //使用channel中的任务队列，做周期循环推送客户端消息

//                Future future = ctx.channel().eventLoop().scheduleAtFixedRate()
//                        (new WebsocketRunnable(ctx, messageRequest), 0, 10, TimeUnit.SECONDS);
                //存储用户和新建立的通道
                cacheService.getUserChnnelMap().put(userId, List.of(channel));
                //存储每个channel中的future，保证每个channel中有一个定时任务在执行
//                futureMap.put(key, future);
            } else {
                if (!cacheService.getUserChnnelMap().get(userId).contains(channel)) {
                    cacheService.getUserChnnelMap().get(userId).add(channel);
                }
                //非初次通讯的其他业务逻辑
                //每次客户端和服务的主动通信，和服务端周期向客户端推送消息互不影响
                ctx.channel().writeAndFlush(new TextWebSocketFrame(Thread.currentThread().getName() + "服务器时间" + LocalDateTime.now()));
            }

            messageRequest.setMsg("转发给另一台websocket服务器");
            //redis的pub/sub模式
//          RTopic topic = redissonClient.getTopic("USER");
//          redisTemplate.convertAndSend("USER", messageRequest);
//          topic.publish(JsonUtil.toJSONString(messageRequest));

            rabbitMqProducer.syncSend(messageRequest);
        } catch (Exception e) {
            log.error("websocket服务器推送消息发生错误：", e);
        }
    }

    /**
     * 客户端连接时候的操作
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("一个客户端连接......" + ctx.channel().remoteAddress());
    }

    /**
     * 客户端掉线时的操作
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //移除通信过的channel
        cacheService.removeChannel(channel);
        log.info("一个客户端移除......" + ctx.channel().remoteAddress());
        ctx.close(); //关闭连接
    }

    /**
     * 发生异常时执行的操作
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        //移除通信过的channel
        cacheService.removeChannel(channel);
        //关闭长连接
        ctx.close();
        log.info("异常发生 " + cause.getMessage());
    }

}
