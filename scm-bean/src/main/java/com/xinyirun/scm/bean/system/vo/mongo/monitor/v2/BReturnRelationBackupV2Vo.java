package com.xinyirun.scm.bean.system.vo.mongo.monitor.v2;

import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author:
 * @Description:
 * @CreateTime : 2024/7/25 9:26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BReturnRelationBackupV2Vo extends UploadFileResultAo implements Serializable {

    private static final long serialVersionUID = 2672763340547788446L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 退货数量
     */
    private BigDecimal qty;

    /**
     * 退货理由
     */
    private String quantity_reason;

    /**
     * 关联单号CODE
     */
    private String serial_code;

    /**
     * 退货code
     */
    private String code;

    /**
     * 单据类型
     */
    private String serial_type_name;

    /**
     * 监管退货入库计划id
     */
    private Integer in_plan_id;

    /**
     * 监管退货入库计划code
     */
    private String in_plan_code;

    /**
     * 单位名称
     */
    private String unit_name;

    /**
     * 监管退货 退货单id
     */
    private Integer in_id;

    /**
     * 监管退货 退货单code
     */
    private String in_code;

    /**
     * 附件信息
     */
    private List<SFileInfoVo> files;

    private Integer files_id;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 状态名
     */
    private String status_name;

    private String status;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 跟新人
     */
    private String u_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    private Long staff_id;

}
