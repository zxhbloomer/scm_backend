package com.xinyirun.scm.core.system.serviceimpl.base.v1.common.inventory;

import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.adjust.BAdjustEntity;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.entity.busniess.out.BOutEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MOwnerEntity;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryAccountEntity;
import com.xinyirun.scm.bean.entity.master.inventory.MInventoryEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.bo.inventory.commonlogic.adjust.StockAdjustBo;
import com.xinyirun.scm.bean.system.bo.inventory.commonlogic.in.StockInBo;
import com.xinyirun.scm.bean.system.bo.inventory.commonlogic.out.StockOutBo;
import com.xinyirun.scm.bean.system.bo.inventory.company.ConsignorBo;
import com.xinyirun.scm.bean.system.bo.inventory.company.OwnerBo;
import com.xinyirun.scm.bean.system.bo.inventory.material.SkuBo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.BLWBo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBinBo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MLocationBo;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MWareHouseBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseLocationBinVo;
import com.xinyirun.scm.bean.system.vo.sys.unit.SUnitVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.InventoryBusinessTypeEnum;
import com.xinyirun.scm.common.enums.InventoryResultEnum;
import com.xinyirun.scm.common.enums.InventoryTypeEnum;
import com.xinyirun.scm.common.exception.inventory.InventoryBusinessException;
import com.xinyirun.scm.common.serialtype.SerialType;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.master.inventory.MInventoryMapper;
import com.xinyirun.scm.core.system.service.base.v1.common.inventory.ICommonInventoryLogicService;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustDetailService;
import com.xinyirun.scm.core.system.service.business.adjust.IBAdjustService;
import com.xinyirun.scm.core.system.service.wms.in.IBInService;
import com.xinyirun.scm.core.system.service.business.out.IBOutService;
import com.xinyirun.scm.core.system.service.master.customer.IMCustomerService;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.service.master.goods.IMGoodsSpecService;
import com.xinyirun.scm.core.system.service.master.goods.unit.IMUnitService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryAccountService;
import com.xinyirun.scm.core.system.service.master.inventory.IMInventoryService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMBinService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMLocationService;
import com.xinyirun.scm.core.system.service.master.warehouse.IMWarehouseService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyInventoryNewV2Service;
import com.xinyirun.scm.core.system.service.sys.unit.ISUnitService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MInventoryAutoCodeServiceImpl;
import com.xinyirun.scm.mq.rabbitmq.producer.business.inventory.RecreateDailyInventoryQueueMqProducter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * wms模块核心代码：库存的更新
 *
 * @author
 */
@Service
@Slf4j
public class CommonInventoryLogicServiceImpl  extends BaseServiceImpl<MInventoryMapper, MInventoryEntity> implements ICommonInventoryLogicService {

    // 库存服务
    private IMInventoryService imInventoryService;

    // sku 服务
    private IMGoodsSpecService imGoodsSpecService;

    // 仓库服务
    private IMWarehouseService imWarehouseService;

    // 库区服务
    private IMLocationService imLocationService;

    // 库位服务
    private IMBinService imBinService;

    // 委托方 客户
    private IMCustomerService imCustomerService;

    // 入库单
    private IBInService ibInService;

    // 出库单
    private IBOutService ibOutService;

    // 调整单
    private IBAdjustService ibAdjustService;

    // 调整单明细
    private IBAdjustDetailService ibAdjustDetailService;

    // 库存流水服务
    private IMInventoryAccountService imInventoryAccountService;

    // 货主服务
    private IMOwnerService imOwnerService;

    // 库存编号生成服务
    private MInventoryAutoCodeServiceImpl mInventoryAutoCodeService;

    // 单位服务
    private IMUnitService imUnitService;
    private ISUnitService isUnitService;

    // 每日库存
    private ISBDailyInventoryNewV2Service isbDailyInventoryService;

    @Autowired
    private RecreateDailyInventoryQueueMqProducter producter;

//    @Autowired
//    private Scheduler scheduler;

    /**
     * 打破循环依赖
     *  https://www.baeldung.com/circular-dependencies-in-spring
     * @param imInventoryService
     * @param imGoodsSpecService
     * @param imWarehouseService
     * @param imLocationService
     * @param imBinService
     * @param imCustomerService
     * @param ibInService
     * @param ibOutService
     * @param ibAdjustService
     * @param ibAdjustDetailService
     * @param imInventoryAccountService
     * @param imOwnerService
     * @param mInventoryAutoCodeService
     * @param imUnitService
     * @param isUnitService
     */
    @Autowired
    public CommonInventoryLogicServiceImpl(
            @Lazy IMInventoryService imInventoryService,
            @Lazy IMGoodsSpecService imGoodsSpecService,
            @Lazy IMWarehouseService imWarehouseService,
            @Lazy IMLocationService imLocationService,
            @Lazy IMBinService imBinService,
            @Lazy IMCustomerService imCustomerService,
            @Lazy IBInService ibInService,
            @Lazy IBOutService ibOutService,
            @Lazy IBAdjustService ibAdjustService,
            @Lazy IBAdjustDetailService ibAdjustDetailService,
            @Lazy IMInventoryAccountService imInventoryAccountService,
            @Lazy IMOwnerService imOwnerService,
            @Lazy MInventoryAutoCodeServiceImpl mInventoryAutoCodeService,
            @Lazy IMUnitService imUnitService,
            @Lazy ISUnitService isUnitService,
            @Lazy ISBDailyInventoryNewV2Service isbDailyInventoryService
    ){
        this.imInventoryService = imInventoryService;
        this.imGoodsSpecService = imGoodsSpecService;
        this.imWarehouseService = imWarehouseService;
        this.imLocationService = imLocationService;
        this.imBinService = imBinService;
        this.imCustomerService = imCustomerService;
        this.ibInService = ibInService;
        this.ibOutService = ibOutService;
        this.ibAdjustService = ibAdjustService;
        this.ibAdjustDetailService = ibAdjustDetailService;
        this.imInventoryAccountService = imInventoryAccountService;
        this.imOwnerService = imOwnerService;
        this.mInventoryAutoCodeService = mInventoryAutoCodeService;
        this.imUnitService = imUnitService;
        this.isUnitService = isUnitService;
        this.isbDailyInventoryService = isbDailyInventoryService;
    }


    /**
     * 通过调整单据，进行调整
     * @param adjust_id 调整单id
     */
    @Override
    public void updWmsStockByAdjustBill(Integer adjust_id) {
        // 初始化StockInBo参数
        StockAdjustBo adjustBo = setStockAdjustBoByBillAdjust(adjust_id);
        // 启动调整逻辑
        updWmsStock(adjustBo);
    }

    /**
     * 通过入库单据，进行入库
     * @param in_id
     */
    @Override
    public void updWmsStockByInBill(Integer in_id) {
        // 初始化StockInBo参数
        StockInBo inBo = setStockInBoByBillIn(in_id);
        // 启动入库逻辑
        updWmsStock(inBo);
    }

    /**
     * 通过出库单据，进行出库
     * @param out_id
     */
    @Override
    public void updWmsStockByOutBill(Integer out_id) {
        // 初始化StockOutBo参数
        StockOutBo outBo = setStockOutBoByBillOut(out_id);
        // 启动出库逻辑
        updWmsStock(outBo);
    }



    /**
     *  修改库存信息：入库
     *  入库逻辑：
     *  1、入库单提交先入锁定库存，流水为入库单
     *  2、入库单审核后才进入可用库存，流水为入库单审核
     *  3、入库单作废-提交阶段锁定库存释放：，流水为入库单作废
     *  4、入库单作废-已审核阶段可用库存释放，流水为入库单作废
     * @param inBo 入库
     */
    @Override
    public void updWmsStock(StockInBo inBo) {
        // check bean 必输项目
        // check库位，并check库区仓库
        checkStockInBo(inBo);
        // 批次号
        String lot = "";
        // todo：批次管理暂未实现，表结构中存在数据，一般来说自有入库存在批次，出库不存在批次
        // check 批次是否重复
        if(isLotCodeDuplicate(lot)){
            throw new InventoryBusinessException(InventoryResultEnum.LOT_NUM_DATA_IS_DUPLICATED);
        }
        // lot = 获取批次
        inBo.setLot(lot);
        // 入库
        // 入库,判断是否根据批次入库
        stockIn(inBo);
    }

