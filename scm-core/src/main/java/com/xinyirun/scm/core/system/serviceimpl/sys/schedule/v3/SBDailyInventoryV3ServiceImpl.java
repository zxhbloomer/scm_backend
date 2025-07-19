package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v3;

import cn.hutool.core.net.url.UrlBuilder;
import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiDailyInventoryPriceSyncVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiDailyInventoryPriceVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiDailyInventorySyncVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiMaterialConvertPriceSyncVo;
import com.xinyirun.scm.bean.entity.busniess.wms.inventory.BDailyInventoryEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inventory.BDailyInventoryVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.mapper.sys.schedule.v3.SBDailyInventoryBatchV3Mapper;
import com.xinyirun.scm.core.system.service.business.wms.inventory.IBDailyInventoryService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.schedule.v3.ISBDailyInventoryV3Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.List;

/**
 * <p>
 *  每日库存变化表的service 定时任务专用
 * </p>
 * 废弃
 * @author zxh
 * @since 2019-07-04
 */
@Service
@Slf4j
public class SBDailyInventoryV3ServiceImpl extends BaseServiceImpl<SBDailyInventoryBatchV3Mapper, BDailyInventoryEntity> implements ISBDailyInventoryV3Service {

    @Autowired
    private SBDailyInventoryBatchV3Mapper mapper;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private ISAppConfigService isAppConfigService;

    @Autowired
    private IBDailyInventoryService service;

    @Autowired
    public WebClient webClient;

    @Value("${server.port}")
    private int port;

    /**
     * 重新生成每日库存表，所有仓库
     *
     */
    @Override
    public void reCreateDailyInventoryAll() {
        reCreateDailyInventoryAll(null,null);
    }

    @Override
    public void reCreateDailyInventoryToday() {

    }

    /**
     * 重新生成每日库存表
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
//    @SysLogAnnotion("每日库存计算")
    public void reCreateDailyInventoryAll(String parameterClass , String parameter) {
        log.debug("---------------每日库存计算参数:"+parameter);
        log.debug("----------------每日库存表start---------");

        /**
         * 锁定工作表
         */
        log.debug("----------------锁定工作表start---------");
        mapper.lockTemoraryTableDailyInventoryTemp10();
        mapper.lockTemoraryTableDailyInventoryWork11();
        mapper.lockTableDailyInventory12();
        mapper.lockTemoraryTableDailyInventoryFinalTemp13();
//            mapper.lockAllTable14();
        log.debug("----------------锁定工作表end---------");

        /**
         * 删除数据工作表
         */
        log.debug("----------------删除数据工作表start---------");
        mapper.deleteTemoraryTableDailyInventoryWork00();
        mapper.deleteTemoraryTableDailyInventoryTemp01();
        mapper.deleteTemoraryTableDailyInventoryTemp02();
        log.debug("----------------删除数据工作表end---------");

        /**
         * 插入数据,尚未清洗的数据源，work
         */
        log.debug("----------------插入数据,尚未清洗的数据源，workstart---------");
        // 此处注意，因为是被调用方，所以是知道bean是什么结构的，直接定义即可
        BDailyInventoryVo condition = null;
        if (parameterClass == null || parameter == null ) {
            condition = new BDailyInventoryVo();
        } else {
            condition = JSON.parseObject(parameter ,BDailyInventoryVo.class);
        }
        mapper.createTemporaryWorkData20(condition);
        log.debug("----------------插入数据,尚未清洗的数据源，workend---------");

        /**
         * 开始数据清洗
         */
        log.debug("----------------开始数据清洗，start---------");
        mapper.insertTableDailyInventoryTemp_30();
        mapper.createTableDailyInventoryFinal_40();

        mapper.updateTableDailyInventoryFinal_41();
        // 每日库存原平均单价计算逻辑2022-11-15 以下逻辑删除 start
        // 计算价格天数
//        SConfigEntity config = isConfigService.selectByKey(SystemConstants.PRICE_DAYS);
//        mapper.updateTableDailyInventoryFinal_42(Integer.parseInt(config.getValue()));
        // 每日库存原平均单价计算逻辑2022-11-15 end

        // 原材料，辅料单价计算
        mapper.updateTableDailyInventoryFinal_44();

        // 15天单价计算
        mapper.updateTableDailyInventoryFinal_43();

        // 调价函
        mapper.updateTableDailyInventoryFinal_45();

        log.debug("----------------开始数据清洗，end---------");

        log.debug("----------------更新至每日库存表中start---------");
        mapper.deleteTableDailyInventory13(condition);
        mapper.insertTableDailyInventoryFinal();

        // 每日货值
        SConfigEntity config = isConfigService.selectByKey(SystemConstants.DAILY_PRICE_DAYS);
        mapper.insertDailyPriceTable50(Integer.parseInt(config.getValue()));
        // 每日单价
        mapper.insertMaterialDailyPriceTable51();
        // 最新单价
        mapper.insertMaterialPriceTable52();
        log.debug("----------------更新至每日库存表中end---------");

