package com.xinyirun.scm.bean.system.vo.master.driver;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 司机
 * </p>
 *
 * @author wwl
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "司机", description = "司机")
public class MDriverVo implements Serializable {

    private static final long serialVersionUID = -8716611295687576992L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 司机编码
     */
    private String code;

    /**
     * 司机名称
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile_phone;

    /**
     * 身份证号
     */
    private String id_card;

    /**
     * 身份正面附件id
     */
    private Integer id_card_front;

    /**
     * 身份正面附件
     */
    private SFileInfoVo id_card_frontVo;

    /**
     * 身份反面附件id
     */
    private Integer id_card_back;

    /**
     * 身份反面附件
     */
    private SFileInfoVo id_card_backVo;

    /**
     * 驾驶证附件id
     */
    private Integer driver_license;

    /**
     * 驾驶证附件
     */
    private SFileInfoVo driver_licenseVo;

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
     * 创建人名称
     */
    private String c_name;

    /**
     * 修改人名称
     */
    private String u_name;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    // 主键集合
    private Integer[] ids;

}
