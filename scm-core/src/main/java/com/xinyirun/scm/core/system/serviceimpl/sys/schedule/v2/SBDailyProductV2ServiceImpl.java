package com.xinyirun.scm.core.system.serviceimpl.sys.schedule.v2;

import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.setting.P00000128Vo;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.DateUtils;
import com.xinyirun.scm.core.system.service.business.rpd.*;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.service.sys.schedule.v2.ISBDailyProductV2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/5/16 15:51
 */

@Service
@Slf4j
public class SBDailyProductV2ServiceImpl implements ISBDailyProductV2Service {

    @Autowired
    private ISPagesService pagesService;

    @Autowired
    private IRProductDailyAService aService;

    @Autowired
    private IRProductDailyBService bService;

    @Autowired
    private IRProductDailyCService cService;

    @Autowired
    private IRProductDailyDService dService;

    @Autowired
    private IRProductDailyEService eService;

    /**
     * 生成每日报表, 定时任务, 十分钟一次的
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(String parameterClass , String parameter) {
        // 查询页面配置
        SPagesVo pageConfig = getPageConfig();

        if ("1".equals(pageConfig.getProduct_daily_processing())) {
            throw new BusinessException("日生产报表执行中!");
        }

        // 更新为 进行中
        pagesService.updateProductDailyProcessing(pageConfig, "1");

        try {
            BProductDailyVo vo = new BProductDailyVo();
            // 查询当前时间
            LocalDateTime dateTime = aService.selectNowTime();
            vo.setDate(dateTime);


            vo.setP00000128Vo(pageConfig.getP00000128Vo());


            // 锁表
            aService.lockR_product_daily_a_10(vo);

            // 新增 稻谷
            aService.insertR_product_daily_a_20(vo);

            // 新增 糙米
            bService.insertR_product_daily_b_30(vo);

            // 新增 玉米
            cService.insertR_product_daily_c_40(vo);

            // 新增 混合物
            dService.insertR_product_daily_d_50(vo);

            // 新增 稻壳
            eService.insertR_product_daily_e_60(vo);
        } catch (Exception e) {
            log.error("create error", e);
            throw new BusinessException("日报表生成失败");
        } finally {
            pagesService.updateProductDailyProcessing(pageConfig, "0");
        }

        // 更新为
    }

    /**
     * 生成日加工报表, 每日库存,
     *
     * @param vo
     */
    @Override
    public void recreate(BProductDailyVo vo) {

        SPagesVo pageConfig = getPageConfig();

        // 更新为 进行中
//        pagesService.updateProductDailyProcessing(pageConfig, "1");

        P00000128Vo p00000128Vo = pageConfig.getP00000128Vo();

        // 获取当前时间 - 1
        LocalDateTime dateTime = aService.selectNowTime();
        String formatDate = dateTime.minusDays(1).format(DateTimeFormatter.ofPattern(DateUtils.YYYY_MM_DD));

        // 如果是 作废, 需要根据入出库单的审核时间, 重新计算审核时间之后的数据, 根据仓库, 只传递 init_time, end_time t-1
        if ("2".equals(vo.getType())) {
            if (null == vo.getInit_time()) {
                throw new BusinessException("作废时计算日加工报表,请传递开始时间");
            }
            // 结束时间
            vo.setEnd_time(formatDate);
        } else if ("1".equals(vo.getType())) {
            // 初始化数据, 结束时间计算到当前日期 - 1, 不传时间, init_time 来自配置, end_time t-1
            // 计算url中传来的日期, 根据参数来
            if (null == vo.getInit_time()) {
                vo.setInit_time(p00000128Vo.getInit_time());
            }
            if (null == vo.getEnd_time()) {
                vo.setEnd_time(formatDate);
            }
        }

        vo.setP00000128Vo(pageConfig.getP00000128Vo());

        aService.lockR_product_daily_a_100(vo);

        // 新增 稻谷
        log.debug("--------  a 表 start  ------------");
        aService.insertR_product_daily_a_200(vo);
        log.debug("--------  a 表 end  ------------");

        // 新增 糙米
        bService.insertR_product_daily_b_300(vo);

        // 新增 玉米
        cService.insertR_product_daily_c_400(vo);

        // 新增 混合物
        dService.insertR_product_daily_d_500(vo);

        // 新增 稻壳
        eService.insertR_product_daily_e_600(vo);
    }

    /**
     * 生成日加工报表
     *
     * @param vo
     */
    @Override
    @Async("logExecutor")
    public void recreate2Cancel(BProductDailyVo vo) {
        vo.setType("2");
        this.recreate(vo);
    }

    private SPagesVo getPageConfig() {
        // 查询 默认商品
        SPagesVo pagesVo = new SPagesVo();
        pagesVo.setCode(PageCodeConstant.PAGE_PRODUCT_DAILY);
        SPagesVo pageVo = pagesService.get(pagesVo);
        P00000128Vo config = pageVo.getP00000128Vo();

        if (null == pageVo || null == config) {
            throw new BusinessException("请配置日报表商品!");
        }

        if ("0".equals(config.getConfig_value())) {
            throw new BusinessException("日生产报表配置未开启!");
        }

        return pageVo;
    }
}
