package com.xinyirun.scm.bean.bpm.vo.props;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : willian fu
 * @date : 2022/7/15
 */
@Data
public class DelayProps implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;
    private Type type;
    private Integer time;
    private String unit;
    private String dateTime;

    public enum Type{
        FIXED, PRECISE, AUTO;
    }
}
