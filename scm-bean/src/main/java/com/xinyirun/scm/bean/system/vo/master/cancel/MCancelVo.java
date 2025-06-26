package com.xinyirun.scm.bean.system.vo.master.cancel;

import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 导入数据日志
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MCancelVo extends BaseEntity<MCancelVo> implements Serializable {

    private static final long serialVersionUID = 7903758397452405890L;

    private Long id;

    /**
     * 业务表id
     */
    private Integer serial_id;

    /**
     * 业务表类型
     */
    private String serial_type;
    private String serial_type_name;

    /**
     * 作废附件
     */
    private Integer file_id;

    /**
     * 备注
     */
    private String remark;

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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
