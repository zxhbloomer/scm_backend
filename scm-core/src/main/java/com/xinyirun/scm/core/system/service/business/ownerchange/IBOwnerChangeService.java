package com.xinyirun.scm.core.system.service.business.ownerchange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeEntity;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeVo;

import java.util.List;

/**
 * <p>
 * 库存调整 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
public interface IBOwnerChangeService extends IService<BOwnerChangeEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BOwnerChangeVo> selectPage(BOwnerChangeVo searchCondition) ;

    /**
     * 查询单条数据
     */
    BOwnerChangeVo get(BOwnerChangeVo vo) ;

    /**
     * 查询by id，返回结果
     */
    BOwnerChangeVo selectById(int id);

    /**
     * 批量审核
     */
    void audit(List<BOwnerChangeVo> searchCondition);

    /**
     * 批量删除
     */
    void delete(List<BOwnerChangeVo> searchCondition);
}
