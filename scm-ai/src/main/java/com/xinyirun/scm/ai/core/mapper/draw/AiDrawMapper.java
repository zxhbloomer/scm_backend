package com.xinyirun.scm.ai.core.mapper.draw;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.draw.AiDrawEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * AI绘图 Mapper接口
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Mapper
public interface AiDrawMapper extends BaseMapper<AiDrawEntity> {

    /**
     * 按UUID查询绘图记录
     *
     * @param draw_uuid 绘图UUID
     * @return 绘图实体
     */
    @Select("""
        SELECT
            id,
            draw_uuid AS drawUuid,
            user_id AS userId,
            ai_model_id AS aiModelId,
            ai_model_name AS aiModelName,
            prompt,
            negative_prompt AS negativePrompt,
            interacting_method AS interactingMethod,
            original_image AS originalImage,
            mask_image AS maskImage,
            generate_number AS generateNumber,
            generate_size AS generateSize,
            generate_quality AS generateQuality,
            generate_seed AS generateSeed,
            generated_images AS generatedImages,
            resp_images_path AS respImagesPath,
            process_status AS processStatus,
            process_status_remark AS processStatusRemark,
            is_public AS isPublic,
            with_watermark AS withWatermark,
            star_count AS starCount,
            dynamic_params AS dynamicParams,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw
        WHERE draw_uuid = #{draw_uuid}
          AND is_deleted = 0
    """)
    AiDrawEntity selectByDrawUuid(@Param("draw_uuid") String draw_uuid);

    /**
     * 查询用户的绘图列表
     * 处理状态：0-处理中,1-成功,2-失败
     *
     * @param user_id 用户ID
     * @param max_id 最大ID(用于分页)
     * @param page_size 每页数量
     * @return 绘图列表
     */
    @Select("""
        SELECT
            id,
            draw_uuid AS drawUuid,
            user_id AS userId,
            ai_model_id AS aiModelId,
            ai_model_name AS aiModelName,
            prompt,
            negative_prompt AS negativePrompt,
            interacting_method AS interactingMethod,
            original_image AS originalImage,
            mask_image AS maskImage,
            generate_number AS generateNumber,
            generate_size AS generateSize,
            generate_quality AS generateQuality,
            generate_seed AS generateSeed,
            generated_images AS generatedImages,
            resp_images_path AS respImagesPath,
            process_status AS processStatus,
            process_status_remark AS processStatusRemark,
            is_public AS isPublic,
            with_watermark AS withWatermark,
            star_count AS starCount,
            dynamic_params AS dynamicParams,
            c_time AS cTime,
            c_id AS cId,
            u_time AS uTime,
            u_id AS uId,
            dbversion
        FROM ai_draw
        WHERE user_id = #{user_id}
          AND is_deleted = 0
          AND id < #{max_id}
        ORDER BY id DESC
        LIMIT #{page_size}
    """)
    List<AiDrawEntity> selectByUserIdWithPaging(@Param("user_id") Long user_id,
                                                  @Param("max_id") Long max_id,
                                                  @Param("page_size") Integer page_size);

    /**
     * 查询公开的绘图列表
     *
     * @param max_id 最大ID
     * @param page_size 每页数量
     * @return 绘图列表
     */
    @Select("""
        SELECT
            id,
            draw_uuid AS drawUuid,
            user_id AS userId,
            ai_model_id AS aiModelId,
            ai_model_name AS aiModelName,
            prompt,
            negative_prompt AS negativePrompt,
            interacting_method AS interactingMethod,
            generate_number AS generateNumber,
            generate_size AS generateSize,
            generate_quality AS generateQuality,
            generated_images AS generatedImages,
            process_status AS processStatus,
            is_public AS isPublic,
            with_watermark AS withWatermark,
            star_count AS starCount,
            c_time AS cTime,
            u_time AS uTime,
            dbversion
        FROM ai_draw
        WHERE is_deleted = 0
          AND is_public = 1
          AND id < #{max_id}
        ORDER BY id DESC
        LIMIT #{page_size}
    """)
    List<AiDrawEntity> selectPublicWithPaging(@Param("max_id") Long max_id,
                                               @Param("page_size") Integer page_size);

    /**
     * 更新绘图公开状态
     *
     * @param draw_uuid 绘图UUID
     * @param is_public 是否公开(0-私有,1-公开)
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_draw
        SET is_public = #{is_public}
        WHERE draw_uuid = #{draw_uuid}
    """)
    int updatePublicStatus(@Param("draw_uuid") String draw_uuid,
                          @Param("is_public") Integer is_public);

    /**
     * 更新处理状态
     * 处理状态：0-处理中,1-成功,2-失败
     *
     * @param id 绘图ID
     * @param process_status 处理状态
     * @return 更新的行数
     */
    @Update("""
        UPDATE ai_draw
        SET process_status = #{process_status}
        WHERE id = #{id}
    """)
    int updateProcessStatus(@Param("id") Long id,
                           @Param("process_status") Integer process_status);
}
