package com.xinyirun.scm.bean.system.vo.master.rbac.permission.operation;


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
public class OperationMenuVo implements Serializable {

    private static final long serialVersionUID = -1409884083936594959L;

    /**
     * 树菜单信息
     */
    List<OperationMenuDataVo> menu_data;

    /**
     * 条数
     */
    int data_count;

}
