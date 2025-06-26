package com.xinyirun.scm.bean.app.bo.inventory.warehouse;

import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MInventoryBo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 库位bo
 * </p>
 *
 */
@Data
public class AppMBinBo implements Serializable {


    private static final long serialVersionUID = 7346866444819678024L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 所属库区id
     */
    private Integer location_id;

    /**
     * 是否默认库位/库区:0否,1是
     */
    private Boolean is_default;

    /**
     * 道编号
     */
    private String line_code;

    /**
     * 列编号
     */
    private String col_code;

    /**
     * 排编号
     */
    private String row_code;

    /**
     * 层号
     */
    private String level_code;

    /**
     * 库位功能 0存储+拣货1存储 2收货 3 退货
     */
    private Boolean func;

    /**
     * 库位类别 1普通货架 2地面平仓 3 高位货架
     */
    private Boolean type;

    /**
     * 物品放置类别 1.托盘放置 2 堆码放置
     */
    private Boolean place_type;

    /**
     * 供应商混放flg 0不允许/1允许
     */
    private Boolean supplier_mix_flag;

    /**
     * 批次混放flg 0不允许/1允许
     */
    private Boolean lot_mix_flag;

    /**
     * 货物混放flg 0不允许/1允许
     */
    private Boolean goods_mix_flag;

    /**
     * 货物状态 0 空库位1 预分配 2 有货
     */
    private Boolean goods_status;

    /**
     * 描述
     */
    private String des;

    /**
     * 状态 0启用 1停用
     */
    private Boolean status;

    /**
     * 状态 0启用 1停用
     */
    private Boolean enable;

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

    /**
     * 库位对多库存
     */
    private List<MInventoryBo> inventories;

    /**
     * 库位对1库存
     */
    private MInventoryBo inventory;

    /**
     * 是否位空仓位
     */
    boolean isEmpty;

}

