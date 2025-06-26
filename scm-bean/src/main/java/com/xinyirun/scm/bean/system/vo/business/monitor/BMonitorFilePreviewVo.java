package com.xinyirun.scm.bean.system.vo.business.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 监管任务附件预览
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorFilePreviewVo implements Serializable {

    private static final long serialVersionUID = 1869240930899100176L;

    /**
     *空车过磅-司机车头照片
     */
    private BMonitorPreviewFileVo file_1;

    /**
     *空车过磅-司机车尾照片
     */
    private BMonitorPreviewFileVo file_2;

    /**
     *空车过磅-车厢情况照片
     */
    private BMonitorPreviewFileVo file_40;

    /**
     *空车过磅-司机承诺书
     */
    private BMonitorPreviewFileVo file_3;

    /**
     *空车过磅-司机身份证
     */
    private BMonitorPreviewFileVo file_4;

    /**
     *空车过磅-司机驾驶证
     */
    private BMonitorPreviewFileVo file_38;

    /**
     *空车过磅-车辆行驶证
     */
    private BMonitorPreviewFileVo file_39;

    /**
     *正在装货-司机车头照片
     */
    private BMonitorPreviewFileVo file_5;

    /**
     *正在装货-司机车尾照片
     */
    private BMonitorPreviewFileVo file_6;

    /**
     *正在装货-车侧身照片
     */
    private BMonitorPreviewFileVo file_7;

    /**
     *正在装货-装货视频
     */
    private BMonitorPreviewFileVo file_8;

    /**
     *正在装货-集装箱箱号照片1
     */
    private BMonitorPreviewFileVo file_9;

    /**
     *正在装货-集装箱内部空箱照片1
     */
    private BMonitorPreviewFileVo file_10;

    /**
     *正在装货-集装箱装货视频1
     */
    private BMonitorPreviewFileVo file_11;

    /**
     *正在装货-磅单1(司机签字)
     */
    private BMonitorPreviewFileVo file_12;

    /**
     *正在装货-集装箱箱号照片2
     */
    private BMonitorPreviewFileVo file_13;

    /**
     *正在装货-集装箱内部空箱照片2
     */
    private BMonitorPreviewFileVo file_14;

    /**
     *正在装货-集装箱装货视频2
     */
    private BMonitorPreviewFileVo file_15;

    /**
     *正在装货-磅单2(司机签字)
     */
    private BMonitorPreviewFileVo file_16;

    /**
     *重车出库-司机车头照片
     */
    private BMonitorPreviewFileVo file_17;

    /**
     *重车出库-司机车尾照片
     */
    private BMonitorPreviewFileVo file_18;

    /**
     *重车出库-磅单
     */
    private BMonitorPreviewFileVo file_19;

    /**
     *重车过磅-司机车头照片
     */
    private BMonitorPreviewFileVo file_20;

    /**
     *重车过磅-司机车尾照片
     */
    private BMonitorPreviewFileVo file_21;

    /**
     *重车过磅-行驶轨迹
     */
    private BMonitorPreviewFileVo file_22;

    /**
     *正在卸货-司机车头照片
     */
    private BMonitorPreviewFileVo file_23;

    /**
     *正在卸货-司机车尾照片
     */
    private BMonitorPreviewFileVo file_24;

    /**
     *正在卸货-车侧身照片
     */
    private BMonitorPreviewFileVo file_25;

    /**
     *正在卸货-卸货视频
     */
    private BMonitorPreviewFileVo file_26;

    /**
     *正在卸货-集装箱箱号照片1
     */
    private BMonitorPreviewFileVo file_27;

    /**
     *正在卸货-集装箱内部空箱照片1
     */
    private BMonitorPreviewFileVo file_28;

    /**
     *正在卸货-集装箱装货视频1
     */
    private BMonitorPreviewFileVo file_29;

    /**
     *正在卸货-磅单1(司机签字)
     */
    private BMonitorPreviewFileVo file_30;

    /**
     *正在卸货-集装箱箱号照片2
     */
    private BMonitorPreviewFileVo file_31;

    /**
     *正在卸货-集装箱内部空箱照片2
     */
    private BMonitorPreviewFileVo file_32;

    /**
     *正在卸货-集装箱装货视频2
     */
    private BMonitorPreviewFileVo file_33;

    /**
     *正在卸货-磅单2(司机签字)
     */
    private BMonitorPreviewFileVo file_34;

    /**
     *空车出库-司机车头照片
     */
    private BMonitorPreviewFileVo file_35;

    /**
     *空车出库-司机车尾照片
     */
    private BMonitorPreviewFileVo file_36;

    /**
     *空车出库-磅单
     */
    private BMonitorPreviewFileVo file_37;

    /**
     * 直采入库 司机行驶证
     */
    private BMonitorPreviewFileVo file_41;

    /**
     * 直采入库 商品近照
     */
    private BMonitorPreviewFileVo file_42;

    /**
     * 直销出库 商品近照
     */
    private BMonitorPreviewFileVo file_43;
}
