package com.xinyirun.scm.bean.entity.base.entity.v1;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.xinyirun.scm.bean.system.config.base.BaseBean;
import lombok.Getter;
import lombok.Setter;

/**
 * @author zxh
 * @date 2019-07-29
 */
public class BaseEntity<T> extends BaseBean<T> {

//    /**
//     * 获取属性名和表列名的map
//     */
//    @TableField(exist = false)
//    private Map<String, String> mapPropertyColumn;
//
//    /**
//     * 获取表列名和属性的map
//     */
//    @TableField(exist = false)
//    private Map<String, String> mapColumnProperty;
//
//    /**
//     * entity名字
//     */
//    @TableField(exist = false)
//    private Class<T> entity;

    /** 数据权限_租户 */
    @Getter
    @Setter
    @TableField(exist = false)
    private Long authorityTenant;

    /** 数据权限_角色 */
    @Getter
    @Setter
    @TableField(exist = false)
    private Long authorityRole;

    /** 数据权限_个人 */
    @Getter
    @Setter
    @TableField(fill = FieldFill.INSERT,exist = false)
    private Long authorityUser;

    /** 数据权限_组织 */
    @Getter
    @Setter
    @TableField(fill = FieldFill.INSERT,exist = false)
    private Long authorityOrg;

    /** 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限）
     *
     *
     *    @ExcelAnnotion(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限")
     *
     **/
    @Getter
    @Setter
    @TableField(exist = false)
    private String dataScope;
}
