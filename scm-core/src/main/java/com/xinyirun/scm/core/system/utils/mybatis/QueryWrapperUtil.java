//package com.xinyirun.scm.core.utils.mybatis;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
//import com.xinyirun.scm.common.utils.string.StringUtil;
//
///**
// * @author zxh
// * @date 2019-07-29
// */
//public class QueryWrapperUtil<T> {
//
//    /**
//     * 获取排序wrapper
//     *
//     * @param entityBeanClassName
//     * @param sortCondition
//     * @return
//     */
//    public static <T> QueryWrapper<T> setSortWrapper(Class<T> entityBeanClassName, String sortCondition)
//        throws IllegalAccessException, InstantiationException {
//        BaseEntity<T> be = (BaseEntity<T>) entityBeanClassName.newInstance();
//        QueryWrapper<T> wrapper = new QueryWrapper<T>();
//        if (StringUtil.isNotEmpty(sortCondition)) {
//            if (sortCondition.startsWith("-")) {
//                // 此为降序
//                wrapper.orderByDesc(be.getProperty2Columnn(sortCondition.substring(1)));
//            } else {
//                wrapper.orderByAsc(be.getProperty2Columnn(sortCondition));
//            }
//        }
//        return wrapper;
//    }
//}
