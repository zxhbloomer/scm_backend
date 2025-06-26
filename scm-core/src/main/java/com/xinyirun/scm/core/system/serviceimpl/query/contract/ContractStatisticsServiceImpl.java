package com.xinyirun.scm.core.system.serviceimpl.query.contract;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.report.contract.PurchaseContractStatisticsExportVo;
import com.xinyirun.scm.bean.system.vo.report.contract.PurchaseContractStatisticsVo;
import com.xinyirun.scm.bean.system.vo.report.contract.SalesContractStatisticsExportVo;
import com.xinyirun.scm.bean.system.vo.report.contract.SalesContractStatisticsVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.query.contract.ContractStatisticsMapper;
import com.xinyirun.scm.core.system.service.query.contract.ContractStatisticsService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/9/19 15:37
 */

@Service
public class ContractStatisticsServiceImpl implements ContractStatisticsService {

    @Autowired
    private ContractStatisticsMapper mapper;

    @Autowired
    private ISConfigService configService;

    /**
     * 采购合同统计表
     *
     * @param param
     * @return
     */
    @Override
    public IPage<PurchaseContractStatisticsVo> queryPageList(PurchaseContractStatisticsVo param) {
        // 分页条件
        Page<PurchaseContractStatisticsVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        return mapper.selectPageList(param, pageCondition);
    }

    /**
     * 采购合同统计表
     *
     * @param param 参数
     * @return
     */
    @Override
    public PurchaseContractStatisticsVo getListSum(PurchaseContractStatisticsVo param) {
        return mapper.getListSum(param);
    }

    @Override
    public List<PurchaseContractStatisticsExportVo> getExportList(PurchaseContractStatisticsVo param) {
        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!CollectionUtils.isEmpty(param.getIds()) && !Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(param);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.getExportList(param);
    }

    /**
     * 查询销售合同量
     *
     * @param param
     * @return
     */
    @Override
    public IPage<SalesContractStatisticsVo> selectSalesPageList(SalesContractStatisticsVo param) {
        // 分页条件
        Page<SalesContractStatisticsVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        IPage<SalesContractStatisticsVo> pageList = mapper.selectSalesPageList(param, pageCondition);
        // 计算执行情况 已出库数量合计 / 合同量合计
        for (SalesContractStatisticsVo record : pageList.getRecords()) {
            BigDecimal has_handle_count = BigDecimal.ZERO;
            BigDecimal contract_num = BigDecimal.ZERO;
            BigDecimal execute_processing = BigDecimal.ZERO;
            for (JSONObject jsonObject : record.getJson_objects()) {
                BigDecimal hasHandleCount = jsonObject.getBigDecimal("has_handle_count");
                BigDecimal contractNum = jsonObject.getBigDecimal("contract_num");
                has_handle_count = has_handle_count.add(hasHandleCount);
                contract_num = contract_num.add(contractNum);
            }
            if (contract_num.compareTo(BigDecimal.ZERO) != 0) {
                execute_processing = has_handle_count.multiply(BigDecimal.valueOf(100)).divide(contract_num, 2, RoundingMode.HALF_UP);
            }
            record.setExecute_processing(execute_processing + "%");
        }
        return pageList;
    }

    /**
     * 查询销售合同量 合计
     *
     * @param param
     * @return
     */
    @Override
    public SalesContractStatisticsVo selectSalesPageListSum(SalesContractStatisticsVo param) {
        return mapper.selectSalesPageListSum(param);
    }

    /**
     * 销售合同量 导出
     *
     * @param param
     * @return
     */
    @Override
    public List<SalesContractStatisticsExportVo> selectSalesListExport(SalesContractStatisticsVo param) {
        List<SalesContractStatisticsVo> list = mapper.selectSalesListExport(param);
        List<SalesContractStatisticsExportVo> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(item -> result.addAll(builderExportVo(item)));
        }
        return result;
    }

    private List<SalesContractStatisticsExportVo> builderExportVo(SalesContractStatisticsVo vo) {
        var result = new ArrayList<SalesContractStatisticsExportVo>();
        List<JSONObject> jsonObjects = vo.getJson_objects();
        for (int i = 0; i < jsonObjects.size(); i++) {
            SalesContractStatisticsExportVo build = SalesContractStatisticsExportVo.builder()
                    .no(vo.getNo())
                    .contract_no(vo.getContract_no())
                    .order_no(vo.getOrder_no())
                    .schedule_count(i == 0 ? vo.getSchedule_count() : null)
                    .status_name(vo.getStatus_name())
                    .contract_dt(LocalDateTimeUtil.format(vo.getContract_dt(), "yyyy年MM月dd日"))
                    .contract_expire_dt(LocalDateTimeUtil.format(vo.getContract_expire_dt(), "yyyy年MM月dd日"))
                    .client_name(vo.getClient_name())
                    .owner_name(vo.getOwner_name())
                    .sku_name(jsonObjects.get(i).getString("sku_name"))
                    .pm(jsonObjects.get(i).getString("pm"))
                    .spec(jsonObjects.get(i).getString("spec"))
                    .contract_num(jsonObjects.get(i).getBigDecimal("contract_num"))
                    .execute_processing(vo.getExecute_processing())
                    .out_address(vo.getOut_address())
                    .has_handle_count(jsonObjects.get(i).getBigDecimal("has_handle_count"))
                    .arrived_count(jsonObjects.get(i).getBigDecimal("arrived_count"))
                    .in_transit_count(jsonObjects.get(i).getBigDecimal("in_transit_count"))
                    .qty_loss(jsonObjects.get(i).getBigDecimal("qty_loss"))
                    .build();
            result.add(build);
        }
        return result;

//        return vo.getJson_objects().stream().map(item ->
//            SalesContractStatisticsExportVo.builder()
//                    .no(vo.getNo())
//                    .contract_no(vo.getContract_no())
//                    .order_no(vo.getOrder_no())
//                    .schedule_count(vo.getSchedule_count())
//                    .status_name(vo.getStatus_name())
//                    .contract_dt(LocalDateTimeUtil.format(vo.getContract_dt(), "yyyy年MM月dd日"))
//                    .contract_expire_dt(LocalDateTimeUtil.format(vo.getContract_expire_dt(), "yyyy年MM月dd日"))
//                    .client_name(vo.getClient_name())
//                    .owner_name(vo.getOwner_name())
//                    .sku_name(item.getString("sku_name"))
//                    .pm(item.getString("pm"))
//                    .spec(item.getString("spec"))
//                    .contract_num(item.getBigDecimal("contract_num"))
//                    .execute_processing(vo.getExecute_processing())
//                    .out_address(vo.getOut_address())
//                    .has_handle_count(item.getBigDecimal("has_handle_count"))
//                    .arrived_count(item.getBigDecimal("arrived_count"))
//                    .in_transit_count(item.getBigDecimal("in_transit_count"))
//                    .qty_loss(item.getBigDecimal("qty_loss"))
//                    .build()
//        ).collect(Collectors.toList());
    }
}
