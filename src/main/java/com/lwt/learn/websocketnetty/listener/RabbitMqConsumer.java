package com.lwt.learn.websocketnetty.listener;

import com.lwt.learn.websocketnetty.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/18 19:33
 */
@Component
@RabbitListener(queues = RabbitMQConfig.FANOUT_EXCHANGE_QUEUE_TOPIC_A)
public class RabbitMqConsumer {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @RabbitHandler
    public void onMessage(Object message) {
        //这里可以处理用户消息，将消息发送到每个用户创建的通道中去，UserMsgMessageListener 中写过了，这里不重复写了
        logger.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), message);
    }
}
