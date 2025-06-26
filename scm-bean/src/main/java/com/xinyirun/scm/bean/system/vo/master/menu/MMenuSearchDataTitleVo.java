package com.xinyirun.scm.bean.system.vo.master.menu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 菜单信息
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class MMenuSearchDataTitleVo implements Serializable {

    
    private static final long serialVersionUID = -5826762314440698647L;
    /**
     * 菜单名
     */
    private String meta_title;

    /**
     * 菜单icon
     */
    private String meta_icon;

    /**
     * menu_id
     */
    private Integer menu_id;

}
