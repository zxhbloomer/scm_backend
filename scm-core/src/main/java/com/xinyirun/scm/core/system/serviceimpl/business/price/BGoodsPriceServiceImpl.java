package com.xinyirun.scm.core.system.serviceimpl.business.price;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.api.vo.business.price.ApiGoodsPriceVo;
import com.xinyirun.scm.bean.entity.business.price.BGoodsPriceEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.price.BGoodsPriceMapper;
import com.xinyirun.scm.core.system.mapper.sys.areas.SAreasMapper;
import com.xinyirun.scm.core.system.service.business.price.IBGoodsPriceService;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-18
 */
@Service
public class BGoodsPriceServiceImpl extends ServiceImpl<BGoodsPriceMapper, BGoodsPriceEntity> implements IBGoodsPriceService {

    @Autowired
    private BGoodsPriceMapper mapper;

    @Autowired
    private SAreasMapper sAreasMapper;

    @Autowired
    private IMGoodsSpecService imGoodsSpecService;

    /**
     * 数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiGoodsPriceVo> voList) {
        List<BGoodsPriceEntity> list = new ArrayList<>();
        // 必输check
        checkSyncList(voList);
        for(ApiGoodsPriceVo vo:voList) {
            BGoodsPriceEntity entity = (BGoodsPriceEntity) BeanUtilsSupport.copyProperties(vo,BGoodsPriceEntity.class);
//            SAreasVo areasVo = sAreasMapper.getByName(vo.getProvince(), vo.getCity(), vo.getDistrict());
//            entity.setProvince_code(String.valueOf(areasVo.getProvince_code()));
//            entity.setCity_code(String.valueOf(areasVo.getCity_code()));
//            entity.setDistrict_code(String.valueOf(areasVo.getCode()));

            MGoodsSpecVo specVo = imGoodsSpecService.selectByCode(vo.getSku_code());
            entity.setSku_id(specVo.getId());
            entity.setSku_name(specVo.getName());
            entity.setSku_code(entity.getSku_code());
            entity.setGoods_id(specVo.getGoods_id());
            entity.setGoods_name(specVo.getGoods_name());
            entity.setGoods_code(specVo.getGoods_code());
            entity.setC_time(LocalDateTime.now());
            list.add(entity);
        }

        // 根据list里的元素对象是否有id值来判断是新增还是修改
        saveOrUpdateBatch(list, 500);
    }

    /**
     * check
     */
    private void checkSyncList(List<ApiGoodsPriceVo> list) {
        // 必输check
        for(ApiGoodsPriceVo vo:list){

           if (vo == null) {
               continue;
           }
            if (vo.getPrice() == null) {
                throw new ApiBusinessException(ApiResultEnum.PRICE_PRICE_NULL);
            }
            if (StringUtils.isEmpty(vo.getSku_code())) {
                throw new ApiBusinessException(ApiResultEnum.PRICE_SKU_CODE_NULL);
            }
//            if (StringUtils.isEmpty(vo.getProvince())) {
//                throw new ApiBusinessException(ApiResultEnum.PRICE_PROVINCE_NULL);
//            }
//            if (StringUtils.isEmpty(vo.getCity())) {
//                throw new ApiBusinessException(ApiResultEnum.PRICE_CITY_NULL);
//            }
//            if (StringUtils.isEmpty(vo.getDistrict())) {
//                throw new ApiBusinessException(ApiResultEnum.PRICE_DISTRICT_NULL);
//            }
            if (StringUtils.isEmpty(vo.getGoods_code())) {
                throw new ApiBusinessException(ApiResultEnum.PRICE_GOODS_CODE_NULL);
            }
//            if (vo.getStartDt() == null) {
//                throw new ApiBusinessException(ApiResultEnum.PRICE_START_DT_NULL);
//            }
//            if (vo.getEndDt() == null) {
//                throw new ApiBusinessException(ApiResultEnum.PRICE_END_DT_NULL);
//            }
            if (vo.getPriceDt() == null) {
                throw new ApiBusinessException(ApiResultEnum.PRICE_PRICE_DT_NULL);
            }
        }
    }
}
