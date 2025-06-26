package com.xinyirun.scm.bean.system.vo.master.enterprise;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 企业信息附件表
 * </p>
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class MEnterpriseAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6433299733360919446L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 企业id
     */
    private Integer enterprise_id;

    /**
     * 营业执照附件id
     */
    private Integer license_att_id;

    /**
     * 法人身份证正面附件id
     */
    private Integer lr_id_front_att_id;

    /**
     * 法人身份证背面附件id
     */
    private Integer lr_id_back_att_id;

    /**
     * 其他材料附件id
     */
    private Integer doc_att_id;

    /**
     * 公司logo附件id
     */
    private Integer logo_id;

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

}
