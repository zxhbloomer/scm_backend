package com.xinyirun.scm.core.system.service.business.rtwo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoProductEntity;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoProductVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 *  生产管理_产成品、副产品
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBRtWoProductService extends IService<BRtWoProductEntity> {

    /**
     * 校验 产成品, 副产品配比是否满足 100 %
     * @param product_list 产成品, 副产品 集合
     * @param result 错误信息集合, map键值 error_msg
     */
//    void checkProductRouter(List<BRtWoProductVo> product_list, List<Map<String, String>> result);

    /**
     * 新增产成品, 副产品
     * @param product_list 产成品, 副产品集合
     * @param wo_id 主表 id
     */
    void insertAll(List<BRtWoProductVo> product_list, Integer wo_id);

    /**
     * 查询产成品, 副产品
     * @param wo_id wo_id
     * @return List<BWoProductVo>
     */
    List<BRtWoProductVo> selectByWoId(Integer wo_id);

    /**
     * 查询 当前 wo_id 的产成品已生产数量
     * @param wo_id
     * @param delivery_order_detail_id 订单详情 ID
     * @return
     */
    BigDecimal selectHasProductNum(Integer wo_id, Integer delivery_order_detail_id);
}
