package com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:保存entity中每个属性的DataChangeLabelAnnotation数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SDataChangeColumnVo implements Serializable {

    
    private static final long serialVersionUID = -4029537050142699554L;

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
