package com.xinyirun.scm.core.system.service.mail;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.mail.SMailConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.mail.SMailConfigVo;

public interface ISMailConfigService extends IService<SMailConfigEntity> {

    /**
     * 根据编码查询
     * @param configCode 编码
     * @return SMailConfigEntity
     */
    SMailConfigEntity selectByCode(String configCode);

    /**
     * 新增
     * @param entity 实体
     * @return InsertResultAo
     */
    InsertResultAo<SMailConfigVo> insert(SMailConfigVo entity);
}
