package com.xinyirun.scm.core.system.service.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.system.vo.excel.customer.MCustomerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMCustomerService extends IService<MCustomerEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MCustomerVo> selectPage(MCustomerVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    List<MCustomerVo> selectList(MCustomerVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MCustomerVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MCustomerVo vo);

    /**
     * 通过credit_no查询
     *
     */
    MCustomerVo selectByCreditNo(String credit_no);

    /**
     * 批量启用
     */
    void enabledByIdsIn(List<MCustomerVo> searchCondition);

    /**
     * 批量禁用
     */
    void disSabledByIdsIn(List<MCustomerVo> searchCondition);

    /**
     * 批量启用/停用
     */
    void enableByIdsIn(List<MCustomerVo> searchCondition);

    /**
     * 查询by id，返回结果
     */
    MCustomerVo selectById(int id);

    /**
     * 导出
     * @param searchConditionList 参数
     * @return List<MCustomerExcelVo>
     */
    List<MCustomerExcelVo> export(MCustomerVo searchConditionList);
}
