package com.xinyirun.scm.core.system.mapper.business.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.appay.BApPaySourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.appay.BApPaySourceAdvanceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 付款来源预付表 Mapper 接口
 */
@Mapper
public interface BApPaySourceAdvanceMapper extends BaseMapper<BApPaySourceAdvanceEntity> {
    /**
     * 根据合同id，获取数据list
     * @param po_contract_id 合同id
     * @return 付款来源预付VO列表
     */
    @Select("SELECT * FROM b_ap_pay_source_advance WHERE po_contract_id = #{po_contract_id}")
    List<BApPaySourceAdvanceVo> selectByContractId(@Param("po_contract_id") Integer po_contract_id);

    /**
     * 根据ap_pay_id查询聚合数据
     * @param apPayId 付款单ID
     * @return 聚合后的付款来源预付VO
     */
    @Select("SELECT ap_pay_id, " +
            "GROUP_CONCAT(po_contract_id) as po_contract_id_gc, " +
            "GROUP_CONCAT(po_contract_code) as po_contract_code_gc, " +
            "GROUP_CONCAT(po_order_code) as po_order_code_gc, " +
            "GROUP_CONCAT(po_order_id) as po_order_id_gc " +
            "FROM b_ap_pay_source_advance WHERE ap_pay_id = #{apPayId}")
    BApPaySourceAdvanceVo selectAggregatedByApPayId(@Param("apPayId") Integer apPayId);
} 