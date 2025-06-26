package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 流程定义(act_re_model)
 * </p>
 *
 * @author xinyirun
 * @since 2023-03-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_bpm_def")
public class BBpmDefVo implements Serializable {

    private static final long serialVersionUID = 2031538247051856816L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * flowable流程模型定义ID
     */
    private String id_;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 模型的标识, 必须是唯一的
     */
    private String def_key;

    /**
     * 流程分模型类
     */
    private String type;
    private String type_name;

    /**
     * 版本号
     */
    private String version;

    /**
     * 描述
     */
    private String description;

    /**
     * 流程定义xmlid
     */
    private Integer def_xml_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private String c_name;

    /**
     * 修改人id
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    private PageCondition pageCondition;


}
