package com.xinyirun.scm.core.system.service.track.gsh56;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.track.BTrackEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.vo.business.track.BTrackVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
public interface IBTrackService extends IService<BTrackEntity> {

    /**
     * 插入一条记录（选择字段，策略插入）
     */
    public InsertResultAo<Integer> insert(BTrackVo vo);

    /**
     * 查询明细
     */
    public BTrackVo get(BTrackVo vo);

    /**
     * 删除
     */
    public void delete(BTrackVo vo);

}
