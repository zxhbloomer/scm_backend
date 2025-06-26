package com.xinyirun.scm.bean.system.vo.master.user;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;


/**
 * <p>
 * 员工权限信息
 * </p>
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MStaffPermissionDataVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7562659326114933854L;

    private Long id;

    /**
     * 名称
     */
    private String label;

    /**
     * 登录名
     */
    private List<MStaffPermissionDataVo> children;

}
