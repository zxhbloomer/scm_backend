package com.xinyirun.scm.core.system.service.master.cancel;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.cancel.MCancelEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
public interface MCancelService extends IService<MCancelEntity> {

    /**
     * 获取列表，页面查询
     */
    IPage<MCancelVo> selectPage(MCancelVo searchCondition);

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MCancelVo vo);

    /**
     * 通过serial_id,serial_type查询数据
     * @param vo
     * @return
     */
    MCancelVo selectBySerialIdAndType(MCancelVo vo);

    /**
     * 通过serial_id,serial_type删除数据
     * @param vo
     * @return
     */
    void delete(MCancelVo vo);

}
