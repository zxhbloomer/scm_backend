package com.xinyirun.scm.bean.system.vo.master.container;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author: Wqf
 * @Description: 集装箱实体类
 * @CreateTime : 2023/5/30 16:20
 */

@Data
public class MContainerVo {

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 集装箱号
     */
    private String code;

    /**
     * 是否删除
     */
    private Integer is_del;

    /**
     * 分页参数
     */
    private PageCondition pageCondition;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 导出 ID
     */
    private List<Integer> ids;

}
