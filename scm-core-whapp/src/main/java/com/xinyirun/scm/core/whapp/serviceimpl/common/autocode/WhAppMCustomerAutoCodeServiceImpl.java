package com.xinyirun.scm.core.whapp.serviceimpl.common.autocode;

import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.whapp.service.autocode.WhAppIAutoCodeService;
import com.xinyirun.scm.core.whapp.service.sys.syscode.WhAppISCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: WhAppMCustomerAutoCodeServiceImpl
 * @Description: 自动生成编码：客户类
 */
@Component
public class WhAppMCustomerAutoCodeServiceImpl implements WhAppIAutoCodeService {

    @Autowired
    WhAppISCodeService service;

    @Override
    public SCodeEntity autoCode() {
        String type = DictConstant.DICT_SYS_CODE_TYPE_M_CUSTOMER;
        AppUpdateResultAo<SCodeEntity> upd = service.createCode(type);
        if(upd.isSuccess()){
            return upd.getData();
        }
        return null;
    }
}
