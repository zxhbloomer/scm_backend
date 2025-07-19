package com.xinyirun.scm.core.api.service.business.v1.pocontract;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import java.util.List;

/**
 * <p>
 * 采购合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface ApiPoContractService extends IService<BPoContractEntity> {

    /**
     * 获取采购合同信息
     */
    PoContractVo selectById(Integer id);

    /**
     * 获取合同附件
     */
    List<SFileInfoVo> getprintEnterpriseLicense(PoContractVo searchCondition);

    /**
     * 获取采购合同商品信息
     */
    List<PoContractDetailVo> selectGoodsById(Integer poContractId);
}
