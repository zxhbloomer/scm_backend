package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v1;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.xinyirun.scm.bean.entity.busniess.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.system.bo.inventory.daily.BDailyInventoryWorkBo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.business.inventory.BDailyInventoryWorkMapper;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v1.SBDailyInventoryMappper;
import com.xinyirun.scm.core.system.service.sys.schedule.v1.ISBDailyInventoryService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 *  每日库存变化表的service
 * </p>
 * 废弃
 * @author zxh
 * @since 2019-07-04
 */
@Deprecated
@Service
public class SBDailyInventoryServiceImpl extends BaseServiceImpl<SBDailyInventoryMappper, BDailyInventoryEntity> implements ISBDailyInventoryService {

    @Autowired
    SBDailyInventoryMappper mapper;

    @Autowired
    BDailyInventoryWorkMapper bDailyInventoryWorkMapper;


    /**
     * 重新生成每日库存表
     */
//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reCreateDailyInventoryAll() {
        log.debug("----------------每日库存表start---------");
        /**
         * 删除数据工作表
         */
        log.debug("----------------删除数据工作表start---------");
        mapper.deleteTemoraryTableDailyInventoryWork00();
        mapper.deleteTemoraryTableDailyInventoryTemp01();
        log.debug("----------------删除数据工作表end---------");
        /**
         * 锁定工作表
         */
        log.debug("----------------锁定工作表start---------");
        mapper.lockTemoraryTableDailyInventoryTemp10();
        mapper.lockTemoraryTableDailyInventoryWork11();
        log.debug("----------------锁定工作表end---------");

        /**
         * 插入数据,尚未清洗的数据源，work
         */
        log.debug("----------------插入数据,尚未清洗的数据源，workstart---------");
        mapper.createTemoraryData20();
        log.debug("----------------插入数据,尚未清洗的数据源，workend---------");

        /**
         * 出库数据：汇总
         */
        log.debug("----------------出库数据：汇总start---------");
        mapper.createTemoraryDataOutInsert30();
        log.debug("----------------出库数据：汇总end---------");
        /**
         * 清洗调整数据，新增，汇总
         */
        log.debug("----------------清洗调整数据，新增，汇总start---------");
        mapper.createTemoraryDataAdjustInsert40();
        log.debug("----------------清洗调整数据，新增，汇总end---------");
        /**
         * 清洗调整数据，更新，汇总
         */
        log.debug("----------------清洗调整数据，更新，汇总start---------");
        mapper.createTemoraryDataAdjustUpdate50();
        log.debug("----------------清洗调整数据，更新，汇总end---------");

        /**
         * 清洗入库数据，新增，汇总
         */
        log.debug("----------------清洗入库数据，新增，汇总start---------");
        mapper.createTemoraryDataInInsert60();
        log.debug("----------------清洗入库数据，新增，汇总end---------");

        /**
         * 清洗入库数据，更新，汇总
         */
        log.debug("----------------清洗入库数据，更新，汇总start---------");
        mapper.createTemoraryDataInUpdate70();
        log.debug("----------------清洗入库数据，更新，汇总end---------");
        /**
         * 获取工作表中的数据，开始逐条计算，计算移动平均单价
         */
        log.debug("----------------获取工作表中的数据，开始逐条计算，计算移动平均单价start---------");
        List<BDailyInventoryWorkBo> workBoList = mapper.getWorkBoList();
        log.debug("----------------获取工作表中的数据，开始逐条计算，计算移动平均单价end---------");
        /**
         * 计算并设置，当天移动平均单价，当天库存,货值
         */
        log.debug("----------------计算并设置，当天移动平均单价，当天库存start---------");
        setAveragePrice(workBoList);
        log.debug("----------------计算并设置，当天移动平均单价，当天库存end---------");

        /**
         * 保存到工作表中
         */
        log.debug("----------------保存到工作表中start---------");
        saveAveragePrice2WorkTable(workBoList);
        log.debug("----------------保存到工作表中end---------");

