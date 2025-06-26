package com.xinyirun.scm.core.system.mapper.business.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.ap.BApSourceEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApRefundSourceEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 应付退款关联单据表-源单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundSourceMapper extends BaseMapper<BApRefundSourceEntity> {

}
