package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.entity.business.materialconvert.BMaterialConvertEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustDetailVo;
import com.xinyirun.scm.bean.system.vo.business.adjust.BAdjustVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BConvertRecordVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertDetailVo;
import com.xinyirun.scm.bean.system.vo.business.materialconvert.BMaterialConvertVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.materialconvert.BMaterialConvertMapper;
import com.xinyirun.scm.core.system.mapper.master.inventory.MInventoryMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MBinMapper;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustDetailService;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertDetailService;
import com.xinyirun.scm.core.system.service.business.materialconvert.IBMaterialConvertRecordService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBMaterialConvertV2Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 物料转换
 * </p>
 *
 * @author wwl
 * @since 2022-05-09
 */
@Service
public class SBMaterialConvertV2ServiceImpl extends BaseServiceImpl<MInventoryMapper, MInventoryEntity> implements ISBMaterialConvertV2Service {

    @Autowired
    private IMInventoryService service;

    @Autowired
    private MBinMapper binMapper;

    @Autowired
    private IBAdjustDetailService ibAdjustDetailService;

    @Autowired
    private IBMaterialConvertDetailService ibMaterialConvertDetailService;

    @Autowired
    BMaterialConvertMapper bMaterialConvertMapper;

    @Autowired
    private IBMaterialConvertRecordService ibMaterialConvertRecordService;

    /**
     * 物料转换逻辑
     * @param parameterClass
     * @param parameter
     */
    @Override
//    @Transactional(rollbackFor = Exception.class)
//    @SysLogAnnotion
    public void materialConvert(String parameterClass, String parameter) {

        log.debug("物料转换定时任务start----------------");

        BMaterialConvertVo condition = null;
        if (parameterClass != null && parameter != null) {
            condition = JSON.parseObject(parameter, BMaterialConvertVo.class);
        } else {
            condition = new BMaterialConvertVo();
        }
        condition.setIs_effective(1);
        List<BMaterialConvertVo> list = bMaterialConvertMapper.getList1(condition);
        for (BMaterialConvertVo vo : list) {

            log.debug("物料转换detail start--------------------------------");

            if (!DictConstant.DICT_B_MATERIAL_CONVERT_TYPE_ONE.equals(vo.getType())) {
                continue;
            }

            materialConvertDetail(vo);

            log.debug("物料转换detail end--------------------------------");

        }


        log.debug("物料转换定时任务end----------------");
    }

