package com.xinyirun.scm.core.system.mapper.business.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.project.BProjectGoodsEntity;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 项目管理-商品明细 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Repository
public interface BProjectGoodsMapper extends BaseMapper<BProjectGoodsEntity> {

    /**
     * 根据项目ID删除商品明细记录
     * @param projectId 项目ID
     * @return 删除数量
     */
    int deleteByProjectId(String projectId);

}