    /**
     *  修改库存信息：出库
     *  出库逻辑：
     *  1、出库单提交先入锁定库存，流水为入库单
     *  2、出库单审核后才进入可用库存，流水为出库单审核
     *  3、出库单作废-提交阶段锁定库存释放：，流水为出库单作废
     *  4、出库单作废-已审核阶段可用库存释放，流水为出库单作废
     * @param outBo 出库
     */
    @Override
    public void updWmsStock(StockOutBo outBo) {
        // check bean 必输项目
        // check库位，并check库区仓库
        checkStockOutBo(outBo);
        // 批次号
        String lot = "";
        // todo：批次管理暂未实现，表结构中存在数据，一般来说自有入库存在批次，出库不存在批次
        // check 批次是否重复
        if(isLotCodeDuplicate(lot)){
            throw new InventoryBusinessException(InventoryResultEnum.LOT_NUM_DATA_IS_DUPLICATED);
        }
        // lot = 获取批次
        outBo.setLot(lot);
        // 出库
        stockOut(outBo);
    }

    /**
     *  修改库存信息：库存调整
     * @param adjBo 库存调整
     */
    @Override
    public void updWmsStock(StockAdjustBo adjBo) {
        // check bean 必输项目
        // check库位，并check库区仓库
        checkStockAdjustBo(adjBo);
        // 库存调整
        stockAdjust(adjBo);
    }

    /**
     * 入库,加库存
     * @param inBo
     */
    private void stockIn(StockInBo inBo) {
        boolean isDataExist = false;
        MInventoryEntity entity ;
        BInEntity bInEntity = inBo.getBInEntity();
        // 获取库存bean
        List<MInventoryEntity> mInventoryEntities = inBo.getInventories();
        if (null != mInventoryEntities && mInventoryEntities.size() > 0) {
            isDataExist = true;
            entity = mInventoryEntities.get(0);
        } else {
            entity = new MInventoryEntity();
            entity.setCode(mInventoryAutoCodeService.autoCode().getCode());
            entity.setPrice(BigDecimal.ZERO);
            entity.setAmount(BigDecimal.ZERO);

//            MUnitVo mUnitVo = imUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//            entity.setUnit_id(mUnitVo.getId());
            SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
            entity.setUnit_id(sUnitVo.getId());

            entity.setWarehouse_id(inBo.getWareHouse_id());
            entity.setLocation_id(inBo.getLocation_id());
            entity.setBin_id(inBo.getBin_id());
            entity.setSku_id(inBo.getSku_id());
            MGoodsSpecEntity spec = imGoodsSpecService.getById(inBo.getSku_id());
            entity.setSku_code(spec.getCode());
            entity.setOwner_id(inBo.getOwner_id());
            entity.setOwner_code(inBo.getBInEntity().getOwner_code());
            entity.setLot(inBo.getLot());
        }

        /**
         * 审核作废：
         *     1、审核同意时，释放可用库存
         *     2、入库单生成时，释放可用库存
         */
        if(inBo.getInventoryBusinessTypeEnum().equals(InventoryBusinessTypeEnum.IN_CANCEL)){
            // 计算单价货值逻辑 作废
            // 单价=(库存总货值-入库总货值)/(可用库存+锁定库存-入库数量)
            BigDecimal count = (entity.getQty_avaible().add(entity.getQty_lock())).subtract(bInEntity.getActual_weight());
            if (BigDecimal.ZERO.compareTo(count) < 0) {
                if (bInEntity.getAmount() != null && entity.getAmount() != null) {
                    BigDecimal amount = entity.getAmount().subtract(bInEntity.getAmount());
                    BigDecimal price = amount.divide(count, 4, BigDecimal.ROUND_HALF_UP);
                    entity.setPrice(price);
                    if (entity.getPrice() == null) {
                        entity.setPrice(BigDecimal.ZERO);
                    }
                    entity.setAmount(amount);
                }
            }

            // 判断是否已审核
//            if ("0".equals(inBo.getBInEntity().getE_opinion())) {
//                // 已审核，释放可用库存
//                if (entity.getQty_avaible().compareTo(inBo.getCount()) < 0) {
//                    throw new InventoryBusinessException(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH);
//                }
//                entity.setQty_avaible(entity.getQty_avaible().subtract(inBo.getCount()));
//            }
//            if (null == inBo.getBInEntity().getE_opinion()) {
//                // 已提交，释放锁定库存
//                entity.setQty_lock(entity.getQty_lock().subtract(inBo.getCount()));
//            }

            // 更新每日库存
//            BDailyInventoryVo bDailyInventoryVo = new BDailyInventoryVo();
//            bDailyInventoryVo.setOwner_id(inBo.getOwner_id());
//            bDailyInventoryVo.setWarehouse_id(inBo.getWareHouse_id());
//            bDailyInventoryVo.setLocation_id(inBo.getLocation_id());
//            bDailyInventoryVo.setBin_id(inBo.getBin_id());
//            bDailyInventoryVo.setSku_id(inBo.getSku_id());
//            bDailyInventoryVo.setDt(inBo.getDt());

//            isbDailyInventoryService.reCreateDailyInventoryAll(BDailyInventoryVo.class.getName(), JSON.toJSONString(bDailyInventoryVo));
// http://yirunscm.com:8080/issue/WMS-948/  不需要重新生成每日库存
//            producter.mqSendMq(bDailyInventoryVo);
        }
        // 入库单提交：入库单库存--->锁定库存更新
        if(inBo.getInventoryBusinessTypeEnum().equals(InventoryBusinessTypeEnum.IN_SUBMIT)){
            // 计算单价货值逻辑
            // 单价=(入库总货值+库存总货值)/(入库数量+可用库存+锁定库存)
            log.debug("=====================================================");
            log.debug("entity.getQty_avaible():" + entity.getQty_avaible());
            log.debug("entity.getQty_lock():" + entity.getQty_lock());
            log.debug("bInEntity.getActual_weight():" + bInEntity.getActual_weight());
            log.debug("=====================================================");
            BigDecimal count = entity.getQty_avaible().add(entity.getQty_lock()).add(bInEntity.getActual_weight());
            if (BigDecimal.ZERO.compareTo(count) < 0) {
                if (bInEntity.getAmount() != null && entity.getAmount() != null) {
                    BigDecimal amount = entity.getAmount().add(bInEntity.getAmount());
                    BigDecimal price = amount.divide(count, 4, BigDecimal.ROUND_HALF_UP);
                    entity.setPrice(price);
                    if (entity.getPrice() == null) {
                        entity.setPrice(BigDecimal.ZERO);
                    }
                    entity.setAmount(amount);
                }


            }

            // 若原数据不为空，则为修改
            entity.setQty_lock(entity.getQty_lock().add(inBo.getCount()));
        }
        // 审核同意：锁定库存--->可用库存--->锁定库存释放
        if(inBo.getInventoryBusinessTypeEnum().equals(InventoryBusinessTypeEnum.IN_AGREE)){
            // 锁定库存--->可用库存
            entity.setQty_avaible(entity.getQty_avaible().add(inBo.getCount()));
            // 锁定库存释放
            entity.setQty_lock(entity.getQty_lock().subtract(inBo.getCount()));
        }
        // 审核驳回，重置到入库单生成：锁定库存--->锁定库存释放
        if(inBo.getInventoryBusinessTypeEnum().equals(InventoryBusinessTypeEnum.IN_NOT_AGREE)){
            // 计算单价货值逻辑 驳回
            // 单价=(库存总货值-入库总货值)/(可用库存+锁定库存-入库数量)
            BigDecimal count = (entity.getQty_avaible().add(entity.getQty_lock())).subtract(bInEntity.getActual_weight());
            if (BigDecimal.ZERO.compareTo(count) < 0) {
                if (bInEntity.getAmount() != null && entity.getAmount() != null) {
                    BigDecimal amount = entity.getAmount().subtract(bInEntity.getAmount());
                    BigDecimal price = amount.divide(count, 4, BigDecimal.ROUND_HALF_UP);
                    entity.setPrice(price);
                    if (entity.getPrice() == null) {
                        entity.setPrice(BigDecimal.ZERO);
                    }
                    entity.setAmount(amount);
                }
            }

            // 锁定库存释放
            entity.setQty_lock(entity.getQty_lock().subtract(inBo.getCount()));
        }


        if(isDataExist){
            // 如果库存清光了,这里就删除库存记录
            if(isInventoryEmpty(entity)){
                imInventoryService.removeById(entity.getId());
            } else {
                imInventoryService.updateById(entity);
            }
        } else {
            // 不存在新增空数据，所以不考虑
            imInventoryService.save(entity);
        }

        // 生成库存流水
        saveInventoryAccount(inBo, entity);

//            ScheduleUtils.createJobDailyInventoryDIff(scheduler,SerialType.BILL_BUSINESS_IN, Long.valueOf(inBo.getSerial_id()), JSON.toJSONString(bDailyInventoryVo));
    }


