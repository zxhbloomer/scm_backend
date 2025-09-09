package com.xinyirun.scm.bean.entity.master.warehouse;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
@TableName("m_bin")
public class MBinEntity implements Serializable {

    private static final long serialVersionUID = 1025065761812210211L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编码
     */
    @TableField("code")
    private String code;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 名称拼音
     */
    @TableField("name_pinyin")
    private String name_pinyin;

    /**
     * 名称拼音首字母
     */
    @TableField("name_pinyin_initial")
    private String name_pinyin_initial;

    /**
     * 仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouse_id;

    /**
     * 所属库区id
     */
    @TableField("location_id")
    private Integer location_id;

    /**
     * 是否默认库位/库区:0否,1是
     */
    @TableField("is_default")
    private Boolean is_default;

    /**
     * 道编号
     */
    @TableField("line_code")
    private String line_code;

    /**
     * 列编号
     */
    @TableField("col_code")
    private String col_code;

    /**
     * 排编号
     */
    @TableField("row_code")
    private String row_code;

    /**
     * 层号
     */
    @TableField("level_code")
    private String level_code;

    /**
     * 库位功能 0存储+拣货1存储 2收货 3 退货
     */
    @TableField("func")
    private Boolean func;

    /**
     * 库位类别 1普通货架 2地面平仓 3 高位货架
     */
    @TableField("type")
    private Boolean type;

    /**
     * 物品放置类别 1.托盘放置 2 堆码放置
     */
    @TableField("place_type")
    private Boolean place_type;

    /**
     * 供应商混放flg 0不允许/1允许
     */
    @TableField("supplier_mix_flag")
    private Boolean supplier_mix_flag;

    /**
     * 批次混放flg 0不允许/1允许
     */
    @TableField("lot_mix_flag")
    private Boolean lot_mix_flag;

    /**
     * 货物混放flg 0不允许/1允许
     */
    @TableField("goods_mix_flag")
    private Boolean goods_mix_flag;

    /**
     * 货物状态 0 空库位1 预分配 2 有货
     */
    @TableField("goods_status")
    private Boolean goods_status;

    /**
     * 描述
     */
    @TableField("remark")
    private String remark;

    /**
     * 状态 0启用 1停用
     */
    @TableField("status")
    private Boolean status;

    /**
     * 状态 0启用 1停用
     */
    @TableField("enable")
    private Boolean enable;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 是否删除:false-未删除,true-已删除
     */
    @TableField("is_del")
    private Boolean is_del;


}
