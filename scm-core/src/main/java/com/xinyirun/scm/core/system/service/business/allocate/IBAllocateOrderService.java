package com.xinyirun.scm.core.system.service.business.allocate;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.allocate.BAllocateOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateOrderVo;

public interface IBAllocateOrderService extends IService<BAllocateOrderEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BAllocateOrderVo> selectPage(BAllocateOrderVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    BAllocateOrderVo selectById(int id);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BAllocateOrderVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BAllocateOrderVo vo);

    /**
     * 删除数据
     * @param vo
     * @return
     */
    DeleteResultAo<Integer> delete(BAllocateOrderVo vo);
}
