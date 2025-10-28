package com.xinyirun.scm.ai.bean.vo.common;

import lombok.Data;
import java.io.Serializable;

/**
 * AI文件VO
 * 用于文件上传接口返回
 *
 * @author SCM AI Team
 * @since 2025-10-28
 */
@Data
public class AiFileVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文件UUID
     */
    private String uuid;

    /**
     * 文件URL
     */
    private String url;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件扩展名
     */
    private String ext;

    /**
     * 文件大小(字节)
     */
    private Long size;
}
