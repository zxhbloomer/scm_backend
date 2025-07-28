package com.xinyirun.scm.core.system.service.business.check;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.check.BCheckResultEntity;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckResultVo;

import java.util.List;

/**
 * <p>
 * 盘盈盘亏 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
public interface IBCheckResultService extends IService<BCheckResultEntity> {
    /**
     * 编辑一条记录
     */
    UpdateResultAo<Integer> update(BCheckResultVo vo);

    /**
     * 获取列表，页面查询
     */
    IPage<BCheckResultVo> selectPage(BCheckResultVo searchCondition) ;

    /**
     * 批量审核
     */
    void audit(List<BCheckResultVo> searchCondition);

    /**
     * 批量作废
     */
    void cancel(List<BCheckResultVo> searchCondition);
}