    /**
     *
     * @param inBo
     * @param qty_avaible
     */
    private void callExceptionMessage(StockInBo inBo, BigDecimal qty_avaible) {
        throw new InventoryBusinessException(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH_1,
                String.format(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH_1.getMsg(), inBo.getOwner().getName()
                        , inBo.getBlw().getWarehouse().getName(), inBo.getSku().getName(), qty_avaible));
    }

    /**
     * 保存入库流水
     * @param inBo
     * @param mInventoryEntity
     */
    private void saveInventoryAccount(StockInBo inBo, MInventoryEntity mInventoryEntity){
        MInventoryAccountEntity entity = MInventoryAccountEntity.builder()
                .type(inBo.getInventoryTypeEnum().getCode())
                .business_type(inBo.getInventoryBusinessTypeEnum().getCode())
                .serial_type(inBo.getSerial_type())
                .serial_id(inBo.getSerial_id())
                .warehouse_id(inBo.getWareHouse_id())
                .location_id(inBo.getLocation_id())
                .bin_id(inBo.getBin_id())
                .sku_id(inBo.getSku_id())
                .owner_id(inBo.getOwner_id())
//                .supplier_id()
                .lot(inBo.getLot())
                .inventory_id(mInventoryEntity.getId())
                .qty(inBo.getCount())
                .qty_inventory(mInventoryEntity.getQty_avaible())
                .qty_lock_inventory(mInventoryEntity.getQty_lock())
                .qty_diff(inBo.getCount().subtract(mInventoryEntity.getQty_avaible()))
                .qty_lock_diff(inBo.getCount().subtract(mInventoryEntity.getQty_lock()))
                .build();
        imInventoryAccountService.save(entity);

        // 设置流水号致入库单中，并保存
        BInEntity bInEntity =  inBo.getBInEntity();
//        bInEntity.setInventory_account_id(entity.getId());
        ibInService.updateById(bInEntity);
    }

    /**
     * 保存出库流水
     * @param outBo
     * @param mInventoryEntities
     */
    private void saveInventoryAccount(StockOutBo outBo, List<MInventoryEntity> mInventoryEntities){
        // 删除空数据
        mInventoryEntities.removeIf(element -> element == null);
        for (MInventoryEntity mInventoryEntity: mInventoryEntities) {
            MInventoryAccountEntity entity = MInventoryAccountEntity.builder()
                    .type(outBo.getInventoryTypeEnum().getCode())
                    .business_type(outBo.getInventoryBusinessTypeEnum().getCode())
                    .serial_type(outBo.getSerial_type())
                    .serial_id(outBo.getSerial_id())
                    .warehouse_id(outBo.getWareHouse_id())
                    .location_id(outBo.getLocation_id())
                    .bin_id(outBo.getBin_id())
                    .sku_id(outBo.getSku_id())
                    .owner_id(outBo.getOwner_id())
//                .supplier_id()
//                .lot(outBo.getLot())
                    .inventory_id(mInventoryEntity.getId())
                    .qty(outBo.getCount())
                    .qty_inventory(mInventoryEntity.getQty_avaible())
                    .qty_lock_inventory(mInventoryEntity.getQty_lock())
                    .qty_diff(outBo.getCount().subtract(mInventoryEntity.getQty_avaible()))
                    .qty_lock_diff(outBo.getCount().subtract(mInventoryEntity.getQty_lock()))
                    .build();
            imInventoryAccountService.save(entity);
        }

    }

    /**
     * 保存调整流水
     * @param adjBo
     * @param mInventoryEntity
     */
    private void saveInventoryAccount(StockAdjustBo adjBo, MInventoryEntity mInventoryEntity){
        MInventoryAccountEntity entity = MInventoryAccountEntity.builder()
                .type(adjBo.getInventoryTypeEnum().getCode())
                .business_type(adjBo.getInventoryBusinessTypeEnum().getCode())
                .serial_type(adjBo.getSerial_type())
                .serial_id(adjBo.getSerial_id())
                .warehouse_id(adjBo.getWareHouse_id())
                .location_id(adjBo.getLocation_id())
                .bin_id(adjBo.getBin_id())
                .sku_id(adjBo.getSku_id())
                .owner_id(adjBo.getOwner_id())
//                .supplier_id()
                .lot(adjBo.getLot())
                .inventory_id(mInventoryEntity.getId())
                .qty(adjBo.getCount_diff())
                .qty_inventory(mInventoryEntity.getQty_avaible())
                .qty_lock_inventory(mInventoryEntity.getQty_lock())
                .qty_diff(adjBo.getCount_diff())
                .qty_lock_diff(BigDecimal.ZERO)
                .build();
        imInventoryAccountService.save(entity);
    }

