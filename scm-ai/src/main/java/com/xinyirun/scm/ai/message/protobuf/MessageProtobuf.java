/**
 * 消息Protobuf传输对象，用于消息在系统间的序列化和传输
 */
package com.xinyirun.scm.ai.message.protobuf;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.message.MessageTypeEnum;
import com.xinyirun.scm.ai.message.MessageStatusEnum;
import com.xinyirun.scm.ai.thread.protobuf.ThreadProtobuf;
import com.xinyirun.scm.ai.user.protobuf.UserProtobuf;
import com.xinyirun.scm.ai.enums.ChannelEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 消息传输对象，所有字段与message.proto中字段一一对应
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageProtobuf implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uid;

	private MessageTypeEnum type;

	private String content;

	private MessageStatusEnum status;

	private LocalDateTime createdAt;

	private ChannelEnum channel;

	private ThreadProtobuf thread;

	private UserProtobuf user;

	private String extra;

	/**
	 * 获取格式化的创建时间字符串，用于前端解析
	 * @return 格式化的时间字符串 (yyyy-MM-dd HH:mm:ss)
	 */
	public String getCreatedAtString() {
		return createdAt != null ? createdAt.toString() : null;
	}

	/**
	 * 获取原始的创建时间
	 * @return LocalDateTime 原始时间对象
	 */
	public LocalDateTime getCreatedAtDateTime() {
		return createdAt;
	}

	public static MessageProtobuf fromJson(String json) {
        return JSON.parseObject(json, MessageProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

	/**
	 * 将createdAt转换为时间戳
	 * @return 时间戳（秒）
	 */
    public Long getTimestamp() {
        return createdAt != null ? 
        	java.time.ZoneOffset.UTC.equals(createdAt.atZone(java.time.ZoneId.systemDefault()).getOffset()) ?
        		createdAt.atZone(java.time.ZoneId.systemDefault()).toEpochSecond() :
        		createdAt.atZone(java.time.ZoneId.systemDefault()).toInstant().getEpochSecond() : null;
    }
}