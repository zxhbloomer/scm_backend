package com.xinyirun.scm.bean.entity.master.menu;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 菜单信息
 * </p>
 *
 * @author zxh
 * @since 2020-07-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("m_staff_menu_collection")
public class MStaffMenuCollectionEntity implements Serializable {

    
    private static final long serialVersionUID = 1209945302933413806L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工id
     */
    @TableField("staff_id")
    private Long staff_id;

    /**
     * 菜单id
     */
    @TableField("menu_id")
    private Long menu_id;

    /**
     * 是否收藏
     */
    @TableField("is_collection")
    private boolean is_collection;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

}
