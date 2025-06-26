package com.xinyirun.scm.core.api.serviceimpl.master.v1.goods;

import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.entity.master.goods.MIndustryEntity;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiCategoryVo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiIndustryVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiCategoryMapper;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiIndustryMapper;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiCategoryService;
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
public class ApiCategoryServiceImpl extends BaseServiceImpl<ApiCategoryMapper, MCategoryEntity> implements ApiCategoryService {

    @Autowired
    private ApiCategoryMapper mapper;

    @Autowired
    private ApiIndustryMapper apiIndustryMapper;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiCategoryVo> voList) {
        List<MCategoryEntity> list = new ArrayList<>();
        // 必输check
        checkSyncList(voList);
        for(ApiCategoryVo vo:voList) {
            MCategoryEntity entity = (MCategoryEntity) BeanUtilsSupport.copyProperties(vo,MCategoryEntity.class);
            entity.setEnable(Boolean.TRUE);
            ApiIndustryVo apiIndustryVo = new ApiIndustryVo();
            apiIndustryVo.setCode(vo.getIndustry_code());
            MIndustryEntity industryEntity = apiIndustryMapper.selectByCodeAppCode(apiIndustryVo);
            if(industryEntity != null) {
                entity.setIndustry_id(industryEntity.getId());
            }
            // 按编号和来源查询数据库数据是否存在
            MCategoryEntity categoryEntity = mapper.selectByCodeAppCode(vo);

            if(categoryEntity != null) {
                // 如果不为空，则为修改
                entity.setId(categoryEntity.getId());
            }
            list.add(entity);
        }

        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }

    /**
     * check
     */
    private void checkSyncList(List<ApiCategoryVo> voList) {
        // 必输check
        for(ApiCategoryVo vo:voList){
            if (StringUtils.isEmpty(vo.getCode())) {
                throw new ApiBusinessException(ApiResultEnum.CATEGORY_CODE_NULL);
            }
            if (StringUtils.isEmpty(vo.getName())) {
                throw new ApiBusinessException(ApiResultEnum.CATEGORY_NAME_NULL);
            }
            if (StringUtils.isEmpty(vo.getIndustry_code())) {
                throw new ApiBusinessException(ApiResultEnum.CATEGORY_INDUSTRY_CODE_NULL);
            }
        }

        // 内部check
        // 编号和来源查询是否有重复数据
        List<String> codeList = voList.stream().map(ApiCategoryVo::getCodeAppCode).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (voList.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.CATEGORY_PARAM_CODE_REPEAT);
        }

        // 相同的行业下，类别名称不能重复
        List<String> nameList = voList.stream().map(ApiCategoryVo::getNameIndustryCode).collect(Collectors.toList());
        long nameCount = nameList.stream().distinct().count();
        if (voList.size() != nameCount) {
            throw new ApiBusinessException(ApiResultEnum.CATEGORY_PARAM_NAME_REPEAT);
        }
    }

}
