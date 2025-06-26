package com.xinyirun.scm.core.system.service.business.wo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterVo;

import java.util.List;

/**
 * <p>
 *  生产配方服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
public interface IBWoRouterService extends IService<BWoRouterEntity> {

    /**
     * 新增配方
     * @param param 参数
     */
    InsertResultAo<BWoRouterVo> insert(BWoRouterVo param);

    /**
     * 更新配方
     * @param param 更新参数
     */
    UpdateResultAo<BWoRouterVo> updateParam(BWoRouterVo param);

    /**
     * 查询详情
     * @param id 主键id
     * @return BWoRouterVo
     */
    BWoRouterVo getDetail(Integer id);

    /**
     * 分页查询生产配方
     * @param param 入参
     * @return IPage<BWoRouterVo>
     */
    IPage<BWoRouterVo> selectPageList(BWoRouterVo param);

    /**
     * 启用生产配方
     * @param param
     */
    void enable(List<BWoRouterVo> param);

    /**
     * 禁用生产配方
     * @param param
     */
    void disabled(List<BWoRouterVo> param);
}