    /**
     * 物料转换detail
     * @param vo
     */
    @Transactional(rollbackFor = Exception.class)
//    @SysLogAnnotion
    public void materialConvertDetail(BMaterialConvertVo vo) {

        vo.setMaterial_convert_id(vo.getId());
        List<BMaterialConvertDetailVo> bMaterialConvertDetailVos = ibMaterialConvertDetailService.getList(vo);
        for (BMaterialConvertDetailVo detailVo : bMaterialConvertDetailVos) {
            if (Objects.equals(detailVo.getIs_effective(), Boolean.FALSE)) {
                continue;
            }

            // 生成库存调整
            MInventoryVo mInventoryVo = new MInventoryVo();
//                mInventoryVo.setWarehouse_id(detailVo.getWarehouse_id());
            mInventoryVo.setOwner_id(detailVo.getOwner_id());
            mInventoryVo.setSku_id(detailVo.getSource_sku_id());
            List<MInventoryVo> oldInventoryVoList = service.getInventoryInfoList(mInventoryVo);

            for (MInventoryVo oldInventoryVo : oldInventoryVoList) {
                if (oldInventoryVo != null && BigDecimal.ZERO.compareTo(oldInventoryVo.getQty_avaible()) < 0) {
                    // 旧库存信息生产库存调整
                    BAdjustVo bAdjustVo = new BAdjustVo();
                    bAdjustVo.setOwner_id(oldInventoryVo.getOwner_id());
                    bAdjustVo.setOwner_code(oldInventoryVo.getOwner_code());
                    bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);
                    bAdjustVo.setRemark("物料转换-原库存信息");

                    BAdjustDetailVo bAdjustDetailVo = new BAdjustDetailVo();
                    MBinEntity binEntity = binMapper.selecBinByWarehouseId(oldInventoryVo.getWarehouse_id());

                    bAdjustDetailVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                    bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
                    bAdjustDetailVo.setBin_id(binEntity.getId());
                    bAdjustDetailVo.setSku_code(detailVo.getSource_sku_code());
                    bAdjustDetailVo.setSku_id(detailVo.getSource_sku_id());

                    BigDecimal price = oldInventoryVo.getPrice();

                    bAdjustDetailVo.setQty(oldInventoryVo.getQty_avaible());
                    bAdjustDetailVo.setQty_adjust(BigDecimal.ZERO);
                    bAdjustDetailVo.setQty_diff(BigDecimal.ZERO.subtract(oldInventoryVo.getQty_avaible()));
                    bAdjustDetailVo.setAdjusted_price(price);
                    bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

                    List<BAdjustDetailVo> bAdjustDetailVoList = new ArrayList<>();
                    bAdjustDetailVoList.add(bAdjustDetailVo);

                    bAdjustVo.setDetailList(bAdjustDetailVoList);
                    ibAdjustDetailService.insertAudit(bAdjustVo);

                    // 新库存信息生产库存调整
                    bAdjustVo = new BAdjustVo();
                    bAdjustVo.setOwner_id(oldInventoryVo.getOwner_id());
                    bAdjustVo.setOwner_code(oldInventoryVo.getOwner_code());
                    bAdjustVo.setRemark("物料转换-新库存信息");
                    bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);

                    bAdjustDetailVo = new BAdjustDetailVo();
                    binEntity = binMapper.selecBinByWarehouseId(oldInventoryVo.getWarehouse_id());

                    bAdjustDetailVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                    bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
                    bAdjustDetailVo.setBin_id(binEntity.getId());
                    bAdjustDetailVo.setSku_code(detailVo.getTarget_sku_code());
                    bAdjustDetailVo.setSku_id(detailVo.getTarget_sku_id());

                    mInventoryVo = new MInventoryVo();
                    mInventoryVo.setOwner_id(bAdjustVo.getOwner_id());
                    mInventoryVo.setSku_id(bAdjustDetailVo.getSku_id());
                    mInventoryVo.setWarehouse_id(bAdjustDetailVo.getWarehouse_id());
                    MInventoryVo newInventoryVo = service.getInventoryInfo(mInventoryVo);

                    if (null == newInventoryVo) {
                        bAdjustDetailVo.setQty(BigDecimal.ZERO);
                        bAdjustDetailVo.setQty_adjust(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setQty_diff(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setAdjusted_price(price);
                    } else {
                        bAdjustDetailVo.setQty(newInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setQty_adjust(newInventoryVo.getQty_avaible().add(oldInventoryVo.getQty_avaible()));
                        bAdjustDetailVo.setQty_diff(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setAdjusted_price(BigDecimal.ZERO);
                    }
                    bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

                    bAdjustDetailVoList = new ArrayList<>();
                    bAdjustDetailVoList.add(bAdjustDetailVo);

                    bAdjustVo.setDetailList(bAdjustDetailVoList);
                    ibAdjustDetailService.insertAudit(bAdjustVo);

                    // 生成转换记录
                    BConvertRecordVo bConvertRecordVo = new BConvertRecordVo();

                    BeanUtilsSupport.copyProperties(vo, bConvertRecordVo);
                    bConvertRecordVo.setConvert_code(vo.getCode());
                    bConvertRecordVo.setOwner_id(oldInventoryVo.getOwner_id());
                    bConvertRecordVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                    bConvertRecordVo.setCalc(detailVo.getCalc());
                    bConvertRecordVo.setConvert_name(vo.getName());
                    bConvertRecordVo.setTarget_sku_code(detailVo.getTarget_sku_code());
                    bConvertRecordVo.setTarget_sku_name(detailVo.getTarget_spec());
                    bConvertRecordVo.setTarget_goods_name(detailVo.getTarget_goods_name());
                    bConvertRecordVo.setTarget_qty(oldInventoryVo.getQty_avaible());

                    bConvertRecordVo.setSource_sku_code(detailVo.getSource_sku_code());
                    bConvertRecordVo.setSource_sku_name(detailVo.getSource_spec());
                    bConvertRecordVo.setSource_goods_name(detailVo.getSource_goods_name());
                    bConvertRecordVo.setSource_qty(oldInventoryVo.getQty_avaible());
                    ibMaterialConvertRecordService.insert(bConvertRecordVo);
                }
            }
        }

        // 更新转换时间
        BMaterialConvertEntity entity = bMaterialConvertMapper.selectById(vo.getId());
        entity.setConvert_time(LocalDateTime.now());
        entity.setConvert_status(Boolean.TRUE);
        bMaterialConvertMapper.updateById(entity);

//        // 更新每日库存
//        isbDailyInventoryService.reCreateDailyInventoryAll();
//
//        // 计算价格天数
//        SConfigEntity config = isConfigService.selectByKey(SystemConstants.PRICE_DAYS);
//        // 更新价格
//        bMaterialConvertMapper.reCallPrice(Integer.parseInt(config.getValue()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(List<BMaterialConvertVo> list) {

        for (BMaterialConvertVo bMaterialConvertVo : list) {
            BMaterialConvertVo vo = bMaterialConvertMapper.getByConvertId(bMaterialConvertVo.getId());

//            vo.setMaterial_convert_id(vo.getId());
            vo.setMaterial_convert_id(bMaterialConvertVo.getId());
            List<BMaterialConvertDetailVo> bMaterialConvertDetailVos = ibMaterialConvertDetailService.getList(vo);
            for (BMaterialConvertDetailVo detailVo : bMaterialConvertDetailVos) {
                if (Objects.equals(detailVo.getIs_effective(), Boolean.FALSE)) {
                    continue;
                }

                // 生成库存调整
                MInventoryVo mInventoryVo = new MInventoryVo();
                mInventoryVo.setOwner_id(detailVo.getOwner_id());
                mInventoryVo.setSku_id(detailVo.getSource_sku_id());
                List<MInventoryVo> oldInventoryVoList = service.getInventoryInfoList(mInventoryVo);

                for (MInventoryVo oldInventoryVo : oldInventoryVoList) {
                    if (oldInventoryVo != null && BigDecimal.ZERO.compareTo(oldInventoryVo.getQty_avaible()) < 0) {
                        // 旧库存信息生产库存调整
                        BAdjustVo bAdjustVo = new BAdjustVo();
                        bAdjustVo.setOwner_id(oldInventoryVo.getOwner_id());
                        bAdjustVo.setOwner_code(oldInventoryVo.getOwner_code());
                        bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);
                        bAdjustVo.setRemark("物料转换-原库存信息");

                        BAdjustDetailVo bAdjustDetailVo = new BAdjustDetailVo();
                        MBinEntity binEntity = binMapper.selecBinByWarehouseId(oldInventoryVo.getWarehouse_id());

                        bAdjustDetailVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                        bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
                        bAdjustDetailVo.setBin_id(binEntity.getId());
                        bAdjustDetailVo.setSku_code(detailVo.getSource_sku_code());
                        bAdjustDetailVo.setSku_id(detailVo.getSource_sku_id());

                        BigDecimal price = oldInventoryVo.getPrice();

                        bAdjustDetailVo.setQty(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setQty_adjust(BigDecimal.ZERO);
                        bAdjustDetailVo.setQty_diff(BigDecimal.ZERO.subtract(oldInventoryVo.getQty_avaible()));
                        bAdjustDetailVo.setAdjusted_price(price);
                        bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

                        List<BAdjustDetailVo> bAdjustDetailVoList = new ArrayList<>();
                        bAdjustDetailVoList.add(bAdjustDetailVo);

                        bAdjustVo.setDetailList(bAdjustDetailVoList);
                        ibAdjustDetailService.insertAudit(bAdjustVo);

                        // 新库存信息生产库存调整
                        bAdjustVo = new BAdjustVo();
                        bAdjustVo.setOwner_id(oldInventoryVo.getOwner_id());
                        bAdjustVo.setOwner_code(oldInventoryVo.getOwner_code());
                        bAdjustVo.setRemark("物料转换-新库存信息");
                        bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);

                        bAdjustDetailVo = new BAdjustDetailVo();
                        binEntity = binMapper.selecBinByWarehouseId(oldInventoryVo.getWarehouse_id());

                        bAdjustDetailVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                        bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
                        bAdjustDetailVo.setBin_id(binEntity.getId());
                        bAdjustDetailVo.setSku_code(detailVo.getTarget_sku_code());
                        bAdjustDetailVo.setSku_id(detailVo.getTarget_sku_id());

                        mInventoryVo = new MInventoryVo();
                        mInventoryVo.setOwner_id(bAdjustVo.getOwner_id());
                        mInventoryVo.setSku_id(bAdjustDetailVo.getSku_id());
                        mInventoryVo.setWarehouse_id(bAdjustDetailVo.getWarehouse_id());
                        MInventoryVo newInventoryVo = service.getInventoryInfo(mInventoryVo);

                        if (null == newInventoryVo) {
                            bAdjustDetailVo.setQty(BigDecimal.ZERO);
                            bAdjustDetailVo.setQty_adjust(oldInventoryVo.getQty_avaible());
                            bAdjustDetailVo.setQty_diff(oldInventoryVo.getQty_avaible());
                            bAdjustDetailVo.setAdjusted_price(price);
                        } else {
                            bAdjustDetailVo.setQty(newInventoryVo.getQty_avaible());
                            bAdjustDetailVo.setQty_adjust(newInventoryVo.getQty_avaible().add(oldInventoryVo.getQty_avaible()));
                            bAdjustDetailVo.setQty_diff(oldInventoryVo.getQty_avaible());
                            bAdjustDetailVo.setAdjusted_price(BigDecimal.ZERO);
                        }
                        bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

                        bAdjustDetailVoList = new ArrayList<>();
                        bAdjustDetailVoList.add(bAdjustDetailVo);

                        bAdjustVo.setDetailList(bAdjustDetailVoList);
                        ibAdjustDetailService.insertAudit(bAdjustVo);

                        // 生成转换记录
                        BConvertRecordVo bConvertRecordVo = new BConvertRecordVo();

                        BeanUtilsSupport.copyProperties(vo, bConvertRecordVo);
                        bConvertRecordVo.setConvert_code(vo.getCode());
                        bConvertRecordVo.setOwner_id(detailVo.getOwner_id());
                        bConvertRecordVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                        bConvertRecordVo.setCalc(detailVo.getCalc());
                        bConvertRecordVo.setConvert_name(vo.getName());
                        bConvertRecordVo.setTarget_sku_code(detailVo.getTarget_sku_code());
                        bConvertRecordVo.setTarget_sku_name(detailVo.getTarget_spec());
                        bConvertRecordVo.setTarget_goods_name(detailVo.getTarget_goods_name());
                        bConvertRecordVo.setTarget_qty(oldInventoryVo.getQty_avaible());

                        bConvertRecordVo.setSource_sku_code(detailVo.getSource_sku_code());
                        bConvertRecordVo.setSource_sku_name(detailVo.getSource_spec());
                        bConvertRecordVo.setSource_goods_name(detailVo.getSource_goods_name());
                        bConvertRecordVo.setSource_qty(oldInventoryVo.getQty_avaible());
                        ibMaterialConvertRecordService.insert(bConvertRecordVo);

                    }
                }
            }
            // 更新转换时间
            BMaterialConvertEntity entity = bMaterialConvertMapper.selectById(bMaterialConvertVo.getId());
            entity.setConvert_time(LocalDateTime.now());
            bMaterialConvertMapper.updateById(entity);

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
//    @SysLogAnnotion
    public void materialConvert1(String parameterClass, String parameter) {
        BMaterialConvertVo condition = null;
        if (parameterClass != null && parameter != null) {
            condition = JSON.parseObject(parameter, BMaterialConvertVo.class);
        } else {
            condition = new BMaterialConvertVo();
        }
        List<BMaterialConvertVo> list = bMaterialConvertMapper.getList1(condition);
        for (BMaterialConvertVo vo : list) {
            vo.setMaterial_convert_id(vo.getId());
            List<BMaterialConvertDetailVo> bMaterialConvertDetailVos = ibMaterialConvertDetailService.getList(vo);
            for (BMaterialConvertDetailVo detailVo : bMaterialConvertDetailVos) {

                // 生成库存调整
                MInventoryVo mInventoryVo = new MInventoryVo();
                mInventoryVo.setWarehouse_id(detailVo.getWarehouse_id());
                mInventoryVo.setOwner_id(detailVo.getOwner_id());
                mInventoryVo.setSku_id(detailVo.getSource_sku_id());
                MInventoryVo oldInventoryVo = service.getInventoryInfo(mInventoryVo);

                if (oldInventoryVo != null && BigDecimal.ZERO.compareTo(oldInventoryVo.getQty_avaible()) < 0) {
                    // 旧库存信息生产库存调整
                    BAdjustVo bAdjustVo = new BAdjustVo();
                    bAdjustVo.setOwner_id(oldInventoryVo.getOwner_id());
                    bAdjustVo.setOwner_code(oldInventoryVo.getOwner_code());
                    bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);
                    bAdjustVo.setRemark("物料转换-原库存信息");

                    BAdjustDetailVo bAdjustDetailVo = new BAdjustDetailVo();
                    MBinEntity binEntity = binMapper.selecBinByWarehouseId(oldInventoryVo.getWarehouse_id());

                    bAdjustDetailVo.setWarehouse_id(oldInventoryVo.getWarehouse_id());
                    bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
                    bAdjustDetailVo.setBin_id(binEntity.getId());
                    bAdjustDetailVo.setSku_code(detailVo.getSource_sku_code());
                    bAdjustDetailVo.setSku_id(detailVo.getSource_sku_id());

                    BigDecimal price = oldInventoryVo.getPrice();

                    bAdjustDetailVo.setQty(oldInventoryVo.getQty_avaible());
                    bAdjustDetailVo.setQty_adjust(BigDecimal.ZERO);
                    bAdjustDetailVo.setQty_diff(BigDecimal.ZERO.subtract(oldInventoryVo.getQty_avaible()));
                    bAdjustDetailVo.setAdjusted_price(price);
                    bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

                    List<BAdjustDetailVo> bAdjustDetailVoList = new ArrayList<>();
                    bAdjustDetailVoList.add(bAdjustDetailVo);

                    bAdjustVo.setDetailList(bAdjustDetailVoList);
                    ibAdjustDetailService.insertAudit(bAdjustVo);

                    // 新库存信息生产库存调整
                    bAdjustVo = new BAdjustVo();
                    bAdjustVo.setOwner_id(oldInventoryVo.getOwner_id());
                    bAdjustVo.setOwner_code(oldInventoryVo.getOwner_code());
                    bAdjustVo.setRemark("物料转换-新库存信息");
                    bAdjustVo.setType(DictConstant.DICT_B_ADJUST_TYPE_ONE);

                    bAdjustDetailVo = new BAdjustDetailVo();
                    binEntity = binMapper.selecBinByWarehouseId(detailVo.getWarehouse_id());

                    bAdjustDetailVo.setWarehouse_id(detailVo.getWarehouse_id());
                    bAdjustDetailVo.setLocation_id(binEntity.getLocation_id());
                    bAdjustDetailVo.setBin_id(binEntity.getId());
                    bAdjustDetailVo.setSku_code(detailVo.getTarget_sku_code());
                    bAdjustDetailVo.setSku_id(detailVo.getTarget_sku_id());

                    mInventoryVo = new MInventoryVo();
                    mInventoryVo.setOwner_id(bAdjustVo.getOwner_id());
                    mInventoryVo.setSku_id(bAdjustDetailVo.getSku_id());
                    mInventoryVo.setWarehouse_id(bAdjustDetailVo.getWarehouse_id());
                    MInventoryVo newInventoryVo = service.getInventoryInfo(mInventoryVo);

                    if (null == newInventoryVo) {
                        bAdjustDetailVo.setQty(BigDecimal.ZERO);
                        bAdjustDetailVo.setQty_adjust(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setQty_diff(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setAdjusted_price(price);
                    } else {
                        bAdjustDetailVo.setQty(newInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setQty_adjust(newInventoryVo.getQty_avaible().add(oldInventoryVo.getQty_avaible()));
                        bAdjustDetailVo.setQty_diff(oldInventoryVo.getQty_avaible());
                        bAdjustDetailVo.setAdjusted_price(BigDecimal.ZERO);
                    }
                    bAdjustDetailVo.setAdjusted_rule(DictConstant.DICT_B_ADJUST_RULE_TWO);

                    bAdjustDetailVoList = new ArrayList<>();
                    bAdjustDetailVoList.add(bAdjustDetailVo);

                    bAdjustVo.setDetailList(bAdjustDetailVoList);
                    ibAdjustDetailService.insertAudit(bAdjustVo);

                    // 生成转换记录
                    BConvertRecordVo bConvertRecordVo = new BConvertRecordVo();

                    BeanUtilsSupport.copyProperties(vo, bConvertRecordVo);
                    bConvertRecordVo.setConvert_code(vo.getCode());
                    bConvertRecordVo.setOwner_id(detailVo.getOwner_id());
                    bConvertRecordVo.setWarehouse_id(detailVo.getWarehouse_id());
                    bConvertRecordVo.setCalc(detailVo.getCalc());
                    bConvertRecordVo.setConvert_name(vo.getName());
                    bConvertRecordVo.setTarget_sku_code(detailVo.getTarget_sku_code());
                    bConvertRecordVo.setTarget_sku_name(detailVo.getTarget_spec());
                    bConvertRecordVo.setTarget_goods_name(detailVo.getTarget_goods_name());
                    bConvertRecordVo.setTarget_qty(oldInventoryVo.getQty_avaible());

                    bConvertRecordVo.setSource_sku_code(detailVo.getSource_sku_code());
                    bConvertRecordVo.setSource_sku_name(detailVo.getSource_spec());
                    bConvertRecordVo.setSource_goods_name(detailVo.getSource_goods_name());
                    bConvertRecordVo.setSource_qty(oldInventoryVo.getQty_avaible());
                    ibMaterialConvertRecordService.insert(bConvertRecordVo);
                }
            }

            // 更新转换时间
            BMaterialConvertEntity entity = bMaterialConvertMapper.selectById(vo.getId());
            entity.setConvert_time(LocalDateTime.now());
            bMaterialConvertMapper.updateById(entity);

        }

//        // 更新每日库存
//        isbDailyInventoryService.reCreateDailyInventoryAll();
//
//        // 计算价格天数
//        SConfigEntity config = isConfigService.selectByKey(SystemConstants.PRICE_DAYS);
//        // 更新价格
//        bMaterialConvertMapper.reCallPrice(Integer.parseInt(config.getValue()));
    }
}
