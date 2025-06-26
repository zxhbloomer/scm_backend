package com.xinyirun.scm.bean.system.vo.master.menu;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 菜单重定向
 * </p>
 *
 * @author zxh
 * @since 2020-09-28
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "菜单重定向bean", description = "菜单重定向bean")
@EqualsAndHashCode(callSuper=false)
public class MMenuRedirectVo implements Serializable {

    private static final long serialVersionUID = -7668181069536025626L;

    private Long id;

    /**
     * 根结点id
     */
    private Long root_id;

    /**
     * 页面id
     */
    private Long page_id;

    /**
     * 菜单中，该页面的id
     */
    private Long menu_page_id;

    /**
     * 页面名称
     */
    private String name;
}
