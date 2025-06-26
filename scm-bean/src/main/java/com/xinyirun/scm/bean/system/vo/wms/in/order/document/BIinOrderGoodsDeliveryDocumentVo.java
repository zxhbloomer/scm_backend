package com.xinyirun.scm.bean.system.vo.wms.in.order.document;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 收货确认函附件
 * </p>
 *
 * @author wwl
 * @since 2022-03-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BIinOrderGoodsDeliveryDocumentVo implements Serializable {

    private static final long serialVersionUID = -1856820137923692444L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单id
     */
    private Integer in_order_goods_id;

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
     * 磅单文件
     */
    private List<SFileInfoVo> files_detail;


    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
