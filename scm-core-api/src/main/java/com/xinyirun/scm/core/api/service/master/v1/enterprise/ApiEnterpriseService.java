package com.xinyirun.scm.core.api.service.master.v1.enterprise;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2021-09-23
 */
public interface ApiEnterpriseService extends IService<MEnterpriseEntity> {

    /**
     * 获取详情
     */
    MEnterpriseVo getDetail(MEnterpriseVo searchCondition);

    /**
     * 获取营业执照
     */
    List<SFileInfoVo> getprintEnterpriseLicense(MEnterpriseVo searchCondition);

}
