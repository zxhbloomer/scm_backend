package com.xinyirun.scm.bean.system.vo.business.so.arreceive;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 应收单表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReceiveVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2847593861752049386L;

    private Integer id;

    /**
     * 应收单编号
     */
    private String code;

    /**
     * 应收账款主表id
     */
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    private String ar_code;

    /**
     * 应收单状态：状态（0-待收款、1已收款、2-作废、-1-中止收款）
     */
    private String status;
    private String status_name;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    private String type;
    private String type_name;

    /**
     * 客户ID
     */
    private Integer customer_id;

    /**
     * 客户编码
     */
    private String customer_code;

    /**
     * 客户名称
     */
    private String customer_name;

    /**
     * 销售方ID
     */
    private Integer seller_id;

    /**
     * 销售方编码
     */
    private String seller_code;

    /**
     * 销售方名称
     */
    private String seller_name;

    /**
     * 收款日期
     */
    private LocalDateTime receive_date;

    /**
     * 收款方式：1-银行转账
     */
    private String payment_type;

    /**
     * 应收单计划收款总金额
     */
    private BigDecimal receivable_amount_total;

    /**
     * 应收单已收款总金额
     */
    private BigDecimal received_amount_total;

    /**
     * 收款指令备注
     */
    private String remark;

    /**
     * 凭证上传备注
     */
    private String voucher_remark;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    // 业务扩展字段

    /**
     * 收款明细列表
     */
    private List<BArReceiveDetailVo> detailListData;

    /**
     * 附件列表
     */
    private List<BArReceiveAttachVo> attachListData;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;

    /**
     * 应收单附件
     */
    private List<SFileInfoVo> doc_att_files;

    /**
     * 凭证文件ID
     */
    private String voucher_file;

    /**
     * 凭证附件
     */
    private List<SFileInfoVo> voucher_files;

    /**
     * 表头：数据汇总
     */
    private BigDecimal sum_receivable_amount_total;
    private BigDecimal sum_received_amount_total;

    /**
     * 单据状态列表
     */
    private String[] status_list;
    
    /**
     * 业务单据信息 销售订单
     */
    private List<BArSourceAdvanceVo> soOrderListData;

    /**
     * 收款状态：0-未收款、1-部分收款、2-已收款、-1-中止收款
     */
    private String receive_status;
    private String receive_status_name;

    /**
     * 作废附件
     */
    private Integer cancel_file;
    private List<SFileInfoVo> cancel_files;

    /**
     * 作废理由
     */
    private String cancel_reason;

    /**
     * 未收款总金额
     */
    private BigDecimal unreceive_amount_total;

    /**
     * 收款中总金额
     */
    private BigDecimal receiving_amount_total;

    /**
     * 作废总金额
     */
    private BigDecimal cancel_amount_total;

    /**
     * 作废附件
     */
    private List<SFileInfoVo> cancel_doc_att_files;

    /**
     * 作废提交人
     */
    private String cancel_name;

    /**
     * 作废时间
     */
    private LocalDateTime cancel_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;
}