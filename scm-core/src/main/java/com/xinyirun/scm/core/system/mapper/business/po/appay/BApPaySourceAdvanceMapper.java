package com.xinyirun.scm.core.system.mapper.business.po.appay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.appay.BApPaySourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.appay.BApPaySourceAdvanceVo;
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
    @Select("""
            -- 根据采购合同ID查询付款来源预付信息
            SELECT * FROM b_ap_pay_source_advance 
            -- #{po_contract_id}: 采购合同ID
            WHERE po_contract_id = #{po_contract_id}
            """)
    List<BApPaySourceAdvanceVo> selectByContractId(@Param("po_contract_id") Integer po_contract_id);

    /**
     * 根据ap_pay_id查询聚合数据
     * @param apPayId 付款单ID
     * @return 聚合后的付款来源预付VO
     */
    @Select("""
            -- 根据付款单ID查询聚合的付款来源预付数据
            SELECT ap_pay_id, 
                   -- po_contract_id: 采购合同ID，使用GROUP_CONCAT聚合
                   GROUP_CONCAT(po_contract_id) as po_contract_id_gc, 
                   -- po_contract_code: 采购合同编号，使用GROUP_CONCAT聚合
                   GROUP_CONCAT(po_contract_code) as po_contract_code_gc, 
                   -- po_order_code: 采购订单编号，使用GROUP_CONCAT聚合
                   GROUP_CONCAT(po_order_code) as po_order_code_gc, 
                   -- po_order_id: 采购订单ID，使用GROUP_CONCAT聚合
                   GROUP_CONCAT(po_order_id) as po_order_id_gc 
            FROM b_ap_pay_source_advance 
            -- #{apPayId}: 付款单主表ID
            WHERE ap_pay_id = #{apPayId}
            """)
    BApPaySourceAdvanceVo selectAggregatedByApPayId(@Param("apPayId") Integer apPayId);
} 