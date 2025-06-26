package com.xinyirun.scm.bean.system.vo.sys.config.dict;

import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhangxh
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "字典类型信息", description = "字典类型vo_bean")
@EqualsAndHashCode(callSuper=false)
public class SDictTypeVo extends UploadFileResultAo implements Serializable {

    private static final long serialVersionUID = 8149295048471235932L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典类型：唯一
     */
    private String code;

    /**
     * 说明
     */
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;

    /**
     * 租户代码
     */
    private String corp_code;

    /**
     * 租户名称
     */
    private String corp_name;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
