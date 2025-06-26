package com.xinyirun.scm.core.system.service.business.wo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoVo;
import com.xinyirun.scm.bean.system.vo.master.goods.unit.MGoodsUnitCalcVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-29
 */
public interface IBWoService extends IService<BWoEntity> {

    /**
     * 新增
     * @param param
     */
    InsertResultAo<BWoVo> insert(BWoVo param);

    /**
     * 公式校验, 键值 error_msg
     * @param param
     * @return
     */
    List<Map<String, String>> check(BWoVo param);

    /**
     * 更新
     * @param param
     */
    UpdateResultAo<BWoVo> updateParam(BWoVo param);

    /**
     * 提交
     * @param param
     */
    void submit(List<BWoVo> param);

    /**
     * 分页查询
     * @param param
     * @return
     */
    IPage<BWoVo> selectPageList(BWoVo param);

    /**
     * 根据 id 查询详情
     * @param id
     * @return
     */
    BWoVo getDetail(Integer id);

    /**
     * 作废
     * @param param 入参
     */
    void cancel(BWoVo param);

    /**
     * 审核通过
     * @param param
     */
    void audit(List<BWoVo> param);

    /**
     * 审核驳回
     * @param param
     */
    void reject(List<BWoVo> param);

    /**
     * 计算
     * @param param
     * @return
     */
    BWoVo checkQty(BWoVo param);

    /**
     * 获取原材料库存
     * @param param
     * @return
     */
    BWoVo calcInventory(BWoVo param);

    List<BWoVo> exportList(BWoVo param);

    /**
     * 查询待办数量
     * @param param
     * @return
     */
    Integer selectTodoCount(BWoVo param);

    /**
     * 求和
     * @param param
     * @return
     */
    BWoVo selectListSum(BWoVo param);

    /**
     * 查询单位换算关系
     */
    List<MGoodsUnitCalcVo> getGoodsUnitCalc(Integer skuId);

    /**
     * 新增默认关系
     */
    MGoodsUnitCalcVo insertGoodsUnitCalc(Integer skuId);
}
