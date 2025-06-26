package com.xinyirun.scm.core.system.serviceimpl.master.goods.unit;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitSelectVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.master.goods.unit.MUnitMapper;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMUnitService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 单位 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MUnitServiceImpl extends BaseServiceImpl<MUnitMapper, MUnitEntity> implements IMUnitService {

    @Autowired
    private MUnitMapper mapper;

    /**
     * 获取列表，页面查询
     */
    @Override
    public IPage<MUnitVo> selectPage(MUnitVo searchCondition) {
        // 分页条件
        Page<MUnitEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public MUnitVo selectByCode(String code) {
        return mapper.selectByCode(code);
    }

    /**
     * 获取列表，页面查询
     */
    @Override
    public MUnitSelectVo getUnitSelectData(MUnitVo searchCondition) {
        MUnitSelectVo rtn = new MUnitSelectVo();

        // 获取下拉选项的数据
        List<MUnitVo> vos = mapper.selectList(searchCondition);
        rtn.setUnit_datas(vos);

        // 获取默认选中的数据
        MUnitVo unit = mapper.selectByName(SystemConstants.DEFAULT_UNIT.NAME, true);
        rtn.setActive_data(unit);
        return rtn;
    }
}
