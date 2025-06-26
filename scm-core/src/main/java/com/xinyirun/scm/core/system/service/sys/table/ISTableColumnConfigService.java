package com.xinyirun.scm.core.system.service.sys.table;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
public interface ISTableColumnConfigService extends IService<STableColumnConfigEntity> {

    /**
     * 查询页面表格配置信息
     * @param vo
     * @return
     */
    List<STableColumnConfigVo> list(STableColumnConfigVo vo);

    /**
     * 重置表格配置信息
     * @param vo
     */
    void reset(STableColumnConfigVo vo);

    /**
     * 重置表格配置信息
     * @param vo
     */
    Boolean check(STableColumnConfigVo vo);

    /**
     * 修改表格配置信息
     * @param list
     */
    void saveList(List<STableColumnConfigVo> list);

}
