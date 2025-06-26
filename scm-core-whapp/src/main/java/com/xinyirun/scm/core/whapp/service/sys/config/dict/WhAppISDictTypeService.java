package com.xinyirun.scm.core.whapp.service.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.xinyirun.scm.bean.whapp.vo.sys.config.dict.WhAppSDictTypeVo;

import java.util.List;

/**
 * <p>
 * 字典类型表、字典主表 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface WhAppISDictTypeService extends IService<SDictTypeEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<WhAppSDictTypeVo> selectPage(WhAppSDictTypeVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<WhAppSDictTypeVo> select(WhAppSDictTypeVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<WhAppSDictTypeVo> selectIdsIn(List<WhAppSDictTypeVo> searchCondition) ;

}
