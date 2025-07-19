package com.xinyirun.scm.core.system.mapper.business.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.wms.warehouse.BWarehouseGroupEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 仓库组一级分类 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-01-30
 */
@Repository
public interface BWarehouseGroupMapper extends BaseMapper<BWarehouseGroupEntity> {

    String common_select = "  "
            + "     SELECT                                                                                                                                  "
            + "            t1.id,                                                                                                                           "
            + "            t1.name,                                                                                                                         "
            + "            t1.code,                                                                                                                         "
            + "            t1.short_name,                                                                                                                   "
            + "            t1.type,                                                                                                                         "
            + "            t5.label type_name,                                                                                                              "
            + "            t1.c_time,                                                                                                                       "
            + "            t1.u_time,                                                                                                                       "
            + "            t2.name as c_name,                                                                                                               "
            + "            t3.name as u_name,                                                                                                               "
            + "            t4.warehouse_count                                                                                                               "
            + "       FROM                                                                                                                                  "
            + "  	       b_warehouse_group t1                                                                                                             "
            + "  LEFT JOIN m_staff t2 ON t1.c_id = t2.id                                                                                                    "
            + "  LEFT JOIN m_staff t3 ON t1.u_id = t3.id                                                                                                    "
            + "     left join (                                                                                                                             "
            + "                  select count(1) warehouse_count,                                                                                           "
            + "                         subt.warehouse_group_id                                                                                             "
            + "                    from b_warehouse_group_relation subt                                                                                     "
            + "                group by subt.warehouse_group_id                                                                                             "
            + "                )  t4 on t4.warehouse_group_id = t1.id                                                                                       "
            + "    LEFT JOIN v_dict_info AS t5 ON t5.code = '" + DictConstant.DICT_B_WAREHOUSE_GROUP + "' and t5.dict_value = t1.type                       "
            + "        "
            ;

    /**
     * 页面查询列表
     * @param page
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (concat(t1.name,t1.code,t1.short_name,t1.name_pinyin,t1.short_name_pinyin,t1.name_pinyin_abbr,t1.short_name_pinyin_abbr) like concat('%',#{p1.keyword,jdbcType=VARCHAR},'%') or #{p1.keyword,jdbcType=VARCHAR} is null)                                                                                                                                                                          "
            + "      ")
    IPage<BWarehouseGroupVo> selectPage(Page page, @Param("p1") BWarehouseGroupVo vo);

    /**
     * 页面查询列表
     * @param page
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (concat(t1.name,t1.code,t1.short_name,t1.name_pinyin,t1.short_name_pinyin,t1.name_pinyin_abbr,t1.short_name_pinyin_abbr) like concat('%',#{p1.keyword,jdbcType=VARCHAR},'%') or #{p1.keyword,jdbcType=VARCHAR} is null)                                                                                                                                                                          "
            + "      ")
    List<BWarehouseGroupVo> selectList(@Param("p1") BWarehouseGroupVo vo);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t1.name =  #{p1,jdbcType=VARCHAR}   "
            + "    and t1.type =  #{p2,jdbcType=VARCHAR}   "
            + "      ")
    List<BWarehouseGroupVo> selectByName(@Param("p1") String name, @Param("p2") String type);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t1.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    List<BWarehouseGroupVo> selectByCode(@Param("p1") String code);

    /**
     * 按条件获取所有数据，没有分页
     * @param shortName
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t1.short_name =  #{p1,jdbcType=VARCHAR}  "
            + "    and t1.type =  #{p2,jdbcType=VARCHAR}   "
            + "      ")
    List<BWarehouseGroupVo> selectByShortName(@Param("p1") String shortName, @Param("p2") String type);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t1.id =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    BWarehouseGroupVo selectDataById(@Param("p1") int id);

    /**
     * 按仓库分组id查询仓库分组关系表中是否存在数据
     * @param id
     * @return
     */
    @Select("    "
            + " select count(1) c from b_warehouse_group_relation t1                                                    "
            + "  where true                                                                                             "
            + "    and t1.warehouse_group_id =  #{p1,jdbcType=VARCHAR}                                                  "
            + "      ")
    Integer selectCountByGroupId(@Param("p1") int id);
}
