package com.xinyirun.scm.bean.system.ao.fs;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author zxh
 * @date 2019年 07月22日 21:56:31
 */
@Data
@AllArgsConstructor
@Builder
public class UrlFileAo extends BaseVo {
    private static final long serialVersionUID = -1384815805407213140L;
    String remoteFileUrl;
    HttpURLConnection conn;
    URL connUrl;
    String fileName;
    /**
     * 文件后缀 docx
     */
    String fileSuffix;
}
