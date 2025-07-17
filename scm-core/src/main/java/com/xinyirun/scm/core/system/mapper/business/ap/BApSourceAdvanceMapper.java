package com.xinyirun.scm.core.system.mapper.business.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.ap.BApSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 应付账款关联单据表-源单-预收款 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApSourceAdvanceMapper extends BaseMapper<BApSourceAdvanceEntity> {

    /**
     * 根据应付账款ID删除预收款数据
     */
    @Delete("""
            DELETE FROM b_ap_source_advance t where t.ap_id = #{ap_id}
            """)
    void deleteByApId(Integer ap_id);

    /**
     * 根据ap_code查询预付款业务表
     */
    @Select("SELECT t1.* FROM b_ap_source_advance t1 WHERE t1.ap_code = #{code}")
    List<BApSourceAdvanceVo> selectByCode(@Param("code") String code);

    /**
     * 根据ap_id查询预收款源单
     */
    @Select("SELECT * FROM b_ap_source_advance t WHERE t.ap_id = #{ap_id}")
    List<BApSourceAdvanceVo> selectByApId(@Param("ap_id") Integer ap_id);

    /**
     * 根据ap_id统计本次付款金额的合计值
     * @param ap_id 应付账款ID
     * @return 本次付款金额合计
     */
    @Select("SELECT COALESCE(SUM(order_amount),0) FROM b_ap_source_advance WHERE ap_id = #{ap_id}")
    BigDecimal getSumPayableAmount(@Param("ap_id") Integer ap_id);

    /**
     * 根据合同id，获取数据list
     * @param po_contract_id 合同id
     * @return 预付款关联单据VO列表
     */
    @Select("SELECT * FROM b_ap_source_advance WHERE po_contract_id = #{po_contract_id}")
    List<BApSourceAdvanceVo> selectByContractId(@Param("po_contract_id") Integer po_contract_id);

}
