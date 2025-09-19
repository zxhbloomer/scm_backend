package com.xinyirun.scm.bean.system.vo.clickhouse.monitor.v1;

import com.xinyirun.scm.bean.system.vo.business.monitor.BContainerInfoVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.clickhouse.file.SFileMonitorInfoMongoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 监管任务_出库/提货
 * </p>
 *
 * @author wwl
 * @since 2022-02-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorOutDeliveryDataMongoVo implements Serializable {

    private static final long serialVersionUID = 8890285418347079789L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 类型
     */
    private String type;

    /**
     * 入库类型
     */
    private String in_type;

    /**
     * 入库类型
     */
    private String in_type_name;

    /**
     * 出库类型
     */
    private String out_type;

    /**
     * 出库类型
     */
    private String out_type_name;

    /**
     * 监管任务主表id
     */
    private Integer monitor_id;

    /**
     * 出库单id
     */
    private Integer out_id;

    /**
     * 单据状态:0空车过磅，1正在装货，2重车出库，3装货完成
     */
    private String status;

    /**
     * 任务单号
     */
    private String code;

    /**
     * 出库数量(吨)
     */
    private BigDecimal qty;

    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 净重
     */
    private BigDecimal net_weight;

    //  ----------------------空车过磅start -------------------------

    /**
     * 司机车头附件id
     */
    private Integer one_file;

    /**
     * 司机车头附件对象
     */
    private SFileMonitorInfoMongoVo one_fileVo;

    /**
     * 司机车尾附件id
     */
    private Integer two_file;

    /**
     * 司机车尾附件对象
     */
    private SFileMonitorInfoMongoVo two_fileVo;

    /**
     * 司机承诺书附件id
     */
    private Integer three_file;

    /**
     * 司机承诺书附件对象
     */
    private SFileMonitorInfoMongoVo three_fileVo;

    /**
     * 司机身份证附件id
     */
    private Integer four_file;

    /**
     * 司机身份证附件对象
     */
    private SFileMonitorInfoMongoVo four_fileVo;

    /**
     * 司机驾驶证附件id
     */
    private Integer twelve_file;

    /**
     * 司机驾驶证附件对象
     */
    private SFileMonitorInfoMongoVo twelve_fileVo;

    /**
     * 车辆行驶证附件id
     */
    private Integer thirteen_file;

    /**
     * 车辆行驶证附件对象
     */
    private SFileMonitorInfoMongoVo thirteen_fileVo;

    /**
     * 车厢情况照片id
     */
    private Integer fourteen_file;

    /**
     * 车厢情况照片对象
     */
    private SFileMonitorInfoMongoVo fourteen_fileVo;

    //  ----------------------空车过磅end -------------------------


    //  ----------------------正在装货start -------------------------
    /**
     * 车头照片附件id
     */
    private Integer five_file;

    /**
     * 车头照片附件对象
     */
    private SFileMonitorInfoMongoVo five_fileVo;

    /**
     * 车尾照片附件id
     */
    private Integer six_file;

    /**
     * 车尾照片附件对象
     */
    private SFileMonitorInfoMongoVo six_fileVo;

    /**
     * 车侧身附件id
     */
    private Integer seven_file;

    /**
     * 车侧身附件对象
     */
    private SFileMonitorInfoMongoVo seven_fileVo;

    /**
     * 装货视频附件id
     */
    private Integer eight_file;

    /**
     * 装货视频附件
     */
    private SFileMonitorInfoMongoVo eight_fileVo;

    //  ----------------------正在装货end -------------------------


    //  ----------------------重车出库start -------------------------
    /**
     * 车头照片附件id
     */
    private Integer nine_file;

    /**
     * 车头照片附件对象
     */
    private SFileMonitorInfoMongoVo nine_fileVo;

    /**
     * 车尾照片附件id
     */
    private Integer ten_file;

    /**
     * 车尾照片附件对象
     */
    private SFileMonitorInfoMongoVo ten_fileVo;

    /**
     * 磅单附件id
     */
    private Integer eleven_file;

    /**
     * 磅单附件对象
     */
    private SFileMonitorInfoMongoVo eleven_fileVo;
    //  ----------------------重车出库end -------------------------

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 装货仓库
     */
    private String out_warehouse_name;

    /**
     * 装货仓库地址
     */
    private String out_warehouse_address;

    /**
     * 卸货仓库
     */
    private String in_warehouse_name;

    /**
     * 卸货仓库地址
     */
    private String in_warehouse_address;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 创建人名
     */
    private String c_name;

    /**
     * 修改人名
     */
    private String u_name;

    /**
     * 监管单创建时间
     */
    private LocalDateTime monitor_time;

    /**
     * 运单号
     */
    private String waybill_code;

    /**
     * 物流订单单号
     */
    private String schedule_code;

    /**
     * 物流订单创建时间
     */
    private LocalDateTime schedule_time;

    /**
     * 出库计划单号
     */
    private String out_plan_code;

    /**
     * 承运商名称
     */
    private String customer_name;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 司机名称
     */
    private String driver_name;

    /**
     * 司机手机号
     */
    private String driver_mobile_phone;


    /**
     * 是否集装箱
     */
    private Boolean is_container;

    /**
     * 集装箱信息
     */
    List<BContainerInfoVo> containerInfos;

    /**
     * 综合查询字段：物流订单号、发货仓库、收货仓库、司机、车牌号
     */
    private String combine_search_condition;



}
