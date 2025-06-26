package com.xinyirun.scm.bean.system.vo.sys.file;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 文件备份vo
 * </p>
 *
 * @author wwl
 * @since 2022-06-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BackupFileVo implements Serializable {

    private static final long serialVersionUID = 8310515418072983622L;

    private String app_key;

    private List<SBackupLogVo> items;

    /**
     * 手工触发条数
     */
    private Integer backup_now_count;
}
