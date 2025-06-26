package com.xinyirun.scm.core.whapp.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.app.vo.master.customer.AppMCustomerVo;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.whapp.vo.master.customer.WhAppMCustomerVo;
import com.xinyirun.scm.common.constant.DictConstant;
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
public interface WhAppMCustomerMapper extends BaseMapper<MCustomerEntity> {
    String common_select = "  "
            + "     SELECT                                                             																"
            + "            t.*,                                                        																"
            + "            t3.label as scope_name,                                     																"
            + "            t4.label as source_name,                                    																"
            + "            t5.label as type_name,                                      																"
            + "            t6.label as mold_name,                                      																"
            + "            t7.label as status_name,                                      															"
            + "            t.status as status,                                      															"
            + "            concat(t.province,t.city,t.district) cascader_areas,        																"
            + "	           t8.type_name as type_name,                                                                                               "
            + "            t1.name as c_name,                                          																"
            + "            t2.name as u_name                                           																"
            + "       FROM                                                             																"
            + "  	       m_customer t                                                																"
            + "   LEFT JOIN m_staff t1 ON t.c_id = t1.id                                															"
            + "   LEFT JOIN m_staff t2 ON t.u_id = t2.id                                															"
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       	"
            + "              where tab2.code = '" + DictConstant.DICT_M_CUSTOMER_SCOPE + "')t3 on t3.dict_value = t.scope                         	"
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       	"
            + "              where tab2.code = '" + DictConstant.DICT_M_CUSTOMER_SOURCE + "')t4 on t4.dict_value = t.source                       	"
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       	"
            + "              where tab2.code = '" + DictConstant.DICT_M_CUSTOMER_TYPE + "')t5 on t5.dict_value = t.type                         	"
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                     	"
            + "              where tab2.code = '" + DictConstant.DICT_M_CUSTOMER_MOLD + "')t6 on t6.dict_value = t.mold                         	"
            + "   LEFT JOIN (select tab1.* from s_dict_data tab1                      	                                                            "
            + "              where tab1.code = '" + DictConstant.DICT_M_CUSTOMER_STATUS + "')t7 on t7.dict_value = t.status                   "
            + "	  LEFT JOIN (select t1.customer_id, GROUP_CONCAT(t2.label ORDER BY t1.customer_id SEPARATOR ', ') as type_name                      "
            + "				from m_customer_type t1 left join s_dict_data t2 on t2.code = '" + DictConstant.DICT_M_CUSTOMER_TYPE +"'                "
            + "                  AND t2.dict_value = t1.type GROUP BY t1.customer_id ) 	AS t8 on t.id = t8.customer_id                              "
            ;

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                                             "
            + "    and t.enable = true                                                                                                  "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                "
            + "    and (CONCAT(t.name,t.short_name,t.credit_no)                                                                         "
            + "           like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%')                                         "
            + "            or #{p1.combine_search_condition,jdbcType=VARCHAR} is null)                                                  "
            + "	ORDER BY                                                                                                                "
            + "	  CASE                                                                                                                  "
            + "    WHEN t.top_time IS NOT NULL THEN 1                                                                                   "
            + "    ELSE 2                                                                                                               "
            + "  END,                                                                                                                   "
            + "	t.top_time desc                                                                                                         "
            + "    limit 20 "
            + "      ")
    List<WhAppMCustomerVo> selectByList( @Param("p1") WhAppMCustomerVo searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            + "  <script>     "
            + common_select
            + "  where true                                                                                                             "
            + "    and t.enable = true                                                                                                  "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                "
            + "    and (t.blacklist = #{p1.blacklist,jdbcType=BOOLEAN} or #{p1.blacklist,jdbcType=BOOLEAN} is null)                     "
            + "    and (t.credit_no like CONCAT ('%',#{p1.credit_no,jdbcType=VARCHAR},'%') or #{p1.credit_no,jdbcType=VARCHAR}          "
            + "             is null or #{p1.credit_no,jdbcType=VARCHAR} = '')                                                           "
            + "    and (t.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null             "
            + "       or #{p1.status,jdbcType=VARCHAR} = '')                                                                      "
            + "    and (CONCAT(t.name,t.short_name,t.credit_no)                                                                         "
            + "           like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%')                                         "
            + "           or #{p1.combine_search_condition,jdbcType=VARCHAR} is null                                                    "
            + "           or #{p1.combine_search_condition,jdbcType=VARCHAR} = '')                                                      "
            + "   <if test='p1.types != null and p1.types.length!=0' >                                                                  "
            + "    and exists (select 1 from m_customer_type tab1 where tab1.customer_id = t.id and tab1.type IN                        "
            + "        <foreach collection='p1.types' item='item' index='index' open='(' separator=',' close=')'>                       "
            + "         #{item}                                                                                                         "
            + "        </foreach>)                                                                                                      "
            + "   </if>                                                                                                                 "
            + " and (date_format(t.c_time, '%Y-%m-%d') &gt;= date_format(#{p1.c_time_start}, '%Y-%m-%d') or #{p1.c_time_start} is null) "
            + " and (date_format(t.c_time, '%Y-%m-%d') &lt;= date_format(#{p1.c_time_end}, '%Y-%m-%d') or #{p1.c_time_end} is null)     "
            // 排序字段作为参数传递
            + "    ORDER BY ${p2} ${p3} ${p4}                                                                                           "
            + "     limit ${(p1.pageCondition.current-1)*p1.pageCondition.size},  #{p1.pageCondition.size}                              "
            + "  </script>     "
            + "      ")
    List<WhAppMCustomerVo> selectPage(@Param("p1") WhAppMCustomerVo searchCondition,@Param("p2") String sort,@Param("p3") String sortType,@Param("p4") String defaultSort);

    /**
     * 查询承运商详情
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)                           "
            + "      ")
    WhAppMCustomerVo getDetail(@Param("p1") WhAppMCustomerVo searchCondition);


    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.enable = 1)                                                                                   "
            + "    and t.name =  #{p1,jdbcType=VARCHAR}                                                                 "
            + "      ")
    List<MCustomerEntity> selectByName(@Param("p1") String name);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.enable = 1)                                                                                   "
            + "    and t.name =  #{p1.name,jdbcType=VARCHAR}                                                            "
            + "    and t.id !=  #{p1.id,jdbcType=INTEGER}                                                               "
            + "      ")
    List<MCustomerEntity> selectByNameId(@Param("p1") WhAppMCustomerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.enable = 1)                                                                                   "
            + "    and t.credit_no =  #{p1.credit_no,jdbcType=VARCHAR}                                                  "
            + "    and t.id !=  #{p1.id,jdbcType=INTEGER}                                                               "
            + "      ")
    List<MCustomerEntity> selectByCreditNoId(@Param("p1") WhAppMCustomerVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and (t.enable = 1)                                                                                   "
            + "    and t.Credit_No =  #{p1,jdbcType=VARCHAR}                                                            "
            + "      ")
    List<MCustomerEntity> selectByCreditNo(@Param("p1") String CreditNo);

    /**
     * 没有分页，按id筛选条件
     */
    @Select("   <script>   "
            + common_select
            + "  where t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<MCustomerEntity> selectIdsIn(@Param("p1") List<WhAppMCustomerVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    WhAppMCustomerVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.code =  #{p1.code,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByCodeAppCode(@Param("p1") WhAppMCustomerVo vo);

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
    List<MCustomerEntity> selectByName(@Param("p1") MEnterpriseVo searchCondition);

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
    List<MCustomerEntity> selectByCreditCode(@Param("p1") MEnterpriseVo searchCondition);
}
