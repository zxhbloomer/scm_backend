package com.xinyirun.scm.core.system.service.sys.app.token;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.bo.user.api.ApiKeyAndSecretKeyBo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
public interface ISTokenService extends IService<SAppConfigEntity> {

    /**
     * api 权限入口
     * @param key_bo
     * @return
     */
    public UserDetails authenticateToken(ApiKeyAndSecretKeyBo key_bo);

    /**
     * 根据登录的app_key,secret_key,查询是否存在
     *
     * @param app_key
     * @return
     */
    public SAppConfigEntity getDataByAppKey(String app_key);

    /**
     * 根据登录的app_key,secret_key,查询是否存在
     *
     * @param key_bo
     * @return
     */
    public SAppConfigEntity getDataByAppKeyAndSecretKey(ApiKeyAndSecretKeyBo key_bo);
}
