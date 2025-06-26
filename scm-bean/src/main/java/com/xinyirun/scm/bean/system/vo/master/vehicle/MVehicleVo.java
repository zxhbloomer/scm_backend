package com.xinyirun.scm.bean.system.vo.master.vehicle;

import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * <p>
 * 车辆管理
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @Schema( name = "车辆管理", description = "车辆管理")
public class MVehicleVo implements Serializable {

    private static final long serialVersionUID = -8883715653642827427L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 车牌号
     */
    private String no;

    /**
     * 车牌颜色
     */
    private String no_color;

    /**
     * 车牌颜色
     */
    private String no_color_str;

    /**
     * 验车日志
     */
    private String validate_log;

    /**
     * 1-验车成功 2-验车失败
     */
    private String validate_status_name;

    /**
     * 1-验车成功 2-验车失败
     */
    private String validate_status;

    /**
     * 最后一次定位时间
     */
    private LocalDateTime gps_time;

    /**
     * 车长车型
     */
    private String spec;

    /**
     * 载重(吨)
     */
    private BigDecimal loading;

    /**
     * 驾驶证正面附件id
     */
    private Integer license_front;
    private SFileInfoVo license_frontVo;

    /**
     * 驾驶证反面附件id
     */
    private Integer license_back;
    private SFileInfoVo license_backVo;

    /**
     * 是否删除
     */
    private Boolean is_del;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
