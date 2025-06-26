package com.xinyirun.scm.core.system.mapper.business.fund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.fund.BFundMonitorEntity;
import com.xinyirun.scm.bean.system.vo.business.fund.BFundMonitorVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 资金流水监控表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
public interface BFundMonitorMapper extends BaseMapper<BFundMonitorEntity> {

}
