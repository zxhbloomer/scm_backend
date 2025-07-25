package com.xinyirun.scm.bean.system.vo.mongo.monitor.v2;

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
public class BMonitorFileDownloadMongoV2Vo implements Serializable {

    private static final long serialVersionUID = 697039210249919869L;

    private String url;

    private String fileName;

    private String dirName;
}
