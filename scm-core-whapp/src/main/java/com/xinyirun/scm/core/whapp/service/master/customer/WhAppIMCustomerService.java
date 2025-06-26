package com.xinyirun.scm.core.whapp.service.master.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.whapp.ao.result.WhAppInsertResultAo;
import com.xinyirun.scm.bean.whapp.ao.result.WhAppUpdateResultAo;
import com.xinyirun.scm.bean.whapp.vo.master.customer.WhAppMCustomerVo;
import com.xinyirun.scm.core.whapp.service.base.v1.WhAppIBaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface WhAppIMCustomerService extends WhAppIBaseService<MCustomerEntity> {
    /**
     * 获取列表，页面查询
     */
    List<WhAppMCustomerVo> list(WhAppMCustomerVo searchCondition) ;

    /**
     * 获取列表，页面查询
     */
    IPage<WhAppMCustomerVo> selectPage(WhAppMCustomerVo searchCondition) ;

    /**
     * 获取企业
     */
    WhAppMCustomerVo getDetail(WhAppMCustomerVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    WhAppInsertResultAo<Integer> insert(WhAppMCustomerVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    WhAppUpdateResultAo<Integer> update(WhAppMCustomerVo vo);

    /**
     * 通过name查询
     *
     */
    List<MCustomerEntity> selectByName(String name);

    /**
     * 通过code查询
     *
     */
    List<MCustomerEntity> selectByCreditNo(String code);

    /**
     * 查询by id，返回结果
     */
    WhAppMCustomerVo selectById(int id);

    /**
     * 删除企业
     */
    void delete(WhAppMCustomerVo searchCondition) ;

    /**
     * 置顶企业
     */
    void top(WhAppMCustomerVo searchCondition) ;


    /**
     * 取消置顶企业
     */
    void canceltop(WhAppMCustomerVo searchCondition) ;

    /**
     * 客户校验逻辑
     */
    CheckResultAo checkLogic(MEnterpriseVo bean, String checkType);
}
