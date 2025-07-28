package com.xinyirun.scm.core.system.mapper.business.so.arrefundreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arrefundreceive.BArReFundReceiveDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveDetailVo;
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
public interface BArReFundReceiveDetailMapper extends BaseMapper<BArReFundReceiveDetailEntity> {

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
              b_ar_refund_receive_detail t1 
            LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
            LEFT JOIN m_bank_accounts_type t3 ON t2.id = t3.bank_id
            WHERE 
              t1.ar_refund_receive_id = #{id}
            GROUP BY 
              t1.ar_refund_receive_code
            """)
    List<BArReFundReceiveDetailVo> selectById(@Param("id") Integer arRefundReceiveId);

    /**
     * 根据ar_refund_code查询退款单明细
     */
    @Select("SELECT t1.* FROM b_ar_refund_receive_detail t1 WHERE t1.ar_refund_code = #{code}")
    List<BArReFundReceiveDetailVo> selectByCode(@Param("code") String code);

}