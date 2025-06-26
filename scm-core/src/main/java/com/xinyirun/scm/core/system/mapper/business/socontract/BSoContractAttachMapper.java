package com.xinyirun.scm.core.system.mapper.business.socontract;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.socontract.BSoContractAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.socontract.SoContractAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 销售合同附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BSoContractAttachMapper extends BaseMapper<BSoContractAttachEntity> {

    @Select("select * from b_so_contract_attach where so_contract_id = #{p1}")
    SoContractAttachVo selBySoContractId(@Param("p1") Integer id);
}
