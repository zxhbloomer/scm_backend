package com.xinyirun.scm.bean.system.vo.sys.config.dict;

import java.io.Serializable;
import java.util.Date;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.common.annotations.ExcelAnnotion;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
// @ApiModel(value = "资源表导出Bean", description = "资源表导出Bean")
public class SDictDataExportVo implements Serializable {

    private static final long serialVersionUID = 6419636324818821669L;
    @ExcelAnnotion(name = "NO")
    private String no;


    @ExcelAnnotion(name = "字典类型")
    private String dictTypeCode;

    @ExcelAnnotion(name = "字典类型名称")
    private String dictTypeName;

    @ExcelAnnotion(name = "字典标签")
    private String label;

    @ExcelAnnotion(name = "字典键值")
    private String dict_value;

    @ExcelAnnotion(name = "额外配置1")
    private String extra1;

    @ExcelAnnotion(name = "额外配置2")
    private String extra2;

    @ExcelAnnotion(name = "额外配置3")
    private String extra3;

    @ExcelAnnotion(name = "额外配置4")
    private String extra4;

    @ExcelAnnotion(name = "字典排序")
    private String sort;

    @ExcelAnnotion(name = "字典说明")
    private String descr;

    @ExcelAnnotion(name = "是否删除")
    private String is_del;

    @ExcelAnnotion(name = "更新时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date u_time;
}