        /**
         * 更新至每日库存表中：
         * 每日库存表工作表（b_daily_inventory_temp）：
         * 1、当天库存（qty）：找到b_daily_inventory_work（仓库、库区、库位、货主、商品）
         *    那天最后一条数据.average_price（移动平均单价），inventory_qty（当时的库存）,货值（amount）
         * 2、全删全插
         */
        mapper.updateWorkTableFinal();

        /**
         * 开始处理temp表：
         * 在这里，需要有个temp2，来处理：
         * 1、保持每天数据的完整性，例如1/1有数据，今天日期为2/1，期间即使没有数据，也要自动生成1个月的数据
         * 2、自动生成的数据保持为上一天的数据（仓库、库区、库位、货主、sku）
         */
        mapper.deleteTableDailyInventoryTemp2_100();
        mapper.lockTableDailyInventoryTemp2_100();
        mapper.createTableDailyInventoryTemp2_100();
        mapper.updateTableDailyInventoryTemp2_100();



        log.debug("----------------更新至每日库存表中start---------");
        mapper.lockTableDailyInventory12();
        mapper.deleteTableDailyInventory13();
        mapper.insertTableDailyInventoryFinal();
        log.debug("----------------更新至每日库存表中end---------");

        log.debug("----------------每日库存表end---------");
    }

    /**
     * 计算完毕的数据，保存至表中
     */
    void saveAveragePrice2WorkTable(List<BDailyInventoryWorkBo> workBoList){
        // 循环
        mapper.deleteTemoraryTableDailyInventoryWork00();
        // 批量插入
        if (workBoList.size() >0) {
            mapper.insertBatchWorkTable(workBoList);
        }
    }

    /**
     * 计算并设置，当天移动平均单价，当天库存
     */
    void setAveragePrice(List<BDailyInventoryWorkBo> workBoList){
        // 记录条数
        int index = 0;
        // 循环
        for (BDailyInventoryWorkBo current_bo : workBoList) {
            index = index + 1;
            // 获取bo当时的库存货值
            setAveragePrice(current_bo, workBoList);
        }

    }

    /**
     * 在相同的仓库、库区、库位、货主、商品下，获取上一日期的货值
     * @param current_bo
     * @param workBoList
     */
    void setAveragePrice(BDailyInventoryWorkBo current_bo, List<BDailyInventoryWorkBo> workBoList){
        /**
         * 注意：
         * 1、第一天不会是出库数据，因为出库不可负库存，如果出库日期大于每日库存表中的数据
         * 2、根据传入的参数查找，仓库、库区、库位、货主、商品，lag_id为上一个日期的id，如果为null，则代表为第一条
         */
        if(current_bo.getLag_id() == null){
            /**
             * 第一条，计算货值和移动单价
             * 若：入库单，当天货值（average_price）= 入库数量（qty）*单价（price）
             *           移动单价（average_price）= 单价（price）
             *    出库单，不可能
             *    调整单：货值=数量*单价
             *           移动单价=单价
             */
            // 为第一条
            current_bo.setInventory_qty(current_bo.getQty());
            current_bo.setAverage_price(current_bo.getPrice());
            current_bo.setAmount(current_bo.getPrice().multiply(current_bo.getQty()));
        } else {
            /**
             * 不是第一条，则开始查找上一条id（lag_id）查找数据
             * 并且找到的数据应该只有一条
             */
            Collection<BDailyInventoryWorkBo> filter = Collections2.filter(workBoList, new Predicate<BDailyInventoryWorkBo>(){
                @Override
                public boolean apply(BDailyInventoryWorkBo input) {
                    try {
                        if(input.getData_id().equals(current_bo.getLag_id())){
                            return true;
                        }else {
                            return false;
                        }
                    } catch (Exception e) {
                        log.debug("error-------------比较对象发生错误：" + e.getMessage());
                    }
                    return false;
                }
            });
            BDailyInventoryWorkBo lag_data = Iterables.getOnlyElement(filter);

            setAveragePrice(current_bo, lag_data);

        }
    }

    /**
     * 找到数据后，获取到上一天的货值和库存，并计算当天的货值，进行计算
     * 上一天应该是计算好的，所以直接获取就行了，当天的根据上一天的开始计算
     *  设置当天库存、当天平均单价
     *  若本数据是：
     *  +入库单：
     *       -库存计算：
     *           1、当天的库存（inventory_qty）=上一天库存（inventory_qty）+当天入库qty
     *       -当天移动平均单价：
     *           1、上一天货值(amount)=上一天货值(amount)
     *           2、当天入库货值(amount)=当天入库（qty）*当天单价(price)
     *           3、当天移动平均单价(average_price) = （上一天货值(amount)+当天入库货值）/ 当天的库存）
     *  +出库单：
     *       -库存计算：
     *           1、当天的库存=上一天库存（inventory_qty）- 当天出库qty
     *       -当天移动平均单价：
     *           1、上一天货值(amount)=上一天货值(amount)
     *           2、当天出库货值(amount)=当天出库（qty）* 上一天移动平均单价(average_price)
     *           3、当天移动平均单价(average_price) = （上一天货值(amount)-当天出库货值）/ 当天的库存）
     *  +调整单：
     *       -库存计算：
     *           1、当天的库存=上一天库存（inventory_qty）+ 当天调整量qty（差量-可能有负数）
     *       -当天移动平均单价：
     *           1、上一天货值(amount)=上一天货值(amount)
     *           2、当天库存调整货值(amount)=当天调整量qty（差量-可能有负数）* 上一天移动平均单价(average_price)
     *           3、当天移动平均单价(average_price) = （上一天货值(amount)+当天库存调整货值(amount可能负数)）/ 当天的库存）
     */
    void setAveragePrice(BDailyInventoryWorkBo current_data, BDailyInventoryWorkBo lag_data) {
        if (current_data.getType().equals(SystemConstants.DAILY_INVENTORY_SYNC_TYPES.IN)) {
            // 入库单
            setAveragePriceIn(current_data, lag_data);
        } else if (current_data.getType().equals(SystemConstants.DAILY_INVENTORY_SYNC_TYPES.OUT)) {
            // 出库单
            setAveragePriceOut(current_data, lag_data);
        } else {
            // 调整单
            setAveragePriceAdjust(current_data, lag_data);
        }
    }


    /**
     *  设置当天库存、当天平均单价
     *  本数据是：
     *  +入库单：
     *       -库存计算：
     *           1、当天的库存（inventory_qty）=上一天库存（inventory_qty）+当天入库qty
     *       -当天移动平均单价：
     *           1、上一天货值(amount)=上一天货值(amount)
     *           2、当天入库货值(amount)=当天入库（qty）*当天单价(price)
     *           3、当天移动平均单价(average_price) = （上一天货值(amount)+当天入库货值）/ 当天的库存）
     */
    void setAveragePriceIn(BDailyInventoryWorkBo current_data, BDailyInventoryWorkBo lag_data){
        // 库存计算：1、当天的库存（inventory_qty）=上一天库存（inventory_qty）+当天入库qty
        BigDecimal current_inventory_qty = lag_data.getInventory_qty().add(current_data.getQty());
        // 当天移动平均单价：1、上一天货值(amount)=上一天货值(amount)
        BigDecimal previous_amount = lag_data.getAmount();
        // 当天移动平均单价：2、当天入库货值(amount)=当天入库（qty）*当天单价(price)
        BigDecimal current_amount = current_data.getQty().multiply(current_data.getPrice());
        // 当天移动平均单价：3、当天移动平均单价(average_price) = （上一天货值(amount)+当天入库货值）/ 当天的库存）
        BigDecimal current_average_price = divide(previous_amount.add(current_amount), current_inventory_qty);
        // 设置当天的库存数量、当天的移动平均单价、当天库存货值
        current_data.setInventory_qty(current_inventory_qty);
        current_data.setAverage_price(current_average_price);
        current_data.setAmount(current_data.getInventory_qty().multiply(current_data.getAverage_price()));
    }

    /**
     *  设置当天库存、当天平均单价
     *  本数据是：
     *  +出库单：
     *       -库存计算：
     *           1、当天的库存=上一天库存（inventory_qty）- 当天出库qty
     *       -当天移动平均单价：
     *           1、上一天货值(amount)=上一天货值(amount)
     *           2、当天出库货值(amount)=当天出库（qty）* 上一天移动平均单价(average_price)
     *           3、当天移动平均单价(average_price) = （上一天货值(amount)-当天出库货值）/ 当天的库存）
     */
    void setAveragePriceOut(BDailyInventoryWorkBo current_data, BDailyInventoryWorkBo lag_data){
        // 库存计算：1、当天的库存=上一天库存（inventory_qty）- 当天出库qty
        BigDecimal current_inventory_qty = divide(lag_data.getInventory_qty(), current_data.getQty());
        // 当天移动平均单价：1、上一天货值(amount)=上一天货值(amount)
        BigDecimal previous_amount = lag_data.getAmount();
        // 当天移动平均单价：2、当天出库货值(amount)=当天出库（qty）* 上一天移动平均单价(average_price)
        BigDecimal current_amount = current_data.getQty().multiply(lag_data.getAverage_price());
        // 当天移动平均单价：3、当天移动平均单价(average_price) = （上一天货值(amount)-当天出库货值）/ 当天的库存）
        BigDecimal current_average_price = divide(previous_amount.subtract(current_amount), current_inventory_qty);
        // 设置当天的库存数量、当天的移动平均单价、当天库存货值
        current_data.setInventory_qty(current_inventory_qty);
        current_data.setAverage_price(current_average_price);
        current_data.setAmount(current_data.getInventory_qty().multiply(current_data.getAverage_price()));
    }

    /**
     *  设置当天库存、当天平均单价
     *  本数据是：
     *  +调整单：
     *       -库存计算：
     *           1、当天的库存=上一天库存（inventory_qty）+ 当天调整量qty（差量-可能有负数）
     *       -当天移动平均单价：
     *           1、上一天货值(amount)=上一天货值(amount)
     *           2、当天库存调整货值(amount)=当天调整量qty（差量-可能有负数）* 上一天移动平均单价(average_price)
     *           3、当天移动平均单价(average_price) = （上一天货值(amount)+当天库存调整货值(amount可能负数)）/ 当天的库存）

     * @param current_data
     * @param lag_data
     */
    void setAveragePriceAdjust(BDailyInventoryWorkBo current_data, BDailyInventoryWorkBo lag_data){
        // 库存计算：1、当天的库存=上一天库存（inventory_qty）+ 当天调整量qty（差量-可能有负数）
        BigDecimal current_inventory_qty = divide(lag_data.getInventory_qty(), current_data.getQty());
        // 当天移动平均单价：1、上一天货值(amount)=上一天货值(amount)
        BigDecimal previous_amount = lag_data.getAmount();
        // 当天移动平均单价：2、当天库存调整货值(amount)=当天调整量qty（差量-可能有负数）* 上一天移动平均单价(average_price)
        BigDecimal current_amount = current_data.getQty().multiply(lag_data.getAverage_price());
        // 当天移动平均单价：3、当天移动平均单价(average_price) = （上一天货值(amount)+当天库存调整货值(amount可能负数)）/ 当天的库存）
        BigDecimal current_average_price = divide(previous_amount.add(current_amount), current_inventory_qty);
        // 设置当天的库存数量、当天的移动平均单价、当天库存货值
        current_data.setInventory_qty(current_inventory_qty);
        current_data.setAverage_price(current_average_price);
        current_data.setAmount(current_data.getInventory_qty().multiply(current_data.getAverage_price()));
    }

    /**
     * 除法运算
     * @param divisor
     * @param dividend
     * @return
     */
    BigDecimal divide(BigDecimal divisor , BigDecimal dividend){
        try {
            return divisor.divide(dividend, BigDecimal.ROUND_HALF_UP);
        } catch (Exception e) {
            return new BigDecimal(0);
        }
    }
}
