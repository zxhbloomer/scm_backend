package com.xinyirun.scm.bean.system.vo.common.component;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zxh
 * @date 2019/9/24
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "共通组件数据，下拉选项", description = "共通组件数据，下拉选项")
@EqualsAndHashCode(callSuper=false)
public class SystemComponentVo extends BaseVo implements Serializable {
    private static final long serialVersionUID = -5919960965277527060L;

    /**
     * 下拉选项卡：删除类型字典
     */
    private List<NameAndValueVo> select_component_delete_map_normal;

    /**
     * 下拉选项卡：删除类型字典，不包含删除
     */
    private List<NameAndValueVo> select_component_delete_map_only_used_data;
}
