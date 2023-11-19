package com.lwt.learn.websocketnetty.producer;

import com.lwt.learn.websocketnetty.config.RabbitMQConfig;
import com.lwt.learn.websocketnetty.util.JsonUtil;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/18 19:08
 */
@Component
public class RabbitMqProducer {
    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private AsyncRabbitTemplate asyncRabbitTemplate;

    public void syncSend(Object obj) {
        // 创建 Demo01Message 消息
        // 同步发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE_DEMO_NAME, null, JsonUtil.toJSONString(obj));
    }

//    @Async
//    public ListenableFuture<Void> asyncSend(Object obj) {
//        try {
//            // 发送消息
//            asyncRabbitTemplate.sendAndReceive(new Message(JsonUtil.toJSONString(obj).getBytes()));
//            // 返回成功的 Future
//            return AsyncResult.forValue(null);
//        } catch (Throwable ex) {
//            // 返回异常的 Future
//            return AsyncResult.forExecutionException(ex);
//        }
//    }
}
