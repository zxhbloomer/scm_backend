package com.xinyirun.scm.bean.bpm.vo.field;

import lombok.Data;

/**
 * bpm使用的表单信息：暂时不支持子bean，只支持基本类型
 *
 * 如：
 * [
 *   {
 *     "title": "合同",
 *     "name": "TextInput",
 *     "value": "",
 *     "valueType": "String",
 *     "props": {
 *       "required": true,
 *       "enablePrint": true
 *     },
 *     "id": "field1871418546678"
 *   },
 *   {
 *     "title": "金额",
 *     "name": "NumberInput",
 *     "value": "",
 *     "valueType": "Number",
 *     "props": {
 *       "required": true
 *     },
 *     "id": "field7010918485111"
 *   },
 *   {
 *     "title": "性别",
 *     "name": "SelectInput",
 *     "value": "",
 *     "valueType": "String",
 *     "props": {
 *       "required": true,
 *       "options": [
 *         "选项1",
 *         "选项2"
 *       ]
 *     },
 *     "id": "field9227018521093"
 *   }
 * ]
 */
@Data
public class FormFieldInfo {
    // 字段名称：中文
    private String title;
    // 字段名称：英文
    private String field_name;
    // 字段类型
    private String name;
    // 值
    private String value;
    // 值类型
    private String valueType;
    // 字段属性：是否必须输入；下拉选项内容
    private Props props;
    // 字段id
    private String id;
}
