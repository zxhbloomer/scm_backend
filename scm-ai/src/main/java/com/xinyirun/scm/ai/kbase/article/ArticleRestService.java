package com.xinyirun.scm.ai.kbase.article;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.kbase.entity.KbaseEntity;
import com.xinyirun.scm.ai.kbase.KbaseMapper;
import com.xinyirun.scm.ai.util.UidUtils;
import com.xinyirun.scm.ai.kbase.article.request.ArticleRequest;
import com.xinyirun.scm.ai.kbase.article.response.ArticleResponse;

/**
 * 文章服务类 - 基于ByteDesk源码
 */
@Service
public class ArticleRestService extends ServiceImpl<ArticleMapper, ArticleEntity> {

    @Autowired
    private ArticleMapper articleRepository;


    // 循环依赖
    // private final KbaseRestService kbaseRestService;
    @Autowired
    private KbaseMapper kbaseRepository;

    protected QueryWrapper<ArticleEntity> createSpecification(ArticleRequest request) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            queryWrapper.like("title", request.getTitle());
        }
        if (request.getKbUid() != null && !request.getKbUid().isEmpty()) {
            queryWrapper.eq("kb_uid", request.getKbUid());
        }
        if (request.getCategoryUid() != null && !request.getCategoryUid().isEmpty()) {
            queryWrapper.eq("category_uid", request.getCategoryUid());
        }
        if (request.getPublished() != null) {
            queryWrapper.eq("published", request.getPublished());
        }
        
        queryWrapper.eq("deleted", false);
        queryWrapper.orderByDesc("created_at");
        
        return queryWrapper;
    }

    protected IPage<ArticleEntity> executePageQuery(QueryWrapper<ArticleEntity> spec, IPage<ArticleEntity> pageable) {
        return articleRepository.selectPage(pageable, spec);
    }

    @Cacheable(value = "article", key="#uid", unless = "#result == null")
    public Optional<ArticleEntity> findByUid(String uid) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid);
        ArticleEntity entity = articleRepository.selectOne(queryWrapper);
        return Optional.ofNullable(entity);
    }

    public List<ArticleEntity> findByKbUid(String kbUid) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("kb_uid", kbUid);
        queryWrapper.eq("deleted", false);
        return articleRepository.selectList(queryWrapper);
    }

    public ArticleResponse create(ArticleRequest request) {
        // 简化用户认证处理，实际应该从AuthService获取
        String currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }

        ArticleEntity entity = new ArticleEntity();
        BeanUtilsSupport.copyProperties(request, entity);
        entity.setUid(UidUtils.uuid());
        // 
        entity.setUser(currentUser);
        entity.setUserUid(getCurrentOrgUid()); // ByteDesk使用userUid字段
        //
        QueryWrapper<KbaseEntity> kbaseQuery = new QueryWrapper<>();
        kbaseQuery.eq("uid", request.getKbUid());
        KbaseEntity kbase = kbaseRepository.selectOne(kbaseQuery);
        if (kbase != null) {
            entity.setKbaseUid(kbase.getUid()); // 正确的字段名是kbaseUid
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        ArticleEntity savedArticle = saveEntity(entity);
        if (savedArticle == null) {
            throw new RuntimeException("article save failed");
        }
        // 
        return convertToResponse(savedArticle);
    }

    public ArticleResponse update(ArticleRequest request) {

        Optional<ArticleEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ArticleEntity entity = optional.get();
            // modelMapper.map(request, entity);
            entity.setTitle(request.getTitle());
            entity.setSummary(request.getSummary());
            entity.setContentHtml(request.getContentHtml());
            entity.setContentMarkdown(request.getContentMarkdown());
            entity.setCategoryUid(request.getCategoryUid());
            //
            ArticleEntity savedArticle = saveEntity(entity);
            if (savedArticle == null) {
                throw new RuntimeException("article save failed");
            }
            //
            return convertToResponse(savedArticle);
            
        } else {
            throw new RuntimeException("article not found");
        }
    }

    public ArticleEntity saveEntity(ArticleEntity entity) {
        try {
            return doSave(entity);
        } catch (Exception e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

    @CachePut(value = "article", key = "#entity.uid")
    public ArticleEntity doSave(ArticleEntity entity)
    {
        if (entity.getId() == null) {
            articleRepository.insert(entity);
        } else {
            articleRepository.updateById(entity);
        }
        return entity;
    }

    @CacheEvict(value = "article", key = "#uid")
    public void deleteByUid(String uid) {
        Optional<ArticleEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            saveEntity(optional.get());
        }
    }

    public void delete(ArticleRequest entity) {
        deleteByUid(entity.getUid());
    }

    public ArticleEntity handleOptimisticLockingFailureException(Exception e, ArticleEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<ArticleEntity> latest = findByUid(entity.getUid());
            if (latest.isPresent()) {
                ArticleEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return doSave(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    public ArticleResponse convertToResponse(ArticleEntity entity) {
        ArticleResponse response = new ArticleResponse();
        BeanUtilsSupport.copyProperties(entity, response);
        return response;
    }

    public ArticleExcel convertToExcel(ArticleEntity article) {
        ArticleExcel excel = new ArticleExcel();
        BeanUtilsSupport.copyProperties(article, excel);
        return excel;
    }

    /**
     * 获取当前用户（简化实现）
     */
    private String getCurrentUser() {
        // 简化实现，实际应该从AuthService获取
        return "system";
    }

    /**
     * 获取当前组织UID（简化实现）
     */
    private String getCurrentOrgUid() {
        // 简化实现，实际应该从AuthService获取
        return "default_org";
    }

    /**
     * 查询组织下的文章
     */
    public Page<ArticleResponse> queryByOrg(ArticleRequest request) {
        QueryWrapper<ArticleEntity> queryWrapper = createSpecification(request);
        queryWrapper.eq("org_uid", getCurrentOrgUid());
        
        Page<ArticleEntity> mybatisPage = 
            new Page<>(request.getPageNumber() + 1, request.getPageSize());
        IPage<ArticleEntity> entityPage = executePageQuery(queryWrapper, mybatisPage);
        
        List<ArticleResponse> content = entityPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        Page<ArticleResponse> responsePage = new Page<>(entityPage.getCurrent(), entityPage.getSize());
        responsePage.setRecords(content);
        responsePage.setTotal(entityPage.getTotal());
        
        return responsePage;
    }

    /**
     * 查询用户下的文章
     */
    public Page<ArticleResponse> queryByUser(ArticleRequest request) {
        QueryWrapper<ArticleEntity> queryWrapper = createSpecification(request);
        queryWrapper.eq("user_uid", getCurrentOrgUid());
        
        Page<ArticleEntity> mybatisPage = 
            new Page<>(request.getPageNumber() + 1, request.getPageSize());
        IPage<ArticleEntity> entityPage = executePageQuery(queryWrapper, mybatisPage);
        
        List<ArticleResponse> content = entityPage.getRecords().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        Page<ArticleResponse> responsePage = new Page<>(entityPage.getCurrent(), entityPage.getSize());
        responsePage.setRecords(content);
        responsePage.setTotal(entityPage.getTotal());
        
        return responsePage;
    }

    /**
     * 根据UID查询文章
     */
    public ArticleResponse queryByUid(ArticleRequest request) {
        Optional<ArticleEntity> optional = findByUid(request.getUid());
        return optional.map(this::convertToResponse).orElse(null);
    }

    /**
     * 更新文章索引（简化实现）
     */
    public void updateIndex(ArticleRequest request) {
        // 简化实现：标记文章已索引
        Optional<ArticleEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ArticleEntity entity = optional.get();
            // 这里可以添加索引标记字段
            saveEntity(entity);
        }
    }

    /**
     * 更新文章向量索引（简化实现）
     */
    public void updateVectorIndex(ArticleRequest request) {
        // 简化实现：标记文章已向量化
        Optional<ArticleEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ArticleEntity entity = optional.get();
            // 这里可以添加向量索引标记字段
            saveEntity(entity);
        }
    }

    /**
     * 更新所有文章索引（简化实现）
     */
    public void updateAllIndex(ArticleRequest request) {
        List<ArticleEntity> articles = findByKbUid(request.getKbUid());
        for (ArticleEntity article : articles) {
            // 简化实现：批量索引处理
            saveEntity(article);
        }
    }

    /**
     * 更新所有文章向量索引（简化实现）
     */
    public void updateAllVectorIndex(ArticleRequest request) {
        List<ArticleEntity> articles = findByKbUid(request.getKbUid());
        for (ArticleEntity article : articles) {
            // 简化实现：批量向量化处理
            saveEntity(article);
        }
    }

    /**
     * 搜索文章（简化实现）
     */
    public List<ArticleResponse> searchArticle(ArticleRequest request) {
        QueryWrapper<ArticleEntity> queryWrapper = new QueryWrapper<>();
        
        if (request.getSearchText() != null && !request.getSearchText().isEmpty()) {
            queryWrapper.like("title", request.getSearchText())
                       .or()
                       .like("summary", request.getSearchText())
                       .or()
                       .like("content_markdown", request.getSearchText());
        }
        
        if (request.getKbUid() != null && !request.getKbUid().isEmpty()) {
            queryWrapper.eq("kb_uid", request.getKbUid());
        }
        
        queryWrapper.eq("published", true);
        queryWrapper.eq("deleted", false);
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT 10"); // 限制搜索结果数量
        
        List<ArticleEntity> entities = articleRepository.selectList(queryWrapper);
        return entities.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}