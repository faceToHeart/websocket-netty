package com.lwt.learn.websocketnetty.config;

import com.lwt.learn.websocketnetty.listener.UserMsgMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @author lwt
 */
@Configuration
public class RedisConfiguration {

    public static final String TOPIC = "USER";

    @Autowired
    private RedisConnectionFactory factory;

    @Autowired
    private UserMsgMessageListener userMsgMessageListener;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        // 创建 RedisMessageListenerContainer 对象
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        // 设置 RedisConnection 工厂,它就是实现多种 Java Redis 客户端接入的秘密工厂。
        container.setConnectionFactory(factory);

        // 添加监听器
//        container.addMessageListener(messageListenerAdapter(), new ChannelTopic(TOPIC));
        container.addMessageListener(userMsgMessageListener, new ChannelTopic(TOPIC));
//        container.addMessageListener(new UserMsgMessageListener(), new PatternTopic("USER"));
        return container;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        // 创建 RedisTemplate 对象
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 设置开启事务支持
        template.setEnableTransactionSupport(true);

        // 设置 RedisConnection 工厂。它就是实现多种 Java Redis 客户端接入的秘密工厂。
        template.setConnectionFactory(factory);

        // 使用 String 序列化方式，序列化 KEY 。
        template.setKeySerializer(RedisSerializer.string());

        // 使用 JSON 序列化方式（库是 Jackson ），序列化 VALUE 。
        template.setValueSerializer(RedisSerializer.json());
        return template;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        return new MessageListenerAdapter(userMsgMessageListener);
    }

//    @Bean
//    public UserMsgMessageListener redisChannelListener() {
//        return new UserMsgMessageListener();
//    }

}
