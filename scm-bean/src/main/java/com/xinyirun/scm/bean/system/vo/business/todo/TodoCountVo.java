package com.xinyirun.scm.bean.system.vo.business.todo;


import com.xinyirun.scm.bean.system.config.base.BaseVo;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 待办条数
 * </p>
 *
 * @author wwl
 * @since 2021-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "待办条数", description = "待办条数")
public class TodoCountVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = 6565531454565902286L;
    /**
     * 待办条数
     */
    private Integer todoCount;

    /**
     * 总条数
     */
    private Integer allCount;

    /**
     * 业务表类型
     */
    private String serial_type;
}
