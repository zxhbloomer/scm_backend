package com.xinyirun.scm.bean.system.vo.sys.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 表格列配置原始详情VO
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-08
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class STableColumnConfigOriginalDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6172948538204756291L;

    private Integer id;

    /**
     * 关联original表ID
     */
    private Integer original_id;

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
     * 是否显示
     */
    private Boolean is_enable;

    /**
     * 是否删除
     */
    private Boolean is_delete;
}