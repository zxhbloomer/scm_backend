package com.xinyirun.scm.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.bean.bpm.vo.field.FormFieldInfo;
import com.xinyirun.scm.bean.bpm.vo.field.Props;
import com.xinyirun.scm.bean.system.vo.business.out.BOutPlanListVo;
import com.xinyirun.scm.common.annotations.bpm.FieldMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FieldInfoGenerator {

    public static void main(String[] args) throws Exception {
        Class<?> clazz = BOutPlanListVo.class;
        Field[] fields = clazz.getDeclaredFields();
        List<FormFieldInfo> fieldList = new ArrayList<>();

        for (Field field : fields) {
            FieldMeta fieldMeta = field.getAnnotation(FieldMeta.class);
            if (fieldMeta != null) {
                FormFieldInfo fieldInfo = new FormFieldInfo();
                fieldInfo.setTitle(fieldMeta.title());
                fieldInfo.setField_name(field.getName());
                fieldInfo.setName(fieldMeta.fieldType());
                fieldInfo.setValue("");
                fieldInfo.setValueType(fieldMeta.valueType());

                Props props = new Props();
                props.setRequired(fieldMeta.required());
                fieldInfo.setProps(props);
                String field_id = "field" + UUID.randomUUID().toString();
                fieldInfo.setId(field_id);
                fieldList.add(fieldInfo);
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fieldList);
        System.out.println(json);
    }
}
