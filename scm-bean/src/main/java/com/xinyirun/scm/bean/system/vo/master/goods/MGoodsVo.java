package com.xinyirun.scm.bean.system.vo.master.goods;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 物料
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "物料", description = "物料")
public class MGoodsVo implements Serializable {

    private static final long serialVersionUID = -2285480623084372727L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 物料名
     */
    private String name;

    /**
     * 编号
     */
    private String code;

    /**
     * 类别id
     */
    private Integer category_id;

    /**
     * 类别名
     */
    private String category_name;

    /**
     * 启用状态
     */
    private Boolean enable;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 创建人姓名
     */
    private String c_name;

    /**
     * 修改人姓名
     */
    private String u_name;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 是否删除:false-未删除,true-已删除
     */
    private Boolean is_del;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    private Integer[] ids;


}
