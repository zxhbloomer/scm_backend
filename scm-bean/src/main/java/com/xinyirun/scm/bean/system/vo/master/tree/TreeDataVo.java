package com.xinyirun.scm.bean.system.vo.master.tree;

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
public class TreeDataVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -7562659326114933854L;

    private Long serial_id;

    /**
     * 类型
     */
    private String serial_type;

    /**
     * code
     */
    private String serial_code;

    /**
     * 名称
     */
    private String label;

    /**
     * 登录名
     */
    private List<TreeDataVo> children;

}
