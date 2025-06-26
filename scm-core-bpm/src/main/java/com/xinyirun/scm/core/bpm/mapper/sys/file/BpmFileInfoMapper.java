package com.xinyirun.scm.core.bpm.mapper.sys.file;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.sys.file.SBackupLogVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 附件详情 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface BpmFileInfoMapper extends BaseMapper<SFileInfoEntity> {

    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name,                                                                           "
            + "            t4.login_name as c_phone,                                                                    "
            + "            t5.login_name as u_phone                                                                     "
            + "       FROM                                                                                              "
            + "  	       s_file_info t                                                                                "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN s_file t3 ON t3.id = t.f_id                                                                  "
            + "  LEFT JOIN m_user t4 ON t4.staff_id = t1.id                                                             "
            + "  LEFT JOIN m_user t5 ON t5.staff_id = t2.id                                                             "
            + "                                                                                                         "
            ;

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t3.id =  #{p1.f_id,jdbcType=INTEGER} or #{p1.f_id,jdbcType=INTEGER} is null) "
            + "      ")
    List<SFileInfoVo> selectLists(SFileInfoVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1}"
            + "      ")
    SFileInfoVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.f_id =  #{p1}"
            + "      ")
    SFileInfoEntity selectFIdEntity(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.f_id =  #{p1}"
            + "      ")
    SFileInfoVo selectFId(@Param("p1") int id);


    /**
     * 查询需要备份数据
     */
    @Select(" <script>   "
            + "		SELECT                                                                                              "
            + "			t1.id,                                                                                          "
            + "			t1.url source_file_url,                                                                         "
            + "			t1.file_name,                                                                                   "
            + "			t1.file_size source_file_size                                                                   "
            + "		FROM                                                                                                "
            + "			s_file_info t1                                                                                  "
            + "		WHERE TRUE                                                                                          "
            + "		AND	DATE_SUB( CURDATE( ), INTERVAL #{p1,jdbcType=INTEGER} DAY ) &gt;= date( t1.file_date )          "
            + "		AND	t1.status is null                                                                               "
            + "   <if test='p2 != null ' >                                                                              "
            + "     limit  #{p2,jdbcType=INTEGER}                                                                       "
            + "   </if>                                                                                                 "
            + "    </script>  ")
    List<SBackupLogVo> selectBackupFileList(@Param("p1") Integer days, @Param("p2") Integer backup_now_count);

    /**
     * 查询需要备份数据
     */
    @Update("  <script>    "
            + "		UPDATE                                                                                              "
            + "			s_file_info t1                                                                                  "
            + "		SET                                                                                                 "
            + "   <if test='p1.url != null ' >                                                                          "
            + "			t1.url =  #{p1.url,jdbcType=VARCHAR} ,                                                          "
            + "   </if>                                                                                                 "
            + "   <if test='p1.type != null ' >                                                                         "
            + "			t1.type = #{p1.type,jdbcType=VARCHAR} ,                                                         "
            + "   </if>                                                                                                 "
            + "   <if test='p1.backup_time != null ' >                                                                  "
            + "			t1.backup_time = #{p1.backup_time,jdbcType=VARCHAR} ,                                           "
            + "   </if>                                                                                                 "
//            + "			t1.u_time = #{p1.u_time,jdbcType=DATE},                                                         "
            + "			t1.status = #{p1.status,jdbcType=INTEGER} ,                                                     "
            + "			t1.remark = #{p1.remark,jdbcType=VARCHAR}                                                       "
            + "		WHERE  t1.id =  #{p1.id,jdbcType=INTEGER}                                                           "
            + "   </script>    ")
    void update(@Param("p1") SFileInfoEntity entity);


}
