package com.xinyirun.scm.core.system.serviceimpl.sys.app.token;

import com.xinyirun.scm.bean.system.bo.user.api.ApiUserBo;
import com.xinyirun.scm.bean.system.bo.user.api.ApiKeyAndSecretKeyBo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiAuthException;
import com.xinyirun.scm.core.system.mapper.sys.app.SAppConfigMapper;
import com.xinyirun.scm.core.system.service.sys.app.token.ISTokenService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Service
public class STokenServiceImpl extends BaseServiceImpl<SAppConfigMapper, SAppConfigEntity> implements ISTokenService {

    @Autowired
    private SAppConfigMapper mapper;

    @Override
    public UserDetails authenticateToken(ApiKeyAndSecretKeyBo key_bo) {

        // check
        checkAppKeyAndSecretKey(key_bo);

        // 查询数据
        SAppConfigEntity sAppConfigEntity = getDataByAppKeyAndSecretKey(key_bo);
        if(sAppConfigEntity == null){
            throw new ApiAuthException(ApiResultEnum.AUTH_DATA_IS_NULL);
        }
        List<String> permissions = new ArrayList<>();
        permissions.addAll(Arrays.asList(new String[]{"ROLE_USER"}));

        return new ApiUserBo(
                sAppConfigEntity.getId(),
                sAppConfigEntity.getApp_key(),
                sAppConfigEntity.getSecret_key(),
                AuthorityUtils.createAuthorityList(permissions.toArray(new String[]{})))   // 加载权限的关键部分
                .setUser(sAppConfigEntity);
    }

    /**
     * check app_key和secret_key数据不能为空
     * @param key_bo
     * @return
     */
    private boolean checkAppKeyAndSecretKey(ApiKeyAndSecretKeyBo key_bo) {
        if (StringUtils.isEmpty(key_bo.getApp_key())) {
            throw new ApiAuthException(ApiResultEnum.NOT_NULL_APP_KEY);
        }
        if (StringUtils.isEmpty(key_bo.getSecret_key())) {
            throw new ApiAuthException(ApiResultEnum.NOT_NULL_SECRET_KEY);
        }
        // 未找到数据
        if(getDataByAppKey(key_bo.getApp_key()) == null){
            throw new ApiAuthException(ApiResultEnum.APP_KEY_DATA_IS_NULL);
        }
        return true;
    }

    /**
     * 根据登录的token获取entity
     *
     * @param key_bo
     * @return
     */
    @Override
    public SAppConfigEntity getDataByAppKeyAndSecretKey(ApiKeyAndSecretKeyBo key_bo){
        return mapper.getDataByAppKeyAndSecurityKey(key_bo.getApp_key(),key_bo.getSecret_key());
    }

    /**
     * 根据登录的token获取entity
     *
     * @param app_key
     * @return
     */
    @Override
    public SAppConfigEntity getDataByAppKey(String app_key) {
        return mapper.getDataByAppKey(app_key);
    }
}
