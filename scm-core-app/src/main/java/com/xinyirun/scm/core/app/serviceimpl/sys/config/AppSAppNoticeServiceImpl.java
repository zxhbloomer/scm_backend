package com.xinyirun.scm.core.app.serviceimpl.sys.config;

import com.xinyirun.scm.bean.app.vo.sys.config.AppNoticeVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppNoticeEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.app.mapper.sys.config.AppSAppNoticeMapper;
import com.xinyirun.scm.core.app.service.sys.config.AppISAppNoticeService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
@Service
public class AppSAppNoticeServiceImpl extends AppBaseServiceImpl<AppSAppNoticeMapper, SAppNoticeEntity> implements AppISAppNoticeService {

    @Autowired
    private AppSAppNoticeMapper mapper;

    @Override
    public AppNoticeVo get(AppNoticeVo searchCondition) {
        // 查询最后一个强制更新版本
        AppNoticeVo latest = mapper.getLatestForceVersion();
        AppNoticeVo current = mapper.selectOne();

        if (StringUtils.isEmpty(searchCondition.getVersion_code())) {
            current.setType(DictConstant.DICT_S_APP_NOTICE_TYPE1);
        }

        if (latest != null && StringUtils.isNotEmpty(searchCondition.getVersion_code()) && StringUtils.isNotEmpty(latest.getVersion_code())) {
            if (Double.parseDouble(searchCondition.getVersion_code()) < Double.parseDouble(latest.getVersion_code())) {
                current.setType(DictConstant.DICT_S_APP_NOTICE_TYPE1);
            }
        }



        return current;
    }
}
