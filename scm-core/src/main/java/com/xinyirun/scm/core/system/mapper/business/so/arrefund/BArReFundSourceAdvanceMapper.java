package com.xinyirun.scm.core.system.mapper.business.so.arrefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundSourceAdvanceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 应收退款关联单据表-源单-预收款 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArReFundSourceAdvanceMapper extends BaseMapper<BArReFundSourceAdvanceEntity> {

    /**
     * 根据应收退款ID删除预收款数据
     */
    @Delete("""
            DELETE FROM b_ar_refund_source_advance t where t.ar_refund_id = #{ar_refund_id}
            """)
    void deleteByArRefundId(Integer ar_refund_id);

    /**
     * 根据ar_refund_code查询预收款业务表
     */
    @Select("SELECT t1.* FROM b_ar_refund_source_advance t1 WHERE t1.ar_refund_code = #{code}")
    List<BArReFundSourceAdvanceVo> selectByCode(@Param("code") String code);

    /**
     * 根据ar_refund_id查询预收款源单
     */
    @Select("SELECT * FROM b_ar_refund_source_advance t WHERE t.ar_refund_id = #{ar_refund_id}")
    List<BArReFundSourceAdvanceVo> selectByArRefundId(@Param("ar_refund_id") Integer ar_refund_id);

    /**
     * 根据ar_refund_id统计本次退款金额的合计值
     * @param ar_refund_id 应收退款ID
     * @return 本次退款金额合计
     */
    @Select("SELECT COALESCE(SUM(order_amount),0) FROM b_ar_refund_source_advance WHERE ar_refund_id = #{ar_refund_id}")
    BigDecimal getSumRefundableAmount(@Param("ar_refund_id") Integer ar_refund_id);

    /**
     * 根据合同id，获取数据list
     * @param so_contract_id 合同id
     * @return 预收款关联单据VO列表
     */
    @Select("SELECT * FROM b_ar_refund_source_advance WHERE so_contract_id = #{so_contract_id}")
    List<BArReFundSourceAdvanceVo> selectByContractId(@Param("so_contract_id") Integer so_contract_id);


}