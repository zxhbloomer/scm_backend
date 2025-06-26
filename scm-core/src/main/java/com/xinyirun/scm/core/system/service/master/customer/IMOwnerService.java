package com.xinyirun.scm.core.system.service.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerExportVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
public interface IMOwnerService extends IService<MOwnerEntity> {


    /**
     * 获取列表，页面查询
     */
    IPage<MOwnerVo> selectPage(MOwnerVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<MOwnerVo> selectList(MOwnerVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MOwnerVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MOwnerVo vo);

    /**
     * 通过name查询
     *
     */
    List<MOwnerEntity> selectByName(String name);

    /**
     * 通过code查询
     *
     */
    List<MOwnerEntity> selectByCode(String code);

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MOwnerVo> searchCondition);

    /**
     * 批量禁用
     */
    void disSabledByIdsIn(List<MOwnerVo> searchCondition);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MOwnerVo> searchCondition);

    /**
     * 查询by id，返回结果
     */
    MOwnerVo selectById(int id);

    /**
     * 导出
     * @param searchCondition 导出参数
     * @return List<MOwnerExportVo>
     */
    List<MOwnerExportVo> export(MOwnerVo searchCondition);
}
