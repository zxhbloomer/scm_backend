package com.xinyirun.scm.bean.system.vo.business.notice;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 通知表
 * </p>
 *
 * @author xinyirun
 * @since 2024-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BNoticeVo implements Serializable {

    
    private static final long serialVersionUID = -3523213207269490454L;

    private Integer id;


    /**
     * 通知类型, 0：用户通知；1:系统通知，
     */
    private String type;

    private String type_name;

    /**
     * 标题
     */
    private String title;

    /**
     * 通知详情
     */
    private String msg;
    private String html;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 文件地址
     */
    private Integer file_one;

    /**
     * 分页数据
     */
    private PageCondition pageCondition;

    /**
     * 发布状态
     */
    private String status;

    /**
     * 发布状态名称
     */
    private String status_name;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 员工集合
     */
    private List<MStaffVo> staff_list;

    /**
     * 员工id
     */
    private Long staff_id;

    /**
     * 是否已读
     */
    private String is_read;

}
