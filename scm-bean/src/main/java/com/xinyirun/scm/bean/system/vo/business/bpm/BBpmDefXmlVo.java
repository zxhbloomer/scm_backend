package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 流程定义xml(act_ge_bytearray)
 * </p>
 *
 * @author xinyirun
 * @since 2023-04-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BBpmDefXmlVo implements Serializable {

    private static final long serialVersionUID = -2830164769204829131L;
    /**
     * 主键id
     */
    private Integer id;

    private Integer id_;

    private Integer dbversion;

    private String name;

    /**
     * 流程定义XML
     */
    private String def_xml;

    /**
     * 流程定义BPMN格式XML
     */
    private String bpmn_xml;

    private PageCondition pageCondition;

}
