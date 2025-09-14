package com.xinyirun.scm.ai.kbase.article;

import lombok.Getter;
import lombok.Setter;

/**
 * 文章Excel导出类
 */
@Getter
@Setter
public class ArticleExcel {

    private String title;
    
    private String summary;
    
    private String contentHtml;
    
    private Integer readCount;
    
    private Integer likeCount;

    private String createdAt;
    
    private String updatedAt;

}