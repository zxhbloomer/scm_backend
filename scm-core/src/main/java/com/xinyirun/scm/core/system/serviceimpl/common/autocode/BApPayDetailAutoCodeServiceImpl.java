package com.xinyirun.scm.core.system.serviceimpl.common.autocode;

import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.service.common.autocode.IAutoCodeService;
import com.xinyirun.scm.core.system.service.sys.platform.syscode.ISCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: BApPayDetailAutoCodeServiceImpl
 * @Description: 自动生成编码：付款单明细
 */
@Component
public class BApPayDetailAutoCodeServiceImpl implements IAutoCodeService {

    @Autowired
    ISCodeService service;

    /**
     * 自动生成付款单明细编码
     * @return 生成的编码实体
     */
    @Override
    public SCodeEntity autoCode() {
        // 使用付款单明细的字典类型生成编码
        String type = DictConstant.DICT_SYS_CODE_TYPE_B_AP_PAY_DETAIL;
        UpdateResultAo<SCodeEntity> upd = service.createCode(type);
        if(upd.isSuccess()){
            return upd.getData();
        }
        return null;
    }
} 