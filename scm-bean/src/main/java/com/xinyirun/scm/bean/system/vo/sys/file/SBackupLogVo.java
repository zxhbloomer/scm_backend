package com.xinyirun.scm.bean.system.vo.sys.file;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SBackupLogVo implements Serializable {

    private static final long serialVersionUID = 8850104238436538269L;

    private Long id;

    /**
     * 源文件id
     */
    private Long source_file_id;

    /**
     * 源文件url
     */
    private String source_file_url;

    /**
     * 源文件id
     */
    private Long source_file_size;

    /**
     * 新文件id
     */
    private Long target_file_id;

    /**
     * 新文件url
     */
    private String target_file_url;

    /**
     * 新文件id
     */
    private Long target_file_size;

    /**
     * 原始磁盘物理地址
     */
    private String original_urldisk;

    /**
     * 待删除临时物理地址
     */
    private String del_urldisk;

    /**
     * 转移至待删除地址的时间
     */
    private LocalDateTime transfer_time;


    /**
     * 删除时间
     */
    private LocalDateTime delete_time;


    /**
     * 上传至oss的时间
     */
    private LocalDateTime backup_time;

    /**
     * 备注
     */
    private String remark;


    /**
     * 执行情况 成功:true 失败false
     */
    private Boolean status;

    private String uri;

    private String url;


}
