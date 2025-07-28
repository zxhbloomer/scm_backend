package com.xinyirun.scm.core.system.service.business.allocate;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.allocate.BAllocateEntity;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateVo;

import java.util.List;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBAllocateService extends IService<BAllocateEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<BAllocateVo> selectPage(BAllocateVo searchCondition) ;

    /**
     * 查询单条数据
     */
    BAllocateVo get(BAllocateVo vo) ;

    /**
     * 查询by id，返回结果
     */
    BAllocateVo selectById(int id);

    /**
     * 批量删除
     */
    void delete(List<BAllocateVo> searchCondition);
}
