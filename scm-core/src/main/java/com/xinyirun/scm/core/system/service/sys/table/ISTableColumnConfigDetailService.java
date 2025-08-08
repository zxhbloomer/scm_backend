package com.xinyirun.scm.core.system.service.sys.table;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigDetailVo;

import java.util.List;

/**
 * <p>
 * 表格列配置详情表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-08
 */
public interface ISTableColumnConfigDetailService extends IService<STableColumnConfigDetailEntity> {

    /**
     * 根据config_id查询详情列表
     */
    List<STableColumnConfigDetailVo> listByConfigId(Integer configId);

    /**
     * 根据多个config_id查询详情列表
     */
    List<STableColumnConfigDetailVo> listByConfigIds(List<Integer> configIds);

    /**
     * 批量保存详情配置
     */
    void saveDetailList(List<STableColumnConfigDetailVo> detailList);

    /**
     * 删除指定config_id的所有详情数据
     */
    void deleteByConfigId(Integer configId);
}