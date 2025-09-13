package com.xinyirun.scm.bean.system.vo.master.warehouse;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库区
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "库区", description = "库区")
public class MLocationVo implements Serializable {

    private static final long serialVersionUID = 7474533109583003800L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 库区名称
     */
    private String name;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库编码
     */
    private String warehouse_code;

    /**
     * 简称
     */
    private String short_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 是否锁定盘点 :0否,1是
     */
    private Boolean inventory;

    /**
     * 是否默认库位/库区:0否,1是
     */
    private Boolean is_default;

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
     * 是否删除：false-未删除，true-已删除
     */
    private Boolean is_del;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;


    /**
     * 综合名称：全称，简称，拼音，简拼
     */
    private String combine_search_condition;

    /**
     * id集合
     */
    private Integer[] ids;
}
