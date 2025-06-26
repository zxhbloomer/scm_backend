package com.xinyirun.scm.core.api.serviceimpl.master.v1.goods;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsSpecVo;
import com.xinyirun.scm.bean.api.vo.master.goods.ApiGoodsVo;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.master.goods.unit.MGoodsUnitCalcEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.unit.SUnitEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecPropVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.exception.api.ApiBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiGoodsMapper;
import com.xinyirun.scm.core.api.mapper.master.goods.ApiGoodsSpecMapper;
import com.xinyirun.scm.core.api.mapper.master.unit.ApiGoodsUnitCalcMapper;
import com.xinyirun.scm.core.api.mapper.master.unit.ApiUnitMapper;
import com.xinyirun.scm.core.api.service.master.v1.goods.ApiGoodsSpecService;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecPropMapper;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class ApiGoodsSpecServiceImpl extends BaseServiceImpl<ApiGoodsSpecMapper, MGoodsSpecEntity> implements ApiGoodsSpecService {

    @Autowired
    private ApiGoodsSpecMapper mapper;

    @Autowired
    private ApiGoodsMapper apiGoodsMapper;

    @Autowired
    private MGoodsSpecPropMapper goodsSpecPropMapper;

    @Autowired
    private ApiGoodsUnitCalcMapper calcMapper;

    @Autowired
    private ApiUnitMapper unitMapper;

    /**
     * 首次所有数据同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAll(List<ApiGoodsSpecVo> voList) {
        saveData(voList);
    }

    /**
     * 新增同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncNewOnly(List<ApiGoodsSpecVo> voList) {
        saveData(voList);
    }

    /**
     * 修改同步
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncUpdateOnly(List<ApiGoodsSpecVo> voList) {
        saveData(voList);
    }

    /**
     * 同步物料规格数据的更新保存
     */
    public void saveData(List<ApiGoodsSpecVo> voList) {
        // 必输、list内部check
        checkSyncList(voList);
        for(ApiGoodsSpecVo vo:voList) {
            MGoodsSpecEntity entity = (MGoodsSpecEntity) BeanUtilsSupport.copyProperties(vo,MGoodsSpecEntity.class);
            entity.setEnable(Boolean.TRUE);
            ApiGoodsVo apiGoodsVo = new ApiGoodsVo();
            apiGoodsVo.setCode(vo.getGoods_code());
            MGoodsEntity goodsEntity = apiGoodsMapper.selectByCodeAppCode(apiGoodsVo);
            if(goodsEntity != null) {
                entity.setGoods_id(goodsEntity.getId());
                entity.setName(goodsEntity.getName());
            }
            if (StringUtils.isNotEmpty(vo.getGoods_attr_id())) {
                MGoodsSpecPropVo mGoodsSpecPropVo = goodsSpecPropMapper.selectByCode(vo.getGoods_attr_id());
                entity.setProp_id(mGoodsSpecPropVo.getId());
            }

            // 按规格编号和来源查询数据库数据是否存在
            MGoodsSpecEntity specEntity = mapper.selectByCodeAppCode(vo);
            if(specEntity != null) {
                // 如果不为空，则为修改
                entity.setId(specEntity.getId());
            }
            // 新增/修改逻辑
            if(entity.getId() == null) {
                mapper.insert(entity);
            }else{
                mapper.updateById(entity);
            }

            // 添加默认单位转换关系 吨:吨
            insertUnitCalc(entity.getId());
        }
    }

    /**
     * 新增 默认 单位换算关系
     * @param sku_id 规格 ID
     */
    private void insertUnitCalc(Integer sku_id) {
        List<MGoodsUnitCalcEntity> list = calcMapper.selectList(Wrappers.<MGoodsUnitCalcEntity>lambdaQuery()
                .eq(MGoodsUnitCalcEntity::getSku_id, sku_id));
        if (!CollectionUtils.isEmpty(list)) {
            return;
        }
        // 查询配置
        SConfigEntity config = mapper.selectConfigByKey(SystemConstants.DEFAULT_GOODS_UNIT_CALC);
        if (null == config) {
            throw new ApiBusinessException("保存失败, 未配置默认单位换算关系");
        }
        // 查询单位ID
        SUnitEntity sUnitEntity = unitMapper.selectSUnitOne(SystemConstants.DEFAULT_UNIT.CODE);

        if (null == sUnitEntity) {
            throw new ApiBusinessException("保存失败, 单位为空");
        }

        JSONObject jsonObject = new JSONObject(config.getValue());
        // 查询配置
        MGoodsUnitCalcEntity vo = new MGoodsUnitCalcEntity();
        vo.setSku_id(sku_id);
        vo.setSrc_unit(jsonObject.getStr("src_unit"));
        vo.setSrc_unit_id(jsonObject.getInt("src_unit_id"));
        vo.setSrc_unit_code(jsonObject.getStr("src_unit_code"));
        vo.setCalc(jsonObject.getBigDecimal("calc"));
        vo.setRemark(jsonObject.getStr("remark"));
        vo.setTgt_unit_id(sUnitEntity.getId());
        vo.setTgt_unit_code(SystemConstants.DEFAULT_UNIT.CODE);
        vo.setTgt_unit(SystemConstants.DEFAULT_UNIT.NAME);
        vo.setEnable(true);
        calcMapper.insert(vo);
    }

    /**
     * 必输、list内部check
     */
    private void checkSyncList(List<ApiGoodsSpecVo> voList) {
        // 必输check
        for(ApiGoodsSpecVo vo:voList){
            vo.setC_time(LocalDateTime.now());
//            checkSyncVo(vo);
        }
        // list内部check

        // 规格编号和来源查询是否有重复数据
//        List<String> specCodeList = voList.stream().map(ApiGoodsSpecVo::getCodeAppCode).collect(Collectors.toList());
//        long specCodeCount = specCodeList.stream().distinct().count();
//        if (voList.size() != specCodeCount) {
//            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_PARAM_CODE_REPEAT);
//        }

        // 相同的商品编号下，规格不能重复
//        List<String> specList = voList.stream().map(ApiGoodsSpecVo::getSpecNameGoodsCodeAppCode).collect(Collectors.toList());
//        long specCount = specList.stream().distinct().count();
//        if (voList.size() != specCount) {
//            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_PARAM_SPEC_REPEAT);
//        }
    }

    /**
     * 必输check
     */
    public void checkSyncVo(ApiGoodsSpecVo vo){
        if (StringUtils.isEmpty(vo.getCode())) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_CODE_NULL);
        }
        if (StringUtils.isEmpty(vo.getGoods_code())) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_GOODS_CODE_NULL);
        }
        if (StringUtils.isEmpty(vo.getName())) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_NAME_NULL);
        }
        if (StringUtils.isEmpty(vo.getSpec())) {
            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_SPEC_NULL);
        }
//        if (StringUtil.isEmpty(vo.getPm())) {
//            throw new ApiBusinessException(ApiResultEnum.GOODS_SPEC_PM_NULL);
//        }

    }
}
