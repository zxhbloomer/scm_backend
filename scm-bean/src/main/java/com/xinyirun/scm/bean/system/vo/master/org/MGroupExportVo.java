package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.annotations.ExcelAnnotion;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 集团主表
 * </p>
 *
 * @author zxh
 * @since 2019-10-30
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "集团主表导出Bean", description = "集团主表导出Bean")
@EqualsAndHashCode(callSuper=false)
public class MGroupExportVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7466167220853981131L;

    private Long id;

    /**
     * 集团编码
     */
    @ExcelAnnotion(name = "编码")
    private String code;

    /**
     * 集团名称
     */
    @ExcelAnnotion(name = "名称")
    private String name;

    /**
     * 简称
     */
    @ExcelAnnotion(name = "简称")
    private String simple_name;

    /**
     * 说明
     */
    @ExcelAnnotion(name = "说明")
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;
    @ExcelAnnotion(name = "是否删除")
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    private Long c_id;

    @ExcelAnnotion(name = "新增人")
    private String c_name;

    @ExcelAnnotion(name = "新增时间")
    private LocalDateTime c_time;

    private Long u_id;

    @ExcelAnnotion(name = "更新人")
    private String u_name;

    @ExcelAnnotion(name = "更新时间")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
