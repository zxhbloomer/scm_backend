package com.xinyirun.scm.core.api.service.master.v1.position;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.api.vo.business.position.ApiPositionVo;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;

import java.util.List;

/**
 * <p>
 * 岗位主表 服务类 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface ApiPositionService extends IService<MPositionEntity> {


    /**
     * 获取所有数据
     */
    List<ApiPositionVo> list(ApiPositionVo searchCondition) ;



}
