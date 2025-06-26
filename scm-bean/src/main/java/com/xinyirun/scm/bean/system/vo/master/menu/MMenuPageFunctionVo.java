package com.xinyirun.scm.bean.system.vo.master.menu;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 菜单按钮数据信息
 * </p>
 *
 * @author zxh
 * @since 2019-11-01
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "页面按钮数据信息", description = "页面按钮数据信息")
@EqualsAndHashCode(callSuper=false)
public class MMenuPageFunctionVo implements Serializable {

    private static final long serialVersionUID = -824111524626109766L;

    /**
     * 按钮id
     */
    private Long id;

    /**
     * 按钮编号
     */
    private String code;

    /**
     * 按钮名称
     */
    private String name;

    /**
     * 字典排序
     */
    private Integer sort;

}
