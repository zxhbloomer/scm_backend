package com.xinyirun.scm.bean.api.vo.business;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 是否可以作废
 * </p>
 *
 * @author wwl
 * @since 2022-02-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiCanceledVo implements Serializable {

    private static final long serialVersionUID = 7254201926369606389L;
    /**
     * 状态码
     */
    private String code;

    /**
     * 序号
     */
    private List<ApiCanceledDataVo> data;

    /**
     * 入库单号
     */
    private String error;

    /**
     * 入库类型：0采购入库，1调拨入库，2退货入库，9监管入库，10普通入库
     */
    private String msg;

    /**
     * 入库状态：0制单，1已提交，2审核通过，3审核驳回，4作废
     */
    private String type;


}
