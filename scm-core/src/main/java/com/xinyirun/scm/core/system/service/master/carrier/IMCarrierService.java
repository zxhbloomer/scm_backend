package com.xinyirun.scm.core.system.service.master.carrier;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.master.carrier.MCarrierVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
public interface IMCarrierService extends IService<MCustomerEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<MCarrierVo> selectPage(MCarrierVo searchCondition) ;

    /**
     * 获取承运商
     */
    MCarrierVo getDetail(MCarrierVo searchCondition) ;

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    InsertResultAo<Integer> insert(MCarrierVo vo);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> update(MCarrierVo vo);

    /**
     * 通过name查询
     *
     */
    List<MCustomerEntity> selectByName(String name);

    /**
     * 通过code查询
     *
     */
    List<MCustomerEntity> selectByCode(String code);

    /**
     * 查询by id，返回结果
     */
    MCarrierVo selectById(int id);

    /**
     * 删除承运商
     */
    void delete(MCarrierVo searchCondition) ;
}
