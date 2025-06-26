package com.xinyirun.scm.core.whapp.serviceimpl.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.xinyirun.scm.bean.whapp.vo.sys.config.dict.WhAppSDictTypeVo;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import com.xinyirun.scm.core.whapp.mapper.sys.dict.WhAppSDictTypeMapper;
import com.xinyirun.scm.core.whapp.service.sys.config.dict.WhAppISDictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典类型表、字典主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class WhAppSDictTypeServiceImpl extends BaseServiceImpl<WhAppSDictTypeMapper, SDictTypeEntity> implements WhAppISDictTypeService {

    @Autowired
    private WhAppSDictTypeMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<WhAppSDictTypeVo> selectPage(WhAppSDictTypeVo searchCondition) {
        // 分页条件
        Page<SDictTypeEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<WhAppSDictTypeVo> select(WhAppSDictTypeVo searchCondition) {
        // 查询 数据
        List<WhAppSDictTypeVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<WhAppSDictTypeVo> selectIdsIn(List<WhAppSDictTypeVo> searchCondition) {
        // 查询 数据
        List<WhAppSDictTypeVo> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

}
