package com.xinyirun.scm.bean.api.vo.master.customer;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 货主下拉
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "货主下拉", description = "货主下拉")
public class ApiOwnerVo implements Serializable {

    private static final long serialVersionUID = 868141082947512801L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 简称
     */
    private String short_name;

    /**
     * 板块
     */
    private String business_type;

}
