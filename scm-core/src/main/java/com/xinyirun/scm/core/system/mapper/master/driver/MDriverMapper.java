package com.xinyirun.scm.core.system.mapper.master.driver;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.driver.MDriverEntity;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverExportVo;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MDriverMapper extends BaseMapper<MDriverEntity> {
    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name                                                                            "
            + "       FROM                                                                                              "
            + "  	       m_driver t                                                                                   "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "                                                                                                         "
            ;

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                                             "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                "
            + "    and (t.id_card like CONCAT ('%',#{p1.id_card,jdbcType=VARCHAR},'%') or #{p1.id_card,jdbcType=VARCHAR} is null or #{p1.id_card,jdbcType=VARCHAR} = '') "
            + "    and (t.is_del = #{p1.is_del,jdbcType=BOOLEAN} or #{p1.is_del,jdbcType=BOOLEAN} is null)                              "
            + "      ")
    IPage<MDriverVo> selectPage(Page page, @Param("p1") MDriverVo searchCondition);

    /**
     * 查询司机详情
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                           "
            + "      ")
    MDriverVo getDetail(@Param("p1") MDriverVo searchCondition);

    /**
     * 身份证号查询司机
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.is_del = 0)                                                                                   "
            + "    and (t.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "    and t.id_card =  #{p1.id_card,jdbcType=VARCHAR}                                                      "
            + "      ")
    List<MDriverEntity> selectByIdCard( @Param("p1") MDriverVo searchCondition);

    /**
     * 手机号查询司机
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.is_del = 0)                                                                                   "
            + "    and (t.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "    and t.mobile_phone =  #{p1.mobile_phone,jdbcType=VARCHAR}                                            "
            + "      ")
    List<MDriverEntity> selectByPhone(@Param("p1") MDriverVo searchCondition);

    /**
     * id查询司机
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.id =  #{p1,jdbcType=VARCHAR}  "
            + "      ")
    MDriverVo selectId(@Param("p1") Integer id);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + common_select
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<MDriverEntity> selectIdsIn(@Param("p1") List<MDriverVo> searchCondition);

    @Select("   <script>   "
            + "     SELECT                                                                                              "
            + "			   @row_num := @row_num + 1 AS no,                                                              "
            + "            t.code,                                                                                      "
            + "            t.name,                                                                                      "
            + "            t.mobile_phone,                                                                              "
            + "            t.id_card,                                                                                   "
//            + "            t3.url id_card_front_url,                                                                    "
//            + "            t4.url id_card_back_url,                                                                     "
//            + "            t5.url driver_license_url,                                                                   "
            + "            if(t.is_del, '已删除', '未删除') is_delete,                                                     "
            + "            t.c_time,                                                                                    "
            + "            t.u_time,                                                                                    "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name                                                                            "
            + "       FROM                                                                                              "
            + "  	       m_driver t                                                                                   "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
//            + "  LEFT JOIN s_file_info t3 ON t.id_card_front = t3.f_id                                                  "
//            + "  LEFT JOIN s_file_info t4 ON t.id_card_back = t4.f_id                                                   "
//            + "  LEFT JOIN s_file_info t5 ON t.driver_license = t5.f_id                                                 "
            + "  ,( SELECT @row_num := 0 ) t6                                                                           "
            + "  where true                                                                                             "
            + "  <if test='p1.ids != null and p1.ids.length != 0'>                                                      "
            + "    and t.id in                                                                                          "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>         "
            + "         #{item,jdbcType=INTEGER}                                                                        "
            + "        </foreach>                                                                                       "
            + "  </if>                                                                                                  "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)"
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)"
            + "    and (t.id_card like CONCAT ('%',#{p1.id_card,jdbcType=VARCHAR},'%') or #{p1.id_card,jdbcType=VARCHAR} is null or #{p1.id_card,jdbcType=VARCHAR} = '') "
            + "    and (t.is_del = #{p1.is_del,jdbcType=BOOLEAN} or #{p1.is_del,jdbcType=BOOLEAN} is null)              "
            + "  </script>    ")
    List<MDriverExportVo> selectExportList(@Param("p1") MDriverVo param);
}
