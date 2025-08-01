package com.xinyirun.scm.core.system.service.business.po.aprefundpay;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.po.aprefundpay.BApReFundPaySourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefundpay.BApReFundPaySourceAdvanceVo;

import java.util.List;

/**
 * <p>
 * 退款单关联单据表-源单-预收款 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
public interface IBApReFundPaySourceAdvanceService extends IService<BApReFundPaySourceAdvanceEntity> {

    /**
     * 查询退款单关联单据列表
     *
     * @param vo 查询条件
     * @return 退款单关联单据列表
     */
    List<BApReFundPaySourceAdvanceVo> selectList(BApReFundPaySourceAdvanceVo vo);

    /**
     * 根据退款单ID查询关联单据
     *
     * @param apRefundPayId 退款单ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceAdvanceEntity> selectByApRefundPayId(Integer apRefundPayId);

    /**
     * 根据退款管理ID查询关联单据
     *
     * @param apRefundId 退款管理ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceAdvanceEntity> selectByApRefundId(Integer apRefundId);

    /**
     * 根据采购合同ID查询关联单据
     *
     * @param poContractId 采购合同ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceAdvanceEntity> selectByPoContractId(Integer poContractId);

    /**
     * 根据采购订单ID查询关联单据
     *
     * @param poOrderId 采购订单ID
     * @return 关联单据列表
     */
    List<BApReFundPaySourceAdvanceEntity> selectByPoOrderId(Integer poOrderId);

    /**
     * 根据退款单ID删除关联单据
     *
     * @param apRefundPayId 退款单ID
     * @return 删除记录数
     */
    int deleteByApRefundPayId(Integer apRefundPayId);

    /**
     * 批量保存关联单据
     *
     * @param apRefundPayId 退款单ID
     * @param list 关联单据列表
     * @return 保存是否成功
     */
    boolean saveBatch(Integer apRefundPayId, List<BApReFundPaySourceAdvanceEntity> list);

    /**
     * 批量插入关联单据
     *
     * @param list 关联单据列表
     * @return 插入记录数
     */
    int insertBatch(List<BApReFundPaySourceAdvanceEntity> list);

}