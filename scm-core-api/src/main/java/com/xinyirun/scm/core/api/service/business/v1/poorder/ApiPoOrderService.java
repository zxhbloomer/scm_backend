package com.xinyirun.scm.core.api.service.business.v1.poorder;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.PoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import java.util.List;

/**
 * <p>
 * 采购订单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface ApiPoOrderService extends IService<BPoOrderEntity> {

    /**
     * 获取采购合同信息
     */
    PoOrderVo selectById(Integer id);

    /**
     * 获取合同附件
     */
    List<SFileInfoVo> getprintEnterpriseLicense(PoOrderVo searchCondition);

    /**
     * 获取采购合同商品信息
     */
    List<PoOrderDetailVo> selectGoodsById(Integer poContractId);
}
