package com.xinyirun.scm.core.system.service.business.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.order.BOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedExportVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderInvertedVo;
import com.xinyirun.scm.bean.system.vo.business.order.BOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface IBOrderService extends IService<BOrderEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BOrderVo> selectPage(BOrderVo searchCondition) ;


    /**
     * 按订单编号查询
     */
    BOrderVo selectByOrderNo(BOrderVo searchCondition) ;

    /**
     * 按订单编号查询
     */
    BOrderVo selectOrder(String order_type, Integer order_id) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BOrderVo vo);

    /**
     * 根据查询条件，获取订单信息 用于合同号是多个的情况
     * @param searchCondition 实体对象
     * @return
     */
    IPage<BOrderVo> selectPage2(BOrderVo searchCondition);
}
