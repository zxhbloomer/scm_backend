package com.xinyirun.scm.core.api.serviceimpl.business.v1.largescreen;

import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiContractQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiTodayQtyStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiWarehouseInventoryStatisticsVo;
import com.xinyirun.scm.bean.api.vo.business.largescreen.ApiWarehouseStatisticsVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BContractReportVo;
import com.xinyirun.scm.bean.system.vo.business.inventory.BQtyLossScheduleReportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.api.mapper.business.largescreen.ApiLargeScreenReportMapper;
import com.xinyirun.scm.core.api.service.business.v1.largescreen.ApiLargeScreenReportService;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ApiLargeScreenReportServiceImpl implements ApiLargeScreenReportService {

    @Autowired
    private ApiLargeScreenReportMapper mapper;

    @Autowired
    private ISDictDataService dictDataService;

    /**
     * 查询业务启动日期
     * @return
     */
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

    /**
     * 按仓库类型 获取库存量
     *
     * @return
     */
    @Override
    public ApiWarehouseInventoryStatisticsVo getWarehouseTypeInventory() {
        // 获取业务启动日期
        String batchDate = getBatchDate();
        List<ApiWarehouseStatisticsVo> warehouseTypeInventory = mapper.getWarehouseTypeInventory(batchDate);
        // 查询不同类型下 有多少个仓库
        List<ApiWarehouseStatisticsVo> warehouseTypeNum = mapper.selectWarehouseNumByType();
//        // 按不同类型, 交给前端
//        if (!CollectionUtils.isEmpty(warehouseTypeInventory)) {
//            warehouseTypeInventory.forEach(item -> {
//                if (DictConstant.DICT_M_WAREHOUSE_TYPE_ZX.equals(item.getWarehouse_type()))
//                    result.setQty_zs(item.getQty());
//                else if (DictConstant.DICT_M_WAREHOUSE_TYPE_WD.equals(item.getWarehouse_type()))
//                    result.setQty_jg(item.getQty());
//                else if (DictConstant.DICT_M_WAREHOUSE_TYPE_TL.equals(item.getWarehouse_type()))
//                    result.setQty_tl(item.getQty());
//                else if (DictConstant.DICT_M_WAREHOUSE_TYPE_ZZ.equals(item.getWarehouse_type()))
//                    result.setQty_zz(item.getQty());
//            });
//        }
        return new ApiWarehouseInventoryStatisticsVo(warehouseTypeInventory, warehouseTypeNum, batchDate);
    }

    /**
     * 查询采购数量
     *
     * @return
     */
    @Override
    public ApiContractQtyStatisticsVo getContractQty() {
        String batchDate = getBatchDate();
        ApiContractQtyStatisticsVo result = new ApiContractQtyStatisticsVo();
        // 查询采购合同
        List<BContractReportVo> inList = mapper.selectInContractQty(batchDate);
        // 筛选原材料中的水稻
        inList.forEach(item -> {
            if ("原材料".equals(item.getGoods_prop()) && "水稻".equals(item.getGoods_name())) {
                result.setQty_rice(item.getQty());
            }
        });
        // 求原材料水稻之外的商品和 即掺混物
        BigDecimal reduce = inList.stream().filter(item -> "原材料".equals(item.getGoods_prop()))
                .filter(item -> !"水稻".equals(item.getGoods_name()))
                .map(BContractReportVo::getQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (reduce != null) {
            result.setQty_blends(reduce);
        }
        // 查询销售合同
        List<BContractReportVo> outList = mapper.selectOutContractQty(batchDate);
        // 求 销售合同 产成品, 除了稻壳
        BigDecimal reduce2 = outList.stream().filter(item -> "产成品".equals(item.getGoods_prop()))
                .filter(item -> !"稻壳".equals(item.getGoods_name()))
                .map(BContractReportVo::getQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (reduce2 != null) {
            result.setQty_product(reduce2);
        }
        // 求 销售合同 副产品, 只有稻壳
        outList.forEach(item -> {
            if ("产成品".equals(item.getGoods_prop()) && "稻壳".equals(item.getGoods_name())) {
                result.setQty_coproduct(item.getQty());
            }
        });
        return result;
    }

    /**
     * 当日累计数量
     *
     * @return
     */
    @Override
    public ApiTodayQtyStatisticsVo getTodayQty() {
        ApiTodayQtyStatisticsVo result = new ApiTodayQtyStatisticsVo();
        // 查询收货数量, 发货数量, 在途数量
        BQtyLossScheduleReportVo scheduleStatistics = mapper.getScheduleStatistics();
        // 查询 生产数量
        ApiTodayQtyStatisticsVo product = mapper.selectProductStatistics();
        // 采购订单数量,  拍卖数量 合同日期是当天的采购合同 合同数量
        ApiTodayQtyStatisticsVo inOrderNum = mapper.selectInOrderNum();
        // 查询销售订单数量, 销售数量, 合同日期是当天的销售合同 合同数量
        ApiTodayQtyStatisticsVo outOrderNum = mapper.selectOutOrderNum();
        // 查询当天原粮出库数量
        BigDecimal rowGrain = mapper.selectRowGrainNum();
        // 查询当天交付数量, 收货地址饲料厂,状态卸货完成的监管任务
        BigDecimal deliverCount = mapper.selectDeliverCount();

        if (scheduleStatistics != null) {
            result.setQty_in_today(scheduleStatistics.getIn_qty());
            result.setQty_out_today(scheduleStatistics.getOut_qty());
            result.setQty_in_transit_today(scheduleStatistics.getQty_loss());
            result.setMonitor_count(scheduleStatistics.getNum() == null ? 0 : scheduleStatistics.getNum());
        }
        if (product != null) {
            result.setQty_product_today(product.getQty_product_today());
        }
        if (inOrderNum != null) {
            result.setPurchase_order_count(inOrderNum.getPurchase_order_count());
            result.setPurchase_contract_num(inOrderNum.getPurchase_contract_num());
        }
        if (outOrderNum != null) {
            result.setSales_order_count(outOrderNum.getSales_order_count());
            result.setSales_coontract_num(outOrderNum.getSales_coontract_num());
        }
        result.setDeliver_num(deliverCount);
        result.setRow_grain_count(rowGrain);
        return result;
    }

    /**
     * 查询监管任务预警数量
     *
     * @return
     */
    @Override
    public Integer selectMonitorAlarmCount() {
        return mapper.selectMonitorAlarmCount();
    }
}