    /**
     * 扣减库存件数(这里只操作锁定库存,出库必需是 :1先锁定库存,2再扣减锁定库存)
     * @param outBo
     */
    private void stockOut(StockOutBo outBo) {
        log.debug("------------------出库共通start-----------------");
        // 获取库存bean
        List<MInventoryEntity> mInventoryEntities = outBo.getInventories();
//        MInventoryEntity entity = mInventoryEntities.get(0);
        // 设置计算参数
        outBo.setCalculate_count(outBo.getCount());

        for ( MInventoryEntity entity: mInventoryEntities) {
            // 出库单提交：可用库存减少 锁定库存增加
            if(InventoryBusinessTypeEnum.OUT_SUBMIT.equals(outBo.getInventoryBusinessTypeEnum())){
                log.debug("------------------出库单提交计算库存start-----------------");
                // 若库存够扣，则直接扣减
                if (outBo.getCalculate_count().compareTo(entity.getQty_avaible()) < 0) {
                    // 若原数据不为空，则为修改
                    entity.setQty_avaible(entity.getQty_avaible().subtract(outBo.getCalculate_count()));

                    entity.setQty_lock(entity.getQty_lock().add(outBo.getCalculate_count()));

                    outBo.setCalculate_count(BigDecimal.ZERO);
                } else {
                    // 若库存不够扣，则全部扣减
                    entity.setQty_lock(entity.getQty_lock().add(entity.getQty_avaible()));

                    outBo.setCalculate_count(outBo.getCalculate_count().subtract(entity.getQty_avaible()));

                    entity.setQty_avaible(BigDecimal.ZERO);
                }

                log.debug("------------------出库单提交计算库存end-----------------");
            }

            // 审核同意：释放锁定库存
            if(InventoryBusinessTypeEnum.OUT_AGREE.equals(outBo.getInventoryBusinessTypeEnum())){
                log.debug("------------------出库单审核计算库存start-----------------");
                // 锁定库存释放
                // 若库存够扣，则直接释放锁定库存
                if (outBo.getCalculate_count().compareTo(entity.getQty_lock()) < 0) {
                    entity.setQty_lock(entity.getQty_lock().subtract(outBo.getCalculate_count()));

                    outBo.setCalculate_count(BigDecimal.ZERO);
                } else {
                    // 若库存不够扣，则全部释放
                    outBo.setCalculate_count(outBo.getCalculate_count().subtract(entity.getQty_lock()));

                    entity.setQty_lock(BigDecimal.ZERO);
                }
                log.debug("------------------出库单审核计算库存end-----------------");
            }

            // 审核驳回，释放锁定库存 增加可用库存
            if(InventoryBusinessTypeEnum.OUT_NOT_AGREE.equals(outBo.getInventoryBusinessTypeEnum())){
                log.debug("------------------出库单驳回计算库存start-----------------");
                // 驳回出库单数量>=锁定库存
                if (outBo.getCalculate_count().compareTo(entity.getQty_lock()) < 0) {
                    // 锁定库存释放
                    entity.setQty_lock(entity.getQty_lock().subtract(outBo.getCalculate_count()));
                    // 增加可用库存
                    entity.setQty_avaible(entity.getQty_avaible().add(outBo.getCalculate_count()));

                    outBo.setCalculate_count(BigDecimal.ZERO);
                } else {
                    outBo.setCalculate_count(outBo.getCalculate_count().subtract(entity.getQty_lock()));
                    // 驳回出库单数量<锁定库存

                    // 增加可用库存
                    entity.setQty_avaible(entity.getQty_avaible().add(entity.getQty_lock()));
                    // 锁定库存释放
                    entity.setQty_lock(BigDecimal.ZERO);
                }
                log.debug("------------------出库单驳回计算库存end-----------------");
            }

            // 过期，释放锁定库存 增加可用库存
            if(InventoryBusinessTypeEnum.OUT_EXPIRES.equals(outBo.getInventoryBusinessTypeEnum())){
                log.debug("------------------出库单过期计算库存start-----------------");
                // 锁定库存释放
                entity.setQty_lock(entity.getQty_lock().subtract(outBo.getCalculate_count()));
                // 增加可用库存
                entity.setQty_avaible(entity.getQty_avaible().add(outBo.getCalculate_count()));

                outBo.setCount(BigDecimal.ZERO);
                log.debug("------------------出库单过期计算库存end-----------------");
            }

            // 如果库存清光了,这里就删除库存记录
            if(isInventoryEmpty(entity)){
                imInventoryService.removeById(entity.getId());
            } else {
                // 重新计算货值
                if (entity.getPrice() == null) {
                    entity.setPrice(BigDecimal.ZERO);
                }
                entity.setAmount(entity.getPrice().multiply(entity.getQty_avaible().add(entity.getQty_lock())));
                imInventoryService.updateById(entity);
            }



            if (BigDecimal.ZERO.compareTo(outBo.getCalculate_count()) <= 0) {
                break;
            }
        }

        /**
         * 审核作废：
         *     1、审核同意时，增加可用库存
         *     2、入库单生成时，释放锁定库存
         */
        if(InventoryBusinessTypeEnum.OUT_CANCEL.equals(outBo.getInventoryBusinessTypeEnum())){
            log.debug("------------------出库单作废计算库存start-----------------");
            if (mInventoryEntities.size() == 0) {
                mInventoryEntities = new ArrayList<>();
                mInventoryEntities.add(null);
            }

            for ( MInventoryEntity entity : mInventoryEntities) {
                // 判断是否已审核
                if ("0".equals(outBo.getBOutEntity().getE_opinion())) {
                    // 已审核，增加可用库存
                    if (null == entity) {
                        entity = new MInventoryEntity();
                        entity.setCode(mInventoryAutoCodeService.autoCode().getCode());

                        SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
                        entity.setUnit_id(sUnitVo.getId());

                        entity.setQty_avaible(outBo.getCalculate_count());
                        entity.setQty_lock(BigDecimal.ZERO);
                        entity.setWarehouse_id(outBo.getWareHouse_id());
                        entity.setLocation_id(outBo.getLocation_id());
                        entity.setBin_id(outBo.getBin_id());
                        entity.setLot("");
                        entity.setOwner_id(outBo.getOwner_id());
                        entity.setOwner_code(outBo.getOwner().getCode());
                        entity.setPrice(outBo.getBOutEntity().getPrice());
                        entity.setAmount(outBo.getBOutEntity().getAmount());
                        entity.setSku_id(outBo.getSku_id());
                        MGoodsSpecEntity spec = imGoodsSpecService.getById(outBo.getSku_id());
                        entity.setSku_code(spec.getCode());
                        imInventoryService.save(entity);
                        mInventoryEntities.add(entity);

                    } else {
                        entity.setQty_avaible(entity.getQty_avaible().add(outBo.getCalculate_count()));
                        // 重新计算货值
                        if (entity.getPrice() == null) {
                            entity.setPrice(BigDecimal.ZERO);
                        }
                        entity.setAmount(entity.getPrice().multiply(entity.getQty_avaible().add(entity.getQty_lock())));
                    }

                    // 生成库存流水
//                    saveInventoryAccount(outBo, entity);

                    if (null == outBo.getBOutEntity().getE_opinion()) {
                        // 已提交，释放锁定库存 增加可用库存
                        // 锁定库存释放
                        entity.setQty_lock(entity.getQty_lock().subtract(outBo.getCalculate_count()));
                        // 增加可用库存
                        entity.setQty_avaible(entity.getQty_avaible().add(outBo.getCalculate_count()));
                    }

                    imInventoryService.updateById(entity);


                    if (BigDecimal.ZERO.compareTo(outBo.getCalculate_count()) <= 0) {
                        break;
                    }
                }

            }

            // 更新每日库存
//            BDailyInventoryVo bDailyInventoryVo = new BDailyInventoryVo();
//            bDailyInventoryVo.setOwner_id(outBo.getOwner_id());
//            bDailyInventoryVo.setWarehouse_id(outBo.getWareHouse_id());
//            bDailyInventoryVo.setLocation_id(outBo.getLocation_id());
//            bDailyInventoryVo.setBin_id(outBo.getBin_id());
//            bDailyInventoryVo.setSku_id(outBo.getSku_id());
//            bDailyInventoryVo.setDt(outBo.getDt());

            log.debug("------------------出库单作废计算每日库存start-----------------");
//            isbDailyInventoryService.reCreateDailyInventoryAll(BDailyInventoryVo.class.getName(), JSON.toJSONString(bDailyInventoryVo));
            // http://yirunscm.com:8080/issue/WMS-948/  不需要重新生成每日库存
//            producter.mqSendMq(bDailyInventoryVo);
            log.debug("------------------出库单作废计算每日库存end-----------------");

            log.debug("------------------出库单作废计算库存end-----------------");

//                outBo.setCount(BigDecimal.ZERO);
        }

        // 生成库存流水
        saveInventoryAccount(outBo, mInventoryEntities);

        log.debug("------------------出库共通end----------------");
//            ScheduleUtils.createJobDailyInventoryDIff(scheduler, SerialType.BILL_BUSINESS_OUT, Long.valueOf(outBo.getSerial_id()), JSON.toJSONString(bDailyInventoryVo));
    }

    /**
     * 出库操作，出可用库存--->锁定库存
     * @param mInventoryEntity
     * @param outBo
     */
    private void stockOutQty(MInventoryEntity mInventoryEntity ,StockOutBo outBo) {
        /**
         * 1、先判断是否足额扣减：
         *      1.1、足额扣减，把可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
         *      1.2、不足额扣减，把所有可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
         */
        // 可用库存大于等于计算列
        if(mInventoryEntity.getQty_avaible().compareTo(outBo.getCalculate_count()) > -1){
            // 1.1、足额扣减，把可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
            // 先加后减
            mInventoryEntity.setQty_lock(mInventoryEntity.getQty_lock().add(outBo.getCalculate_count()));
            mInventoryEntity.setQty_avaible(mInventoryEntity.getQty_avaible().subtract(outBo.getCalculate_count()));
            outBo.setCalculate_count(BigDecimal.ZERO);
        } else {
            // 1.2、不足额扣减，把所有可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
            // 先加后减
            mInventoryEntity.setQty_lock(mInventoryEntity.getQty_lock().add(mInventoryEntity.getQty_avaible()));
            outBo.setCalculate_count(outBo.getCalculate_count().subtract(mInventoryEntity.getQty_avaible()));
            mInventoryEntity.setQty_avaible(BigDecimal.ZERO);
        }
    }

