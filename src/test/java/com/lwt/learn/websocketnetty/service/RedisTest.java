package com.lwt.learn.websocketnetty.service;

import com.lwt.learn.websocketnetty.msg.MessageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/11 11:22
 */
@SpringBootTest
public class RedisTest {

//    @Autowired
//    private RedissonClient redissonClient;

    @Autowired
    RedisTemplate redisTemplate;
    public static final String TOPIC = "USER";

    @Test
    public void test01() throws InterruptedException {
        System.out.println("test01()测试");
        redisTemplate.convertAndSend(TOPIC, new MessageRequest("112233444", 1, 10, "pub了一条消息"));
        Thread.sleep(1000L);
    }

    @Test
    public void test1() {
        System.out.println(111);
    }

}
