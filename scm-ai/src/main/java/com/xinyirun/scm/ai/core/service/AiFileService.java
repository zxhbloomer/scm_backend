package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.ai.bean.vo.common.AiFileVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * AI文件服务接口
 *
 * @author SCM AI Team
 * @since 2025-10-28
 */
public interface AiFileService {

    /**
     * 保存上传的文件
     *
     * @param file 上传的文件
     * @return 文件VO(包含uuid和url)
     * @throws IOException IO异常
     */
    AiFileVo saveFile(MultipartFile file) throws IOException;

    /**
     * 删除文件
     *
     * @param uuid 文件UUID
     * @return 是否成功
     */
    boolean deleteFile(String uuid);
}
