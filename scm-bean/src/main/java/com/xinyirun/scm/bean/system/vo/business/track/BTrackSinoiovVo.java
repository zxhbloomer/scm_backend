package com.xinyirun.scm.bean.system.vo.business.track;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class BTrackSinoiovVo implements Serializable {

    private static final long serialVersionUID = -3376888056337326786L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 用户名
     */
    private String user;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 私钥
     */
    private String srt;

    /**
     * 客户端 id
     */
    private String cid;

    /**
     * token
     */
    private String token;

    /**
     * 时间
     */
    private LocalDateTime dt;


}
