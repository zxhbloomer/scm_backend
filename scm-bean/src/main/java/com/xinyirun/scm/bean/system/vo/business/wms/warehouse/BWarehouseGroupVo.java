package com.xinyirun.scm.bean.system.vo.business.wms.warehouse;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 仓库组一级分类
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BWarehouseGroupVo implements Serializable {

    private static final long serialVersionUID = 7908667618714147329L;

    /**
     * id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;

    /**
     * 类型：1一级；2二级；3三级
     */
    private String type;

    /**
     * 类型：1一级；2二级；3三级
     */
    private String type_name;

    /**
     * 简称
     */
    private String short_name;

    /**
     * 名称拼音
     */
    private String name_pinyin;

    /**
     * 简称拼音
     */
    private String short_name_pinyin;

    /**
     * 名称简拼
     */
    private String name_pinyin_abbr;

    /**
     * 简称简拼
     */
    private String short_name_pinyin_abbr;

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
     * 创建人
     */
    private String c_name;

    /**
     * 修改人
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 已设置的仓库数量
     */
    private Integer warehouse_count;

    /**
     * 搜索关键词
     */
    private String keyword;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
