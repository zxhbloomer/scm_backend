package com.xinyirun.scm.core.api.serviceimpl.master.v1.unit;

import com.xinyirun.scm.bean.entity.master.goods.unit.MUnitEntity;
import com.xinyirun.scm.bean.api.vo.master.unit.ApiUnitVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.unit.ApiUnitMapper;
import com.xinyirun.scm.core.api.service.master.v1.unit.ApiUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class ApiUnitServiceImpl extends BaseServiceImpl<ApiUnitMapper, MUnitEntity> implements ApiUnitService {

    @Autowired
    private ApiUnitMapper mapper;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiUnitVo> unitList) {
        List<MUnitEntity> list = new ArrayList<>();
        // 必输check
        checkSyncList(unitList);
        for(ApiUnitVo vo:unitList) {
            // 赋值物料数据
            MUnitEntity entity = (MUnitEntity) BeanUtilsSupport.copyProperties(vo,MUnitEntity.class);
            entity.setEnable(true);
            // 按编号和来源查询数据库数据是否存在
            MUnitEntity goodsEntity = mapper.selectByCodeAppCode(vo);
            if(goodsEntity != null) {
                // 如果不为空，则为修改
                entity.setId(goodsEntity.getId());
            }
            list.add(entity);

        }

        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }

    /**
     * check
     */
    private void checkSyncList(List<ApiUnitVo> unitList) {
        // 必输check
        for(ApiUnitVo vo:unitList){
            if (StringUtils.isEmpty(vo.getCode())) {
                throw new ApiBusinessException(ApiResultEnum.UNIT_CODE_NULL);
            }
            if (StringUtils.isEmpty(vo.getName())) {
                throw new ApiBusinessException(ApiResultEnum.UNIT_NAME_NULL);
            }
        }

        // 内部check 列表check
        List<String> codeList = unitList.stream().map(ApiUnitVo::getCodeAppCode).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (unitList.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.UNIT_PARAM_CODE_REPEAT);
        }
    }

}
