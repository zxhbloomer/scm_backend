package com.xinyirun.scm.core.system.mapper.business.aprefundpay;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.aprefundpay.BApReFundPaySourceEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefundpay.BApReFundPaySourceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 退款单关联单据表-源单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Mapper
public interface BApReFundPaySourceMapper extends BaseMapper<BApReFundPaySourceEntity> {

    /**
     * 查询退款单关联单据列表
     *
     * @param vo 查询条件
     * @return 退款单关联单据列表
     */
    List<BApReFundPaySourceVo> selectList(@Param("vo") BApReFundPaySourceVo vo);

    /**
     * 根据退款单ID查询关联单据
     *
     * @param apRefundPayId 退款单ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceEntity> selectByApRefundPayId(@Param("apRefundPayId") Integer apRefundPayId);

    /**
     * 根据退款管理ID查询关联单据
     *
     * @param apRefundId 退款管理ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceEntity> selectByApRefundId(@Param("apRefundId") Integer apRefundId);

    /**
     * 根据采购合同ID查询关联单据
     *
     * @param poContractId 采购合同ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceEntity> selectByPoContractId(@Param("poContractId") Integer poContractId);

    /**
     * 根据采购订单ID查询关联单据
     *
     * @param poOrderId 采购订单ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceEntity> selectByPoOrderId(@Param("poOrderId") Integer poOrderId);

    /**
     * 根据退款单ID删除关联单据
     *
     * @param apRefundPayId 退款单ID
     * @return 删除记录数
     */
    int deleteByApRefundPayId(@Param("apRefundPayId") Integer apRefundPayId);

    /**
     * 批量插入关联单据
     *
     * @param list 关联单据列表
     * @return 插入记录数
     */
    int insertBatch(@Param("list") List<BApReFundPaySourceEntity> list);

}