    /**
     * 出库操作，出锁定库存
     * @param mInventoryEntity
     * @param outBo
     */
    private void stockOutLockQty(MInventoryEntity mInventoryEntity ,StockOutBo outBo) {
        /**
         * 1、先判断是否足额扣减，一般来说不可能存在，因为都是先制单----出致锁定库存--->审核---锁定库存扣减---->结束：
         *      1.1、足额扣减，把锁定库存扣减，同时bean中计算列同步扣减
         *      1.2、不足额扣减，把所有可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
         */
        // 可用库存大于等于计算列
        if(mInventoryEntity.getQty_lock().compareTo(outBo.getCalculate_count()) > -1){
            // 1.1、足额扣减，把可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
            mInventoryEntity.setQty_lock(mInventoryEntity.getQty_lock().subtract(outBo.getCalculate_count()));
            outBo.setCalculate_count(BigDecimal.ZERO);
        } else {
            // 1.2、不足额扣减，把所有可用库存扣减，锁定库存增加，同时bean中计算列同步扣减
            mInventoryEntity.setQty_lock(BigDecimal.ZERO);
            outBo.setCalculate_count(outBo.getCalculate_count().subtract(mInventoryEntity.getQty_avaible()));
        }
    }

    /**
     * 库存调整
     * @param adjBo
     */
    private void stockAdjust(StockAdjustBo adjBo) {
        // 获取库存bean
        List<MInventoryEntity> mInventoryEntities = adjBo.getInventories();
        if (null != mInventoryEntities && mInventoryEntities.size() > 0) {
            // 如果已有库存记录则修改
            MInventoryEntity entity = mInventoryEntities.get(0);
            entity.setQty_avaible(adjBo.getCount());

            // 计算单价，货值  id = 1843
//            if (DictConstant.DICT_B_ADJUST_RULE_ONE.equals(adjBo.getRule())) {
//                // 保持库存货值不变 重新计算单价
//                BigDecimal price = entity.getAmount().divide(adjBo.getCount(), 2, BigDecimal.ROUND_HALF_UP);
//                entity.setPrice(price);
//                if (entity.getPrice() == null) {
//                    entity.setPrice(BigDecimal.ZERO);
//                }
//                entity.setAmount(adjBo.getAmount());
//            } else {
//                // 调整货值
//                entity.setPrice(adjBo.getPrice());
//                if (entity.getPrice() == null) {
//                    entity.setPrice(BigDecimal.ZERO);
//                }
//                entity.setAmount(adjBo.getPrice().multiply(adjBo.getCount()));
//            }

//            imInventoryService.updateById(entity);
            if(isInventoryEmpty(entity)){
                imInventoryService.removeById(entity.getId());
            } else {
                imInventoryService.updateById(entity);
            }
            // 生成库存流水
            saveInventoryAccount(adjBo, entity);
        } else {
            // 如果无库存记录,新增
            MInventoryEntity entity = new MInventoryEntity();
            // 生成code
            entity.setCode(mInventoryAutoCodeService.autoCode().getCode());

//            MUnitVo mUnitVo = imUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
//            entity.setUnit_id(mUnitVo.getId());
            SUnitVo sUnitVo = isUnitService.selectByCode(SystemConstants.DEFAULT_UNIT.CODE);
            entity.setUnit_id(sUnitVo.getId());

            entity.setAmount(BigDecimal.ZERO);

            entity.setWarehouse_id(adjBo.getWareHouse_id());
            entity.setLocation_id(adjBo.getLocation_id());
            entity.setBin_id(adjBo.getBin_id());
            entity.setSku_id(adjBo.getSku_id());
            MGoodsSpecEntity spec = imGoodsSpecService.getById(adjBo.getSku_id());
            entity.setSku_code(spec.getCode());

            entity.setOwner_id(adjBo.getOwner_id());
            entity.setOwner_code(adjBo.getOwner().getCode());
            entity.setLot(adjBo.getLot());
            entity.setQty_avaible(adjBo.getCount());
            entity.setQty_lock(BigDecimal.ZERO);

            // 计算单价，货值
            if (DictConstant.DICT_B_ADJUST_RULE_ONE.equals(adjBo.getRule())) {
                // 保持库存货值不变 重新计算单价
                BigDecimal price = entity.getAmount().divide(adjBo.getCount(), 4, BigDecimal.ROUND_HALF_UP);
                entity.setPrice(price);
                if (entity.getPrice() == null) {
                    entity.setPrice(BigDecimal.ZERO);
                }
                entity.setAmount(adjBo.getAmount());
            } else {
                // 调整货值
                entity.setPrice(adjBo.getPrice());
                if (entity.getPrice() == null) {
                    entity.setPrice(BigDecimal.ZERO);
                }
                entity.setAmount(adjBo.getPrice().multiply(adjBo.getCount()));
            }

            entity.setAmount(entity.getPrice().multiply(entity.getQty_avaible().add(entity.getQty_lock())));
            if(isInventoryEmpty(entity)){
                imInventoryService.removeById(entity.getId());
            } else {
                imInventoryService.save(entity);
            }
//            imInventoryService.save(entity);
            // 生成库存流水
            saveInventoryAccount(adjBo, entity);

//                ScheduleUtils.createJobDailyInventoryDIff(scheduler, SerialType.BILL_BUSINESS_ADJUST, Long.valueOf(adjBo.getSerial_id()), JSON.toJSONString(bDailyInventoryVo));
        }

        // 更新每日库存
//        BDailyInventoryVo bDailyInventoryVo = new BDailyInventoryVo();
//        bDailyInventoryVo.setOwner_id(adjBo.getOwner_id());
//        bDailyInventoryVo.setWarehouse_id(adjBo.getWareHouse_id());
//        bDailyInventoryVo.setLocation_id(adjBo.getLocation_id());
//        bDailyInventoryVo.setBin_id(adjBo.getBin_id());
//        bDailyInventoryVo.setSku_id(adjBo.getSku_id());
//        bDailyInventoryVo.setDt(adjBo.getDt());
//
//        isbDailyInventoryService.reCreateDailyInventoryAll(BDailyInventoryVo.class.getName(), JSON.toJSONString(bDailyInventoryVo));
    }

