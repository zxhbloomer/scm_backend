package com.xinyirun.scm.core.api.service.business.v1.aprefund;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundDetailVo;

import java.util.List;

/**
 * <p>
 * 应付退款表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface ApiApReFundService extends IService<BApReFundEntity> {

    /**
     * 获取退款信息
     */
    BApReFundVo selectById(Integer id);

    /**
     * 应付账款管理-业务单据信息
     */
    List<BApReFundSourceAdvanceVo> printPoOrder(BApReFundVo searchCondition);

    /**
     * 应付账款管理-付款信息
     */
    List<BApReFundDetailVo> bankAccounts(BApReFundVo searchCondition);
}
