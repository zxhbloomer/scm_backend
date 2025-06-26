package com.xinyirun.scm.bean.bpm.vo;

//import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author LoveMyOrange
 * @create 2022-10-16 9:42
 */
//@ApiModel("评论的VO")
@Data
public class CommentVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 2687473935676839354L;
    private String comments;
    private String userId;
    private String userName;
    private Date createTime;
}
