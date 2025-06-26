package com.xinyirun.scm.core.system.service.business.ownerchange;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.ownerchange.BOwnerChangeOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeOrderVo;

public interface IBOwnerChangeOrderService extends IService<BOwnerChangeOrderEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<BOwnerChangeOrderVo> selectPage(BOwnerChangeOrderVo searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    BOwnerChangeOrderVo selectById(int id);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(BOwnerChangeOrderVo vo);

    /**
     * 修改数据
     * @param vo
     * @return
     */
    UpdateResultAo<Integer> update(BOwnerChangeOrderVo vo);

    /**
     * 删除数据
     * @param vo
     * @return
     */
    DeleteResultAo<Integer> delete(BOwnerChangeOrderVo vo);
}
