package com.xinyirun.scm.bean.system.vo.sys.pages.setting;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/5/22 15:20
 */

@Data
public class P00000128Vo implements Serializable {

    private static final long serialVersionUID = -3441250344965469554L;

    /**
     * 初始化时间
     */
    private String init_time;

    /**
     * 稻谷
     */
    private String column_one;

    /**
     * 糙米
     */
    private String column_two;

    /**
     * 玉米
     */
    private String column_three;

    /**
     * 混合物
     */
    private List<String> columns_four;

    /**
     * 杂质
     */
    private String column_four_zz;

    /**
     * 稻壳
     */
    private String column_five;

    /**
     * 备注
     */
    private String comment;

    /**
     * 当前执行时间
     */
    private String process_time;

    /**
     * 是否执行, 0不执行, 1执行
     */
    private String config_value;
}
