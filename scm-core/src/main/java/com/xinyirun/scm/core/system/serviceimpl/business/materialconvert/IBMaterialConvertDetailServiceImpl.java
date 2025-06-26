package com.xinyirun.scm.core.system.serviceimpl.business.materialconvert;

import com.xinyirun.scm.bean.entity.busniess.materialconvert.BMaterialConvertDetailEntity;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertDetailVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo;
import com.xinyirun.scm.core.system.mapper.business.materialconvert.BMaterialConvertDetailMapper;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertDetailService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class IBMaterialConvertDetailServiceImpl extends BaseServiceImpl<BMaterialConvertDetailMapper, BMaterialConvertDetailEntity> implements IBMaterialConvertDetailService {

    @Autowired
    private BMaterialConvertDetailMapper mapper;

//    @Autowired
//    private BMaterialConvertMapper materialConvertMapper;
//
//    @Autowired
//    private BMaterialConvertAutoCodeServiceImpl autoCode;
//
//    @Autowired
//    private ISBMaterialConvertV2Service isbMaterialConvertV2Service;

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public InsertResultAo<Integer> insert(BMaterialConvertVo vo) {
//
//        List<BMaterialConvertVo> list = null;
//        if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ONE.equals(vo.getType())) {
//            // check
//            list = materialConvertMapper.getCheckList1(vo);
//            if (list != null && list.size() > 0) {
//                throw new BusinessException("该货主仓库已存在转换数据");
//            }
//        }
//
//        BMaterialConvertEntity entity = new BMaterialConvertEntity();
//        BeanUtilsSupport.copyProperties(vo, entity);
//        entity.setIs_latested(Boolean.TRUE);
//        entity.setData_version(0);
//        entity.setWarehouse_id(vo.getWarehouse_id());
//        entity.setType(vo.getType());
//
//        // 生成单号
//        String code = autoCode.autoCode().getCode();
//        entity.setCode(code);
//        if ("0".equals(entity.getType())) {
//            entity.setIs_effective(-1);
//        }
//
//        int rtn = materialConvertMapper.insert(entity);
//        vo.setId(entity.getId());
//
//        for (BMaterialConvertDetailVo detailVo: vo.getDetailList()) {
//            // check
//            detailVo.setSource_sku_id(detailVo.getSource_sku_id());
//            detailVo.setOwner_id(vo.getOwner_id());
//            detailVo.setWarehouse_id(vo.getWarehouse_id());
//            if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ONE.equals(vo.getType())) {
//                list = materialConvertMapper.getCheckList2(detailVo);
//                if (list != null && list.size() > 0) {
//                    throw new BusinessException("该货主仓库已存在转换数据");
//                }
//            }
//
//
//            BMaterialConvertDetailEntity detailEntity = new BMaterialConvertDetailEntity();
//            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
//
//            detailEntity.setTarget_sku_code(vo.getSku_code());
//            detailEntity.setTarget_sku_id(vo.getSku_id());
//            detailEntity.setStatus(DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ZERO);
//
//            detailVo.setWarehouse_id(vo.getWarehouse_id());
//            detailVo.setOwner_id(vo.getOwner_id());
//            detailEntity.setMaterial_convert_id(entity.getId());
//
//            mapper.insert(detailEntity);
//
//        }
//
//        if ("0".equals(entity.getType())) {
//            entity.setIs_effective(-1);
//        }
//
//        return InsertResultUtil.OK(rtn);
//    }
//
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public UpdateResultAo<Integer> update(BMaterialConvertVo vo) {
//
//        List<BMaterialConvertVo> list = materialConvertMapper.getCheckList1(vo);
//        if (list != null && list.size() > 0) {
//            throw new BusinessException("该货主仓库已存在转换数据");
//        }
//
//        BMaterialConvertEntity entity = materialConvertMapper.selectById(vo.getMaterial_convert_id());
//        entity.setIs_latested(Boolean.FALSE);
//        materialConvertMapper.updateById(entity);
//
//        BeanUtilsSupport.copyProperties(vo, entity, new String[]{"id", "dbversion"});
//        entity.setData_version(entity.getData_version()+1);
//        entity.setId(null);
//        entity.setIs_latested(Boolean.TRUE);
//        if ("0".equals(entity.getType())) {
//            entity.setIs_effective(-1);
//        }
//
//        int rtn = materialConvertMapper.insert(entity);
//        vo.setId(entity.getId());
//
//        for (BMaterialConvertDetailVo detailVo: vo.getDetailList()) {
//            BMaterialConvertDetailEntity detailEntity = new BMaterialConvertDetailEntity();
//            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
//
//            detailEntity.setId(null);
//            detailEntity.setTarget_sku_code(vo.getSku_code());
//            detailEntity.setTarget_sku_id(vo.getSku_id());
//
//            detailEntity.setMaterial_convert_id(entity.getId());
//            mapper.insert(detailEntity);
//
//        }
//
//        if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ZERO.equals(entity.getType())) {
//            BMaterialConvertVo bMaterialConvertVo = new BMaterialConvertVo();
//            bMaterialConvertVo.setSku_id(entity.getSku_id());
//            bMaterialConvertVo.setWarehouse_id(entity.getWarehouse_id());
//            bMaterialConvertVo.setOwner_id(entity.getOwner_id());
//            bMaterialConvertVo.setMaterial_convert_id(entity.getId());
//
//            isbMaterialConvertV2Service.materialConvert1(bMaterialConvertVo.getClass().getName(), JSON.toJSONString(bMaterialConvertVo));
//
//        }
//
//        return UpdateResultUtil.OK(rtn);
//    }

    @Override
    public List<BMaterialConvertDetailVo> getList(BMaterialConvertVo vo) {
        return mapper.selectList(vo);
    }

}
