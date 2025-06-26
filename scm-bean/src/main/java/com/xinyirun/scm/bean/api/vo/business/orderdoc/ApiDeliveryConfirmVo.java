package com.xinyirun.scm.bean.api.vo.business.orderdoc;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class ApiDeliveryConfirmVo implements Serializable {

    private static final long serialVersionUID = 6865131847272755092L;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 后缀名
     */
    private String fileSuffix;

    /**
     * 附件名称
     */
    private String fileUrl;

    /**
     * 附件名称
     */
    private String fileName;

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
