package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.po.ap.BApSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApSourceAdvanceVo;
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
            -- 根据应付账款主表ID删除所有相关的预收款数据
            DELETE FROM b_ap_source_advance t 
            -- ap_id: 应付账款主表ID参数
            where t.ap_id = #{ap_id}
            """)
    void deleteByApId(Integer ap_id);

    /**
     * 根据ap_code查询预付款业务表
     */
    @Select("""
            -- 根据应付账款编号查询预付款业务信息
            SELECT t1.* FROM b_ap_source_advance t1 
            -- code: 应付账款主表编号参数
            WHERE t1.ap_code = #{code}
            """)
    List<BApSourceAdvanceVo> selectByCode(@Param("code") String code);

    /**
     * 根据ap_id查询预收款源单
     */
    @Select("""
            -- 根据应付账款主表ID查询预收款源单信息
            SELECT * FROM b_ap_source_advance t 
            -- ap_id: 应付账款主表ID参数
            WHERE t.ap_id = #{ap_id}
            """)
    List<BApSourceAdvanceVo> selectByApId(@Param("ap_id") Integer ap_id);

    /**
     * 根据ap_id统计本次付款金额的合计值
     * @param ap_id 应付账款ID
     * @return 本次付款金额合计
     */
    @Select("""
            -- 根据应付账款主表ID统计本次付款金额的合计值
            SELECT COALESCE(SUM(order_amount),0) FROM b_ap_source_advance 
            -- ap_id: 应付账款主表ID参数
            -- order_amount: 本次申请金额
            WHERE ap_id = #{ap_id}
            """)
    BigDecimal getSumPayableAmount(@Param("ap_id") Integer ap_id);

    /**
     * 根据合同id，获取数据list
     * @param po_contract_id 合同id
     * @return 预付款关联单据VO列表
     */
    @Select("""
            -- 根据采购合同ID查询预付款关联单据信息
            SELECT * FROM b_ap_source_advance 
            -- po_contract_id: 采购合同ID参数
            WHERE po_contract_id = #{po_contract_id}
            """)
    List<BApSourceAdvanceVo> selectByContractId(@Param("po_contract_id") Integer po_contract_id);

}
