package com.xinyirun.scm.bean.system.vo.business.wms.out;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 出库单附件VO类
 * 
 * @author system
 * @since 2025-01-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutAttachVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -766625332272355626L;

    // ==================== 实体类字段 ====================
    
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 出库单id
     */
    private Integer out_id;

    /**
     * 磅单文件
     */
    private Integer one_file;

    /**
     * 出库明细附件
     */
    private Integer two_file;

    /**
     * 检验单
     */
    private Integer three_file;

    /**
     * 货物照片
     */
    private Integer four_file;

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

    // ========== 扩展字段 ==========

    /**
     * 创建人名称
     */
    private String c_name;

    /**
     * 修改人名称
     */
    private String u_name;
}