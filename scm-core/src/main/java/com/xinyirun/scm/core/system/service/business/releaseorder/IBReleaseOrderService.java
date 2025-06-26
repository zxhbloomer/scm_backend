package com.xinyirun.scm.core.system.service.business.releaseorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.releaseorder.BReleaseOrderVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-11-29
 */
public interface IBReleaseOrderService extends IService<BReleaseOrderEntity> {

    /**
     * 查询放货指令列表
     * @param param 入参
     * @return IPage<BOwnerChangeVo>
     */
    IPage<BReleaseOrderVo> selectPage(BReleaseOrderVo param);

    /**
     * 根据 ID 查询详情
     * @param param
     * @return
     */
    BReleaseOrderVo get(BReleaseOrderVo param);

    /**
     * 根据 ID 查询详情
     * @param param
     * @return
     */
    BReleaseOrderVo getDetail(BReleaseOrderVo param);

    /**
     * 查询列表, 带商品
     * @param param
     * @return
     */
    IPage<BReleaseOrderVo> selectCommPage(BReleaseOrderVo param);

    /**
     * 新增 api
     * @param param 参数
     * @return InsertResultAo<BReleaseOrderVo>
     */
    InsertResultAo<String> insert(BReleaseOrderVo param);

    UpdateResultAo<String> updateByParam(BReleaseOrderVo param);

    /**
     * 删除
     * @param param
     */
    void delete(BReleaseOrderVo param);

}
