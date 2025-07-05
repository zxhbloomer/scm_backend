package com.xinyirun.scm.core.system.service.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeExportVo;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface ISDictDataService extends IService<SDictDataEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<SDictDataVo> selectPage(SDictDataVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<SDictDataVo> select(SDictDataVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<SDictDataVo> selectIdsIn(List<SDictDataVo> searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    SDictDataVo selectByid(Long id);

    /**
     * 批量导入
     */
    boolean saveBatches(List<SDictDataEntity> entityList);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void deleteByIdsIn(List<SDictDataVo> searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(SDictDataEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SDictDataEntity entity);

    /**
     * sort保存
     *
     */
    UpdateResultAo<List<SDictDataVo>> saveList(List<SDictDataVo> data);

    /**
     *
     * @param searchCondition
     * @return
     */
    List<SDictDataVo> selectColumnComment(SDictDataVo searchCondition);

    /**
     * 更新顺序, 上移
     * @param bean
     */
    void updateSortUp(SDictDataVo bean);

    /**
     * 更新顺序, 下移
     * @param bean
     */
    void updateSortDown(SDictDataVo bean);

    /**
     * 导出 数据
     * @param searchConditionList
     * @return
     */
    List<SDictDataExportVo> selectListExport(List<SDictDataVo> searchConditionList);

    /**
     * 全部数据导出
     * @param searchCondition
     * @return
     */
    List<SDictDataExportVo> selectAllExport(SDictDataVo searchCondition);

    /**
     * 获取字典数据
     */
    List<SDictDataVo> selectData(SDictDataVo searchCondition);

    /**
     * 根据code和dict_value获取字典数据表信息
     */
    SDictDataVo get(SDictDataVo bean);
}
