package com.xinyirun.scm.core.api.service.master.v1.token;

import com.xinyirun.scm.bean.api.bo.steel.ApiTokenBo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwl
 * @since 2021-11-18
 */
public interface ApiTokenService{

    /**
     * 首次所有数据同步
     */
    void getToken(ApiTokenBo vo);

}
