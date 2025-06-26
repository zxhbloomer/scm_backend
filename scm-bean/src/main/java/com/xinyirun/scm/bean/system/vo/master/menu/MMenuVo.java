package com.xinyirun.scm.bean.system.vo.master.menu;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 菜单信息
 * </p>
 *
 * @author zxh
 * @since 2019-11-01
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "菜单信息", description = "菜单信息")
@EqualsAndHashCode(callSuper=false)
public class MMenuVo implements Serializable {

    private static final long serialVersionUID = -1409884083936594959L;

    /**
     * 树菜单信息
     */
    List<MMenuDataVo> menu_data;

    /**
     * 按钮清单
     */
    List<MMenuPageFunctionVo> menu_buttons;

    /**
     * 重定向
     */
    MMenuRedirectVo menu_redirect;
}
