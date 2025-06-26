package com.xinyirun.scm.core.api.service.business.v1.soorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.soorder.BSoOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.soorder.SoOrderVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import java.util.List;

/**
 * <p>
 * 销售订单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface ApiSoOrderService extends IService<BSoOrderEntity> {

    /**
     * 获取采购合同信息
     */
    SoOrderVo selectById(Integer id);

    /**
     * 获取合同附件
     */
    List<SFileInfoVo> getprintEnterpriseLicense(SoOrderVo searchCondition);

    /**
     * 获取销售订单商品信息
     */
    List<SoOrderDetailVo> selectGoodsById(Integer poContractId);
}
