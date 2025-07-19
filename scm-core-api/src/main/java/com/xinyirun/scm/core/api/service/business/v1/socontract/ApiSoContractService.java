package com.xinyirun.scm.core.api.service.business.v1.socontract;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.SoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.SoContractVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import java.util.List;

/**
 * <p>
 * 销售合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface ApiSoContractService extends IService<BSoContractEntity> {

    /**
     * 获取采购合同信息
     */
    SoContractVo selectById(Integer id);

    /**
     * 获取合同附件
     */
    List<SFileInfoVo> getprintEnterpriseLicense(SoContractVo searchCondition);

    /**
     * 获取采购合同商品信息
     */
    List<SoContractDetailVo> selectGoodsById(Integer soContractId);
}
