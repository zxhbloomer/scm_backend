package com.xinyirun.scm.core.app.serviceimpl.common.autocode;

import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.app.service.common.autocode.AppIAutoCodeService;
import com.xinyirun.scm.core.app.service.sys.platform.syscode.AppISCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: TenantAutoCode
 * @Description: 自动生成编码：收货单类
 * @Author: zxh
 * @date: 2019/12/13
 * @Version: 1.0
 */
@Component
public class AppBReceiveAutoCodeServiceImpl implements AppIAutoCodeService {

    @Autowired
    AppISCodeService service;

    @Override
    public SCodeEntity autoCode() {
        String type = DictConstant.DICT_SYS_CODE_TYPE_B_RECEIVE;
        AppUpdateResultAo<SCodeEntity> upd = service.createCode(type);
        if(upd.isSuccess()){
            return upd.getData();
        }
        return null;
    }
}
