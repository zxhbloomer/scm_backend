package com.xinyirun.scm.core.bpm.serviceimpl.common.autocode;

import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.bpm.service.common.autocode.BpmIAutoCodeService;
import com.xinyirun.scm.core.bpm.service.platform.syscode.BpmISCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: TenantAutoCode
 * @Description: 自动生成编码：入库单类
 * @Author: zxh
 * @date: 2019/12/13
 * @Version: 1.0
 */
@Component
public class BpmProcessTemplatesAutoCodeServiceImpl implements BpmIAutoCodeService {

    @Autowired
    private BpmISCodeService service;

    @Override
    public SCodeEntity autoCode() {
        String type = DictConstant.DICT_SYS_CODE_TYPE_BPM_PROCESS_TEMPLATES;
        UpdateResultAo<SCodeEntity> upd = service.createCode(type);
        if(upd.isSuccess()){
            return upd.getData();
        }
        return null;
    }
}
