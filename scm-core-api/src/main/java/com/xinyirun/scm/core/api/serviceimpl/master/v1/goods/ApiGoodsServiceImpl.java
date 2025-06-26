package com.xinyirun.scm.core.api.serviceimpl.master.v1.goods;

import com.xinyirun.scm.bean.api.vo.master.goods.ApiCategoryVo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsVo;
import com.xinyirun.scm.bean.entity.master.goods.MCategoryEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiCategoryMapper;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiGoodsMapper;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiGoodsService;
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
public class ApiGoodsServiceImpl extends BaseServiceImpl<ApiGoodsMapper, MGoodsEntity> implements ApiGoodsService {

    @Autowired
    private ApiGoodsMapper mapper;

    @Autowired
    private ApiCategoryMapper apiCategoryMapper;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiGoodsVo> goodsList) {
        List<MGoodsEntity> list = new ArrayList<>();
        // 必输check
//        checkSyncList(goodsList);
        for(ApiGoodsVo vo:goodsList) {
            // 赋值物料数据
            MGoodsEntity entity = (MGoodsEntity) BeanUtilsSupport.copyProperties(vo,MGoodsEntity.class);
            entity.setEnable(true);
            // 查询类别数据
            MCategoryEntity categoryEntity = getCategory(vo);
            if (categoryEntity != null) {
                entity.setCategory_id(categoryEntity.getId());
                entity.setCategory_code(categoryEntity.getCode());
            } else {
                throw new ApiBusinessException("请先同步类别");
            }

            // 按编号和来源查询数据库数据是否存在
            MGoodsEntity goodsEntity = mapper.selectByCodeAppCode(vo);
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
     * 查询类别数据
     */
    public MCategoryEntity getCategory(ApiGoodsVo vo) {
        ApiCategoryVo apiCategoryVo = new ApiCategoryVo();
        apiCategoryVo.setCode(vo.getCategory_code());
        return apiCategoryMapper.selectByCodeAppCode(apiCategoryVo);
    }

    /**
     * check
     */
    private void checkSyncList(List<ApiGoodsVo> goodsList) {
        // 必输check
        for(ApiGoodsVo vo:goodsList){
            if (StringUtils.isEmpty(vo.getCode())) {
                throw new ApiBusinessException(ApiResultEnum.GOODS_CODE_NULL);
            }
            if (StringUtils.isEmpty(vo.getName())) {
                throw new ApiBusinessException(ApiResultEnum.GOODS_NAME_NULL);
            }
            if (StringUtils.isEmpty(vo.getCategory_code())) {
                throw new ApiBusinessException(ApiResultEnum.GOODS_CATEGORY_CODE_NULL);
            }
        }

        // 内部check
        // 编号和来源查询是否有重复数据
        List<String> codeList = goodsList.stream().map(ApiGoodsVo::getCodeAppCode).collect(Collectors.toList());
        long codeCount = codeList.stream().distinct().count();
        if (goodsList.size() != codeCount) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_PARAM_CODE_REPEAT);
        }

        // 相同的类别下，商品名称不能重复
        List<String> nameList = goodsList.stream().map(ApiGoodsVo::getNameCategoryCode).collect(Collectors.toList());
        long nameCount = nameList.stream().distinct().count();
        if (goodsList.size() != nameCount) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_PARAM_NAME_REPEAT);
        }
    }

}
