package com.xinyirun.scm.bean.system.vo.business.track.bestfriend;

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
public class BTrackBestFriend56Vo implements Serializable {

    private static final long serialVersionUID = -2850694522449185011L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 登录名
     */
    private String account_num;

    /**
     * 密吗
     */
    private String password;

    /**
     * 是否增值服务
     */
    private Boolean is_added_action;

}
