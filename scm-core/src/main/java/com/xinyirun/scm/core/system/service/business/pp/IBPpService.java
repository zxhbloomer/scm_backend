package com.xinyirun.scm.core.system.service.business.pp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.pp.BPpEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.pp.BPpVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 生产计划表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-18
 */
public interface IBPpService extends IService<BPpEntity> {

    /**
     *分页查询
     */
    IPage<BPpVo> selectPageList(BPpVo bPpVo);

    /**
     *新增生产计划
     */
    InsertResultAo<BPpVo> insert(BPpVo bPpVo);

    /**
     * 公式校验
     */
    List<Map<String, String>> check(BPpVo bPpVo);

    /**
     * 计算数量
     */
    BPpVo checkQty(BPpVo bPpVo);

    /**
     * 状态修改 已提交
     */
    void submit(List<BPpVo> bPpVo);

    /**
     *状态修改 作废
     */
    void cancel(BPpVo bPpVo);

    /**
     *状态修改 审核通过
     */
    void audit(List<BPpVo> param);

    /**
     *状态修改 驳回
     */
    void reject(List<BPpVo> param);

    /**
     *修改状态已完成
     */
    void finish(List<BPpVo> param);

    /**
     * 获取生产计划详情
     */
    BPpVo getDetail(Integer id);

    /**
     * 修改生产计划
     */
    UpdateResultAo<BPpVo> updateParam(BPpVo bPpVo);

    /**
     * 查询待办数量
     */
    Integer selectTodoCount(BPpVo bPpVo);

    /**
     * 查询统计数量
     */
    BPpVo selectListSum(BPpVo param);

    /**
     *导出
     */
    List<BPpVo> exportList(BPpVo bPpVo);
}
