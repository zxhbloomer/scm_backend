package com.xinyirun.scm.core.app.serviceimpl.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppNutuiNameAndValue;
import com.xinyirun.scm.bean.app.vo.sys.config.dict.AppSDictDataVo;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.xinyirun.scm.core.app.mapper.sys.config.dict.AppSDictDataMapper;
import com.xinyirun.scm.core.app.service.sys.config.dict.AppISDictDataService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import com.xinyirun.scm.core.app.utils.mybatis.AppPageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 字典数据表 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-12-20
 */
@Service
public class AppSDictDataServiceImpl extends AppBaseServiceImpl<AppSDictDataMapper, SDictDataEntity> implements AppISDictDataService {

    @Autowired
    private AppSDictDataMapper mapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<AppSDictDataVo> selectPage(AppSDictDataVo searchCondition) {
        return mapper.selectPage( searchCondition);
    }

    /**
     * 获取能在Nutui显示数据的字典数据
     * @param searchCondition
     * @return
     */
    @Override
    public List<AppNutuiNameAndValue> selectNutuiNameAndValue(AppNutuiNameAndValue searchCondition){
        return mapper.selectNutuiNameAndValue(searchCondition);
    }
}
