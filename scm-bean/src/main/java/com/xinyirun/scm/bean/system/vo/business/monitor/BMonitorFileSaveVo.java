package com.xinyirun.scm.bean.system.vo.business.monitor;

import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
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
public class BMonitorFileSaveVo implements Serializable {

    private static final long serialVersionUID = -6252143420041391372L;

    /**
     * 类型
     */
    private Integer monitor_id;

    /**
     * 附件对象
     */
    private SFileInfoVo file;

    /**
     * 类型
     */
    private String type;


}
