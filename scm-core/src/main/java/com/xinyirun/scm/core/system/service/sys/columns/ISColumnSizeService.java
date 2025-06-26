package com.xinyirun.scm.core.system.service.sys.columns;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.columns.SColumnSizeEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.sys.columns.SColumnSizeVo;

import java.util.List;

/**
 * <p>
 * 表格列宽 服务类
 * </p>
 *
 * @author zxh
 * @since 2020-06-09
 */
public interface ISColumnSizeService extends IService<SColumnSizeEntity> {

    /**
     * 获取列表，页面查询
     */
    List<SColumnSizeVo> getData(SColumnSizeVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    UpdateResultAo<Boolean> saveColumnsSize(SColumnSizeVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    DeleteResultAo<Boolean> deleteColumnsSize(SColumnSizeVo searchCondition) ;
}
