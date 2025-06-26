package com.xinyirun.scm.core.system.serviceimpl.query.largescreen;

import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsLargeScreenVo;
import com.xinyirun.scm.bean.system.vo.report.largescreen.LogisticsMonitorLargeScreenVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.query.largescreen.LogisticsLargeScreenMapper;
import com.xinyirun.scm.core.system.service.query.largescreen.LogisticsLargeScreenService;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/9/27 14:41
 */

@Service
public class LogisticsLargeScreenServiceImpl implements LogisticsLargeScreenService {

    @Autowired
    private LogisticsLargeScreenMapper mapper;

    @Autowired
    private ISDictDataService dictDataService;
    /**
     * 供应链相关
     *
     * @return
     */
    @Override
    public LogisticsLargeScreenVo querySupply() {
        String batchDate = getBatchDate();
        // 原粮累计出库量
        BigDecimal row_grain = mapper.selectRowGrainCount(batchDate);
        // 销售累计出库量
        BigDecimal purchase = mapper.selectPurchaseOutQty(batchDate);
        // 加工厂累计生产数量
        BigDecimal productQty = mapper.selectProductQty(batchDate);
        // 饲料厂收货数量
        BigDecimal feedQty = mapper.selectFeedQty(batchDate);
        // 加工厂累计生产杂质数量
        BigDecimal impurityQty = mapper.selectProductQtyByCode(batchDate, SystemConstants.PRODUCT_COMM_CODE.COMM_IMPURITIES);
        // 加工厂累计生产稻壳数量
        BigDecimal riceHuskQty = mapper.selectProductQtyByCode(batchDate, SystemConstants.PRODUCT_COMM_CODE.COMM_RICE_HULL_CODE);

        return LogisticsLargeScreenVo.builder()
                .product_qty(productQty)
                .feed_in_qty(feedQty)
                .purchase_out_qty(purchase)
                .raw_grain_out_qty(row_grain)
                .rice_husk_qty(riceHuskQty)
                .impurity_qty(impurityQty)
                .batch_date(batchDate)
                .build();
    }

    /**
     * 车次相关
     *
     * @return
     */
    @Override
    public LogisticsLargeScreenVo queryTrainCount() {
        String batchDate = getBatchDate();
        // 累计派车次数
        int monitorCount = mapper.selectMonitorCount(batchDate);
        // 在途车次数量
        int inTransitMonitor = mapper.selectInTransitMonitor(batchDate);
        // 累计完成车次
        int completeMonitor = mapper.selectCompleteMonitor(batchDate);
        // 异常车次
        int unusualMonitor = mapper.selectUnusualMonitor(batchDate);
        // 当月派车次数
        int monthMonitor = mapper.selectMonthMonitor();
        // 当月完成车次
        int monthCompleteMonitor = mapper.selectMonthCompleteMonitor();
        return LogisticsLargeScreenVo.builder()
                .monitor_total(monitorCount)
                .in_transit_monitor(inTransitMonitor)
                .complete_monitor(completeMonitor)
                .unusual_monitor(unusualMonitor)
                .month_monitor(monthMonitor)
                .month_complete_monitor(monthCompleteMonitor)
                .build();
    }

    /**
     * 物流合同
     *
     * @return
     */
    @Override
    public LogisticsLargeScreenVo querySchedule() {
        String batchDate = getBatchDate();
        LogisticsLargeScreenVo result = mapper.querySchedule(batchDate);

        //物流查询开始日期
        result.setBatch_date(batchDate);

        // 承运商数量
//        int customerCount = mapper.selectCustomerCount(batchDate);
        // 合同逾期数量
//        int contractOverDueCount = mapper.selectContractOverDueCount(batchDate);
//        result.setCustomer_count(customerCount);
//        result.setContract_over_due_count(contractOverDueCount);
        return result;
    }

    /**
     * 当月 & 当天数据
     *
     * @return
     */
    @Override
    public LogisticsLargeScreenVo queryMonthData() {
        // 当月完成运输量和损耗
        Map<String, BigDecimal> completeAndLossCarriage = mapper.selectCompleteAndLossCarriage();
        if (completeAndLossCarriage == null) {
            completeAndLossCarriage = new HashMap<>();
        }
        // 饲料厂收货量
        BigDecimal monthFeedInQty = mapper.selectMonthFeedInQty();
        // 加工厂生产量
        BigDecimal monthProductQty = mapper.selectMonthProductQty();
        // 原粮出库量
        BigDecimal monthRawGrain = mapper.selectMonthRawGrainOutQty();
        // 销售出库量
        BigDecimal monthSalesOutQty = mapper.selectMonthSalesOutQty();
        // 当天派车数
        int dailyMonitorCount = mapper.selectDailyMonitorCount();
        // 當天完成車次
        int dailyCompleteMonitorCount = mapper.selectDailyCompleteMonitorCount();
        // 当天在途车次
        int dailyInTransitMonitorCount = mapper.selectDailyInTransitMonitorCount();
        // 当天销售出库数量
        BigDecimal dailySalesQty = mapper.selectDailySalesQty();
        // 当天生产数量;
        BigDecimal dailyProductQty = mapper.selectDailyProductQty();
        // 当天收货数量
        BigDecimal dai = mapper.selectDailyInQty();
        return LogisticsLargeScreenVo.builder()
                .month_complete_qty(completeAndLossCarriage.get("out_qty"))
                .month_loss_qty(completeAndLossCarriage.get("qty_loss"))
                .month_feed_in_qty(monthFeedInQty)
                .month_product_qty(monthProductQty)
                .month_raw_grain_qty(monthRawGrain)
                .month_sales_out_qty(monthSalesOutQty)
                .daily_monitor_count(dailyMonitorCount)
                .daily_sales_out_qty(dailySalesQty)
                .daily_complete_monitor_count(dailyCompleteMonitorCount)
                .daily_product_qty(dailyProductQty)
                .daily_in_transit_monitor_count(dailyInTransitMonitorCount)
                .daily_in_qty(dai)
                .build();
    }

    /**
     * 实时运输状态
     *
     * @return
     */
    @Override
    public List<LogisticsMonitorLargeScreenVo> queryMonitor() {
        return mapper.queryMonitor();
    }


    private String getBatchDate() {
        List<SDictDataVo> list = dictDataService.select(new SDictDataVo(DictConstant.DICT_B_REPORT_BUSINESS_START_DATE, false));
        if (!CollectionUtils.isEmpty(list)) {
            // 默认的只有一个, 如果有, 就返回第一个
            Optional<SDictDataVo> first = list.stream().filter(item -> "1".equals(item.getExtra1())).findFirst();
            if (first.isPresent()) {
                return first.get().getDict_value();
            }
        }
        return null;
    }
}
