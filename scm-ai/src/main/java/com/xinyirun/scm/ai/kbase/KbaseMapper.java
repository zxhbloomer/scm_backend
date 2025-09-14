package com.xinyirun.scm.ai.kbase;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.kbase.entity.KbaseEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface KbaseMapper extends BaseMapper<KbaseEntity> {

    /**
     * 根据UID查询知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE uid = #{uid} AND deleted = 0")
    KbaseEntity findByUid(@Param("uid") String uid);

    /**
     * 根据分类UID查询知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE category_uid = #{categoryUid} AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findByCategoryUid(@Param("categoryUid") String categoryUid);

    /**
     * 根据类型查询知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE type = #{type} AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findByType(@Param("type") String type);

    /**
     * 根据状态查询知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE status = #{status} AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findByStatus(@Param("status") String status);

    /**
     * 查询启用的知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE enabled = 1 AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findEnabled();

    /**
     * 查询FAQ知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE faq = 1 AND enabled = 1 AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findFaq();

    /**
     * 查询热门知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE hot = 1 AND enabled = 1 AND deleted = 0 ORDER BY view_count DESC, created_at DESC")
    List<KbaseEntity> findHot();

    /**
     * 查询置顶知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE top = 1 AND enabled = 1 AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findTop();

    /**
     * 根据标题模糊搜索
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE title LIKE CONCAT('%', #{keyword}, '%') AND enabled = 1 AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findByTitleContaining(@Param("keyword") String keyword);

    /**
     * 根据内容模糊搜索
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE content LIKE CONCAT('%', #{keyword}, '%') AND enabled = 1 AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findByContentContaining(@Param("keyword") String keyword);

    /**
     * 全文搜索
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE (title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%') OR tags LIKE CONCAT('%', #{keyword}, '%')) AND enabled = 1 AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    List<KbaseEntity> findByKeyword(@Param("keyword") String keyword);

    /**
     * 分页查询知识库条目
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE deleted = 0 ORDER BY order_num ASC, created_at DESC")
    IPage<KbaseEntity> findByPage(Page<KbaseEntity> page);

    /**
     * 根据组织UID分页查询
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE org_uid = #{orgUid} AND deleted = 0 ORDER BY order_num ASC, created_at DESC")
    IPage<KbaseEntity> findByOrgUidAndPage(Page<KbaseEntity> page, @Param("orgUid") String orgUid);

    /**
     * 根据创建者查询
     */
    @Select("SELECT * FROM scm_ai_kbase WHERE created_by = #{createdBy} AND deleted = 0 ORDER BY created_at DESC")
    List<KbaseEntity> findByCreatedBy(@Param("createdBy") String createdBy);

    /**
     * 更新浏览次数
     */
    @Update("UPDATE scm_ai_kbase SET view_count = view_count + 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int incrementViewCount(@Param("uid") String uid);

    /**
     * 更新点赞次数
     */
    @Update("UPDATE scm_ai_kbase SET like_count = like_count + 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int incrementLikeCount(@Param("uid") String uid);

    /**
     * 更新踩数次数
     */
    @Update("UPDATE scm_ai_kbase SET dislike_count = dislike_count + 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int incrementDislikeCount(@Param("uid") String uid);

    /**
     * 软删除知识库条目
     */
    @Update("UPDATE scm_ai_kbase SET deleted = 1, updated_at = NOW() WHERE uid = #{uid}")
    int deleteByUid(@Param("uid") String uid);

    /**
     * 启用知识库条目
     */
    @Update("UPDATE scm_ai_kbase SET enabled = 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int enableByUid(@Param("uid") String uid);

    /**
     * 禁用知识库条目
     */
    @Update("UPDATE scm_ai_kbase SET enabled = 0, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int disableByUid(@Param("uid") String uid);

    /**
     * 设置为FAQ
     */
    @Update("UPDATE scm_ai_kbase SET faq = 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int setFaq(@Param("uid") String uid);

    /**
     * 取消FAQ
     */
    @Update("UPDATE scm_ai_kbase SET faq = 0, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int unsetFaq(@Param("uid") String uid);

    /**
     * 设置为热门
     */
    @Update("UPDATE scm_ai_kbase SET hot = 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int setHot(@Param("uid") String uid);

    /**
     * 取消热门
     */
    @Update("UPDATE scm_ai_kbase SET hot = 0, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int unsetHot(@Param("uid") String uid);

    /**
     * 设置为置顶
     */
    @Update("UPDATE scm_ai_kbase SET top = 1, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int setTop(@Param("uid") String uid);

    /**
     * 取消置顶
     */
    @Update("UPDATE scm_ai_kbase SET top = 0, updated_at = NOW() WHERE uid = #{uid} AND deleted = 0")
    int unsetTop(@Param("uid") String uid);

    /**
     * 统计知识库条目数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_kbase WHERE deleted = 0")
    Long countAll();

    /**
     * 统计启用的知识库条目数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_kbase WHERE enabled = 1 AND deleted = 0")
    Long countEnabled();

    /**
     * 统计指定分类的知识库条目数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_kbase WHERE category_uid = #{categoryUid} AND deleted = 0")
    Long countByCategoryUid(@Param("categoryUid") String categoryUid);

    /**
     * 统计指定类型的知识库条目数量
     */
    @Select("SELECT COUNT(*) FROM scm_ai_kbase WHERE type = #{type} AND deleted = 0")
    Long countByType(@Param("type") String type);

    /**
     * 获取最大排序号
     */
    @Select("SELECT IFNULL(MAX(order_num), 0) FROM scm_ai_kbase WHERE category_uid = #{categoryUid} AND deleted = 0")
    Integer getMaxOrderNum(@Param("categoryUid") String categoryUid);
}