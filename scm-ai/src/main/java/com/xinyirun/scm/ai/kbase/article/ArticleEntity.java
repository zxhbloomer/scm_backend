package com.xinyirun.scm.ai.kbase.article;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 帮助文档
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@TableName("scm_ai_kbase")
public class ArticleEntity extends AbstractArticleEntity {

    private static final long serialVersionUID = 1L;

    public void prePersist() {
        if (getType() == null) {
            setType("TEXT");
        }
        if (getAuditStatus() == null) {
            setAuditStatus("PENDING");
        }
    }
    
    public void postLoad() {
        if (getType() == null) {
            setType("TEXT");
        }
    }

    public ArticleEntity setElasticSuccess() {
        this.setElasticStatus("SUCCESS");
        return this;
    }

    public ArticleEntity setElasticError() {
        this.setElasticStatus("ERROR");
        return this;
    }

    public ArticleEntity setVectorSuccess() {
        this.setVectorStatus("SUCCESS");
        return this;
    }
    
    public ArticleEntity setVectorError() {
        this.setVectorStatus("ERROR");
        return this;
    }

    public boolean isElasticStatusSuccess() {
        return "SUCCESS".equals(getElasticStatus());
    }

    /**
     * 检查文章是否已经完成向量索引
     * @return true if vectorStatus is SUCCESS
     */
    public boolean isVectorIndexed() {
        return "SUCCESS".equals(getVectorStatus());
    }
}