package com.xinyirun.scm.bean.system.vo.mongo.monitor.v2;

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
public class BPreviewBackupDataV2Vo implements Serializable {

    private static final long serialVersionUID = 3071566068860694848L;

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

    public BPreviewBackupDataV2Vo(int file_num) {
        this.file_num = file_num;
    }
}
