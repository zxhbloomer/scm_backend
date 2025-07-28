package com.xinyirun.scm.core.system.mapper.business.so.ar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.ar.BArSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceAdvanceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 应收账款关联单据表-源单-预收款 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArSourceAdvanceMapper extends BaseMapper<BArSourceAdvanceEntity> {

    /**
     * 根据应收账款ID删除预收款数据
     */
    @Delete("""
            DELETE FROM b_ar_source_advance t where t.ar_id = #{ar_id}
            """)
    void deleteByArId(Integer ar_id);

    /**
     * 根据ar_code查询预收款业务表
     */
    @Select("SELECT t1.* FROM b_ar_source_advance t1 WHERE t1.ar_code = #{code}")
    List<BArSourceAdvanceVo> selectByCode(@Param("code") String code);

    /**
     * 根据ar_id查询预收款源单
     */
    @Select("SELECT * FROM b_ar_source_advance t WHERE t.ar_id = #{ar_id}")
    List<BArSourceAdvanceVo> selectByArId(@Param("ar_id") Integer ar_id);

    /**
     * 根据ar_id统计本次收款金额的合计值
     * @param ar_id 应收账款ID
     * @return 本次收款金额合计
     */
    @Select("SELECT COALESCE(SUM(order_amount),0) FROM b_ar_source_advance WHERE ar_id = #{ar_id}")
    BigDecimal getSumReceivableAmount(@Param("ar_id") Integer ar_id);

    /**
     * 根据合同id，获取数据list
     * @param so_contract_id 合同id
     * @return 预收款关联单据VO列表
     */
    @Select("SELECT * FROM b_ar_source_advance WHERE so_contract_id = #{so_contract_id}")
    List<BArSourceAdvanceVo> selectByContractId(@Param("so_contract_id") Integer so_contract_id);

}