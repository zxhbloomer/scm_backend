package com.xinyirun.scm.bean.system.vo.sys.pages.function;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 按钮表
 * </p>
 *
 * @author zxh
 * @since 2020-06-16
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "按钮表vo", description = "按钮表vo")
@EqualsAndHashCode(callSuper=false)
public class SFunctionVo implements Serializable {

    private static final long serialVersionUID = 2586852812669387371L;

    private Long id;

    /**
     * 编号
     */
    private String code;

    /**
     * 按钮名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;
    /**
     * 排序的最大最小值
     */
    private int max_sort;
    private int min_sort;

    /**
     * 说明
     */
    private String descr;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    private String c_name;
    private String u_name;

}
