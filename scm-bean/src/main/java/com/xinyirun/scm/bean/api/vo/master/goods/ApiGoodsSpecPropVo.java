package com.xinyirun.scm.bean.api.vo.master.goods;


// import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 物料属性API
 * </p>
 *
 * @author wwl
 * @since 2022-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiGoodsSpecPropVo implements Serializable {


    private static final long serialVersionUID = 4182685725916369303L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 编号
     */
    private String code;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}
