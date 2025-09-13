package com.xinyirun.scm.bean.system.vo.master.warehouse;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 仓库库区库位
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "仓库库区库位", description = "仓库库区库位")
public class MWarehouseLocationBinVo implements Serializable {

    private static final long serialVersionUID = 885007979426566333L;

    /**
     * warehouse_id
     */
    private Integer warehouse_id;

    /**
     * location_id
     */
    private Integer location_id;

    /**
     * bin_id
     */
    private Integer bin_id;

    /**
     * 仓库名称
     */
    private String warehouse_name;

    /**
     * 仓库简称
     */
    private String warehouse_short_name;

    /**
     * 库区名称
     */
    private String location_name;

    /**
     * 库区简称
     */
    private String location_short_name;

    /**
     * 库位name
     */
    private String bin_name;

    /**
     * 仓库编码
     */
    private String warehouse_code;

}
