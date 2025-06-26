package com.xinyirun.scm.bean.api.vo.business;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 入库单返回结果
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "入出库单结算结果", description = "入出库单结算结果")
public class ApiSettlementedVo implements Serializable {

    private static final long serialVersionUID = 2110894796928067322L;
    private String code;

    private List<ApiSettlementedDataVo> data;

    private String error;

    private String msg;

    private String type;


}
