package com.xinyirun.scm.bean.utils.annotation;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeDetailMongoVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnsVo;
import com.xinyirun.scm.bean.system.vo.sys.log.datachange.annotationutil.SDataChangeColumnVo;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import com.xinyirun.scm.common.utils.string.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注解处理工具类。
 * 提供处理特定注解的通用方法，适用于任何对象类型。
 */
public class AnnotationUtil {

    /**
     * 解析任意对象的字段及其 Label 注解，并将这些信息存储到 SDataChangeColumnsVo 实例列表中。
     *
     * @param obj 任意对象，其字段可能包含 Label 注解。
     * @return 包含字段名称和 Label 注解值的 SDataChangeColumnsVo 实例列表。
     */
    public static SDataChangeColumnsVo getFieldNameAndDataChageLabel(Object obj) {
        SDataChangeColumnsVo dataChangeColumnVo = new SDataChangeColumnsVo();

        List<SDataChangeColumnVo> columns = new ArrayList<>();
        if (obj == null) {
            return null;
        }

        // 获取 @TableName 注解值
        TableName tableNameAnnotation = obj.getClass().getAnnotation(TableName.class);
        dataChangeColumnVo.setTable_name(tableNameAnnotation.value());

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            DataChangeLabelAnnotation label = field.getAnnotation(DataChangeLabelAnnotation.class);
            if (label != null) {
                // 创建一个 SDataChangeColumnsVo 实例并设置字段名称和注解值
                SDataChangeColumnVo columnVo = new SDataChangeColumnVo();
                columnVo.setClm_name(field.getName());
                columnVo.setClm_label(label.value());

                // 将 columnVo 添加到列表中
                columns.add(columnVo);
            }
        }
        dataChangeColumnVo.setColumns(columns);
        // 转换成map
        Map<String, SDataChangeColumnVo> columnMap = new HashMap<>();
        for (SDataChangeColumnVo column : dataChangeColumnVo.getColumns()) {
            columnMap.put(column.getClm_name(), column);
        }
        dataChangeColumnVo.setColumns_map(columnMap);

        return dataChangeColumnVo;
    }

//    /**
//     * 根据字段名称获取字段的标签信息
//     *
//     * @param entity    实体对象
//     * @param clm_name  字段名称
//     * @return  SLogDataChangeDetailVo对象，包含字段名称和标签信息
//     */
//    public static SLogDataChangeDetailMongoVo getColumnAndLabel(Object entity, String clm_name) {
//        SLogDataChangeDetailMongoVo dataChangeColumnVo = new SLogDataChangeDetailMongoVo();
//
//        if (entity == null) {
//            return null;
//        }
//
//        Field[] fields = entity.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            DataChangeLabelAnnotation label = field.getAnnotation(DataChangeLabelAnnotation.class);
//            if (label != null && StringUtils.isNotEmpty(label.extension())) {
//                if (field.getName().equals(clm_name)) {
//                    // 创建一个 SDataChangeColumnsVo 实例并设置字段名称和注解值
//                    dataChangeColumnVo.setClm_name(field.getName());
//                    dataChangeColumnVo.setClm_label(label.value());
//                }
//            }
//        }
//
//        return dataChangeColumnVo;
//    }

}