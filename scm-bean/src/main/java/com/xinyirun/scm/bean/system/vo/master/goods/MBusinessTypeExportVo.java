package com.xinyirun.scm.bean.system.vo.master.goods;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.format.DateTimeFormat;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MBusinessTypeExportVo implements Serializable {

        private static final long serialVersionUID = -6074849484777119863L;

        @ExcelProperty(value = "No", index = 0)
        @ColumnWidth(15)
        private Integer no;

        @ExcelProperty(value = "板块名称", index = 1)
        private String name;

        @ExcelProperty(value = "板块编号", index = 2)
        private String code;

        @ExcelProperty(value = "是否启用", index = 3)
        private String enable;

        @ExcelProperty(value = "创建人", index = 4)
        private String c_name;

        @ExcelProperty(value = "创建时间", index = 5)
        @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
        @ColumnWidth(20)
        private LocalDateTime c_time;

        @ExcelProperty(value = "更新人", index = 6)
        private String u_name;

        @ExcelProperty(value = "更新时间", index = 7)
        @DateTimeFormat("yyyy年MM月dd日 HH:mm:ss")
        @ColumnWidth(20)
        private LocalDateTime u_time;

}
