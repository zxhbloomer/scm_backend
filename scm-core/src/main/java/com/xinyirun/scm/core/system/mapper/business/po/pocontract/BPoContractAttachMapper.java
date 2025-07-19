package com.xinyirun.scm.core.system.mapper.business.po.pocontract;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractAttachVo;
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

    @Select("select * from b_po_contract_attach where po_contract_id = #{p1}")
    PoContractAttachVo selectByPoContractId(@Param("p1") Integer id);
}
