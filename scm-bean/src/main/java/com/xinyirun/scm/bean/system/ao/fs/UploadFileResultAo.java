package com.xinyirun.scm.bean.system.ao.fs;

import com.xinyirun.scm.bean.system.config.base.BaseVo;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangxh
 */
@Slf4j
@ToString
@Data
// @ApiModel(value = "文件上传结果Ao", description = "文件上传结果Ao")
@EqualsAndHashCode(callSuper=false)
public class UploadFileResultAo extends BaseVo {

    private static final long serialVersionUID = -490216525018123645L;
    /**
     * 文件ID
     */
    private String fileUuid;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小(B)
     */
    private Long file_size;
    /**
     * internal_url
     */
    private String internal_url;


    /**
     * url
     */
        private String url;
}
