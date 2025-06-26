package com.xinyirun.scm.bean.system.vo.business.monitor;

import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BContainerInfoVo implements Serializable {

    private static final long serialVersionUID = 7186055081451253973L;
    /**
     * id
     */
    private Integer id;

    /**
     * 箱号
     */
    private String code;

    /**
     * 铁运/海运运单号
     */
    private String waybill_code;

    /**
     * 业务类型
     */
    private String serial_type;

    /**
     * 业务id
     */
    private Integer serial_id;

    /**
     * 毛重
     */
    private BigDecimal gross_weight;

    /**
     * 皮重
     */
    private BigDecimal tare_weight;

    /**
     * 净重
     */
    private BigDecimal net_weight;

    /**
     * 集装箱箱号照片
     */
    private Integer file_one;
    private SFileInfoVo file_oneVo;


    /**
     * 集装箱内部空箱照片
     */
    private Integer file_two;
    private SFileInfoVo file_twoVo;

    /**
     * 集装箱装货视频
     */
    private Integer file_three;
    private SFileInfoVo file_threeVo;

    /**
     * 磅单
     */
    private Integer file_four;
    private SFileInfoVo file_fourVo;

    private String preview_urls1;

    private String preview_urls2;

    private String preview_urls3;

    private String preview_urls4;


}
