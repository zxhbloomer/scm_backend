package com.xinyirun.scm.bean.system.vo.business.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorFileExportVo implements Serializable {

    private static final long serialVersionUID = 6549438361755162614L;

    /**
     * id
     */
    private Integer id;

    /**
     * 监管任务编号
     */
    private String code;

    /**
     * 车牌号
     */
    private String no;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     *空车过磅-司机车头照片
     */
    private BMonitorFileVo file_1;

    /**
     *空车过磅-司机车尾照片
     */
    private BMonitorFileVo file_2;

    /**
     *空车过磅-车厢情况照片
     */
    private BMonitorFileVo file_40;

    /**
     *空车过磅-司机承诺书
     */
    private BMonitorFileVo file_3;

    /**
     *空车过磅-司机身份证
     */
    private BMonitorFileVo file_4;

    /**
     *空车过磅-司机驾驶证
     */
    private BMonitorFileVo file_38;

    /**
     *空车过磅-车辆行驶证
     */
    private BMonitorFileVo file_39;

    /**
     *正在装货-司机车头照片
     */
    private BMonitorFileVo file_5;

    /**
     *正在装货-司机车尾照片
     */
    private BMonitorFileVo file_6;

    /**
     *正在装货-车侧身照片
     */
    private BMonitorFileVo file_7;

    /**
     *正在装货-装货视频
     */
    private BMonitorFileVo file_8;

    /**
     *正在装货-集装箱箱号照片1
     */
    private BMonitorFileVo file_9;

    /**
     *正在装货-集装箱内部空箱照片1
     */
    private BMonitorFileVo file_10;

    /**
     *正在装货-集装箱装货视频1
     */
    private BMonitorFileVo file_11;

    /**
     *正在装货-磅单1(司机签字)
     */
    private BMonitorFileVo file_12;

    /**
     *正在装货-集装箱箱号照片2
     */
    private BMonitorFileVo file_13;

    /**
     *正在装货-集装箱内部空箱照片2
     */
    private BMonitorFileVo file_14;

    /**
     *正在装货-集装箱装货视频2
     */
    private BMonitorFileVo file_15;

    /**
     *正在装货-磅单2(司机签字)
     */
    private BMonitorFileVo file_16;

    /**
     *重车出库-司机车头照片
     */
    private BMonitorFileVo file_17;

    /**
     *重车出库-司机车尾照片
     */
    private BMonitorFileVo file_18;

    /**
     *重车出库-磅单
     */
    private BMonitorFileVo file_19;

    /**
     *重车过磅-司机车头照片
     */
    private BMonitorFileVo file_20;

    /**
     *重车过磅-司机车尾照片
     */
    private BMonitorFileVo file_21;

    /**
     *重车过磅-行驶轨迹
     */
    private BMonitorFileVo file_22;

    /**
     *正在卸货-司机车头照片
     */
    private BMonitorFileVo file_23;

    /**
     *正在卸货-司机车尾照片
     */
    private BMonitorFileVo file_24;

    /**
     *正在卸货-车侧身照片
     */
    private BMonitorFileVo file_25;

    /**
     *正在卸货-卸货视频
     */
    private BMonitorFileVo file_26;

    /**
     *正在卸货-集装箱箱号照片1
     */
    private BMonitorFileVo file_27;

    /**
     *正在卸货-集装箱内部空箱照片1
     */
    private BMonitorFileVo file_28;

    /**
     *正在卸货-集装箱装货视频1
     */
    private BMonitorFileVo file_29;

    /**
     *正在卸货-磅单1(司机签字)
     */
    private BMonitorFileVo file_30;

    /**
     *正在卸货-集装箱箱号照片2
     */
    private BMonitorFileVo file_31;

    /**
     *正在卸货-集装箱内部空箱照片2
     */
    private BMonitorFileVo file_32;

    /**
     *正在卸货-集装箱装货视频2
     */
    private BMonitorFileVo file_33;

    /**
     *正在卸货-磅单2(司机签字)
     */
    private BMonitorFileVo file_34;

    /**
     *空车出库-司机车头照片
     */
    private BMonitorFileVo file_35;

    /**
     *空车出库-司机车尾照片
     */
    private BMonitorFileVo file_36;

    /**
     *空车出库-磅单
     */
    private BMonitorFileVo file_37;
    /**
     * 直采入库 司机行驶证
     */
    private BMonitorFileVo file_41;

    /**
     * 直采入库 商品近照
     */
    private BMonitorFileVo file_42;

    /**
     * 直销出库 商品近照
     */
    private BMonitorFileVo file_43;

}
