package com.xinyirun.scm.core.system.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MCustomerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;
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
public interface MCustomerMapper extends BaseMapper<MCustomerEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.label as scope_name,                                          "
            + "            t4.label as source_name,                                          "
            + "            t5.label as type_name,                                          "
            + "            t6.label as mold_name,                                          "
            + "            concat(t.province,t.city,t.district) cascader_areas,          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_customer t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN s_dict_data t3 ON t.scope = t3.dict_value   and t3.dict_type_id=5   "
            + "  LEFT JOIN s_dict_data t4 ON t.source = t4.dict_value   and t4.dict_type_id=6   "
            + "  LEFT JOIN s_dict_data t5 ON t.type = t5.dict_value   and t5.dict_type_id=7   "
            + "  LEFT JOIN s_dict_data t6 ON t.mold = t6.dict_value   and t6.dict_type_id=8   "
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
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null)                              "
            + "    and (CONCAT(t.name,t.short_name,t.name_pinyin,t.short_name_pinyin) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') "
            + "           or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '') "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    IPage<MCustomerVo> selectPage(Page page, @Param("p1") MCustomerVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "      ")
    List<MCustomerVo> selectList(@Param("p1") MCustomerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "    and t.name =  #{p1.name,jdbcType=VARCHAR}                                                            "
            + "    and t.enable =  true                                                                                 "
            + "      ")
    List<MCustomerEntity> selectByName(@Param("p1") MCustomerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "    and t.code =  #{p1.code,jdbcType=VARCHAR}                                                            "
            + "    and t.enable =  true                                                                                 "
            + "      ")
    List<MCustomerEntity> selectByCode(@Param("p1") MCustomerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                         "
            + "    and t.credit_no =  #{p1.credit_no,jdbcType=VARCHAR}                                                  "
            + "    and t.enable =  true                                                                                 "
            + "      ")
    List<MCustomerEntity> selectByCreditCode(@Param("p1") MCustomerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.credit_no =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    MCustomerVo selectByCreditNo(@Param("p1") String credit_no);

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
    List<MCustomerEntity> selectIdsIn(@Param("p1") List<MCustomerVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MCustomerVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.code =  #{p1.code,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByCustomerCode(@Param("p1") ApiCustomerVo vo);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.credit_no =  #{p1.credit_no,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByCustomerCreditNo(@Param("p1") ApiCustomerVo vo);

    @Select({"<script>                                                                                                 "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.code,                                                                                     "
            + "            t.name,                                                                                     "
            + "            t.short_name,                                                                               "
            + "            t.superior_id,                                                                              "
            + "            t.contact_number,                                                                           "
            + "            if(t.enable, '启用', '禁用') enable,                                                          "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_customer t                                                                                "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + " ,(select @row_num:=0) t2                                                                               "
            + "  where true                                                                                            "
            + "    and (CONCAT(t.name,t.short_name,t.name_pinyin,t.short_name_pinyin) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') "
            + "           or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '') "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)"
            + "  <if test='p1.ids != null and p1.ids.size != 0'>                                                       "
            + "    and t.id in                                                                                         "
            + "      <foreach collection ='p1.ids' item='item' index='index' open='(' close=')' separator=','>         "
            + "          #{item}                                                                                       "
            + "       </foreach>                                                                                       "
            + "   </if>                                                                                                "
            + "  </script>                                                                                             "
    })
    List<MCustomerExcelVo> exportList(@Param("p1") MCustomerVo searchConditionList);

}