    /**
     * 判断库存是否被使用完
     * @param entity
     * @return
     */
    private boolean isInventoryEmpty(MInventoryEntity entity){
        BigDecimal nowCount = BigDecimal.ZERO;
        nowCount = nowCount.add(entity.getQty_avaible())
                .add(entity.getQty_lock());
        return nowCount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * check并获取库位、库区、仓库，关系1：1
     * @param inBo
     * @return
     */
    private void checkStockInBo(StockInBo inBo){
        // 数量check
        checkStockInBoCount(inBo);
        // 库位check
        MBinBo bin = checkBin(inBo.getBin_id());
        // 库区check
        inBo.setLocation_id(bin.getLocation_id());
        MLocationBo location = checkLocation(bin.getLocation_id());
        location.setBin(bin);
        // 仓库check
        inBo.setWareHouse_id(bin.getWarehouse_id());
        MWareHouseBo wareHouse = checkWareHouse(bin.getWarehouse_id());
        wareHouse.setLocation(location);
        // 类型check
        if(inBo.getInventoryTypeEnum() == null){
            throw new InventoryBusinessException(InventoryResultEnum.TYPE_DATA_IS_NULL);
        }
        if(inBo.getInventoryBusinessTypeEnum() == null){
            throw new InventoryBusinessException(InventoryResultEnum.BUSINESS_TYPE_DATA_IS_NULL);
        }

        // 设置大bean
        BLWBo blw = new BLWBo();
        blw.setBin(bin);
        blw.setLocation(location);
        blw.setWarehouse(wareHouse);
        inBo.setBlw(blw);
        inBo.setLocation_id(location.getId());
        inBo.setWareHouse_id(wareHouse.getId());

        // sku check
        SkuBo sku = checkSku(inBo.getSku_id());
        inBo.setSku(sku);
        // 委托方check
        ConsignorBo consignorBo = checkConsignor(inBo.getConsignor_id());
        inBo.setConsignor(consignorBo);
        // 货主check
        OwnerBo ownerBo = checkOwner(inBo.getOwner_id());
        inBo.setOwner(ownerBo);


        // 设置悲观锁
        setInventoryForUpdate(inBo);

        // check库存是否够扣
        checkInventory(inBo);

    }

    /**
     * check并获取库位、库区、仓库，关系1：1
     * @param outBo
     * @return
     */
    private void checkStockOutBo(StockOutBo outBo){
        // 数量check
        checkStockOutBoCount(outBo);
        // 库位check
        MBinBo bin = checkBin(outBo.getBin_id());
        // 库区check
        outBo.setLocation_id(bin.getLocation_id());
        MLocationBo location = checkLocation(bin.getLocation_id());
        location.setBin(bin);
        // 仓库check
        outBo.setWareHouse_id(bin.getWarehouse_id());
        MWareHouseBo wareHouse = checkWareHouse(bin.getWarehouse_id());
        wareHouse.setLocation(location);

        // 设置大bean
        BLWBo blw = new BLWBo();
        blw.setBin(bin);
        blw.setLocation(location);
        blw.setBin(bin);
        outBo.setBlw(blw);
        outBo.setLocation_id(location.getId());
        outBo.setWareHouse_id(wareHouse.getId());

        // sku check
        SkuBo sku = checkSku(outBo.getSku_id());
        outBo.setSku(sku);
        // 委托方check
        ConsignorBo consignorBo = checkConsignor(outBo.getConsignor_id());
        outBo.setConsignor(consignorBo);
        // 货主check
        OwnerBo ownerBo = checkOwner(outBo.getOwner_id());
        outBo.setOwner(ownerBo);
        // 设置悲观锁
        setInventoryForUpdate(outBo);
        // check库存是否够出
        isOutCountOver(outBo);
    }

    /**
     * check并获取库位、库区、仓库，关系1：1
     * @param adjBo
     * @return
     */
    private void checkStockAdjustBo(StockAdjustBo adjBo){
        // 数量check
        checkStockAdjustBoCount(adjBo);
        // 库位check
        MBinBo bin = checkBin(adjBo.getBin_id());
        // 库区check
        adjBo.setLocation_id(adjBo.getLocation_id());
        MLocationBo location = checkLocation(bin.getLocation_id());
        location.setBin(bin);
        // 仓库check
        adjBo.setWareHouse_id(bin.getWarehouse_id());
        MWareHouseBo wareHouse = checkWareHouse(bin.getWarehouse_id());
        wareHouse.setLocation(location);

        // 设置大bean
        BLWBo blw = new BLWBo();
        blw.setBin(bin);
        blw.setLocation(location);
        blw.setBin(bin);
        adjBo.setBlw(blw);
        adjBo.setLocation_id(location.getId());
        adjBo.setWareHouse_id(wareHouse.getId());

        // sku check
        SkuBo sku = checkSku(adjBo.getSku_id());
        adjBo.setSku(sku);
        // 委托方check
//        ConsignorBo consignorBo = checkConsignor(adjBo.getConsignor_id());
//        adjBo.setConsignor(consignorBo);
        // 货主check
        OwnerBo ownerBo = checkOwner(adjBo.getOwner_id());
        adjBo.setOwner(ownerBo);
        // 设置悲观锁
        setInventoryForUpdate(adjBo);
    }

    /**
     * check入库数量，不能<=0,不能空
     * @param inBo
     * @return
     */
    private void checkStockInBoCount(StockInBo inBo){
        // 库位check
        if (inBo == null ) {
            throw new InventoryBusinessException(InventoryResultEnum.IN_BO_DATA_IS_NULL);
        }
        if (inBo.getCount() == null ) {
            throw new InventoryBusinessException(InventoryResultEnum.IN_BO_COUNT_DATA_IS_NULL);
        }
        if (inBo.getCount().compareTo(BigDecimal.ZERO) < 1) {
            throw new InventoryBusinessException(InventoryResultEnum.IN_BO_COUNT_DATA_IS_INVALID);
        }
    }

    /**
     * check入库数量，不能<=0,不能空
     * @param outBo
     * @return
     */
    private void checkStockOutBoCount(StockOutBo outBo){
        // 库位check
        if (outBo == null ) {
            throw new InventoryBusinessException(InventoryResultEnum.OUT_BO_DATA_IS_NULL);
        }
        if (outBo.getCount() == null ) {
            throw new InventoryBusinessException(InventoryResultEnum.OUT_BO_COUNT_DATA_IS_NULL);
        }
        if (outBo.getCount().compareTo(BigDecimal.ZERO) < 1) {
            throw new InventoryBusinessException(InventoryResultEnum.OUT_BO_COUNT_DATA_IS_INVALID);
        }
    }

    /**
     * check调整数量，不能<=0,不能空
     * @param adjBo
     * @return
     */
    private void checkStockAdjustBoCount(StockAdjustBo adjBo){
        // 库位check
        if (adjBo == null ) {
            throw new InventoryBusinessException(InventoryResultEnum.ADJUST_BO_DATA_IS_NULL);
        }
        if (adjBo.getCount() == null ) {
            throw new InventoryBusinessException(InventoryResultEnum.ADJUST_BO_COUNT_DATA_IS_NULL);
        }
        // <0时
//        if (adjBo.getCount().compareTo(BigDecimal.ZERO) == -1) {
//            throw new InventoryBusinessException(InventoryResultEnum.ADJUST_BO_COUNT_DATA_IS_INVALID);
//        }
    }

    /**
     * 库位check
     * @param bin_id
     */
    private MBinBo checkBin(Integer bin_id){
        /**
         * 1、bin_id  是否为空
         * 2、m_bin中是否存在
         * 3、m_bin中是否是不可用
         * 如果没问题返回entity
         */
        if (bin_id == null || bin_id ==0) {
            throw new InventoryBusinessException(InventoryResultEnum.BIN_DATA_IS_NOT_EXISTS);
        }
        // 通过库位表获取仓库，库区
        MBinEntity entity = imBinService.getById(bin_id);
        if(entity == null) {
            throw new InventoryBusinessException(InventoryResultEnum.BIN_DATA_IS_NOT_EXISTS);
        }

        // 是否可用
        if(!entity.getEnable()) {
            throw new InventoryBusinessException(InventoryResultEnum.BIN_DATA_IS_NOT_ENABLED);
        }
        return (MBinBo) BeanUtilsSupport.copyProperties(entity, MBinBo.class);
    }

    /**
     * 库区check
     * @param location_id
     */
    private MLocationBo checkLocation(Integer location_id){
        /**
         * 1、location_id  是否为空
         * 2、m_location中是否存在
         * 3、m_location中是否是不可用
         * 如果没问题返回entity
         */
        if (location_id == null || location_id ==0) {
            throw new InventoryBusinessException(InventoryResultEnum.LOCATION_DATA_IS_NOT_EXISTS);
        }
        // 通过库位表获取仓库，库区
        MLocationEntity entity = imLocationService.getById(location_id);
        if(entity == null) {
            throw new InventoryBusinessException(InventoryResultEnum.LOCATION_DATA_IS_NOT_EXISTS);
        }

        // 是否可用
        if(!entity.getEnable()) {
            throw new InventoryBusinessException(InventoryResultEnum.LOCATION_DATA_IS_NOT_ENABLED);
        }
        return (MLocationBo) BeanUtilsSupport.copyProperties(entity, MLocationBo.class);
    }

    /**
     * 仓库check
     * @param wareHouse_id
     */
    private MWareHouseBo checkWareHouse(Integer wareHouse_id){
        /**
         * 1、wareHouse_id  是否为空
         * 2、m_warehouse中是否存在
         * 3、m_warehouse中是否是不可用
         * 如果没问题返回entity
         */
        if (wareHouse_id == null || wareHouse_id ==0) {
            throw new InventoryBusinessException(InventoryResultEnum.WAREHOUSE_DATA_IS_NOT_EXISTS);
        }
        // 通过库位表获取仓库，库区
        MWarehouseEntity entity = imWarehouseService.getById(wareHouse_id);
        if(entity == null) {
            throw new InventoryBusinessException(InventoryResultEnum.WAREHOUSE_DATA_IS_NOT_EXISTS);
        }

        // 是否可用
        if(!entity.getEnable()) {
            throw new InventoryBusinessException(InventoryResultEnum.WAREHOUSE_DATA_IS_NOT_ENABLED);
        }
        return (MWareHouseBo) BeanUtilsSupport.copyProperties(entity, MWareHouseBo.class);
    }

    /**
     * inventory check
     * @param inBo
     */
    private void checkInventory(StockInBo inBo) {
        // 作废情况下，需要确认可用库存或锁定库存是否够扣
        if (InventoryBusinessTypeEnum.IN_CANCEL.equals(inBo.getInventoryBusinessTypeEnum())) {
            MInventoryEntity entity;

            List<MInventoryEntity> mInventoryEntities = inBo.getInventories();
            if (null != mInventoryEntities && mInventoryEntities.size() > 0) {
                entity = mInventoryEntities.get(0);
            } else {
                String owner = "";
                String warehouse = "";
                String sku = "";
                if (!Objects.isNull(inBo.getOwner())) {
                    owner = inBo.getOwner().getName();
                }
                if (!Objects.isNull(inBo.getBlw()) && !Objects.isNull(inBo.getBlw().getWarehouse())) {
                    warehouse = inBo.getBlw().getWarehouse().getName();
                }
                if (!Objects.isNull(inBo.getSku())) {
                    sku = inBo.getSku().getName();
                }
                throw new InventoryBusinessException(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH_1,
                        String.format(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH_1.getMsg(), owner, warehouse, sku, "0.0000"));
            }

            // 判断是否已审核
//            if ("0".equals(inBo.getBInEntity().getE_opinion())) {
//                // 已审核，判断可用库存
//                if (entity.getQty_avaible().compareTo(inBo.getBInEntity().getActual_weight()) < 0) {
//                    callExceptionMessage(inBo, entity.getQty_avaible());
////                    throw new InventoryBusinessException(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH);
//                }
//            }
//            if (null == inBo.getBInEntity().getE_opinion()) {
//                // 已提交，判断锁定库存
//                if (entity.getQty_avaible().compareTo(inBo.getBInEntity().getActual_weight()) < 0) {
//                    throw new InventoryBusinessException(InventoryResultEnum.INVENTORY_IS_NOT_ENOUGH);
//                }
//            }
        }
    }

    /**
     * sku check
     * @param sku_id
     * @return
     */
    private SkuBo checkSku(Integer sku_id) {
        if (sku_id == null || sku_id ==0) {
            throw new InventoryBusinessException(InventoryResultEnum.SKU_DATA_IS_NULL);
        }
        // 通过库位表获取仓库，库区
        MGoodsSpecEntity entity = imGoodsSpecService.getById(sku_id);
        if(entity == null) {
            throw new InventoryBusinessException(InventoryResultEnum.SKU_DATA_IS_NOT_EXISTS);
        }

        // 是否可用
        if(!entity.getEnable()) {
            throw new InventoryBusinessException(InventoryResultEnum.SKU_DATA_IS_NOT_ENABLED);
        }
        return (SkuBo) BeanUtilsSupport.copyProperties(entity, SkuBo.class);
    }

    /**
     * 委托方 check
     * @param consignor_id
     * @return
     */
    private ConsignorBo checkConsignor(Integer consignor_id) {
        if (consignor_id == null || consignor_id ==0) {
            throw new InventoryBusinessException(InventoryResultEnum.CONSIGNOR_DATA_IS_NULL);
        }
        // 通过库位表获取仓库，库区
        MCustomerEntity entity = imCustomerService.getById(consignor_id);
        if(entity == null) {
            throw new InventoryBusinessException(InventoryResultEnum.CONSIGNOR_DATA_IS_NOT_EXISTS);
        }

        // 是否可用
        if(!entity.getEnable()) {
            throw new InventoryBusinessException(InventoryResultEnum.CONSIGNOR_DATA_IS_NOT_ENABLED);
        }
        return (ConsignorBo) BeanUtilsSupport.copyProperties(entity, ConsignorBo.class);
    }

    /**
     * 货主 check
     * @param owner_id
     * @return
     */
    private OwnerBo checkOwner(Integer owner_id) {
        if (owner_id == null || owner_id ==0) {
            throw new InventoryBusinessException(InventoryResultEnum.OWNER_DATA_IS_NULL);
        }
        // 通过库位表获取仓库，库区
        MOwnerEntity entity = imOwnerService.getById(owner_id);
        if(entity == null) {
            throw new InventoryBusinessException(InventoryResultEnum.OWNER_DATA_IS_NOT_EXISTS);
        }

        // 是否可用
        if(!entity.getEnable()) {
            throw new InventoryBusinessException(InventoryResultEnum.OWNER_DATA_IS_NOT_ENABLED);
        }
        return (OwnerBo) BeanUtilsSupport.copyProperties(entity, OwnerBo.class);
    }

    /**
     * 出库时的库存check
     * @param outBo
     * @return
     */
    private void isOutCountOver(StockOutBo outBo){
        // 出库单提交状态跟库存进行判断check
        if(InventoryBusinessTypeEnum.OUT_SUBMIT.equals(outBo.getInventoryBusinessTypeEnum())) {
            if(outBo.getLock()){
                // 出的是锁定库存时，判断库存量 ！>= 出库量
                if(!(outBo.getTotalInventoryLockCount().compareTo(outBo.getCount()) > -1)){
                    throw new InventoryBusinessException(InventoryResultEnum.OUT_COUNT_MORE_THAN_INVENTORY_LOCK_QTY);
                }
            } else {
                // 出的是可用库存时，判断库存量 ！>= 出库量
                if(outBo.getTotalInventoryCount() == null){
                    throw new InventoryBusinessException(InventoryResultEnum.OUT_COUNT_MORE_THAN_INVENTORY_QTY);
                }
                if(!(outBo.getTotalInventoryCount().compareTo(outBo.getCount()) > -1)){
                    throw new InventoryBusinessException(InventoryResultEnum.OUT_COUNT_MORE_THAN_INVENTORY_QTY);
                }
            }
        }
    }

    /**
     * 设置悲观锁
     * @param inBo
     */
    private void setInventoryForUpdate(StockInBo inBo){
        List<MInventoryEntity> mInventoryEntities =
                imInventoryService.getInventoryForUpdate(
                        inBo.getBin_id(),
                        inBo.getOwner_id(),
                        inBo.getLot(),
                        inBo.getSku_id());
        inBo.setInventories(mInventoryEntities);
    }

    /**
     * 设置入库单悲观锁
     * @param id
     * @return
     */
    public BInEntity setBillInForUpdate(Integer id) {
        return ibInService.setBillInForUpdate(id);
    }

    /**
     * 设置出库单悲观锁
     * @param id
     * @return
     */
    public BOutEntity setBillOutForUpdate(Integer id) {
        return ibOutService.setBillOutForUpdate(id);
    }

    /**
     * 设置调整单悲观锁
     * @param id
     * @return
     */
    public BAdjustDetailEntity setBillAdjustForUpdate(Integer id) {
        return ibAdjustDetailService.setAdjustForUpdate(id);
    }

    /**
     * 设置悲观锁
     * @param outBo
     */
    private void setInventoryForUpdate(StockOutBo outBo){
        List<MInventoryEntity> mInventoryEntities =
                imInventoryService.getInventoryForUpdate(
                        outBo.getBin_id(),
                        outBo.getOwner_id(),
                        outBo.getSku_id());
        outBo.setInventories(mInventoryEntities);

        // 循环获取的数组，统计当前所有的库存
        for (MInventoryEntity entity:mInventoryEntities) {
            if(entity.getQty_avaible().add(entity.getQty_lock()).equals(BigDecimal.ZERO)){
                // 库存为0时，不应该存在这种数据
                outBo.setTotalInventoryCount(BigDecimal.ZERO);
                outBo.setTotalInventoryLockCount(BigDecimal.ZERO);
            } else {
                if(outBo.getTotalInventoryCount() != null){
                    outBo.setTotalInventoryCount(outBo.getTotalInventoryCount().add(entity.getQty_avaible()));
                }else{
                    outBo.setTotalInventoryCount((entity.getQty_avaible()));
                }
                if(outBo.getTotalInventoryLockCount() != null){
                    outBo.setTotalInventoryLockCount(outBo.getTotalInventoryLockCount().add(entity.getQty_lock()));
                }else{
                    outBo.setTotalInventoryLockCount(entity.getQty_lock());
                }

            }
        }
    }

    /**
     * 设置悲观锁
     * @param adjBo
     */
    private void setInventoryForUpdate(StockAdjustBo adjBo){
        List<MInventoryEntity> mInventoryEntities =
                imInventoryService.getInventoryForUpdate(
                        adjBo.getBin_id(),
                        adjBo.getOwner_id(),
                        adjBo.getLot(),
                        adjBo.getSku_id());
        adjBo.setInventories(mInventoryEntities);
    }

    /**
     * 查询批次号是否重复
     * @return
     */
    private boolean isLotCodeDuplicate(String lot){
        return ibInService.isDuplicate(lot);
    }

    /**
     * 查询是否存在入库单
     * @param serial_id
     */
    private StockInBo setStockInBoByBillIn(Integer serial_id){

        BInEntity entity = setBillInForUpdate(serial_id);
        if(entity == null){
            throw new InventoryBusinessException(InventoryResultEnum.BILL_IN_DATA_IS_NOT_EXISTS);
        }
        // check inventory_account_id是否为空，暂不考虑该id在数据库是否存在
//        if(entity.getInventory_account_id() != null){
//            throw new InventoryBusinessException(InventoryResultEnum.BILL_IN_DATA_IS_READY_RUN);
//        }

        StockInBo inBo = StockInBo.builder()
                .dt(entity.getU_time())
                .bin_id(entity.getBin_id())
                .sku_id(entity.getSku_id())
                .count(entity.getActual_weight())
                .serial_type(SerialType.BILL_BUSINESS_IN)
                .serial_id(entity.getId())
                .consignor_id(entity.getConsignor_id())
                .owner_id(entity.getOwner_id())
                .inventoryTypeEnum(InventoryTypeEnum.IN)
                .bInEntity(entity)
                .build();
        // 判断入库单：制单、已审核、驳回、作废
        // lock
        // WmsInventoryBusinessTypeEnum
        switch (entity.getStatus()) {
            case "0":
                // 制单
//                inBo.setLock(true);
                inBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.IN_CREATE);
                break;
            case "1":
                // 提交
//                inBo.setLock(true);
                inBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.IN_SUBMIT);
                break;
            case "2":
                // 审核同意
//                inBo.setLock(false);
                inBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.IN_AGREE);
                break;
            case "3":
                // 审核驳回
//                inBo.setLock(true);
                inBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.IN_NOT_AGREE);
                break;
            case "4":
                // 审核作废
