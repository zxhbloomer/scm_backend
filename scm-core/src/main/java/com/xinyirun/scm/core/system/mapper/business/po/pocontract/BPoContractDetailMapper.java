package com.xinyirun.scm.core.system.mapper.business.po.pocontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractDetailEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 采购合同明细表-商品 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BPoContractDetailMapper extends BaseMapper<BPoContractDetailEntity> {

    /**
     * 删除当天数据
     */
    @Delete("""
            -- 根据采购合同主表ID删除明细数据
            DELETE FROM b_po_contract_detail t 
            -- po_contract_id: 采购合同主表ID参数
            where t.po_contract_id = #{po_contract_id}
            """)
    void deleteByPoContractId(Integer po_contract_id);


    /**
     * 查询明细数据
     */
    @Select("""
            -- 根据采购合同主表ID查询明细数据
            select * FROM b_po_contract_detail t 
            -- po_contract_id: 采购合同主表ID参数
            where t.po_contract_id = #{po_contract_id}
            """)
    BPoContractDetailEntity selectByPoContractId(Integer po_contract_id);
}
