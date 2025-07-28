package com.xinyirun.scm.core.system.service.business.check;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.check.BCheckEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckVo;

import java.util.List;

/**
 * <p>
 * 盘点 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
public interface IBCheckService extends IService<BCheckEntity> {

    /**
     * 插入一条记录
     */
    InsertResultAo<Integer> insert(BCheckVo vo);

    /**
     * 编辑一条记录
     */
    UpdateResultAo<Integer> update(BCheckVo vo);

    /**
     * 获取列表，页面查询
     */
    IPage<BCheckVo> selectPage(BCheckVo searchCondition) ;

    /**
     * 批量审核
     */
    void audit(List<BCheckVo> searchCondition);

    /**
     * 批量作废
     */
    void cancel(List<BCheckVo> searchCondition);
}
