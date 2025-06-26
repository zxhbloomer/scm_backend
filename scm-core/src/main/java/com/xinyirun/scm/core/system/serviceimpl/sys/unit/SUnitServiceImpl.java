package com.xinyirun.scm.core.system.serviceimpl.sys.unit;

import com.xinyirun.scm.bean.entity.sys.unit.SUnitEntity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.system.vo.master.goods.MUnitVo;
import com.xinyirun.scm.bean.system.vo.sys.unit.SUnitVo;
import com.xinyirun.scm.core.system.mapper.sys.unit.SUnitMapper;
import com.xinyirun.scm.core.system.service.sys.unit.ISUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 单位 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-03
 */
@Service
public class SUnitServiceImpl extends ServiceImpl<SUnitMapper, SUnitEntity> implements ISUnitService {

    @Autowired
    SUnitMapper mapper;

    @Override
    public SUnitVo selectByCode(String code) {
        return mapper.selectByCode(code);
    }

}
