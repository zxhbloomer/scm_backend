package com.xinyirun.scm.core.system.service.business.rtwo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterVo;

import java.util.List;

/**
 * <p>
 *  生产配方服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBRtWoRouterService extends IService<BRtWoRouterEntity> {

    /**
     * 新增配方
     * @param param 参数
     */
    InsertResultAo<BRtWoRouterVo> insert(BRtWoRouterVo param);

    /**
     * 更新配方
     * @param param 更新参数
     */
    UpdateResultAo<BRtWoRouterVo> updateParam(BRtWoRouterVo param);

    /**
     * 查询详情
     * @param id 主键id
     * @return BWoRouterVo
     */
    BRtWoRouterVo getDetail(Integer id);

    /**
     * 分页查询生产配方
     * @param param 入参
     * @return IPage<BWoRouterVo>
     */
    IPage<BRtWoRouterVo> selectPageList(BRtWoRouterVo param);

    /**
     * 启用生产配方
     * @param param
     */
    void enable(List<BRtWoRouterVo> param);

    /**
     * 禁用生产配方
     * @param param
     */
    void disabled(List<BRtWoRouterVo> param);
}
