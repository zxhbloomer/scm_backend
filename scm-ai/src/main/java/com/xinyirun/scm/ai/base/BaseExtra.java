/**
 * 扩展信息基类，用于存储额外的JSON格式信息
 */
package com.xinyirun.scm.ai.base;

import java.io.Serializable;

import com.alibaba.fastjson2.JSON;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseExtra implements Serializable {

    private static final long serialVersionUID = 1L;

    public String toJson() {
        return JSON.toJSONString(this);
    }
    
    /**
     * 通用的JSON反序列化方法
     * @param <T> 返回类型，必须是BaseExtra的子类
     * @param json JSON字符串
     * @param clazz 目标类类型
     * @return 反序列化后的对象
     */
    public static <T extends BaseExtra> T fromJson(String json, Class<T> clazz) {
        try {
            if (json == null || json.isEmpty()) {
                // 这里我们不能直接使用builder，因为静态方法不能引用T的实例方法
                // 但调用者可以使用具体子类的builder
                return null;
            }
            return JSON.parseObject(json, clazz);
        } catch (Exception e) {
            // 如果解析失败，返回null
            return null;
        }
    }
}