package com.xinyirun.scm.core.system.service.business.rpd;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.rpd.RProductDailyAEntity;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyExportVo;
import com.xinyirun.scm.bean.system.vo.business.rpd.BProductDailyVo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 稻谷 加工日报表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2023-05-16
 */
public interface IRProductDailyAService extends IService<RProductDailyAEntity> {

    /**
     * 锁表
     */
    void lockR_product_daily_a_10(BProductDailyVo vo);

    /**
     * 保存全部
     * @param vo
     */
    void insertR_product_daily_a_20(BProductDailyVo vo);

    /**
     * 分页查询
     * @param vo
     */
    IPage<BProductDailyVo> selectPageList(BProductDailyVo vo);

    /**
     * 加工报表 导出查询
     * @param vo
     * @return
     */
    List<BProductDailyExportVo> exportList(BProductDailyVo vo);

    /**
     * 查询当前时间
     * @return
     */
    LocalDateTime selectNowTime();

    /**
     * 加工报表, 根据每日库存生成
     * @param vo
     */
    void insertR_product_daily_a_200(BProductDailyVo vo);

    void lockR_product_daily_a_100(BProductDailyVo vo);

    /**
     * 加工报表, 合计
     * @param vo
     * @return
     */
    List<BProductDailyVo> selectListSumApi(BProductDailyVo vo);
}
