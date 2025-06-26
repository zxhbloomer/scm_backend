package com.xinyirun.scm.bean.bpm.vo.form;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : willian fu
 * @date : 2022/8/22
 */
@Data
public class FormPerm  implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;

    private String id;
    private PermEnum perm;
    private String title;

    //表单权限枚举，只读，可编辑，隐藏
    public enum PermEnum{
        R, E, H
    }
}
