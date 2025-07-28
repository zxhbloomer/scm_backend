package com.xinyirun.scm.core.system.mapper.business.po.aprefundpay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.aprefundpay.BApReFundPayDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPayDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 退款单明细表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundPayDetailMapper extends BaseMapper<BApReFundPayDetailEntity> {

    /**
     * 根据退款单ID查询退款单明细
     */
    @Select("""
            SELECT 
              t1.*,
              t2.name as account_name,
              t2.bank_name as bank_name,
              t2.account_number as account_number,
              GROUP_CONCAT(t3.NAME) AS bank_type_name
            FROM 
              b_ap_refund_pay_detail t1 
            LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
            LEFT JOIN m_bank_accounts_type t3 ON t2.id = t3.bank_id
            WHERE 
              t1.ap_refund_pay_id = #{id}
            GROUP BY 
              t1.ap_refund_pay_code
            """)
    List<BApReFundPayDetailVo> selectById(@Param("id") Integer apRefundPayId);

    /**
     * 根据ap_refund_code查询退款单明细
     */
    @Select("SELECT t1.* FROM b_ap_refund_pay_detail t1 WHERE t1.ap_refund_code = #{code}")
    List<BApReFundPayDetailVo> selectByCode(@Param("code") String code);

}
