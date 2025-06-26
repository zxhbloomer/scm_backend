package com.xinyirun.scm.bean.system.vo.sys.log.datachange;

import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnsVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SDataChangeLogDetailVo implements Serializable {

    
    private static final long serialVersionUID = 624010046238136675L;

    /**
     * 编号
     */
    private String order_code;

    /**
     * 调用策略模式_数据变更的类名
     */
    private String class_name;

    /**
     * 对应的实体类名
     */
    private String entity_name;

    /**
     * 数据库表对应的id
     */
    private Integer table_id;

    /**
     * 返回单条数据的结果
     */
    private String result;

    /**
     * 返回单条数据的结果bean_name
     */
    private String result_bean_name;

    /**
     * 注释类
     */
    private SDataChangeColumnsVo tableColumns;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人
     */
    private String u_name;

}
