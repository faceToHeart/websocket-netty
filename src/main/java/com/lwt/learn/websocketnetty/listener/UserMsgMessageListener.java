package com.lwt.learn.websocketnetty.listener;

import com.lwt.learn.websocketnetty.msg.MessageRequest;
import com.lwt.learn.websocketnetty.service.CacheService;
import com.lwt.learn.websocketnetty.util.JsonUtil;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/11 0:22
 */
@Slf4j
@Component
public class UserMsgMessageListener implements MessageListener {

    @Autowired
    private CacheService cacheService;

    /**
     * @param message message must not be {@literal null}.
     * @param pattern topic pattern matching the channel (if specified) - can be {@literal null}.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("接收到redis的消息");
        log.info("message：" + message);
        log.info("pattern：" + new String(pattern));
        // 获取消息
        byte[] messageBody = message.getBody();
        // 使用值序列化器转换
//        Object msg = redisTemplate.getValueSerializer().deserialize(messageBody);
        MessageRequest req = JsonUtil.parseObject(new String(messageBody), MessageRequest.class);
        String userId = req.getUserId();
        // 获取监听的频道
        byte[] channelByte = message.getChannel();
//        // 使用字符串序列化器转换
//        Object channel = redisTemplate.getStringSerializer().deserialize(channelByte);
//        // 渠道名称转换
//        String patternStr = new String(pattern);
        if (cacheService.getUserChnnelMap().containsKey(userId)) {
            //这里是循环发给用户的每个通道
            cacheService.getUserChnnelMap().get(userId).forEach(channel -> {
                channel.writeAndFlush(new TextWebSocketFrame("msg已收到：" + req.getMsg()));
            });
        }
    }
}
