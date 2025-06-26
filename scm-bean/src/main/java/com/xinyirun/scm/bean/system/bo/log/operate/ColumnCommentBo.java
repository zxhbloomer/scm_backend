package com.xinyirun.scm.bean.system.bo.log.operate;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: ColumnCommentBo
 * @Author: zxh
 * @date: 2019/12/31
 * @Version: 1.0
 */
@Data
public class ColumnCommentBo implements Serializable {
    private static final long serialVersionUID = -2892157633338247686L;
    private String table_name;
    private String table_comment;
    private String column_name;
    private String column_comment;
}
