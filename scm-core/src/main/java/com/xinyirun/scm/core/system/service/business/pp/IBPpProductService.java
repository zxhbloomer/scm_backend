package com.xinyirun.scm.core.system.service.business.pp;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.pp.BPpProductEntity;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpProductVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoProductVo;

import java.util.List;

/**
 * <p>
 * 生产计划_产成品、副产品 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
public interface IBPpProductService extends IService<BPpProductEntity> {

    /**
     * 新增生产计划管理产成品, 副产品
     */
    void insertAll(List<BPpProductVo> productList, Integer id);

    List<BPpProductVo> selectByWoId(Integer id);

    /**
     * 删除生产计划表关联信息
     */
    void deleteByPpId(Integer id);
}
