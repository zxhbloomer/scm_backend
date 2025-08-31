package com.xinyirun.scm.bean.system.vo.sys.config.config;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.common.annotations.ExcelAnnotion;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zxh
 * @date 2019/9/26
 */
@Data
@EqualsAndHashCode(callSuper = false)
// @ApiModel(value = "参数配置表导出Bean", description = "参数配置表导出Bean")
public class SConfigDataExportVo implements Serializable {

    private static final long serialVersionUID = -21039960705170821L;

    @ExcelAnnotion(name = "NO")
    private Integer no;

    @ExcelAnnotion(name = "参数名称")
    private String name;

    @ExcelAnnotion(name = "参数键名")
    private String config_key;

    @ExcelAnnotion(name = "参数键值")
    private String value;

    @ExcelAnnotion(name = "额外配置1")
    private String extra1;

    @ExcelAnnotion(name = "额外配置2")
    private String extra2;

    @ExcelAnnotion(name = "额外配置3")
    private String extra3;

    @ExcelAnnotion(name = "额外配置4")
    private String extra4;

    @ExcelAnnotion(name = "说明")
    private String descr;

    @ExcelAnnotion(name = "更新人")
    private String u_name;

    @ExcelAnnotion(name = "更新时间", dateFormat="yyyy-MM-dd HH:mm:ss", width=25)
    private Date u_time;
}
