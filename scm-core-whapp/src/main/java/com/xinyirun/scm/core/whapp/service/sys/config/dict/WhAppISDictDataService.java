package com.xinyirun.scm.core.whapp.service.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.whapp.vo.sys.config.dict.WhAppSDictDataVo;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
public interface WhAppISDictDataService extends IService<SDictDataEntity> {
    /**
     * 获取列表，页面查询
     */
    IPage<WhAppSDictDataVo> selectPage(WhAppSDictDataVo searchCondition) ;

    /**
     * 获取所有数据
     */
    List<WhAppSDictDataVo> select(WhAppSDictDataVo searchCondition) ;

    /**
     * 获取所选id的数据
     */
    List<WhAppSDictDataVo> selectIdsIn(List<WhAppSDictDataVo> searchCondition) ;

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    WhAppSDictDataVo selectByid(Long id);

}
