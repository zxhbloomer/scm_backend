package com.xinyirun.scm.core.system.mapper.business.so.arreceive;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.arreceive.BArReceiveSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arreceive.BArReceiveSourceAdvanceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 收款来源预收表 Mapper 接口
 */
@Mapper
public interface BArReceiveSourceAdvanceMapper extends BaseMapper<BArReceiveSourceAdvanceEntity> {
    /**
     * 根据合同id，获取数据list
     * @param so_contract_id 合同id
     * @return 收款来源预收VO列表
     */
    @Select("SELECT * FROM b_ar_receive_source_advance WHERE so_contract_id = #{so_contract_id}")
    List<BArReceiveSourceAdvanceVo> selectByContractId(@Param("so_contract_id") Integer so_contract_id);

    /**
     * 根据ar_receive_id查询聚合数据
     * @param arReceiveId 收款单ID
     * @return 聚合后的收款来源预收VO
     */
    @Select("SELECT ar_receive_id, " +
            "GROUP_CONCAT(so_contract_id) as so_contract_id_gc, " +
            "GROUP_CONCAT(so_contract_code) as so_contract_code_gc, " +
            "GROUP_CONCAT(so_order_code) as so_order_code_gc, " +
            "GROUP_CONCAT(so_order_id) as so_order_id_gc " +
            "FROM b_ar_receive_source_advance WHERE ar_receive_id = #{arReceiveId}")
    BArReceiveSourceAdvanceVo selectAggregatedByArReceiveId(@Param("arReceiveId") Integer arReceiveId);
}