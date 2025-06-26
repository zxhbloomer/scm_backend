package com.xinyirun.scm.bean.system.vo.business.track;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class BTrackGsh56Vo implements Serializable {

    private static final long serialVersionUID = 5176248481317286734L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 用户唯一标识
     */
    private String open_id;

    /**
     * 密钥
     */
    private String secret_key;

}
