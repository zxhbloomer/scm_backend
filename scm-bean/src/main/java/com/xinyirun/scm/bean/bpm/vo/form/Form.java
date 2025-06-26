package com.xinyirun.scm.bean.bpm.vo.form;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author : willian fu
 * @date : 2022/7/7
 */
@Data
public class Form implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;

    private String id;
    private String icon;
    private String name;
    private Map<String, Object> props;
    private String title;
    private Object value;
    private ValueType valueType;
    private FormPerm.PermEnum perm;
}
