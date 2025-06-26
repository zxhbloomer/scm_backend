package com.xinyirun.scm.bean.app.vo.sys.config;

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
 * @since 2022-02-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class AppLogoVo implements Serializable {

    private static final long serialVersionUID = 4335747798092493015L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 是否禁用(1:true-未启用,0:false-已启用)
     */
    private Boolean is_enable;

    /**
     * 图片url
     */
    private String url;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


}
