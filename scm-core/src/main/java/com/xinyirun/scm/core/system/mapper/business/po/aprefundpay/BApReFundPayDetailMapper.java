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
            -- 根据退款单支付ID查询退款单支付明细信息，包含银行账户相关信息
            SELECT 
              -- t1.*: 退款单支付明细表所有字段
              t1.*,
              -- account_name: 银行账户名称
              t2.name as account_name,
              -- bank_name: 银行名称
              t2.bank_name as bank_name,
              -- account_number: 银行账号
              t2.account_number as account_number,
              -- bank_type_name: 银行账户类型名称（GROUP_CONCAT聚合）
              GROUP_CONCAT(t3.NAME) AS bank_type_name
            FROM 
              -- 主表：退款单支付明细表
              b_ap_refund_pay_detail t1 
            -- 关联银行账户表：获取银行账户基本信息
            LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
            -- 关联银行账户类型表：获取账户用途类型
            LEFT JOIN m_bank_accounts_type t3 ON t2.id = t3.bank_id
            WHERE 
              -- id: 退款单支付主表ID参数精确匹配
              t1.ap_refund_pay_id = #{id}
            -- GROUP BY: 按退款单支付编号分组，防止GROUP_CONCAT造成的数据重复
            GROUP BY 
              t1.ap_refund_pay_code
            """)
    List<BApReFundPayDetailVo> selectById(@Param("id") Integer apRefundPayId);

    /**
     * 根据ap_refund_code查询退款单明细
     */
    @Select("""
            -- 根据应付退款编号查询退款单支付明细信息
            SELECT t1.* FROM b_ap_refund_pay_detail t1 
            -- code: 应付退款编号参数，精确匹配
            WHERE t1.ap_refund_code = #{code}
            """)
    List<BApReFundPayDetailVo> selectByCode(@Param("code") String code);

}
