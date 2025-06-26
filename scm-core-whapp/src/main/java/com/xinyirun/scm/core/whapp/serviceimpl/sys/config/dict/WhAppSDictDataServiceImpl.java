package com.xinyirun.scm.core.whapp.serviceimpl.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.whapp.vo.sys.config.dict.WhAppSDictDataVo;
import com.xinyirun.scm.bean.whapp.vo.sys.config.dict.WhAppSDictTypeVo;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import com.xinyirun.scm.core.whapp.mapper.sys.dict.WhAppSDictDataMapper;
import com.xinyirun.scm.core.whapp.service.sys.config.dict.WhAppISDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class WhAppSDictDataServiceImpl extends BaseServiceImpl<WhAppSDictDataMapper, SDictDataEntity> implements WhAppISDictDataService {

    @Autowired
    private WhAppSDictDataMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<WhAppSDictDataVo> selectPage(WhAppSDictDataVo searchCondition) {
        // 分页条件
        Page<WhAppSDictTypeVo> pageCondition =
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
    public List<WhAppSDictDataVo> select(WhAppSDictDataVo searchCondition) {
        // 查询 数据
        List<WhAppSDictDataVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<WhAppSDictDataVo> selectIdsIn(List<WhAppSDictDataVo> searchCondition) {
        // 查询 数据
        List<WhAppSDictDataVo> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public WhAppSDictDataVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }


}