//                inBo.setLock(true);
                inBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.IN_CANCEL);
                break;
            default:
                break;
        }
        return inBo;
    }

    /**
     * 查询是否存在出库单
     * @param serial_id
     */
    private StockOutBo setStockOutBoByBillOut(Integer serial_id){

        BOutEntity entity = setBillOutForUpdate(serial_id);
        if(entity == null){
            throw new InventoryBusinessException(InventoryResultEnum.BILL_OUT_DATA_IS_NOT_EXISTS);
        }
        // check inventory_account_id是否为空，暂不考虑该id在数据库是否存在
        if(entity.getInventory_account_id() != null){
            throw new InventoryBusinessException(InventoryResultEnum.BILL_OUT_DATA_IS_READY_RUN);
        }

        StockOutBo outBo = StockOutBo.builder()
                .dt(entity.getU_time())
                .bin_id(entity.getBin_id())
                .sku_id(entity.getSku_id())
                .count(entity.getActual_weight())
                .serial_type(SerialType.BILL_BUSINESS_OUT)
                .serial_id(entity.getId())
                .consignor_id(entity.getConsignor_id())
                .owner_id(entity.getOwner_id())
                .inventoryTypeEnum(InventoryTypeEnum.OUT)
                .bOutEntity(entity)
                .lock(Boolean.FALSE)
                .build();
        // 判断入库单：制单、已审核、驳回、作废、过期
        // lock
        // WmsInventoryBusinessTypeEnum
        switch (entity.getStatus()) {
            case "0":
                // 制单
                outBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.OUT_CREATE);
                break;
            case "1":
                // 提交
                outBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.OUT_SUBMIT);
                break;
            case "2":
                // 审核同意
                outBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.OUT_AGREE);
                break;
            case "3":
                // 审核驳回
                outBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.OUT_NOT_AGREE);
                break;
            case "4":
                // 审核作废
                outBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.OUT_CANCEL);
                break;
            case "6":
                // 过期
                outBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.OUT_EXPIRES);
                break;
            default:
                break;
        }
        return outBo;
    }

    /**
     * 查询是否存在调整单
     * @param serial_id
     */
    private StockAdjustBo setStockAdjustBoByBillAdjust(Integer serial_id){

        BAdjustDetailEntity entity = setBillAdjustForUpdate(serial_id);
        BAdjustEntity adjustEntity = ibAdjustService.getById(entity.getAdjust_id());
        if(adjustEntity == null){
            throw new InventoryBusinessException(InventoryResultEnum.BILL_ADJUST_DATA_IS_NOT_EXISTS);
        }
        // check inventory_account_id是否为空，暂不考虑该id在数据库是否存在
        if(entity.getInventory_account_id() != null){
            throw new InventoryBusinessException(InventoryResultEnum.BILL_ADJUST_DATA_IS_READY_RUN);
        }

        MWarehouseLocationBinVo locationBinVo = imWarehouseService.selectWarehouseLocationBin(entity.getWarehouse_id());

        StockAdjustBo adjustBo = StockAdjustBo.builder()
                .dt(entity.getU_time())
                .wareHouse_id(locationBinVo.getWarehouse_id())
                .location_id(locationBinVo.getLocation_id())
                .bin_id(locationBinVo.getBin_id())
                .sku_id(entity.getSku_id())
                .price(entity.getAdjusted_price())
                .amount(entity.getAdjusted_amount())
                .rule(entity.getAdjusted_rule())
                .count(entity.getQty_adjust())
                .count_diff(entity.getQty_diff())
                .serial_type(SerialType.BILL_BUSINESS_ADJUST)
                .serial_id(entity.getId())
                .owner_id(adjustEntity.getOwner_id())
                .inventoryTypeEnum(InventoryTypeEnum.ADJUST)
                .bAdjustDetailEntity(entity)
//                .lock(Boolean.FALSE)
                .build();
        // 判断调整单：制单、已审核、驳回、作废
        // lock
        // WmsInventoryBusinessTypeEnum
        switch (entity.getStatus()) {
            case "0":
                // 制单
                adjustBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.ADJUST_CREATE);
                break;
            case "1":
                // 提交
                adjustBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.ADJUST_AGREE);
                break;
            case "2":
                // 审核同意
                adjustBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.ADJUST_AGREE);
                break;
            case "3":
                // 审核驳回
                adjustBo.setInventoryBusinessTypeEnum(InventoryBusinessTypeEnum.ADJUST_NOT_AGREE);
                break;
            default:
                break;
        }
        return adjustBo;
    }


    /**
     * 获取当前登录用户的staff id
     * @return
     */
    public Long getStaffId(){
        Long staffId = ((UserSessionBo) ServletUtil.getUserSession()).getStaff_Id();
        return staffId;
    }
}
