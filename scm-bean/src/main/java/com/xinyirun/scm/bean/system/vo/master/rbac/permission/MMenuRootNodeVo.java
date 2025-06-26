package com.xinyirun.scm.bean.system.vo.master.rbac.permission;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @ClassName: MMenuRootNode
 * @Description: 系统菜单的根节点
 * @Author: zxh
 * @date: 2020/8/3
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "系统菜单的根节点", description = "系统菜单的根节点")
@EqualsAndHashCode(callSuper=false)
public class MMenuRootNodeVo implements Serializable {

    private static final long serialVersionUID = 3014514062292138636L;

    private Long id;

    /**
     * 结点id
     */
    private Long value;


    /**
     * 结点name
     */
    private String label;
}
