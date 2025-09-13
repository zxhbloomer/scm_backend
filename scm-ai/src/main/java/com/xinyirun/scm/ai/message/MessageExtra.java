/**
 * 消息扩展字段，用于存储额外的信息
 */
package com.xinyirun.scm.ai.message;

import com.xinyirun.scm.ai.base.BaseExtra;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class MessageExtra extends BaseExtra {

    // 是否内部消息
    // 例如：企业内部员工之间的消息，true: 内部消息，false: 外部消息
    @Builder.Default
    private Boolean isInternal = false; // 设置默认值为false

    private String translatedText; // 翻译后的文本
    
    private String orgUid; // 组织UID

    public static MessageExtra fromJson(String json) {
        MessageExtra result = BaseExtra.fromJson(json, MessageExtra.class);
        return result != null ? result : MessageExtra.builder().build();
    }
}