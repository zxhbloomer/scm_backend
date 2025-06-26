package com.xinyirun.scm.core.api.service.business.v1.ap;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.ap.BApEntity;
import com.xinyirun.scm.bean.system.vo.business.ap.BApDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.ap.BApVo;

import java.util.List;

/**
 * <p>
 * 应付账款表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface ApiApService extends IService<BApEntity> {

    /**
     * 获取应付账款信息
     */
    BApVo selectById(Integer id);

    /**
     * 应付账款管理-业务单据信息
     */
    List<BApSourceAdvanceVo> printPoOrder(BApVo searchCondition);

    /**
     * 应付账款管理-付款信息
     */
    List<BApDetailVo> bankAccounts(BApVo searchCondition);
}
