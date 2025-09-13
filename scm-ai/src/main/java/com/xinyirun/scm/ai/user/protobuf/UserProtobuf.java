/**
 * 用户传输对象，用于用户信息在系统间的序列化和传输
 */
package com.xinyirun.scm.ai.user.protobuf;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * User Protobuf传输对象
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserProtobuf implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uid;

	private String username;

	private String nickname;

	private String avatar;

	private String email;

	private String mobile;

	private String orgUid;

	private String extra;

	public static UserProtobuf fromJson(String json) {
        return JSON.parseObject(json, UserProtobuf.class);
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }
}