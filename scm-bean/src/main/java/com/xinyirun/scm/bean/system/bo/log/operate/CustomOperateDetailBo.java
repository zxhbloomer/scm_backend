package com.xinyirun.scm.bean.system.bo.log.operate;

import com.xinyirun.scm.common.enums.OperationEnum;
import lombok.Data;

import java.util.Map;

/**
 * @ClassName: CustomOperateDetailBo
 * @Author: zxh
 * @date: 2020/1/16
 * @Version: 1.0
 */
@Data
public class CustomOperateDetailBo<T> {

    /** 业务名 */
    private String name;

    /** 业务名 */
    private OperationEnum type;

    /** 业务操作说明 */
    private String oper_info;

    /** 表名 */
    private String table_name;

    /** 旧值 */
    private T oldData;

    /** 新值 */
    private T newData;

    /** 列名称 */
    private Map<String, String> columns;

}
