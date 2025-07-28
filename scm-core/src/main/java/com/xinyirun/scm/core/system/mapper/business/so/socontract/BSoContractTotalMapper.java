package com.xinyirun.scm.core.system.mapper.business.so.socontract;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractTotalEntity;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractTotalVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 销售合同表-财务数据汇总 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-27
 */
@Repository
public interface BSoContractTotalMapper extends BaseMapper<BSoContractTotalEntity> {

    /**
     * 根据销售合同主表ID查询销售合同财务信息
     */
    @Select("select * from b_so_contract_total where so_contract_id = #{soContractId}")
    BSoContractTotalVo selectBySoContractId(@Param("soContractId") Integer soContractId);

    /**
     * 根据应收账款ID查询对应的销售合同信息
     * @param arId 应收账款ID
     * @return BSoContractVo
     */
    @Select("SELECT " +
            "  t3.* " +
            "FROM " +
            "  b_ar_total t1 " +
            "  LEFT JOIN b_ar_source_advance t2 ON t2.ar_id = t1.ar_id " +
            "  LEFT JOIN b_so_contract t3 ON t3.contract_code = t2.so_contract_code " +
            "WHERE TRUE " +
            "  AND t1.ar_id = #{arId}")
    BSoContractVo getSoContractTotalByArId(@Param("arId") Integer arId);

    /**
     * 更新销售合同预收款汇总数据（优化版）
     * 根据合同ID汇总其下所有销售订单的预收款数据到合同级别
     * 使用一次JOIN子查询汇总所有字段，避免多次重复查询
     * @param contractId 合同ID
     * @return 更新记录数
     */
    @Update("<script>                                                                                                                                           " +
            "    UPDATE b_so_contract_total t1                                                                                                                   " +
            "    INNER JOIN (                                                                                                                                    " +
            "        SELECT                                                                                                                                      " +
            "            t3.so_contract_id,                                                                                                                      " +
            "            COALESCE(SUM(t2.advance_unreceive_total), 0) AS sum_advance_unreceive_total,                                                           " +
            "            COALESCE(SUM(t2.advance_received_total), 0) AS sum_advance_received_total,                                                             " +
            "            COALESCE(SUM(t2.advance_receive_total), 0) AS sum_advance_receive_total,                                                               " +
            "            COALESCE(SUM(t2.advance_stopreceive_total), 0) AS sum_advance_stopreceive_total                                                        " +
            "        FROM b_so_order_total t2                                                                                                                    " +
            "        INNER JOIN b_so_order t3 ON t2.so_order_id = t3.id                                                                                         " +
            "        WHERE t3.so_contract_id = #{contractId}                                                                                                     " +
            "        GROUP BY t3.so_contract_id                                                                                                                  " +
            "    ) AS summary ON t1.so_contract_id = summary.so_contract_id                                                                                      " +
            "    SET                                                                                                                                             " +
            "        t1.advance_unreceive_total = summary.sum_advance_unreceive_total,                                                                           " +
            "        t1.advance_received_total = summary.sum_advance_received_total,                                                                             " +
            "        t1.advance_receive_total = summary.sum_advance_receive_total,                                                                               " +
            "        t1.advance_stopreceive_total = summary.sum_advance_stopreceive_total                                                                        " +
            "    WHERE t1.so_contract_id = #{contractId}                                                                                                        " +
            "</script>                                                                                                                                           ")
    int updateContractAdvanceTotalData(@Param("contractId") Integer contractId);
}