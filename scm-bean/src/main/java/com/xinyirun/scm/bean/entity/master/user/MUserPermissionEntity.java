package com.xinyirun.scm.bean.entity.master.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 用户权限关联表
 * </p>
 *
 * @author zxh
 * @since 2021-02-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("m_user_permission")
public class MUserPermissionEntity implements Serializable {

    private static final long serialVersionUID = 7007395855009004504L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户主表id
     */
    @TableField("user_id")
    private Long user_id;

    /**
     * 权限主表id
     */
    @TableField("permission_id")
    private Long permission_id;
}
