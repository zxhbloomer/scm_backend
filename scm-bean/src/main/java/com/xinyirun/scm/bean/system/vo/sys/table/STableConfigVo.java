package com.xinyirun.scm.bean.system.vo.sys.table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-08-29
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class STableConfigVo implements Serializable {

    private static final long serialVersionUID = -983751599560780293L;

    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 页面code
     */
    private String page_code;

    /**
     * 类型：基本上页面只有一个table，如果出现两个table则需要区分
     */
    private String type;

    /**
     * 开始列
     */
    private Integer start_column_index;

    /**
     * 员工id
     */
    private Integer staff_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;


}
