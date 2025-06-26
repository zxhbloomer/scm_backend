package com.xinyirun.scm.bean.system.vo.master.org;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MOrgRouteVo
 * @Description: 组织机构路径
 * @Author: zxh
 * @date: 2020/5/14
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "组织机构路径bean", description = "组织机构路径bean")
@EqualsAndHashCode(callSuper=false)
public class MOrgRouteVo extends BaseVo implements Serializable {
    private static final long serialVersionUID = -5464464504137068866L;

    /**
     * 组织id
     */
    private Long org_id;

    /**
     * 父结点id
     */
    private Long parent_id;

    /**
     * 租户id
     */
//    private Long tenant_id;

    /**
     * 关联单号类型
     */
    private String serial_type;

    /**
     * 关联单号
     */
    private Long serial_id;

    /**
     * 编号，00010001..
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 简称
     */
    private String simple_name;

    /**
     * 是否删除
     */
    private Boolean is_del;

}
