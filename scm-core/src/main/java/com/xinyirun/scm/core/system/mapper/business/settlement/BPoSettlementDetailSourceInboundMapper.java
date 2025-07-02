package com.xinyirun.scm.core.system.mapper.business.settlement;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.settlement.BPoSettlementDetailSourceInboundEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购结算明细-源单-按采购入库结算 Mapper 接口
 */
@Mapper
public interface BPoSettlementDetailSourceInboundMapper extends BaseMapper<BPoSettlementDetailSourceInboundEntity> {

}