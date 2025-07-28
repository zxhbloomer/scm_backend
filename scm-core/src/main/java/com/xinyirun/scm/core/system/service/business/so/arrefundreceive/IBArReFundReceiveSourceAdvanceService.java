package com.xinyirun.scm.core.system.service.business.so.arrefundreceive;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.arrefundreceive.BArReFundReceiveSourceAdvanceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.arrefundreceive.BArReFundReceiveSourceAdvanceVo;

import java.util.List;

/**
 * <p>
 * 退款单关联单据表-源单-预收款 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
public interface IBArReFundReceiveSourceAdvanceService extends IService<BArReFundReceiveSourceAdvanceEntity> {

    /**
     * 查询退款单关联单据列表
     *
     * @param vo 查询条件
     * @return 退款单关联单据列表
     */
    List<BArReFundReceiveSourceAdvanceVo> selectList(BArReFundReceiveSourceAdvanceVo vo);

    /**
     * 根据退款单ID查询关联单据
     *
     * @param arRefundReceiveId 退款单ID
     * @return 关联单据列表
     */
    List<BArReFundReceiveSourceAdvanceEntity> selectByArRefundReceiveId(Integer arRefundReceiveId);

    /**
     * 根据退款管理ID查询关联单据
     *
     * @param arRefundId 退款管理ID
     * @return 关联单据列表
     */
    List<BArReFundReceiveSourceAdvanceEntity> selectByArRefundId(Integer arRefundId);

    /**
     * 根据销售合同ID查询关联单据
     *
     * @param soContractId 销售合同ID
     * @return 关联单据列表
     */
    List<BArReFundReceiveSourceAdvanceEntity> selectBySoContractId(Integer soContractId);

    /**
     * 根据销售订单ID查询关联单据
     *
     * @param soOrderId 销售订单ID
     * @return 关联单据列表
     */
    List<BArReFundReceiveSourceAdvanceEntity> selectBySoOrderId(Integer soOrderId);

    /**
     * 根据退款单ID删除关联单据
     *
     * @param arRefundReceiveId 退款单ID
     * @return 删除记录数
     */
    int deleteByArRefundReceiveId(Integer arRefundReceiveId);

    /**
     * 批量保存关联单据
     *
     * @param arRefundReceiveId 退款单ID
     * @param list 关联单据列表
     * @return 保存是否成功
     */
    boolean saveBatch(Integer arRefundReceiveId, List<BArReFundReceiveSourceAdvanceEntity> list);

    /**
     * 批量插入关联单据
     *
     * @param list 关联单据列表
     * @return 插入记录数
     */
    int insertBatch(List<BArReFundReceiveSourceAdvanceEntity> list);

}