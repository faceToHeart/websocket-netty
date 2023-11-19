package com.lwt.learn.websocketnetty.service;

import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/19 12:28
 */
@Service
public class CacheService {

    /**
     * 一个userId对应多个channel，就像是QQ可以登录在手机或电脑上
     */
    private static Map<String, List<Channel>> userChannelMap = new ConcurrentHashMap<>();

    /**
     * channelId 对 userId
     */
    private static Map<String, String> channelUserMap = new ConcurrentHashMap<>();

    /**
     * 任务map，存储future，用于停止队列任务
     */
    private static Map<String, Future> futureMap = new ConcurrentHashMap<>();

    public static Map<String, Future> getFutureMap() {
        return futureMap;
    }

    public Map<String, List<Channel>> getUserChnnelMap() {
        return userChannelMap;
    }

    public Map<String, String> getChannelUserMap() {
        return channelUserMap;
    }

    public List<Channel> getChnnelByUserId(String userId) {
        return userChannelMap.get(userId);
    }

    public void removeChannel(Channel channel) {
        String channelId = channel.id().asLongText();
        String userId = channelUserMap.get(channelId);
        //去掉记录的用户当前channel
        if (userChannelMap.containsKey(userId)) {
            List<Channel> channelList = userChannelMap.get(userId);
            if (channelList.size() == 1) {
                userChannelMap.remove(userId);
            } else {
                userChannelMap.get(userId).remove(channel);
            }
        }
        //去掉channelId对userId的关联关系
        channelUserMap.remove(channelId);
        //移除定时任务
        Future future = futureMap.get(channelId);
        Optional.ofNullable(future).ifPresent(f -> {
            f.cancel(true);
            futureMap.remove(channelId);
        });
    }

}
