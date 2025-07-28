package com.xinyirun.scm.core.system.mapper.business.so.socontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractDetailEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 销售合同明细表-商品 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@Repository
public interface BSoContractDetailMapper extends BaseMapper<BSoContractDetailEntity> {

    /**
     * 删除销售合同明细数据
     */
    @Delete(""
            + " DELETE FROM b_so_contract_detail t where t.so_contract_id =  #{so_contract_id}            "
            +"    ")
    void deleteBySoContractId(Integer so_contract_id);

    /**
     * 根据销售合同ID查询明细数据
     */
    @Select(""
            + " select * FROM b_so_contract_detail t where t.so_contract_id =  #{so_contract_id}            "
            +"    ")
    BSoContractDetailEntity selectBySoContractId(Integer so_contract_id);
}