package com.xinyirun.scm.bean.system.vo.master.menu;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单信息
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class MMenuSearchCacheDataVo implements Serializable {

    
    private static final long serialVersionUID = -5481406801230385364L;

    /**
     * 菜单数据
     */
    private List<MMenuSearchDataTitleVo> meta;

    /**
     * 对应名称拼音
     */
    private List<String> name_py;

    /**
     * 对应名称简拼
     */
    private List<String> name_simple_py;

    /**
     * 对应标题
     */
    private List<String> title;

    /**
     * 唯一id
     */
    private Long menu_id;

    /**
     * 路径
     */
    private String path;

    /**
     * 是否收藏
     */
    private Boolean is_collection;
}
