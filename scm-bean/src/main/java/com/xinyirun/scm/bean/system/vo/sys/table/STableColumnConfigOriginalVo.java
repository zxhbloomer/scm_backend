package com.xinyirun.scm.bean.system.vo.sys.table;

import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigOriginalEntity;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class STableColumnConfigOriginalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7018973519503040720L;

    private Integer id;

    /**
     * 表格code
     */
    private String table_code;

    /**
     * 表格id
     */
    private Integer table_id;

    /**
     * 字段名
     */
    private String name;


    /**
     * 表头名
     */
    private String label;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 排序的最大最小值
     */
    private Integer max_sort;
    private Integer min_sort;

    /**
     * 不可排序
     */
    private Boolean fix;

    /**
     * 是否显示
     */
    private Boolean is_enable;

    /**
     * 是否删除
     */
    private Boolean is_delete;

    /**
     * 名称
     */
    private String table_name;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * 类型：基本上页面只有一个table，如果出现两个table则需要区分
     */
    private String type;

    /**
     * 开始列
     */
    private Integer start_column_index;

    /**
     * 员工id
     */
    private Integer staff_id;

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
     * 分页条件
     */
    private PageCondition pageCondition;

    /**
     * 排序, up升序, down降序
     */
    private String sort_type;

    /**
     * 列 list
     */
    private List<STableColumnConfigOriginalEntity> itemList;

    /**
     * 是否为分组：0-普通列，1-分组
     */
    private Integer is_group;

    /**
     * 分组子项列表 - 当is_group=1时，包含该分组内的所有列配置
     */
    private List<STableColumnConfigOriginalVo> groupChildren;

}
