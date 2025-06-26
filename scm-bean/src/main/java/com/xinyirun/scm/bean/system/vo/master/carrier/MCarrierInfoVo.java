package com.xinyirun.scm.bean.system.vo.master.carrier;

import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
// import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 承运商明细
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @Schema( name = "承运商明细", description = "承运商明细")
public class MCarrierInfoVo implements Serializable {

    private static final long serialVersionUID = -4435845221688751203L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 客户id
     */
    private Integer customer_id;

    /**
     * 名称
     */
    private String name;

    /**
     * 道路运输证号
     */
    private String transport_no;

    /**
     * 经营许可证号
     */
    private String licence_no;

    /**
     * 身份证正面附件id
     */
    private Integer id_front_file_id;

    /**
     * 身份证正面附件
     */
    private SFileInfoVo id_front_file;

    /**
     * 身份证反面附件id
     */
    private Integer id_back_file_id;

    /**
     * 身份证反面附件
     */
    private SFileInfoVo id_back_file;

    /**
     * 企业认证附件id
     */
    private Integer confirm_file_id;

    /**
     * 企业认证附件
     */
    private SFileInfoVo confirm_file;

    /**
     * 经营许可认证附件id
     */
    private Integer licence_confirm_file_id;

    /**
     * 经营许可认证附件
     */
    private SFileInfoVo licence_confirm_file;
}
