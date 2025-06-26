package com.xinyirun.scm.bean.system.vo.websocket;

import com.xinyirun.scm.bean.system.config.base.websocket.BaseWsVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 消息中心 count 数量 vo
 * </p>
 *
 * @author xyr
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class WMessageCenterCountWsVo extends BaseWsVo implements Serializable {

    private static final long serialVersionUID = 7284498062347909002L;

    /**
     * 待办
     */
    private Integer todos;

    /**
     * 订阅
     */
    private Integer subscriptions;
}
