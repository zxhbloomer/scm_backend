package com.xinyirun.scm.bean.api.vo.master.warehouse;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 仓库下拉
 * </p>
 *
 * @author htt
 * @since 2021-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "仓库下拉bean", description = "仓库下拉bean")
public class ApiWarehouseVo implements Serializable {

    private static final long serialVersionUID = 162141082947597309L;

    /**
     * 仓库编码
     */
    private String code;

    /**
     * 仓库名称
     */
    private String name;

    /**
     * 仓库地址
     */
    private String address;

    /**
     * 仓库简称
     */
    private String short_name;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 板块
     */
    private String business_type;
}
