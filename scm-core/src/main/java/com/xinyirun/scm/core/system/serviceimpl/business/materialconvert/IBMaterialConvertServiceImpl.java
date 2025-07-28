package com.xinyirun.scm.core.system.serviceimpl.business.materialconvert;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertDetailEntity;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.*;
import com.xinyirun.scm.bean.system.vo.excel.materialconvert.BMaterialConvertExportVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.business.materialconvert.BMaterialConvertDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.materialconvert.BMaterialConvertMapper;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertService;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBMaterialConvertV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BMaterialConvertAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class IBMaterialConvertServiceImpl extends BaseServiceImpl<BMaterialConvertMapper, BMaterialConvertEntity> implements IBMaterialConvertService {

    @Autowired
    BMaterialConvertMapper mapper;

    @Autowired
    BMaterialConvertDetailMapper bMaterialConvertDetailMapper;

    @Autowired
    private ISBMaterialConvertV2Service isbMaterialConvertV2Service;

    @Autowired
    private BMaterialConvertAutoCodeServiceImpl autoCode;

    @Autowired
    private IMOwnerService imOwnerService;

    @Autowired
    private IMWarehouseService imWarehouseService;

    @Autowired
    private IMGoodsSpecService imGoodsSpecService;

    @Autowired
    private ISConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BMaterialConvertVo vo) {

        List<BMaterialConvertVo> list = null;
        if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ONE.equals(vo.getType())) {
            // check
            list = mapper.getCheckList1(vo);
            if (list != null && list.size() > 0) {
                throw new BusinessException("该货主、仓库、转换后物料已存在转换数据");
            }
        }

        BMaterialConvertEntity entity = new BMaterialConvertEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setIs_latested(Boolean.TRUE);
        entity.setData_version(0);
        entity.setWarehouse_id(vo.getWarehouse_id());
        entity.setType(vo.getType());

        // 生成单号
        String code = autoCode.autoCode().getCode();
        entity.setCode(code);
        if ("0".equals(entity.getType())) {
            entity.setIs_effective(-1);
        }

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        for (BMaterialConvertDetailVo detailVo: vo.getDetailList()) {
            // check
            detailVo.setSource_sku_id(detailVo.getSource_sku_id());
            detailVo.setOwner_id(vo.getOwner_id());
            detailVo.setWarehouse_id(vo.getWarehouse_id());
            if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ONE.equals(vo.getType())) {
                list = mapper.getCheckList2(detailVo);
                if (list != null && list.size() > 0) {
                    throw new BusinessException("该货主、仓库、转换后物料已存在转换数据");
                }
            }


            BMaterialConvertDetailEntity detailEntity = new BMaterialConvertDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);

            detailEntity.setTarget_sku_code(vo.getSku_code());
            detailEntity.setTarget_sku_id(vo.getSku_id());
            detailEntity.setStatus(DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ZERO);

            detailVo.setWarehouse_id(vo.getWarehouse_id());
            detailVo.setOwner_id(vo.getOwner_id());
            detailEntity.setMaterial_convert_id(entity.getId());

            bMaterialConvertDetailMapper.insert(detailEntity);

        }

        if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ZERO.equals(entity.getType())) {
            BMaterialConvertVo bMaterialConvertVo = new BMaterialConvertVo();
            bMaterialConvertVo.setSku_id(entity.getSku_id());
            bMaterialConvertVo.setWarehouse_id(entity.getWarehouse_id());
            bMaterialConvertVo.setOwner_id(entity.getOwner_id());
            bMaterialConvertVo.setMaterial_convert_id(entity.getId());

            isbMaterialConvertV2Service.materialConvert1(bMaterialConvertVo.getClass().getName(), JSON.toJSONString(bMaterialConvertVo));

        }

        return InsertResultUtil.OK(rtn);
    }

    @Override
    public InsertResultAo<Integer> newInsert(BMaterialConvertNewVo vo) {

        List<BMaterialConvertNewVo> list = mapper.getCheckListNew(vo);
        if (list != null && list.size() > 0) {
            MOwnerVo owner = imOwnerService.selectById(vo.getOwner_id());
            MWarehouseVo warehouse = imWarehouseService.selectById(vo.getWarehouse_id());
            MGoodsSpecVo sourceSku = imGoodsSpecService.selectById(vo.getSource_sku_id());
            MGoodsSpecVo targetSku = imGoodsSpecService.selectById(vo.getTarget_sku_id());

            throw new BusinessException("该"+warehouse.getName()+"仓库"+owner.getName()+"货主物料转换已存在："+sourceSku.getGoods_name()+"|"+sourceSku.getSpec()+"  ---> "+targetSku.getGoods_name()+"|"+targetSku.getSpec());
        }

        BMaterialConvertEntity entity = new BMaterialConvertEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setIs_latested(Boolean.TRUE);
        entity.setData_version(0);
        entity.setWarehouse_id(vo.getWarehouse_id());
        entity.setType(vo.getType());
        entity.setIs_effective(0);

        // 生成单号
        String code = autoCode.autoCode().getCode();
        entity.setCode(code);

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        for (BMaterialConvertDetailVo detailVo: vo.getDetailList()) {
            // check
            detailVo.setSource_sku_id(detailVo.getSource_sku_id());
            detailVo.setOwner_id(vo.getOwner_id());
            detailVo.setWarehouse_id(vo.getWarehouse_id());

            BMaterialConvertDetailEntity detailEntity = new BMaterialConvertDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);

            detailEntity.setTarget_sku_code(vo.getSku_code());
            detailEntity.setTarget_sku_id(vo.getSku_id());
            detailEntity.setStatus(DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ZERO);
            detailEntity.setIs_effective(Boolean.TRUE);

            detailVo.setWarehouse_id(vo.getWarehouse_id());
            detailVo.setOwner_id(vo.getOwner_id());
            detailEntity.setMaterial_convert_id(entity.getId());

            bMaterialConvertDetailMapper.insert(detailEntity);
        }

        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BMaterialConvertVo vo) {

        List<BMaterialConvertVo> list = mapper.getCheckList1(vo);
        if (list != null && list.size() > 1) {
            throw new BusinessException("该货主仓库已存在转换数据");
        }

        BMaterialConvertEntity entity = mapper.selectById(vo.getMaterial_convert_id());
        entity.setIs_latested(Boolean.FALSE);
        mapper.updateById(entity);

        BeanUtilsSupport.copyProperties(vo, entity, new String[]{"id", "dbversion"});
        entity.setData_version(entity.getData_version()+1);
        entity.setId(null);
        entity.setIs_latested(Boolean.TRUE);
        if ("0".equals(entity.getType())) {
            entity.setIs_effective(-1);
        }

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        for (BMaterialConvertDetailVo detailVo: vo.getDetailList()) {
            BMaterialConvertDetailEntity detailEntity = new BMaterialConvertDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);

            detailEntity.setId(null);
            detailEntity.setTarget_sku_code(vo.getSku_code());
            detailEntity.setTarget_sku_id(vo.getSku_id());

            detailEntity.setMaterial_convert_id(entity.getId());
            bMaterialConvertDetailMapper.insert(detailEntity);

        }

        if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ZERO.equals(entity.getType())) {
            BMaterialConvertVo bMaterialConvertVo = new BMaterialConvertVo();
            bMaterialConvertVo.setSku_id(entity.getSku_id());
            bMaterialConvertVo.setWarehouse_id(entity.getWarehouse_id());
            bMaterialConvertVo.setOwner_id(entity.getOwner_id());
            bMaterialConvertVo.setMaterial_convert_id(entity.getId());

            isbMaterialConvertV2Service.materialConvert1(bMaterialConvertVo.getClass().getName(), JSON.toJSONString(bMaterialConvertVo));

        }

        return UpdateResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> newUpdate(BMaterialConvertVo vo) {

        List<BMaterialConvertVo> list = mapper.getCheckList1(vo);
        if (list != null && list.size() > 1) {
            throw new BusinessException("该货主仓库已存在转换数据");
        }

        BMaterialConvertEntity entity = mapper.selectById(vo.getMaterial_convert_id());
        BeanUtilsSupport.copyProperties(vo, entity);

        int rtn = mapper.updateById(entity);
        vo.setId(entity.getId());

        for (BMaterialConvertDetailVo detailVo: vo.getDetailList()) {
            BMaterialConvertDetailEntity detailEntity = new BMaterialConvertDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);

            detailEntity.setId(null);
            detailEntity.setTarget_sku_code(vo.getSku_code());
            detailEntity.setTarget_sku_id(vo.getSku_id());

            detailEntity.setMaterial_convert_id(entity.getId());
            if (detailEntity.getId() == null) {
                bMaterialConvertDetailMapper.insert(detailEntity);
            } else {
                bMaterialConvertDetailMapper.updateById(detailEntity);
            }

        }

        return UpdateResultUtil.OK(rtn);
    }

    @Override
    public IPage<BMaterialConvertVo> selectPage(BMaterialConvertVo searchCondition) {
        // 分页条件
        Page<BMaterialConvertEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询page
        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public List<BMaterialConvertVo> selectList(BMaterialConvertVo searchCondition) {
        return mapper.getList(searchCondition);
    }

    @Override
    public List<BMaterialConvertVo> selectList1(BMaterialConvertVo searchCondition) {
        return mapper.getCheckList1(searchCondition);
    }

    @Override
    public BMaterialConvertVo get(BMaterialConvertVo vo) {
        // 查询调整单page
        BMaterialConvertVo bMaterialConvertVo = mapper.get(vo.getId());
        // 11.28 调整 ID 为  b_material_convert 表 ID
//        BMaterialConvertVo bMaterialConvertVo = mapper.getConvert(vo.getId());
//        bMaterialConvertVo.setMaterial_convert_id(vo.getId());
        // 查询调整单明细list
        bMaterialConvertVo.setDetailList(bMaterialConvertDetailMapper.selectList(bMaterialConvertVo));
        return bMaterialConvertVo;
    }

    @Override
    public BMaterialConvertVo getByConvertId(BMaterialConvertVo vo) {
        // 查询调整单page
        BMaterialConvertVo ownerChangeVo = mapper.getByConvertId(vo.getId());
        // 查询调整单明细list
        ownerChangeVo.setDetailList(bMaterialConvertDetailMapper.selectList(ownerChangeVo));
        return ownerChangeVo;
    }

    @Override
    public BMaterialConvertVo selectById(int id) {
        return mapper.getConvert(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabled(List<BMaterialConvertVo> list) {
        for (BMaterialConvertVo vo: list) {
            BMaterialConvertEntity entity = mapper.selectById(vo.getMaterial_convert_id());
//            BMaterialConvertEntity entity = mapper.selectById(vo.getId());
            if (!Objects.equals(entity.getIs_effective(), Boolean.TRUE)) {
                entity.setIs_effective(1);
            } else {
                entity.setIs_effective(0);
            }
            mapper.updateById(entity);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> submit(List<BMaterialConvertDetailVo> searchCondition) {
        int updCount = 0;

        List<BMaterialConvertDetailEntity> list = bMaterialConvertDetailMapper.selectIdsIn(searchCondition);
        for(BMaterialConvertDetailEntity entity : list) {
            // 查询出库计划明细，更新已处理和待处理数量
            // check
            checkLogic(entity, CheckResultAo.SUBMIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ONE);
            updCount = bMaterialConvertDetailMapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

        }
        return UpdateResultUtil.OK(true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean audit(List<BMaterialConvertDetailVo> searchCondition) {

//        3.1、转换类型为立即执行
//        3.1.1、单据审核通过后，“执行完成”状态
//        3.1.2、单据审核通过后，表格中的启用状态是不可变更，变灰不可点击
//        3.2、转换类型为定时任务
//        3.2.1、单据审核通过后，如果表格中启用状态是停用，则单据是“执行完成”状态，
//        3.2.1.1、停用改成执行中，需check，该物料转换单的货主、仓库，转换后商品是否已重复(与已有的制单、已提交、执行中、执行完成状态的转换单判重)？重复则报错,提示“该货主、仓库、转换后物料已存在物料转换单”
//        3.2.2、单据审核通过后，如果表格中启用状态是启用，则单据是“执行中”状态

        int updCount = 0;
        Boolean flag = Boolean.FALSE;

        List<BMaterialConvertDetailEntity> list = bMaterialConvertDetailMapper.selectIdsIn(searchCondition);
        for(BMaterialConvertDetailEntity entity : list) {
            BMaterialConvertEntity bMaterialConvertEntity = mapper.selectById(entity.getMaterial_convert_id());
            // 查询出库计划明细，更新已处理和待处理数量
            // check
            checkLogic(entity, CheckResultAo.AUDIT_CHECK_TYPE);
            entity.setStatus(DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_TWO);
            updCount = bMaterialConvertDetailMapper.updateById(entity);
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }

            if (DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ZERO.equals(bMaterialConvertEntity.getType())) {
                flag = Boolean.TRUE;
                BMaterialConvertVo bMaterialConvertVo = new BMaterialConvertVo();
                bMaterialConvertVo.setSku_id(bMaterialConvertEntity.getSku_id());
                bMaterialConvertVo.setWarehouse_id(bMaterialConvertEntity.getWarehouse_id());
                bMaterialConvertVo.setOwner_id(bMaterialConvertEntity.getOwner_id());
                bMaterialConvertVo.setMaterial_convert_id(bMaterialConvertEntity.getId());
                bMaterialConvertVo.setIs_effective(1);



                isbMaterialConvertV2Service.materialConvert1(bMaterialConvertVo.getClass().getName(), JSON.toJSONString(bMaterialConvertVo));

//                entity.setStatus(DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_SIX);
                updCount = bMaterialConvertDetailMapper.updateById(entity);
                if(updCount == 0){
                    throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
                }
            }

//            BMaterialConvertVo bMaterialConvertVo = new BMaterialConvertVo();
//            bMaterialConvertVo.setSku_id(bMaterialConvertEntity.getSku_id());
//            bMaterialConvertVo.setWarehouse_id(bMaterialConvertEntity.getWarehouse_id());
//            bMaterialConvertVo.setOwner_id(bMaterialConvertEntity.getOwner_id());
//            bMaterialConvertVo.setMaterial_convert_id(bMaterialConvertEntity.getId());
//            bMaterialConvertVo.setIs_effective(Boolean.TRUE);
//
//            isbMaterialConvertV2Service.materialConvert(bMaterialConvertVo.getClass().getName(), JSON.toJSONString(bMaterialConvertVo));


        }
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> cancel(List<BMaterialConvertDetailVo> searchCondition) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Boolean> reject(List<BMaterialConvertDetailVo> searchCondition) {
        return null;
    }

    /**
     * 物料转换商品价格查询
     *
     * @param searchCondition 查询参数
     * @return IPage<BMaterialConvertPriceVo>
     */
    @Override
    public IPage<BMaterialConvertPriceVo> selectConvertPricePage(BMaterialConvertPriceVo searchCondition) {
        // 分页条件
        Page<BMaterialConvertEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectConvertPricePage(searchCondition, pageCondition);
    }

    /**
     * 物料转换商品价格 导出
     *
     * @param searchCondition 查询参数
     * @return List<BMaterialConvertPriceExportVo>
     */
    @Override
    public List<BMaterialConvertPriceExportVo> exportList(BMaterialConvertPriceVo searchCondition) {
        return mapper.exportList(searchCondition);
    }

    /**
     * 物料转换商品价格 求和
     *
     * @param searchCondition 查询参数
     * @return BMaterialConvertPriceVo
     */
    @Override
    public BMaterialConvertPriceVo selectConvertPriceSum(BMaterialConvertPriceVo searchCondition) {
        return mapper.selectConvertPriceSum(searchCondition);
    }

    /**
     * 获取列表，页面查询, 主表有更改
     *
     * @param searchCondition
     */
    @Override
    public IPage<BMaterialConvert1Vo> selectPage1(BMaterialConvertVo searchCondition) {
        // 分页条件
        Page<BMaterialConvertEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage1(pageCondition, searchCondition);
    }

    /**
     * 获取详情
     *
     * @param vo 入参, 主表ID
     * @return BMaterialConvertVo
     */
    @Override
    public BMaterialConvertVo getDetail(BMaterialConvertVo vo) {
//        return mapper.getConvert(vo.getId());
        return null;
    }

    @Override
    public BMaterialConvertVo get1(BMaterialConvertVo vo) {
        // 查询调整单page
//        BMaterialConvertVo ownerChangeVo = mapper.get(vo.getId());
        // 11.28 调整 ID 为  b_material_convert 表 ID
        BMaterialConvertVo bMaterialConvertVo = mapper.getConvert(vo.getId());
        bMaterialConvertVo.setMaterial_convert_id(vo.getId());
        // 查询调整单明细list
        bMaterialConvertVo.setDetailList(bMaterialConvertDetailMapper.selectList(bMaterialConvertVo));
        return bMaterialConvertVo;
    }

    @Override
    public void enabled1(List<BMaterialConvertVo> list) {
        for (BMaterialConvertVo vo: list) {
//            BMaterialConvertEntity entity = mapper.selectById(vo.getMaterial_convert_id());
            BMaterialConvertEntity entity = mapper.selectById(vo.getId());
            if (entity.getIs_effective() != 1) {
                entity.setIs_effective(1);
            } else {
                entity.setIs_effective(0);
            }
            mapper.updateById(entity);
        }
    }

    @Override
    public IPage<BMaterialConvertNewVo> selectPageNew(BMaterialConvertNewVo searchCondition) {
        // 分页条件
        Page<BMaterialConvertEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPageNew(pageCondition, searchCondition);
    }

    /**
     * 以 主表作为 ID， 一对一
     *
     * @param searchCondition
     */
    @Override
    public IPage<BMaterialConvert1Vo> selectPage2(BMaterialConvertVo searchCondition) {
        // 分页条件
        Page<BMaterialConvertEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        // 查询入库计划page
        return mapper.selectPage2(pageCondition, searchCondition);
    }

    /**
     * 物料转换, 部分导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BMaterialConvertExportVo> selectExportList(List<BMaterialConvertNewVo> searchCondition) {
        return mapper.selectExportList(searchCondition);
    }

    /**
     * 物料转换, 全部导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<BMaterialConvertExportVo> selectExportAll(BMaterialConvertNewVo searchCondition) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.getExportAllNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        // 查询入库计划page
        return mapper.selectExportAllList(searchCondition);
    }

    /**
     * check逻辑
     * @return
     */
    private void checkLogic(BMaterialConvertDetailEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 是否制单或者驳回状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ZERO) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_THREE)) {
                    throw new BusinessException("修改失败，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.SUBMIT_CHECK_TYPE:
                // 是否制单或驳回状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ZERO) && !Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_THREE)) {
                    throw new BusinessException("无法提交，该单据不是制单或驳回状态");
                }
                break;
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ONE)) {
                    throw new BusinessException("无法审核，该单据不是已提交状态");
                }
                break;
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 是否已经作废
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_FOUR)) {
                    throw new BusinessException("无法重复作废");
                }
                break;
            case CheckResultAo.REJECT_CHECK_TYPE:
                // 是否已提交状态
                if(!Objects.equals(entity.getStatus(), DictConstant.DICT_B_MATERIAL_CONVERT_STATUS_ONE)) {
                    throw new BusinessException("无法驳回，该单据不是已提交状态");
                }
                break;
            default:
        }
    }
}
