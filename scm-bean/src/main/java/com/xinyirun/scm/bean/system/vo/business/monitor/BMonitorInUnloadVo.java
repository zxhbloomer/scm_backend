package com.xinyirun.scm.bean.system.vo.business.monitor;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 监管任务_入库/卸货
 * </p>
 *
 * @author wwl
 * @since 2022-02-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @Schema( name = "监管任务_入库/卸货", description = "监管任务_入库/卸货")
public class BMonitorInUnloadVo implements Serializable {

    private static final long serialVersionUID = 8282770417952954227L;
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
     * 入库单id
     */
    private Integer in_id;

    /**
     * 出库监管任务id
     */
    private Integer monitor_out_id;

    /**
     * 单据状态:4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status;

    /**
     * 任务单号
     */
    private String code;

    /**
     * 入库/卸货数量(吨)
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

    //  ----------------------重车过磅start -------------------------

    /**
     * 司机车头照片id
     */
    private Integer one_file;

    /**
     * 司机车头照片附件对象
     */
    private SFileInfoVo one_fileVo;

    /**
     * 司机车尾附件id
     */
    private Integer two_file;

    /**
     * 司机车尾附件对象
     */
    private SFileInfoVo two_fileVo;

    /**
     * 行车轨迹附件id
     */
    private Integer ten_file;

    /**
     * 行车轨迹附件对象
     */
    private SFileInfoVo ten_fileVo;

    //  ----------------------重车过磅end -------------------------


    //  ----------------------重车卸货start -------------------------

    /**
     * 车头照片附件id
     */
    private Integer three_file;

    /**
     * 车头照片附件对象
     */
    private SFileInfoVo three_fileVo;

    /**
     * 车尾照片附件id
     */
    private Integer four_file;

    /**
     * 车尾照片附件对象
     */
    private SFileInfoVo four_fileVo;

    /**
     * 车侧身照片附件id
     */
    private Integer five_file;

    /**
     * 车侧身照片附件对象
     */
    private SFileInfoVo five_fileVo;

    /**
     * 卸货视频附件id
     */
    private Integer six_file;

    /**
     * 卸货视频附件对象
     */
    private SFileInfoVo six_fileVo;

    //  ----------------------重车卸货end -------------------------

    //  ----------------------空车过磅start -------------------------

    /**
     * 司机车头附件id
     */
    private Integer seven_file;

    /**
     * 司机车头附件对象
     */
    private SFileInfoVo seven_fileVo;

    /**
     * 司机车尾附件id
     */
    private Integer eight_file;

    /**
     * 司机车尾附件对象
     */
    private SFileInfoVo eight_fileVo;

    /**
     * 磅单
     */
    private Integer nine_file;

    /**
     * 磅单
     */
    private SFileInfoVo nine_fileVo;

    //  ----------------------空车出库end -------------------------

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
     * 入库计划单号
     */
    private String in_plan_code;

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

    /**
     * 监管出库
     */
    private BMonitorOutDeliveryVo bMonitorOutVo;

    /**
     * 提货单id
     */
    private Integer delivery_id;

    /**
     * 外部关联单号
     */
    private String in_extra_code;


    /**
     * 司机行驶证ID
     */
    private Integer eleven_file;
    private SFileInfoVo eleven_fileVo;

    /**
     * 商品近照附件id
     */
    private Integer twelve_file;
    private SFileInfoVo twelve_fileVo;


}
