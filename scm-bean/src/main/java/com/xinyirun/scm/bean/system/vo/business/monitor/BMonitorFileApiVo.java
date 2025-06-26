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
public class BMonitorFileApiVo implements Serializable {

    private static final long serialVersionUID = 4031095220234012119L;
    private String url;

    private String fileName;

    private String dirName;
}
