package com.xinyirun.scm.core.system.service.business.check;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckOperateEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateVo;

import java.util.List;

/**
 * <p>
 * 盘点 服务类
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
public interface IBCheckOperateService extends IService<BCheckOperateEntity> {

    /**
     * 插入一条记录
     */
    InsertResultAo<Integer> insert(BCheckOperateVo vo);

    /**
     * 编辑一条记录
     */
    UpdateResultAo<Integer> update(BCheckOperateVo vo);

    /**
     * 获取列表，页面查询
     */
    IPage<BCheckOperateVo> selectPage(BCheckOperateVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    BCheckOperateVo selectDetail(int id) ;

    /**
     * 盘点启动
     */
    void start(List<BCheckOperateVo> searchCondition);

    /**
     * 盘点完成
     */
    void finish(List<BCheckOperateVo> searchCondition);

}
