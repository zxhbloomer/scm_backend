package com.xinyirun.scm.core.whapp.service.autocode;

import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;

/**
 * @ClassName: IAutoCodeService
 * @Description: 自动生成编码的接口
 * @Author: zxh
 * @date: 2019/12/13
 * @Version: 1.0
 */
public interface WhAppIAutoCodeService {

    SCodeEntity autoCode();
}
