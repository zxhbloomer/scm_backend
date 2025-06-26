package com.xinyirun.scm.bean.system.vo.sys.log.datachange;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnsVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据变化的bean
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SDataChangeLogVo implements Serializable {

    
    private static final long serialVersionUID = 1205879683358718187L;

    /**
     * 操作业务名:entity的注解名称
     */
    private String name;

    /**
     * 单号
     */
    private String order_code;

    /**
     * 获取请求id
     */
    private String request_id;

    /**
     * 数据操作类型
     */
    private String SqlCommandType;

    /**
     * 表名
     */
    private String table_name;

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
     * 执行前的数据
     */
    private SDataChangeLogDetailVo beforeVo;

    /**
     * 执行后的数据
     */
    private SDataChangeLogDetailVo afterVo;

    /*
     * 注释类
     */
    private SDataChangeColumnsVo tableColumns;

    /**
     * 最后更新时间：被动态更新
     */
    private LocalDateTime u_time;

    /**
     * 更新人名称
     */
    private String u_name;

    /**
     * 租户code
     */
    private String tenant_code;
}
