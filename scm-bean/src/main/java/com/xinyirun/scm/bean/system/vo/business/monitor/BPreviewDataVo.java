package com.xinyirun.scm.bean.system.vo.business.monitor;

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
public class BPreviewDataVo implements Serializable {

    private static final long serialVersionUID = -1655943039155418744L;

    private String html;

    private String src;

    private String url;

    private String thumb;

    private String subHtml;

    private Integer index;

}
