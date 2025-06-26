package com.xinyirun.scm.core.api.serviceimpl.master.v1.goods;

import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecPropVo;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecPropEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecPropVo;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiGoodsSpecPropMapper;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiGoodsSpecPropService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class ApiGoodsSpecPropServiceImpl extends BaseServiceImpl<ApiGoodsSpecPropMapper, MGoodsSpecPropEntity> implements ApiGoodsSpecPropService {

    @Autowired
    private ApiGoodsSpecPropMapper mapper;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiGoodsSpecPropVo> propList) {
        List<MGoodsSpecPropEntity> list = new ArrayList<>();
        // 必输check
        checkSyncList(propList);
        for(ApiGoodsSpecPropVo vo:propList) {
            // 赋值
            MGoodsSpecPropEntity entity = (MGoodsSpecPropEntity) BeanUtilsSupport.copyProperties(vo,MGoodsSpecPropEntity.class);
            // 按编号和来源查询数据库数据是否存在
            MGoodsSpecPropVo propEntity = mapper.selectByCode(vo.getCode());
            if(propEntity != null) {
                // 如果不为空，则为修改
                entity.setId(propEntity.getId());
            }
            list.add(entity);
        }

        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }

    /**
     * check
     */
    private void checkSyncList(List<ApiGoodsSpecPropVo> propList) {
        // 必输check
        for(ApiGoodsSpecPropVo vo:propList){
            if (StringUtils.isEmpty(vo.getCode())) {
                throw new ApiBusinessException(ApiResultEnum.GOODS_PROP_CODE_NULL);
            }
            if (StringUtils.isEmpty(vo.getName())) {
                throw new ApiBusinessException(ApiResultEnum.GOODS_PROP_NAME_NULL);
            }
        }

        // 内部check
        // 编号和来源查询是否有重复数据
        List<String> codeList = propList.stream().map(ApiGoodsSpecPropVo::getCode).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (propList.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_PROP_PARAM_CODE_REPEAT);
        }

        // 相同的类别下，商品名称不能重复
        List<String> nameList = propList.stream().map(ApiGoodsSpecPropVo::getName).collect(Collectors.toList());
        long nameCount = nameList.stream().distinct().count();
        if (propList.size() != nameCount) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_PROP_PARAM_NAME_REPEAT);
        }
    }

}
