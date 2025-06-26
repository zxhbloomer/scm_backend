package com.xinyirun.scm.bean.system.vo.wms.in;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * <p>
 * 入库单附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BInAttachVo extends PageCondition {


    @Serial
    private static final long serialVersionUID = 1724074181216466940L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 入库单id
     */
    private Integer in_id;

    /**
     * 磅单文件
     */
    private Integer one_file;

    /**
     * 入库明细附件
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
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
