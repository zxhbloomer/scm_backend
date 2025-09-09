package com.xinyirun.scm.bean.system.vo.master.warhouse;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库位
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库位", description = "库位")
public class MBinVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3290384895645755663L;
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
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 仓库是否禁用
     */
    private Boolean warehouse_enable;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 库区名称
     */
    private String location_name;

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
    private String remark;

    /**
     * 状态 0启用 1停用
     */
    private Boolean status;

    /**
     * 状态 0启用 1停用
     */
    private Boolean enable;

    /**
     * 启用库区 0-不启用 1-启用
     */
    private Boolean enable_location;

    /**
     * 启用库位 0-不启用 1-启用
     */
    private Boolean enable_bin;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;


    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

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
     * 是否删除:false-未删除,true-已删除
     */
    private Boolean is_del;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 综合名称：全称，简称，拼音，简拼，所有的仓库、库区、库位
     */
    private String combine_search_condition;

    /**
     * 仓库类型
     */
    private String warehouse_type_name;

    /**
     * 仓库类型
     */
    private String warehouse_type;

    /**
     * id集合
     */
    private Integer[] ids;

    /**
     * 仓库地址
     */
    private String warehouse_address;

    /**
     * 过滤掉传过来的仓库类型
     */
    private String[] filterWarehouseType;

    /**
     * 仓库简称
     */
    private String warehouse_short_name;
}
