package com.xinyirun.scm.bean.api.vo.business.position;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 岗位主表
 * </p>
 *
 * @author zxh
 * @since 2019-11-12
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "岗位主表", description = "岗位主表")
@EqualsAndHashCode(callSuper=false)
public class ApiPositionVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7836862046180500810L;

    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 全称
     */
    private String name;

    /**
     * 简称
     */
    private String simple_name;

    /**
     * 说明
     */
    private String descr;

}
