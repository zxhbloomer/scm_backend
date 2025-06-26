package com.xinyirun.scm.bean.system.bo.log.operate;

import com.xinyirun.scm.common.enums.OperationEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @ClassName: OperationOldDataBo
 * @Description: 操作日志，执行过程bean
 * @Author: zxh
 * @date: 2020/1/2
 * @Version: 1.0
 */
@Data
public class OperationDataBo implements Serializable {

    private static final long serialVersionUID = -6996215927742385704L;

    /** 业务名 */
    private String name;

    /** 业务名 */
    private OperationEnum type;

    /** 业务操作说明 */
    private String oper_info;

    /** 需要记录的字段 */
    private String[] cloums;

    /** 表名 */
    private String table_name;

    /** 查询的sql */
    private String sqlTemplate;

    /** 表字段 */
    private Map<String, Object> columnCommentMap;

    /** 参数 */
    private Object[] args;

    /** 旧值 */
    private Map<String, Object> oldData;

    /** 参数的位置 */
    private int para_position;
    /** id的位置 */
    private int ids_index;
}
