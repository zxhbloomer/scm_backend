package com.xinyirun.scm.bean.system.vo.sys.config.dict;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.common.annotations.ExcelAnnotion;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 资源表导出Bean
 * </p>
 *
 * @author zxh
 * @since 2019-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
// @ApiModel(value = "字典类型导出Bean", description = "字典类型导出Bean")
public class SDictTypeExportVo implements Serializable {

    private static final long serialVersionUID = -6942475112738825609L;

    @ExcelAnnotion(name = "NO")
    private Integer no;

    @ExcelAnnotion(name = "字典类型")
    private String code;

    @ExcelAnnotion(name = "字典名称")
    private String name;

    @ExcelAnnotion(name = "说明")
    private String descr;

    @ExcelAnnotion(name = "是否删除")
    private String is_del;

    @ExcelAnnotion(name = "更新时间", dateFormat = "yyyy-MM-dd HH:mm:ss", width = 25)
    private Date u_time;;


}
