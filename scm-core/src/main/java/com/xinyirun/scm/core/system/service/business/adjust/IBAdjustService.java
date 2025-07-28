package com.xinyirun.scm.core.system.service.business.adjust;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.adjust.BAdjustEntity;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;

import java.util.List;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBAdjustService extends IService<BAdjustEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BAdjustVo> selectPage(BAdjustVo searchCondition) ;

    /**
     * 查询入库计划
     */
    BAdjustVo get(BAdjustVo vo) ;

    /**
     * 查询by id，返回结果
     */
    List<BAdjustVo> selectById(int id);

    /**
     * 批量提交
     */
    void submit(List<BAdjustVo> searchCondition);

    /**
     * 批量审核
     */
    void audit(List<BAdjustVo> searchCondition);

    /**
     * 批量删除
     */
    void delete(List<BAdjustVo> searchCondition);
}
