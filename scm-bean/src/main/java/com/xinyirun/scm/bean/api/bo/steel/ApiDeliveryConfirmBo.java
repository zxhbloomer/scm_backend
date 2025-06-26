package com.xinyirun.scm.bean.api.bo.steel;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 收货确认函
 * </p>
 *
 * @author xyr
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiDeliveryConfirmBo implements Serializable {

    private static final long serialVersionUID = -2738112360568274291L;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 订单id
     */
    private Integer in_order_goods_id;

    /**
     * 后缀名
     */
    private String fileSuffix;

    /**
     * 附件名称
     */
    private String fileUrl;

    /**
     * 商品编号
     */
    private String goodsCode;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品规格
     */
    private String goodsSpecName;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 订单编号
     */
    private String orderCode;

    /**
     * 上传人
     */
    private String uploaderName;

    /**
     * 上传时间
     */
    private String uploaderTime;

}
