package com.xinyirun.scm.core.system.service.sys.config.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigDataExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *q
 * @author zxh
 * @since 2019-08-23
 */
public interface ISConfigService extends IService<SConfigEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<SConfigVo> selectPage(SConfigVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<SConfigDataExportVo> selectExportList(SConfigVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<SConfigEntity> selectIdsIn(List<SConfigVo> searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<SConfigDataExportVo> selectIdsInForExport(List<SConfigVo> searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    SConfigVo selectByid(Long id);

    /**
     * 批量导入
     */
    boolean saveBatches(List<SConfigEntity> entityList);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(SConfigVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(SConfigVo vo);

    /**
     * 通过name查询
     *
     */
    List<SConfigEntity> selectByName(String name);

    /**
     * 通过key查询
     *
     */
    SConfigEntity selectByKey(String key);

    /**
     * 通过key查询
     *
     */
    SConfigVo getByKey(SConfigVo searchCondition);

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
    DeleteResultAo<Integer> realDeleteByIdsIn(List<SConfigVo> searchCondition);

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    void enabledByIdsIn(List<SConfigVo> searchCondition);

    /**
     * 初始化配置缓存
     * 1、删除缓存、2、查询数据、3、设置缓存
     */
    void initConfigCache();

}
