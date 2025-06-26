package com.xinyirun.scm.bean.system.vo.business.adjust;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 库存调整
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库存调整", description = "库存调整")
public class BAdjustVo extends BaseVo implements Serializable {


    private static final long serialVersionUID = -4005543014695399742L;
    /**
     * 调整单明细id
     */
    private Integer id;

    /**
     * 调整单id
     */
    private Integer adjust_id;

    /**
     * 序号
     */
    private Integer idx;

    /**
     * 调整单号
     */
    private String code;

    /**
     * 类型 1、库存调整；2、盘盈调整；3、盘亏调整
     */
    private String type;

    /**
     * 类型 1、库存调整；2、盘盈调整；3、盘亏调整
     */
    private String type_name;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主名稱
     */
    private String owner_name;

    /**
     * 调整原因
     */
    private String remark;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 单据状态
     */
    private String status_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库简称
     */
    private String warehouse_short_name;

    /**
     * 库区id
     */
    private Integer location_id;

    /**
     * 库位id
     */
    private Integer bin_id;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料id
     */
    private String sku_code;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格
     */
    private String spec;

    /**
     * 库存id
     */
    private Integer stock_id;

    /**
     * 库存code
     */
    private String stock_code;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 原库存数量
     */
    private BigDecimal qty;

    /**
     * 调整库存数量
     */
    private BigDecimal qty_adjust;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 调整单明细集合
     */
    private List<BAdjustDetailVo> detailList;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建开始时间
     */
    private LocalDateTime start_time;


    /**
     * 创建结束时间
     */
    private LocalDateTime over_time;


    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 0待办/1已办/2全部
     */
    private String todo_status;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 审核人
     */
    private String e_name;

    /**
     * 审核时间
     */
    private String e_dt;

    /**
     * 附件信息
     */
    private List<SFileInfoVo> files;

    /**
     * 附件信息
     */
    private Integer files_id;

    /**
     * 业务启动日期
     */
    private String batch;
}
