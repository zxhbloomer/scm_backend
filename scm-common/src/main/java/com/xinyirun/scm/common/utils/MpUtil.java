package com.xinyirun.scm.common.utils;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

/**
 * MP工具类
 *
 */
public class MpUtil {

    /**
     * 获取关联的 TableInfo
     */
    public static TableInfo getTableInfo(String tableName) {
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                return tableInfo;
            }
        }
        return null;
    }

}