        log.debug("----------------每日库存表end---------");

        log.debug("----------日报表新增 开始----------------");
        // 查询配置， 是否开启 日加工报表查询
//        try {
//            SConfigEntity entity = isConfigService.selectByKey(SystemConstants.QRTZ_PRODUCT_DAILY);
//            if ("1".equals(entity.getValue())) {
//                productV2Service.create(new BProductDailyVo());
//            }
//        } catch(Exception e) {
//            log.error("日加工报表 未配置!");
//        }
        log.debug("----------日报表新增 结束----------------");
    }

    @Override
    public void syncDailyInventoryAll(String parameterClass , String parameter) {
        List<ApiDailyInventoryVo> apiDailyInventoryVos = service.getDailyInventory();

        List<ApiMaterialConvertPriceVo> apiMaterialConvertPriceVos = service.getMaterialConvertPrice();

        // 调用API接口，同步每日库存
        callAsyncDailyInventoryApiController(apiDailyInventoryVos);

        // 调用API接口，同步物料转换单价
        callAsyncMaterialConvertPriceApiController(apiMaterialConvertPriceVos);
    }

    @Override
    public void syncDailyInventoryPriceAll(String parameterClass, String parameter) {
        List<ApiDailyInventoryPriceVo> apiDailyInventoryPriceVos = mapper.selectDailyPriceTableAll();
        callAsyncDailyInventoryPriceAllApiController(apiDailyInventoryPriceVos);
    }

    @Override
    public void syncDailyInventoryPriceLatest(String parameterClass, String parameter) {
        List<ApiDailyInventoryPriceVo> apiDailyInventoryPriceVos = mapper.selectDailyPriceTableLatest();
        callAsyncDailyInventoryPriceApiController(apiDailyInventoryPriceVos);
    }

    /**
     * 调用API接口，同步每日库存
     * @param beans
     */
    private void callAsyncDailyInventoryApiController(List<ApiDailyInventoryVo> beans) {
        log.debug("=============同步每日库存start=============");


        ApiDailyInventorySyncVo asyncVo = new ApiDailyInventorySyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/inventory/daily/sync", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);




        log.debug("=============同步每日库存end=============");
    }

    /**
     * 调用API接口，同步物料转换单价
     * @param beans
     */
    private void callAsyncMaterialConvertPriceApiController(List<ApiMaterialConvertPriceVo> beans) {
        log.debug("=============同步物料转换单价start=============");

        ApiMaterialConvertPriceSyncVo asyncVo = new ApiMaterialConvertPriceSyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/materialconvert/price/sync", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步物料转换单价end=============");
    }

    /**
     * 拼接中台同步数据url
     * @param uri
     * @param appCode
     * @return
     */
    private String getBusinessCenterUrl(String uri, String appCode) {
        try {
            SAppConfigEntity sAppConfigEntity = isAppConfigService.getDataByAppCode(appCode);
            String app_key= sAppConfigEntity.getApp_key();
            String secret_key = sAppConfigEntity.getSecret_key();
            String host = InetAddress.getLocalHost().getHostAddress();

            String url = UrlBuilder.create()
                    .setScheme("http")
                    .setHost(host)
                    .setPort(port)
                    .addPath(uri)
                    .addQuery("app_key", app_key)
                    .addQuery("secret_key", secret_key)
                    .build();
            return url.replaceAll("%2F", "/");
        } catch (Exception e) {
            log.error("getBusinessCenterUrl error", e);
        }
        return "";
    }

    private void callback(String result) {
        log.debug(result);
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

    /**
     * 调用API接口，同步物料转换单价
     * @param beans
     */
    private void callAsyncDailyInventoryPriceApiController(List<ApiDailyInventoryPriceVo> beans) {
        log.debug("=============同步每日货值start=============");

        ApiDailyInventoryPriceSyncVo asyncVo = new ApiDailyInventoryPriceSyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/inventory/price/sync", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步每日货值end=============");
    }

    /**
     * 调用API接口，同步物料转换单价
     * @param beans
     */
    private void callAsyncDailyInventoryPriceAllApiController(List<ApiDailyInventoryPriceVo> beans) {
        log.debug("=============同步每日货值all start=============");

        ApiDailyInventoryPriceSyncVo asyncVo = new ApiDailyInventoryPriceSyncVo();
        asyncVo.setList(beans);

        String url = getBusinessCenterUrl("/wms/api/service/v1/steel/inventory/price/sync/all", SystemConstants.APP_CODE.ZT);
        Mono<String> mono = webClient
                .post() // 发送POST 请求
                .uri(url) // 服务请求路径，基于baseurl
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(asyncVo))
                .retrieve() // 获取响应体
                .bodyToMono(String.class); // 响应数据类型转换

        //异步非阻塞处理响应结果
        mono.subscribe(this::callback);

        log.debug("=============同步每日货值allend=============");
    }
}
