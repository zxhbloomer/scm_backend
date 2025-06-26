package com.xinyirun.scm.bean.system.vo.sys.config.config;


// import io.swagger.annotations.ApiModel;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * app配置明细
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "app配置明细", description = "app配置明细")
public class SAppConfigDetailVo implements Serializable {

    private static final long serialVersionUID = -3924454522568599567L;



    /**
     * 主键id
     */
    private Integer id;

    /**
     * 客户编码
     */
    private String code;

    /**
     * uri
     */
    private String uri;

    /**
     * 备注
     */
    private String remark;

    /**
     * 类型
     */
    private String type;

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
}
