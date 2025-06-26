package com.xinyirun.scm.core.system.service.business.out.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.out.BOutOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BContractReportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BDirectlyWarehouseVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BOutContractReportExportVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderExportVo;
import com.xinyirun.scm.bean.system.vo.business.out.BOutOrderVo;

import java.util.List;

public interface IBOutOrderService extends IService<BOutOrderEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BOutOrderVo> selectPage(BOutOrderVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    IPage<BOutOrderVo> selectList(BOutOrderVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    BOutOrderVo get(BOutOrderVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    BOutOrderVo selectById(int id);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BOutOrderVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BOutOrderVo vo);

    /**
     * 删除数据
     * @param vo
     * @return
     */
    void delete(List<BOutOrderVo> vo);

    /**
     * 销售合同 汇总
     * @param param
     * @return
     */
    IPage<BContractReportVo> queryOutContractList(BContractReportVo param);

    /**
     * 销售合同 汇总 求和
     * @param param
     * @return
     */
    BContractReportVo queryOutContractListSum(BContractReportVo param);

    /**
     * 销售合同导出， 部分导出
     * @param param
     * @return
     */
    List<BOutContractReportExportVo> queryOutContractListExport(List<BContractReportVo> param);

    /**
     * 销售合同导出， 全部导出
     * @param param 入参
     * @return List<>
     */
    List<BOutContractReportExportVo> queryOutContractListExportAll(BContractReportVo param);

    /**
     * 直属库合同统计
     * @param param
     * @return
     */
    IPage<BDirectlyWarehouseVo> getDirectlyWarehouseList(BDirectlyWarehouseVo param);

    /**
     * 求和
     * @param searchCondition 参数
     * @return BOutOrderVo
     */
    BOutOrderVo getListSum(BOutOrderVo searchCondition);

    /**
     * 导出
     * @param param
     * @return
     */
    List<BOutOrderExportVo> exportOutOrder(BOutOrderVo param);
}
