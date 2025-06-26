package com.xinyirun.scm.bean.system.vo.business.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BMonitorPreviewFileVo implements Serializable {

    private static final long serialVersionUID = 4031095220234012119L;

    private Integer id;

    private String url;

    private String file_name;

    private String file_title;

    private String dir_name;

    private String u_time;

    private String u_name;

    private String login_name;

    private String status;
}
