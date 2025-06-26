package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 模板分组(act_re_model)
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmGroupVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -8521715224298998246L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 组名称
     */
    private String group_name;




}
