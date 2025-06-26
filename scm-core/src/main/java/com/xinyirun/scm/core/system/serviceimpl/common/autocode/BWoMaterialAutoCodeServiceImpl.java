package com.xinyirun.scm.core.system.serviceimpl.common.autocode;

import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.service.common.autocode.IAutoCodeService;
import com.xinyirun.scm.core.system.service.sys.platform.syscode.ISCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: Wang Qianfeng
 * @DATE: 2022/12/27 : 13:25
 * @Description: 生产管理, 自动生成编码
 **/
@Component
public class BWoMaterialAutoCodeServiceImpl implements IAutoCodeService {

    @Autowired
    private ISCodeService service;

    @Override
    public SCodeEntity autoCode() {
        String type = DictConstant.DICT_B_WO_MATERiAL;
        UpdateResultAo<SCodeEntity> upd = service.createCode(type);
        if(upd.isSuccess()){
            return upd.getData();
        }
        return null;
    }
}
