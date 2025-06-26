package com.xinyirun.scm.core.app.service.sys.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.app.vo.sys.config.AppSConfigVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *q
 * @author zxh
 * @since 2019-08-23
 */
public interface AppISConfigService extends IService<SConfigEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<AppSConfigVo> selectPage(AppSConfigVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<AppSConfigVo> select(AppSConfigVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<SConfigEntity> selectIdsIn(List<AppSConfigVo> searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<AppSConfigVo> selectIdsInForExport(List<AppSConfigVo> searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    AppSConfigVo selectByid(Long id);

    /**
     * 批量导入
     */
    boolean saveBatches(List<SConfigEntity> entityList);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(AppSConfigVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(AppSConfigVo vo);

    /**
     * 通过name查询
     *
     */
    List<SConfigEntity> selectByName(String name);

    /**
     * 通过key查询
     *
     */
    AppSConfigVo selectByKey(String key);

    /**
     * 通过key查询
     *
     */
    AppSConfigVo getByKey(AppSConfigVo searchCondition);

    /**
     * 通过value查询:参数键值
     *
     */
    List<SConfigEntity> selectByValue(String value);

    /**
     * 批量物理删除
     * @param searchCondition
     * @return
     */
    DeleteResultAo<Integer> realDeleteByIdsIn(List<AppSConfigVo> searchCondition);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void enabledByIdsIn(List<AppSConfigVo> searchCondition);

}
