package com.xinyirun.scm.bean.system.vo.sys.pages;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 页面按钮表
 * </p>
 *
 * @author zxh
 * @since 2020-06-04
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "页面按钮表vo", description = "页面按钮表vo")
@EqualsAndHashCode(callSuper=false)
public class SPagesFunctionVo implements Serializable {

    private static final long serialVersionUID = 6408649934054725183L;

    private Long id;
    private Long ne_id;

    /**
     * 页面id
     */
    private Long page_id;
    private String page_code;
    private String page_name;
    private String page_perms;

    /**
     * 类型：PAGE：主页面上，TABLE：表格上，POPUP：弹出框上
     */
    private String type;

    /**
     * 按钮id
     */
    private Long function_id;
    private String function_code;
    private String function_name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 权限标识
     */
    private String perms;

    /**
     * 说明
     */
    private String descr;

    private Long c_id;
    private String c_name;

    private LocalDateTime c_time;

    private Long u_id;
    private String u_name;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    private Boolean edit_cell_model = false;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 导出id集合
     */
    private Integer[] ids;
}
