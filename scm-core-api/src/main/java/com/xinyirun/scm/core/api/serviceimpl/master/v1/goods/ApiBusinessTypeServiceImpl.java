package com.xinyirun.scm.core.api.serviceimpl.master.v1.goods;

import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiBusinessTypeVo;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiBusinessTypeMapper;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiBusinessTypeService;
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
public class ApiBusinessTypeServiceImpl extends BaseServiceImpl<ApiBusinessTypeMapper, MBusinessTypeEntity> implements ApiBusinessTypeService {

    @Autowired
    private ApiBusinessTypeMapper mapper;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiBusinessTypeVo> voList) {
        List<MBusinessTypeEntity> list = new ArrayList<>();
        // 必输check
        checkSyncList(voList);
        for(ApiBusinessTypeVo vo:voList) {
            MBusinessTypeEntity entity = (MBusinessTypeEntity) BeanUtilsSupport.copyProperties(vo, MBusinessTypeEntity.class);

            // 按编号和来源查询数据库数据是否存在
            MBusinessTypeEntity businessTypeEntity = mapper.selectByCodeAppCode(vo);
            entity.setEnable(Boolean.TRUE);
            if(businessTypeEntity != null) {
                // 如果不为空，则为修改
                entity.setId(businessTypeEntity.getId());
            }
            list.add(entity);
        }

        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }

    /**
     * check
     */
    private void checkSyncList(List<ApiBusinessTypeVo> voList) {
        // 必输check
        for(ApiBusinessTypeVo vo:voList){
            if (StringUtils.isEmpty(vo.getCode())) {
                throw new ApiBusinessException(ApiResultEnum.BUSINESS_TYPE_CODE_NULL);
            }
            if (StringUtils.isEmpty(vo.getName())) {
                throw new ApiBusinessException(ApiResultEnum.BUSINESS_TYPE_NAME_NULL);
            }
        }

        // 内部check
        // 编号和来源查询是否有重复数据
        List<String> codeList = voList.stream().map(ApiBusinessTypeVo::getCodeAppCode).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (voList.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.BUSINESS_TYPE_PARAM_CODE_REPEAT);
        }

        // 相同的编号下，名字不能重复
        List<String> nameList = voList.stream().map(ApiBusinessTypeVo::getNameCodeAppCode).collect(Collectors.toList());
        long nameCount = nameList.stream().distinct().count();
        if (voList.size() != nameCount) {
            throw new ApiBusinessException(ApiResultEnum.BUSINESS_TYPE_PARAM_NAME_REPEAT);
        }
    }

}
