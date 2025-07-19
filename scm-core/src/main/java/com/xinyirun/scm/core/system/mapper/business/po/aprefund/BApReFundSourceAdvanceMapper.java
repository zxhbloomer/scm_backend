package com.xinyirun.scm.core.system.mapper.business.po.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.aprefund.BApReFundSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundSourceAdvanceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 应付退款关联单据表-源单-预收款 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundSourceAdvanceMapper extends BaseMapper<BApReFundSourceAdvanceEntity> {

    /**
     * 根据应付退款ID删除预收款数据
     */
    @Delete("""
            DELETE FROM b_ap_refund_source_advance t where t.ap_refund_id = #{ap_refund_id}
            """)
    void deleteByApRefundId(Integer ap_refund_id);

    /**
     * 根据ap_refund_code查询预付款业务表
     */
    @Select("SELECT t1.* FROM b_ap_refund_source_advance t1 WHERE t1.ap_refund_code = #{code}")
    List<BApReFundSourceAdvanceVo> selectByCode(@Param("code") String code);

    /**
     * 根据ap_refund_id查询预收款源单
     */
    @Select("SELECT * FROM b_ap_refund_source_advance t WHERE t.ap_refund_id = #{ap_refund_id}")
    List<BApReFundSourceAdvanceVo> selectByApRefundId(@Param("ap_refund_id") Integer ap_refund_id);

    /**
     * 根据ap_refund_id统计本次退款金额的合计值
     * @param ap_refund_id 应付退款ID
     * @return 本次退款金额合计
     */
    @Select("SELECT COALESCE(SUM(order_amount),0) FROM b_ap_refund_source_advance WHERE ap_refund_id = #{ap_refund_id}")
    BigDecimal getSumRefundableAmount(@Param("ap_refund_id") Integer ap_refund_id);

    /**
     * 根据合同id，获取数据list
     * @param po_contract_id 合同id
     * @return 预付款关联单据VO列表
     */
    @Select("SELECT * FROM b_ap_refund_source_advance WHERE po_contract_id = #{po_contract_id}")
    List<BApReFundSourceAdvanceVo> selectByContractId(@Param("po_contract_id") Integer po_contract_id);


}
