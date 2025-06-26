package com.xinyirun.scm.bean.entity.master.menu;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_menu_redirect")
public class MMenuRedirectEntity implements Serializable {

    private static final long serialVersionUID = 8578274941498485429L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 根结点id
     */
    @TableField("root_id")
    private Long root_id;

    /**
     * 页面id
     */
    @TableField("page_id")
    private Long page_id;

    /**
     * 菜单中，该页面的id
     */
    @TableField("menu_page_id")
    private Long menu_page_id;


}
