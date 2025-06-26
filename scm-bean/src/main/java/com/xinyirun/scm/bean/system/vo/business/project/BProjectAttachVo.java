package com.xinyirun.scm.bean.system.vo.business.project;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目管理附件表VO
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BProjectAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 1023474871385475409L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 项目id
     */
    private Integer project_id;

    /**
     * 项目附件
     */
    private Integer one_file;

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
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
