package com.xinyirun.scm.core.app.mapper.sys.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.sys.config.AppSConfigVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 字典数据表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface AppSConfigMapper extends BaseMapper<SConfigEntity> {

    String common_select = "  "
        + "     SELECT                                                             "
        + "            t.*,                                                        "
        + "            t1.name as c_name,                                          "
        + "            t2.name as u_name                                           "
        + "       FROM                                                             "
        + "  	       s_config t                                                  "
        + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
        + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
        + "                                                                        "
        ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + common_select
        + "  where true "
        + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "    and (t.config_key  like CONCAT ('%',#{p1.config_key,jdbcType=VARCHAR},'%') or #{p1.config_key,jdbcType=VARCHAR} is null) "
        + "    and (t.value  like CONCAT ('%',#{p1.value,jdbcType=VARCHAR},'%') or #{p1.value,jdbcType=VARCHAR} is null) "
        + "      ")
    IPage<AppSConfigVo> selectPage(Page page, @Param("p1") AppSConfigVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + common_select
        + "  where true "
        + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "    and (t.config_key  like CONCAT ('%',#{p1.config_key,jdbcType=VARCHAR},'%') or #{p1.config_key,jdbcType=VARCHAR} is null) "
        + "    and (t.value  like CONCAT ('%',#{p1.value,jdbcType=VARCHAR},'%') or #{p1.value,jdbcType=VARCHAR} is null) "
        + "      ")
    List<AppSConfigVo> select(@Param("p1") AppSConfigVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
        + common_select
        + "  where t.id in "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
        + "         #{item.id}  "
        + "        </foreach>    "
        + "  </script>    ")
    List<SConfigEntity> selectIdsIn(@Param("p1") List<AppSConfigVo> searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
        + common_select
        + "  where t.id in "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
        + "         #{item.id}  "
        + "        </foreach>    "
        + "  </script>    ")
    List<AppSConfigVo> selectIdsInForExport(@Param("p1") List<AppSConfigVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
        + common_select
        + "  where true "
        + "    and t.name =  #{p1}"
        + "      ")
    List<SConfigEntity> selectByName(@Param("p1") String name);

    /**
     * 按条件获取所有数据，没有分页
     * @param config_key
     * @return
     */
    @Select("    "
        + common_select
        + "  where true "
        + "    and t.config_key =  #{p1}"
        + "      ")
    AppSConfigVo selectByKey(@Param("p1") String config_key);

    /**
     * 按条件获取所有数据，没有分页
     * @param config_key
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.config_key =  #{p1}"
            + "      ")
    AppSConfigVo getByKey(@Param("p1") String config_key);

    /**
     * 按条件获取所有数据，没有分页
     * @param config_key
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.config_key =  #{p1}"
            + "      ")
    List<SConfigEntity> selectListByKey(@Param("p1") String config_key);

    /**
     * 按条件获取所有数据，没有分页
     * @param value
     * @return
     */
    @Select("    "
        + common_select
        + "  where true "
        + "    and t.value =  #{p1}"
        + "      ")
    List<SConfigEntity> selectByValue(@Param("p1") String value);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
        + common_select
        + "  where t.id =  #{p1}"
        + "      ")
    AppSConfigVo selectId(@Param("p1") Long id);
}
