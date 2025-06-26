package com.xinyirun.scm.core.system.mapper.sys.config.dict;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictTypeEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeExportVo;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictTypeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 字典类型表、字典主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface SDictTypeMapper extends BaseMapper<SDictTypeEntity> {
    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from s_dict_type t "
        + "  where true "
        + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "    and (t.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null) "
        + "      ")
    IPage<SDictTypeEntity> selectPage(Page page, @Param("p1") SDictTypeVo searchCondition );

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from s_dict_type t "
        + "  where true "
        + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
        + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
        + "    and (t.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null) "
        + "      ")
    List<SDictTypeEntity> select(@Param("p1") SDictTypeVo searchCondition );

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
        + " select t.* "
        + "   from s_dict_type t "
        + "  where t.id in "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
        + "         #{item.id}  "
        + "        </foreach>    "
        + "  </script>    ")
    List<SDictTypeVo> selectIdsIn(@Param("p1") List<SDictTypeVo> searchCondition );

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from s_dict_type t "
        + "  where true "
        + "    and t.code =  #{p1}"
        + "      ")
    List<SDictTypeVo> selectByCode(@Param("p1") String code);

    /**
     * 部分导出
     * @param searchConditionList
     * @return
     */
    @Select("   <script>   "
            + " select t.name,                                                                                          "
            + "        t.code,                                                                                          "
            + "        t.descr,                                                                                         "
            + "        if(t.is_del, '已删除', '未删除') is_del,                                                            "
            + "        t.u_time,                                                                                         "
            + "       @row_num:= @row_num+ 1 as no                                      "
            + "   from s_dict_type t "
            + "   ,(select @row_num:=0) t3                                              "
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<SDictTypeExportVo> selectListExport(@Param("p1") List<SDictTypeVo> searchConditionList);


    /**
     * 全部导出
     * @param searchCondition
     * @return
     */
    @Select("    "
            + " select t.name,                                                                                          "
            + "        t.code,                                                                                          "
            + "        t.descr,                                                                                         "
            + "        if(t.is_del, '已删除', '未删除') is_del,                                                            "
            + "        t.u_time,                                                                                         "
            + "       @row_num:= @row_num+ 1 as no                                                                      "
            + "   from s_dict_type t "
            + "   ,(select @row_num:=0) t3                                              "
            + "  where true "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null) "
            + "      ")
    List<SDictTypeExportVo> selectAllExport(@Param("p1") SDictTypeVo searchCondition);

}
