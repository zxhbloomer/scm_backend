package com.xinyirun.scm.bean.system.vo.mongo.monitor.v1;

import com.xinyirun.scm.bean.system.vo.mongo.track.BMonitorTrackMongoDataVo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Wang Qianfeng
 * @Description TODO
 * @date 2023/2/10 10:42
 */
@Data
public class BMonitorDataDetailMongoVo implements Serializable {

    private static final long serialVersionUID = 363999131410492541L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 任务单号
     */
    private String code;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status_name;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status;

    /**
     * 物流订单单号
     */
    private String schedule_code;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 运单号
     */
    private String waybill_code;

    /**
     * 承运商名称
     */
    private String customer_name;

    /**
     * 司机名称
     */
    private String driver_name;

    /**
     * 司机手机号
     */
    private String driver_mobile_phone;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 验车状态
     */
    private String validate_vehicle;

    /**
     * 验车时间
     */
    private LocalDateTime validate_time;

    /**
     * 定位时间
     */
    private LocalDateTime gps_time;

    /**
     * 损耗
     */
    private BigDecimal qty_loss;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 创建人名
     */
    private String c_name;

    /**
     * 监管任务_出库类型
     */
    private String out_type_name;

    /**
     * 装货仓库
     */
    private String out_warehouse_name;

    /**
     * 收货仓库类型
     */
    private String out_warehouse_type_name;

    /**
     * 装货仓库地址
     */
    private String out_warehouse_address;

    /**
     * 出库计划单号
     */
    private String out_plan_code;

    /**
     * 出库单号
     */
    private String out_code;

    /**
     * 出库/提货时间
     */
    private LocalDateTime out_time;

    /**
     * 监管任务_入库类型
     */
    private String in_type_name;

    /**
     * 收货地
     */
    private String in_warehouse_name;

    /**
     * 卸货仓库类型
     */
    private String in_warehouse_type_name;

    /**
     * 卸货仓库地址
     */
    private String in_warehouse_address;

    /**
     * 入库计划单号
     */
    private String in_plan_code;

    /**
     * 入库单号
     */
    private String in_code;

    /**
     *入库/卸货时间
     */
    private LocalDateTime in_time;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 规格编号
     */
    private String sku_code;

    /**
     * 轨迹查询开始时间：重车出库
     */
    private LocalDateTime track_start_time;

    /**
     * 轨迹查询结束时间：重车入库
     */
    private LocalDateTime track_end_time;

    /**
     * 监管任务_出库对象
     */
    private BMonitorOutDeliveryDataMongoVo monitorOutVo;

    /**
     * 出库单状态
     */
    private String out_status_name;

    /**
     * 监管任务_入库对象
     */
    private BMonitorInUnloadDataMongoVo monitorInVo;

    /**
     * 入库单状态
     */
    private String in_status_name;

    /**
     * 监管任务_入库id
     */
    private Integer monitor_in_id;

    /**
     * 监管任务_出库id
     */
    private Integer monitor_out_id;


    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 监管任务_入库类型
     */
    private String in_type;

    /**
     * 监管任务_出库类型
     */
    private String out_type;


    /**
     * 司机id
     */
    private Integer driver_id;

    /**
     * 司机code
     */
    private String driver_code;

    /**
     * 车辆id
     */
    private Integer vehicle_id;

    /**
     * 车辆code
     */
    private String vehicle_code;

    /**
     * 承运商id
     */
    private Integer customer_id;

    /**
     * 轨迹
     */
    private BMonitorTrackMongoDataVo trackContent;

    /**
     * 图片详情
     */
    private List<BPreviewBackupDataVo> previewFiles;

    /**
     * 物流合同号
     */
    private String waybill_contract_no;
}
