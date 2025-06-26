package com.xinyirun.scm.bean.system.vo.mongo.monitor.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 附件预览vo
 * </p>
 *
 * @author wwl
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class BPreviewBackupDataVo implements Serializable {

    private static final long serialVersionUID = 4769325374162764310L;

    private String html;

    private String src;

    private String url;

    private String thumb;

    private String subHtml;

    private Integer index;

    private String file_name;

    /**
     * 图片是第几个
     */
    private int file_num;

    public BPreviewBackupDataVo(int file_num) {
        this.file_num = file_num;
    }
}
