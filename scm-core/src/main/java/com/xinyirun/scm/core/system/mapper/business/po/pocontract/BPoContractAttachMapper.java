package com.xinyirun.scm.core.system.mapper.business.po.pocontract;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.pocontract.BPoContractAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.BPoContractAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 采购合同附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BPoContractAttachMapper extends BaseMapper<BPoContractAttachEntity> {

    @Select("""
            -- 根据采购合同ID查询附件信息
            select * from b_po_contract_attach 
            -- p1: 采购合同ID参数
            where po_contract_id = #{p1}
            """)
    BPoContractAttachVo selectByPoContractId(@Param("p1") Integer id);
}
