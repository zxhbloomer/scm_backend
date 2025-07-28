package com.xinyirun.scm.bean.system.vo.business.so.socontract;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: 销售合同附件信息
 * @CreateTime : 2025/1/22 15:48
 */

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoContractAttachVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -5318322607462564265L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 营业执照
     */
    private Integer one_file;

    /**
     * 法人身份证正面
     */
    private Integer two_file;

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
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

}