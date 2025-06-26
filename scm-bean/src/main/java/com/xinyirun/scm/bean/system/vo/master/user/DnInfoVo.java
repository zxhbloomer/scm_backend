package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
// @ApiModel(value = "dnnode信息", description = "dnnode信息")
@EqualsAndHashCode(callSuper=false)
public class DnInfoVo extends BaseVo implements Serializable {
    private static final long serialVersionUID = -8965123313778670198L;

    private String username;

    private String dnnode;

}
