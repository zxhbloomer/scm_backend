package com.xinyirun.scm.core.system.service.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyVo;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyExportVo;

import java.util.List;

/**
 * <p>
 * 公司主表 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface IMCompanyService extends IService<MCompanyEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MCompanyVo> selectPage(MCompanyVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<MCompanyVo> select(MCompanyVo searchCondition) ;

    /**
     * 获取导出列表数据 - 标准化实现
     * @param searchCondition 查询条件
     * @param orderByClause 动态排序子句
     * @return 导出数据列表
     */
    List<MCompanyExportVo> selectExportList(MCompanyVo searchCondition);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<MCompanyVo> searchCondition);

    /**
     * 从组织架构删除主体企业（同时删除企业实体和组织关联关系）
     * @param searchCondition
     * @return
     */
    void deleteByIdsFromOrg(List<MCompanyVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MCompanyEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MCompanyEntity entity);

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    MCompanyVo selectByid(Long id);
}
