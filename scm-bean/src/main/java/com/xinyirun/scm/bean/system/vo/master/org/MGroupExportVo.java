package com.xinyirun.scm.bean.system.vo.master.org;

import cn.idev.excel.annotation.ExcelIgnore;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

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
public class MGroupExportVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -5468874597001009192L;

    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "NO", index = 0)
    private Integer no;

    /**
     * 上级集团简称
     */
    @ExcelProperty(value = "上级集团", index = 1)
    private String parent_group_simple_name;

    /**
     * 集团编码
     */
    @ExcelProperty(value = "集团编号", index = 2)
    private String code;

    /**
     * 集团名称
     */
    @ExcelProperty(value = "集团名称", index = 3)
    private String name;

    /**
     * 简称
     */
    @ExcelProperty(value = "集团简称", index = 4)
    private String simple_name;

    /**
     * 备注
     */
    @ExcelIgnore
    private String descr;

    /**
     * 是否删除
     */
    @ExcelIgnore
    private Boolean is_del;
    @ExcelIgnore
    private String is_del_name;

    /**
     * 租户id
     */
//    private Long tenant_id;

    @ExcelIgnore
    private Long c_id;

    @ExcelIgnore
    private String c_name;

    @ExcelIgnore
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime c_time;

    @ExcelIgnore
    private Long u_id;

    @ExcelProperty(value = "更新人", index = 5)
    private String u_name;

    @ExcelProperty(value = "更新时间", index = 6)
    @ColumnWidth(20)
    @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @ExcelIgnore
    private Integer dbversion;

    /**
     * 换页条件
     */
    @ExcelIgnore
    private PageCondition pageCondition;

}
