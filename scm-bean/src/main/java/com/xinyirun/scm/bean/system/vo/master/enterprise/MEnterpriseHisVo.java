package com.xinyirun.scm.bean.system.vo.master.enterprise;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业调整表
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MEnterpriseHisVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2173060459297932206L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 统一社会信用代码
     */
    private String uscc;

    /**
     * 企业id
     */
    private Integer enterprise_id;

    /**
     * 版本，0开始每次审批通过后累加1
     */
    private Integer version;

    /**
     * 修改理由，在单据完成审批后，修改时需要记录修改理由
     */
    private String modify_reason;

    /**
     * 企业名称
     */
    private String enterprise_name;

    /**
     * 调整信息json
     */
    private String  adjust_info_json;

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
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 流程实例code
     */
    private String bpm_instance_code;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
