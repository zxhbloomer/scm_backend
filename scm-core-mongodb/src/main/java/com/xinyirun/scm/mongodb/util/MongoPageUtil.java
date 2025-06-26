package com.xinyirun.scm.mongodb.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2024/2/28 11:09
 */


public class MongoPageUtil {
    /**
     *
     * 转换为 Page 类型 返回前台
     * @param pageCondition
     * @param total
     * @param records
     * @param <T>
     * @return
     */
    public static <T> Page<T> covertPages(PageCondition pageCondition, long total, List<T> records) {
        Page<T> page = new Page<T>(pageCondition.getCurrent(), pageCondition.getSize(), total);
        page.setRecords(records);
        return page;
    }
}
