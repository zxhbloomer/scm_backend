package com.xinyirun.scm.bean.system.vo.sys.columns;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 表格列宽
 * </p>
 *
 * @author zxh
 * @since 2020-06-09
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "表格列宽vo", description = "表格列宽vo")
@EqualsAndHashCode(callSuper=false)
public class SColumnSizeVo implements Serializable {

    private static final long serialVersionUID = 5189684771582598858L;

    private Long id;

    /**
     * 配置vue export default  name时所使用的type：constants_program.P_VUE_SETTING
     */
    private String page_code;

    /**
     * 页面id
     */
    private Long page_id;

    /**
     * 类型：基本上页面只有一个table，如果出现两个table则需要区分
     */
    private String type;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * table的column的property属性
     */
    private String column_property;

    /**
     * table的column的label属性
     */
    private String column_label;

    /**
     * table的column的第几个列，从0开始
     */
    private Integer column_index;

    /**
     * 列宽
     */
    private Integer min_width;

    /**
     * 列宽
     */
    private Integer real_width;

    private String cache_key;

}
