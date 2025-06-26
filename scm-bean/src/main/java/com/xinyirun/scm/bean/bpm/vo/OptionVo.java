package com.xinyirun.scm.bean.bpm.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author LoveMyOrange
 * @create 2022-10-16 9:42
 */
@Data
public class OptionVo implements Serializable {
    @Serial
    private static final long serialVersionUID = -1547941675420114768L;
    private String comments;
    private String userId;
    private String userName;
    private Date createTime;
}
