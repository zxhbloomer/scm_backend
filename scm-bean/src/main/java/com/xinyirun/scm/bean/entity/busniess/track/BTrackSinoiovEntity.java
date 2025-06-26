package com.xinyirun.scm.bean.entity.busniess.track;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_track_sinoiov")
public class BTrackSinoiovEntity implements Serializable {

    private static final long serialVersionUID = 5732987501349581143L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @TableField("user")
    private String user;

    /**
     * 密码
     */
    @TableField("pwd")
    private String pwd;

    /**
     * 客户端 id
     */
    @TableField("cid")
    private String cid;

    /**
     * token
     */
    @TableField("token")
    private String token;

    /**
     * 时间
     */
    @TableField("dt")
    private LocalDateTime dt;


}
