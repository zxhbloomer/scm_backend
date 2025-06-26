package com.xinyirun.scm.bean.bpm.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author LoveMyOrange
 * @create 2022-10-15 17:04
 */
@Data
public class AttachmentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4583473726839997131L;

    private String id;
    private String name;
    private String url;
}
