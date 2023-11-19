package com.lwt.learn.websocketnetty.msg;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LiangWenTong
 * @version 1.0
 * @date 2023/11/10 0:21
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest implements Serializable {
    private String userId;

    private Integer current = 1;

    private Integer size = 10;

    private String msg;
}
