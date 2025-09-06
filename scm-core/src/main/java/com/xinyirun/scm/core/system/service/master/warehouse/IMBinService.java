package com.xinyirun.scm.core.system.service.master.warehouse;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MBinExportVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MBinVo;

import java.util.List;

/**
 * <p>
 * 库位 服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMBinService extends IService<MBinEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MBinVo> selectPage(MBinVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<MBinVo> selecList(MBinVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MBinVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MBinVo vo);


    /**
     * 通过name查询
     *
     */
    List<MBinEntity> selectByName(String name,int warehouse_id,int location_id);

    /**
     * 通过code查询
     *
     */
    List<MBinEntity> selectByCode(String code, int warehouse_id,int location_id);


    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MBinVo> searchCondition);

    /**
     * 批量停用
     */
    void disSabledByIdsIn(List<MBinVo> searchCondition);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MBinVo> searchCondition);

    /**
     * 查询by id，返回结果
     */
    MBinVo selectById(int id);

    /**
     * 库位导出 全部
     * @param searchCondition 查询参数
     * @return List<MBinExportVo>
     */
    List<MBinExportVo> exportAll(MBinVo searchCondition);

    /**
     * 库位导出 部分
     * @param searchCondition 查询参数
     * @return List<MBinExportVo>
     */
    List<MBinExportVo> export(List<MBinVo> searchCondition);

    /**
     * 导出专用查询方法 (完全按照仓库模式设计)
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @return List<MBinExportVo>
     */
    List<MBinExportVo> selectExportList(MBinVo searchCondition);

    /**
     * 删除库位（逻辑删除）
     * 参考仓库管理删除模式，支持删除/恢复切换
     * @param searchCondition 包含待删除库位ID的条件对象
     */
    void delete(MBinVo searchCondition);
}
