package com.xinyirun.scm.core.system.service.business.monitor;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.monitor.BCarriageOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.carriage.BCarriageOrderVo;

import java.util.List;

/**
 *  承运订单 接口
 *
 * @author xinyirun
 * @since 2023-05-04
 */
public interface IBCarriageOrderService extends IService<BCarriageOrderEntity> {


    /**
     * 頁面查詢
     * @param param
     * @return
     */
    IPage<BCarriageOrderVo> selectPage(BCarriageOrderVo param);

    /**
     * 导出
     * @param param
     * @return
     */
    List<BCarriageOrderExportVo> exportList(BCarriageOrderVo param);

    /**
     * 新增
     * @param param
     * @return
     */
    InsertResultAo<BCarriageOrderVo> insert(BCarriageOrderVo param);

    /**
     * 查询详情
     * @param id 订单 id
     * @return
     */
    BCarriageOrderVo getVoById(Integer id);

    /**
     * 更新
     * @param param
     * @return
     */
    UpdateResultAo<BCarriageOrderVo> updateByParam(BCarriageOrderVo param);

}
