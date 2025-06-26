package com.xinyirun.scm.core.system.service.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupVo;

import java.util.List;

/**
 * <p>
 * 集团主表 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface IMGroupService extends IService<MGroupEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MGroupVo> selectPage(MGroupVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<MGroupVo> select(MGroupVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<MGroupVo> selectIdsInForExport(List<MGroupVo> searchCondition) ;

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MGroupVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MGroupEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MGroupEntity entity);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MGroupVo selectByid(Long id);
}
