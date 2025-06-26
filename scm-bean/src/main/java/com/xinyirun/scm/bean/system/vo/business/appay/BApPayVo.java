package com.xinyirun.scm.bean.system.vo.business.appay;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.xinyirun.scm.bean.system.vo.business.ap.BApSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 付款单表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApPayVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 4705871377388125154L;

    private Integer id;

    /**
     * 付款单编号
     */
    private String code;

    /**
     * 应付账款主表id
     */
    private Integer ap_id;

    /**
     * 应付账款主表code
     */
    private String ap_code;

    /**
     * 付款单状态：状态（0-待付款、1已付款、2-作废、-1-中止付款）
     */
    private String status;
    private String status_name;

    /**
     * 1-应付、2-预付、3-其他支出
     */
    private String type;
    private String type_name;

    /**
     * 供应商ID
     */
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    private String supplier_code;

    /**
     * 供应商名称
     */
    private String supplier_name;

    /**
     * 购买方ID
     */
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    private String purchaser_code;

    /**
     * 采购方名称
     */
    private String purchaser_name;

    /**
     * 付款日期
     */
    private LocalDateTime pay_date;

    /**
     * 付款方式：1-银行转账
     */
    private String payment_type;

    /**
     * 付款单计划付款总金额
     */
    private BigDecimal payable_amount_total;

    /**
     * 付款单已付款总金额
     */
    private BigDecimal paid_amount_total;

    /**
     * 付款指令备注
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
     * 付款明细列表
     */
    private List<BApPayDetailVo> detailListData;

    /**
     * 附件列表
     */
    private List<BApPayAttachVo> attachListData;

    /**
     * 分页条件
     */
    private PageCondition pageCondition;

    /**
     * 付款单附件
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
    private BigDecimal sum_payable_amount_total;
    private BigDecimal sum_paid_amount_total;

    /**
     * 单据状态列表
     */
    private String[] status_list;
    /**
     * 业务单据信息 采购订单
     */
    private List<BApSourceAdvanceVo> poOrderListData;

    /**
     * 付款状态：0-未付款、1-部分付款、2-已付款、-1-中止付款
     */
    private String pay_status;
    private String pay_status_name;

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
     * 未付款总金额
     */
    private BigDecimal unpay_amount_total;

    /**
     * 付款中总金额
     */
    private BigDecimal paying_amount_total;

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
