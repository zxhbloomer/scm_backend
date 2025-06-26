package com.xinyirun.scm.bean.system.vo.business.ownerchange;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 库存调拨
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOwnerChangeVo implements Serializable {

    private static final long serialVersionUID = 2078453525940979541L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 序号
     */
    private Integer idx;


    /**
     * 货权转移id
     */
    private Integer owner_change_id;

    /**
     * 状态
     */
    private String status_name;

    /**
     * 状态
     */
    private String status;

    /**
     * 调拨订单id
     */
    private Integer order_id;

    /**
     * 调拨订单编号
     */
    private String order_no;

    /**
     * 调拨订单合同号
     */
    private String contract_no;

    /**
     * 调拨单号
     */
    private String code;

    /**
     * 原货主id
     */
    private Integer out_owner_id;

    /**
     * 原货主code
     */
    private String out_owner_code;

    /**
     * 原货主
     */
    private String out_owner_name;

    /**
     * 原委托方id
     */
    private Integer out_consignor_id;

    /**
     * 原委托方code
     */
    private String out_consignor_code;

    /**
     * 原委托方
     */
    private String out_consignor_name;

    /**
     * 新货主id
     */
    private Integer in_owner_id;

    /**
     * 新货主code
     */
    private String in_owner_code;

    /**
     * 新货主
     */
    private String in_owner_name;

    /**
     * 新委托方id
     */
    private Integer in_consignor_id;

    /**
     * 新委托方code
     */
    private String in_consignor_code;

    /**
     * 新委托方
     */
    private String in_consignor_name;

    /**
     * 新仓库id
     */
    private Integer in_warehouse_id;

    /**
     * 新仓库code
     */
    private String in_warehouse_code;

    /**
     * 新仓库
     */
    private String in_warehouse_name;

    /**
     * 原仓库id
     */
    private Integer out_warehouse_id;

    /**
     * 原仓库code
     */
    private String out_warehouse_code;

    /**
     * 原仓库
     */
    private String out_warehouse_name;

    /**
     * 转移日期
     */
    private LocalDateTime change_time;

    /**
     * 审核人id
     */
    private Integer e_id;

    /**
     * 审核人名称
     */
    private String e_name;

    /**
     * 审核时间
     */
    private LocalDateTime e_dt;

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
     * 物料名
     */
    private String goods_name;

    /**
     * 规格编码
     */
    private String sku_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物料规格
     */
    private String spec;

    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 调拨单明细集合
     */
    private List<BOwnerChangeDetailVo> detailList;

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
     * 附件
     */
    private List<SFileInfoVo> file_files;
    private Integer files;


}
