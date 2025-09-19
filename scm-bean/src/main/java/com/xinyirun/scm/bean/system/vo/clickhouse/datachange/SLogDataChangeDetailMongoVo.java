package com.xinyirun.scm.bean.system.vo.clickhouse.datachange;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 数据变更明细
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogDataChangeDetailMongoVo implements Serializable {
    
    private static final long serialVersionUID = 7144130155130966643L;

    /**
     * 属性字段名称
     */
    private String clm_name;

    /**
     * 属性字段对应的注解DataChangeLabelAnnotation名
     */
    private String clm_label;

    /**
     * 旧值
     */
    private Object old_value;

    /**
     * 新值
     */
    private Object new_value;
}
