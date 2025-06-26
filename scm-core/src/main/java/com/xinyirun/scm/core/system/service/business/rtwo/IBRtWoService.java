package com.xinyirun.scm.core.system.service.business.rtwo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoVo;

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
public interface IBRtWoService extends IService<BRtWoEntity> {

    /**
     * 新增
     * @param param
     */
    InsertResultAo<BRtWoVo> insert(BRtWoVo param);

    /**
     * 公式校验, 键值 error_msg
     * @param param
     * @return
     */
    List<Map<String, String>> check(BRtWoVo param);

    /**
     * 更新
     * @param param
     */
    UpdateResultAo<BRtWoVo> updateParam(BRtWoVo param);

    /**
     * 提交
     * @param param
     */
    void submit(List<BRtWoVo> param);

    /**
     * 分页查询
     * @param param
     * @return
     */
    IPage<BRtWoVo> selectPageList(BRtWoVo param);

    /**
     * 根据 id 查询详情
     * @param id
     * @return
     */
    BRtWoVo getDetail(Integer id);

    /**
     * 作废
     * @param param 入参
     */
    void cancel(BRtWoVo param);

    /**
     * 审核通过
     * @param param
     */
    void audit(List<BRtWoVo> param);

    /**
     * 审核驳回
     * @param param
     */
    void reject(List<BRtWoVo> param);

    /**
     * 计算
     * @param param
     * @return
     */
    BRtWoVo checkQty(BRtWoVo param);

    /**
     * 获取 产成品， 副产品库存
     * @param param
     * @return
     */
    BRtWoVo calcInventory(BRtWoVo param);

    /**
     * 导出查询
     * @param param
     * @return
     */
    List<BRtWoVo> selectExportList(BRtWoVo param);

    /**
     * 查询待办
     * @param param
     * @return
     */
    Integer selectTodoCount(BRtWoVo param);
}
