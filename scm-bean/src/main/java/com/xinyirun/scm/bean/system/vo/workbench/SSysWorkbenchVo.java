package com.xinyirun.scm.bean.system.vo.workbench;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-17
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SSysWorkbenchVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2204925888648428028L;

    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 配置
     */
    private String config;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
