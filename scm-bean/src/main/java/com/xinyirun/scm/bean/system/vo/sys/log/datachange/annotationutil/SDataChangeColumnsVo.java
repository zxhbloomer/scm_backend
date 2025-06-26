package com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description:保存整个entity的DataChangeLabelAnnotation数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SDataChangeColumnsVo implements Serializable {


    
    private static final long serialVersionUID = -6644720359798033797L;

    /**
     * 表名
     */
    private String table_name;

    /**
     * 所有字段的DataChangeLabelAnnotation数据
     */
    private List<SDataChangeColumnVo> columns;

    /**
     * 转换成map
     */
    private Map<String, SDataChangeColumnVo> columns_map;


}
