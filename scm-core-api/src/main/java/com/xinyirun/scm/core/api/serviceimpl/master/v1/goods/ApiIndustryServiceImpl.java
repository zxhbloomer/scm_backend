package com.xinyirun.scm.core.api.serviceimpl.master.v1.goods;

import com.xinyirun.scm.bean.entity.master.goods.MBusinessTypeEntity;
import com.xinyirun.scm.bean.entity.master.goods.MIndustryEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiBusinessTypeVo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiIndustryVo;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiBusinessTypeMapper;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiIndustryMapper;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiIndustryService;
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
public class ApiIndustryServiceImpl extends BaseServiceImpl<ApiIndustryMapper, MIndustryEntity> implements ApiIndustryService {

    @Autowired
    private ApiBusinessTypeMapper apiBusinessTypeMapper;

    @Autowired
    private ApiIndustryMapper mapper;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiIndustryVo> voList) {
        List<MIndustryEntity> list = new ArrayList<>();
        // 必输check
        checkSyncList(voList);
        for(ApiIndustryVo vo:voList) {
            MIndustryEntity entity = (MIndustryEntity) BeanUtilsSupport.copyProperties(vo,MIndustryEntity.class);
            entity.setBusiness_type_code(vo.getBusiness_type_code());
            entity.setEnable(Boolean.TRUE);
            ApiBusinessTypeVo apiBusinessTypeVo = new ApiBusinessTypeVo();
            apiBusinessTypeVo.setCode(vo.getBusiness_type_code());
            MBusinessTypeEntity businessTypeEntity = apiBusinessTypeMapper.selectByCodeAppCode(apiBusinessTypeVo);
            if(businessTypeEntity != null) {
                entity.setBusiness_id(businessTypeEntity.getId());
            }
            // 按编号和来源查询数据库数据是否存在
            MIndustryEntity industryEntity = mapper.selectByCodeAppCode(vo);

            if(industryEntity != null) {
                // 如果不为空，则为修改
                entity.setId(industryEntity.getId());
            }
            list.add(entity);
        }

        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }


    /**
     * check
     */
    private void checkSyncList(List<ApiIndustryVo> voList) {
        // 必输check
        for(ApiIndustryVo vo:voList){
            if (StringUtils.isEmpty(vo.getCode())) {
                throw new ApiBusinessException(ApiResultEnum.INDUSTRY_CODE_NULL);
            }
            if (StringUtils.isEmpty(vo.getName())) {
                throw new ApiBusinessException(ApiResultEnum.INDUSTRY_NAME_NULL);
            }
            if (StringUtils.isEmpty(vo.getBusiness_type_code())) {
                throw new ApiBusinessException(ApiResultEnum.INDUSTRY_BUSINESS_TYPE_CODE_NULL);
            }
        }

        // 内部check
        // 编号和来源查询是否有重复数据
        List<String> codeList = voList.stream().map(ApiIndustryVo::getCodeAppCode).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (voList.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.INDUSTRY_PARAM_CODE_REPEAT);
        }

        // 相同的板块下，行业名称不能重复
        List<String> nameList = voList.stream().map(ApiIndustryVo::getNameBusinessTypeCode).collect(Collectors.toList());
        long nameCount = nameList.stream().distinct().count();
        if (voList.size() != nameCount) {
            throw new ApiBusinessException(ApiResultEnum.INDUSTRY_PARAM_NAME_REPEAT);
        }
    }

}
