package com.xinyirun.scm.bean.entity.busniess.so.socontract;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 销售合同附件表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_contract_attach")
public class BSoContractAttachEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5545275066201925598L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 营业执照
     */
    @TableField("one_file")
    private Integer one_file;

    /**
     * 法人身份证正面
     */
    @TableField("two_file")
    private Integer two_file;

    /**
     * 法人身份证反面
     */
    @TableField("three_file")
    private Integer three_file;

    /**
     * 其他材料
     */
    @TableField("four_file")
    private Integer four_file;


}
