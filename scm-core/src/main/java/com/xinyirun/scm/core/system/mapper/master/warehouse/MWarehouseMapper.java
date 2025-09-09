package com.xinyirun.scm.core.system.mapper.master.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.master.warehouse.ApiWarehouseVo;
import com.xinyirun.scm.bean.entity.master.warehouse.MWarehouseEntity;
import com.xinyirun.scm.bean.system.bo.inventory.warehouse.MBLWBo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupOperationVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseTransferVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.*;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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
public interface MWarehouseMapper extends BaseMapper<MWarehouseEntity> {

    String common_select = "  "
            + "     SELECT                                                                                                                  "
            + "            t.*,                                                                                                             "
            + "            concat(t.province,t.city,t.district) cascader_areas,                                                             "
            + "            concat(t6.province_name, '/', t6.city_name, '/', t6.area_name) cascader_areas_name,                              "
            + "            t1.name as c_name,                                                                                               "
            + "            t2.name as u_name,                                                                                               "
            + "            t3.label as zone_name,                                                                                           "
            + "            t4.label as warehouse_type_name,                                                                                 "
            + "            t7.name as charge_company_name,                                                                                  "
            + "            t8.name as operate_company_name,                                                                                 "
            + "            ifnull(t5.warehouse_group_count,0) warehouse_group_count                                                         "
            + "       FROM                                                                                                                  "
            + "  	       m_warehouse t                                                                                                    "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                     "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                     "
            + "  LEFT JOIN v_dict_info t3 ON t3.dict_value = t.zone and t3.code = '" + DictConstant.DICT_M_WAREHOUSE_ZONE + "'              "
            + "  LEFT JOIN v_dict_info t4 ON t4.dict_value = t.warehouse_type and t4.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "'    "
            + "  LEFT JOIN v_areas_info t6 ON t6.province_code = t.province and t6.city_code = t.city and t6.area_code = t.district         "
            + "  LEFT JOIN m_enterprise t7 ON t.charge_company_id = t7.id                                                                   "
            + "  LEFT JOIN m_enterprise t8 ON t.operate_company_id = t8.id                                                                  "
            + "     left join (                                                                                                             "
            + "                  select count(1) warehouse_group_count,                                                                     "
            + "                         subt.warehouse_id                                                                                   "
            + "                    from b_warehouse_group_relation subt                                                                     "
            + "                group by subt.warehouse_id                                                                                   "
            + "                )  t5 on t5.warehouse_id = t.id                                                                              "
            + "      where t.is_del = false                                                                                                 "
            ;

    String export_select = "  "
            + "     SELECT                                                                                                                  "
            + "            t.code,                                                                                                          "
            + "            @row_num:= @row_num+ 1 as no,                                                                                    "
            + "            t.name,                                                                                                          "
            + "            concat(t6.province_name, '/', t6.city_name, '/', t6.area_name) cascader_areas_name,                              "
            + "            t.short_name,                                                                                                    "
            + "            t7.name as charge_company_name,                                                                                  "
            + "            t8.name as operate_company_name,                                                                                 "
            + "            t.contact_person,                                                                                                "
            + "            t.mobile_phone,                                                                                                  "
            + "            t.address,                                                                                                       "
            + "            t.warehouse_capacity,                                                                                            "
            + "            t.area,                                                                                                          "
            + "            if(t.enable, '是', '否') enable,                                                                                  "
            + "            t.c_time,                                                                                                        "
            + "            t.u_time,                                                                                                        "
            + "            t1.name as c_name,                                                                                               "
            + "            t2.name as u_name,                                                                                               "
            + "            t3.label as zone_name,                                                                                           "
            + "            t4.label as warehouse_type_name                                                                                  "
            + "       FROM                                                                                                                  "
            + "  	       m_warehouse t                                                                                                    "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                                     "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                                     "
            + "  LEFT JOIN v_dict_info t3 ON t3.dict_value = t.zone and t3.code = '" + DictConstant.DICT_M_WAREHOUSE_ZONE + "'              "
            + "  LEFT JOIN v_dict_info t4 ON t4.dict_value = t.warehouse_type and t4.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "'    "
            + "  LEFT JOIN v_areas_info t6 ON t6.province_code = t.province and t6.city_code = t.city and t6.area_code = t.district         "
            + "  LEFT JOIN m_enterprise t7 ON t.charge_company_id = t7.id                                                                   "
            + "  LEFT JOIN m_enterprise t8 ON t.operate_company_id = t8.id                                                                  "
            + " ,(select @row_num:=0) t5                                                                                                    "
            + "      where t.is_del = false                                                                                                 "

            ;

    // 导出专用查询SQL，支持完整字段和动态排序（按照岗位模式设计）
    String selectExportList_select = "  "
            + "     SELECT                                                                                                                  "
            + "            @row_num:= @row_num+ 1 as no,                                                                                    "
            + "            t1.code,                                                                                                         "
            + "            t1.name,                                                                                                         "
            + "            t1.short_name,                                                                                                   "
            + "            t4.label as warehouse_type_name,                                                                                 "
            + "            t7.name as charge_company_name,                                                                                  "
            + "            t8.name as operate_company_name,                                                                                 "
            + "            t1.contact_person,                                                                                               "
            + "            t1.mobile_phone,                                                                                                 "
            + "            t6.province_name as province,                                                                                    "
            + "            t6.city_name as city,                                                                                            "
            + "            t6.area_name as district,                                                                                        "
            + "            t1.address,                                                                                                      "
            + "            concat(t6.province_name, '/', t6.city_name, '/', t6.area_name) cascader_areas_name,                              "
            + "            t3.label as zone_name,                                                                                           "
            + "            t1.area,                                                                                                         "
            + "            t1.warehouse_capacity,                                                                                           "
            + "            if(t1.enable, '启用', '停用') enable_status,                                                                       "
            + "            t2.name as c_name,                                                                                               "
            + "            t1.c_time,                                                                                                       "
            + "            t9.name as u_name,                                                                                               "
            + "            t1.u_time                                                                                                        "
            + "       FROM                                                                                                                  "
            + "  	       m_warehouse t1                                                                                                   "
            + "  LEFT JOIN m_staff t2 ON t1.c_id = t2.id                                                                                    "
            + "  LEFT JOIN v_dict_info t3 ON t3.dict_value = t1.zone and t3.code = '" + DictConstant.DICT_M_WAREHOUSE_ZONE + "'           "
            + "  LEFT JOIN v_dict_info t4 ON t4.dict_value = t1.warehouse_type and t4.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "'  "
            + "  LEFT JOIN v_areas_info t6 ON t6.province_code = t1.province and t6.city_code = t1.city and t6.area_code = t1.district     "
            + "  LEFT JOIN m_enterprise t7 ON t1.charge_company_id = t7.id                                                                  "
            + "  LEFT JOIN m_enterprise t8 ON t1.operate_company_id = t8.id                                                                 "
            + "  LEFT JOIN m_staff t9 ON t1.u_id = t9.id                                                                                    "
            + " ,(select @row_num:=0) t5                                                                                                    "
            + "      where t1.is_del = false                                                                                                "

            ;

    /**
     * 页面查询列表
     * @param searchCondition 搜索条件
     * @return 仓库列表
     */
    @Select("    "
            + common_select
            + "    and (t.operate_company_id = #{p1.operate_company_id,jdbcType=INTEGER} or #{p1.operate_company_id,jdbcType=INTEGER} is null)                                                                                                                                       "
            + "    and (t.operate_company_id = #{p1.charge_company_id,jdbcType=INTEGER} or #{p1.charge_company_id,jdbcType=INTEGER} is null)                                                                                                                                         "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null)                                                                                                                                                                           "
//            + "    and (t.name = #{p1.name,jdbcType=BOOLEAN} or #{p1.name,jdbcType=VARCHAR} is null)                                                                                                                                                                               "
            + "    and (CONCAT (ifnull(t.code,''),ifnull(t.name,''),ifnull(t.short_name,''),ifnull(t.name_pinyin,''),ifnull(t.short_name_pinyin,''),ifnull(t.name_pinyin_initial,''),ifnull(t.short_name_pinyin_initial,'')) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '')        "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                                                                                                                             "
            + "    and (t.warehouse_type = #{p1.warehouse_type,jdbcType=VARCHAR} or #{p1.warehouse_type,jdbcType=VARCHAR} is null or #{p1.warehouse_type,jdbcType=VARCHAR} = '')"
            + "      ")
    IPage<MWarehouseVo> selectPage(Page page, @Param("p1") MWarehouseVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition 搜索条件
     * @return 仓库列表
     */
    @Select("    "
            + common_select
//            + "    and t.enable = 1                                          "
            + "    and (t.operate_company_id = #{p1.operate_company_id,jdbcType=INTEGER} or #{p1.operate_company_id,jdbcType=INTEGER} is null)      "
            + "    and (t.operate_company_id = #{p1.charge_company_id,jdbcType=INTEGER} or #{p1.charge_company_id,jdbcType=INTEGER} is null)        "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                            "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                            "
            + "    and (t.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                       "
            + "    and (t.enable = #{p1.enable} or #{p1.enable} is null)                                                                            "
            + "      ")
    List<MWarehouseVo> selectList(@Param("p1") MWarehouseVo searchCondition);

    /**
     * 仓库下拉
     * @param vo
     * @return
     */
    @Select("    "
            + common_select
            + "    and (t.code = #{p1.code,jdbcType=VARCHAR} or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.short_name like CONCAT ('%',#{p1.short_name,jdbcType=VARCHAR},'%') or #{p1.short_name,jdbcType=VARCHAR} is null) "
            + "    and (t.business_type like CONCAT ('%',#{p1.business_type,jdbcType=VARCHAR},'%') or #{p1.business_type,jdbcType=VARCHAR} is null) "
            + "      ")
    List<ApiWarehouseVo> getWarehouse(@Param("p1") ApiWarehouseVo vo);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "    and t.name =  #{p1}"
            + "    and (t.id <>  #{p2,jdbcType=INTEGER} or  #{p2,jdbcType=INTEGER} is null)      "
            + "      ")
    List<MWarehouseEntity> selectByName(@Param("p1") String name, @Param("p2") Integer id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "    and t.code =  #{p1,jdbcType=VARCHAR}"
            + "    and (t.id <>  #{p2,jdbcType=INTEGER} or  #{p2,jdbcType=INTEGER} is null)      "
            + "      ")
    List<MWarehouseEntity> selectByCode(@Param("p1") String name, @Param("p2") Integer id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "    and t.short_name =  #{p1,jdbcType=VARCHAR}"
            + "    and (t.id <>  #{p2,jdbcType=INTEGER} or  #{p2,jdbcType=INTEGER} is null)      "
            + "      ")
    List<MWarehouseEntity> selectByShortName(@Param("p1") String name, @Param("p2") Integer id);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("   <script>   "
            + common_select
            + "  and t.id in "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item.id,jdbcType=INTEGER}  "
            + "        </foreach>    "
            + "  </script>    ")
    List<MWarehouseEntity> selectIdsIn(@Param("p1") List<MWarehouseVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  and t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MWarehouseVo selectId(@Param("p1") int id);

    /**
     * 根据传入的仓库id，获取到相应的库区/库位
     * @param warehouse_id
     * @return
     */
    @Select(  "                                                                                                         "
            + "          SELECT                                                                                         "
            + "          	     t3.warehouse_id ,                                                                      "
            + "          	     t3.location_id ,                                                                       "
            + "          	     t3.id as bin_id ,                                                                      "
            + "          	     t1.name as warehouse_name,                                                             "
            + "          	     t1.code as warehouse_code,                                                             "
            + "          	     t1.short_name as warehouse_short_name,                                                 "
            + "          	     t2.name as location_name,                                                              "
            + "          	     t2.short_name as location_short_name,                                                  "
            + "          	     t3.name as bin_name                                                                    "
            + "          from                                                                                           "
            + "          	     m_warehouse t1                                                                         "
            + "          	     inner join m_location t2 on t2.warehouse_id = t1.id                                    "
            + "          	     inner join m_bin t3 on t3.location_id = t2.id  and t3.warehouse_id = t1.id             "
            + "          where                                                                                          "
            + "          	     t1.id =  #{p1,jdbcType=INTEGER}                                                        "
            + "            and   t2.enable = true                                                                       "
            + "            and   t3.enable = true                                                                       "

//            + "            and   t1.enable = true                                                                       "
//            + "            and   t1.enable_location = false                                                             "
//            + "            and   t1.enable_bin = false                                                                  "
            + "      ")
    MWarehouseLocationBinVo selectWarehouseLocationBin(@Param("p1")int warehouse_id);

    /**
     * 获取全部仓库组
     * @param vo
     * @return
     */
    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "             t1.id AS `key`,                                                                             "
            + "             t1.NAME AS label                                                                            "
            + "       FROM  m_warehouse t1                                                                              "
            + "      WHERE                                                                                              "
            + "            true                                                                                         "
            + "   order by  t1.name                                                                                     "
            + "                                                                                                         ")
    List<BWarehouseTransferVo> getAllWarehouseTransferList(@Param("p1") BWarehouseTransferVo vo);


    /**
     * 获取该仓库组下，全部仓库
     * @param condition
     * @return
     */
    @Select("                                                                                                          "
            + "     SELECT                                                                                             "
            + "             t1.warehouse_id AS `key`                                                                   "
            + "       FROM  b_warehouse_group_relation t1                                                          "
            + "  LEFT JOIN  m_warehouse t2 ON t1.warehouse_id = t2.id                                                  "
            + "      where  t1.warehouse_group_id = #{p1.warehouse_group_id,jdbcType=BIGINT}                           "
            + "                                                                                                        ")
    List<Integer> getWarehouseGroupOneTransferList(@Param("p1") BWarehouseTransferVo condition);

    /**
     * 获取该仓库组下，全部仓库
     * @param condition
     * @return
     */
    @Select("                                                                                                          "
            + "     SELECT                                                                                             "
            + "             t1.warehouse_id AS `key`                                                                   "
            + "       FROM  b_warehouse_group_three_relation t1                                                        "
            + "  LEFT JOIN  m_warehouse t2 ON t1.warehouse_id = t2.id                                                  "
            + "      where  t1.warehouse_group_id = #{p1.warehouse_group_id,jdbcType=BIGINT}                           "
            + "                                                                                                        ")
    List<Integer> getWarehouseGroupThreeTransferList(@Param("p1") BWarehouseTransferVo condition);

    /**
     * 获取要删除，仓库组1-仓库数据
     * @param bean
     * @return
     */
    @Select("  <script>        "
            + "       select t1.id ,                                                                                           "
            + "              t2.name as warehouse_name ,                                                                       "
            + "              t3.name as warehouse_group_name                                                                   "
            + "         from                                                                                                   "
            + "               b_warehouse_group_relation t1                                                                "
            + "    left join  m_warehouse t2 on t1.warehouse_id = t2.id                                                        "
            + "    left join  b_warehouse_group t3 on t3.id = t1.warehouse_group_id                                        "
            + "        where                                                                                                   "
            + "               t1.warehouse_group_id =  #{p1.warehouse_group_id,jdbcType=BIGINT}                                "
            + "   <if test='p1.group_warehouses != null and p1.group_warehouses.length!=0' >                                   "
            + "         and t1.warehouse_id not in                                                                             "
            + "        <foreach collection='p1.group_warehouses' item='item' index='index' open='(' separator=',' close=')'>   "
            + "         #{item}                                                                                                "
            + "        </foreach>                                                                                              "
            + "   </if>                                                                                                        "
            + "   </script>                                                                                                    ")
    List<BWarehouseGroupOperationVo> selectDeleteMemberOne(@Param("p1") BWarehouseTransferVo bean);

    /**
     * 获取要新增的仓库组1-仓库数据
     * @param bean
     * @return
     */
    @Select("  <script>                                                                                                        "
            + "       select  t1.id                                                                                            "
            + "         from  m_warehouse t1                                                                                   "
            + "        where  not exists (                                                                                     "
            + "                 select true                                                                                    "
            + "                   from b_warehouse_group_relation t2                                                       "
            + "                  where t2.warehouse_group_id = #{p1.warehouse_group_id,jdbcType=BIGINT}                        "
            + "                    and t1.id = t2.warehouse_id                                                                 "
            + "              )                                                                                                 "
            + "     <choose>                                                                                                   "
            + "       <when test='p1.group_warehouses != null and p1.group_warehouses.length!=0'>                              "
            + "           and t1.id in                                                                                         "
            + "          <foreach collection='p1.group_warehouses' item='item' index='index' open='(' separator=',' close=')'> "
            + "           #{item}                                                                                              "
            + "          </foreach>                                                                                            "
            + "       </when>                                                                                                  "
            + "       <otherwise>                                                                                              "
            + "           and false                                                                                            "
            + "       </otherwise>                                                                                             "
            + "     </choose>                                                                                                  "
            + "   </script>                                                                                                    ")
    List<BWarehouseGroupOperationVo> selectInsertMemberOne(@Param("p1") BWarehouseTransferVo bean);

    /**
     * 仓库导出 全部
     *
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    @Select({
            export_select
                    + "  and true "
                    + "    and (t.operate_company_id = #{p1.operate_company_id,jdbcType=INTEGER} or #{p1.operate_company_id,jdbcType=INTEGER} is null)                                                                                                                                       "
                    + "    and (t.operate_company_id = #{p1.charge_company_id,jdbcType=INTEGER} or #{p1.charge_company_id,jdbcType=INTEGER} is null)                                                                                                                                         "
                    + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null)                                                                                                                                                                           "
                    + "    and (CONCAT (ifnull(t.code,''),ifnull(t.name,''),ifnull(t.short_name,''),ifnull(t.name_pinyin,''),ifnull(t.short_name_pinyin,''),ifnull(t.name_pinyin_initial,''),ifnull(t.short_name_pinyin_initial,'')) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '')        "
                    + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                                                                                                                             "
                    + "    and (t.warehouse_type = #{p1.warehouse_type,jdbcType=VARCHAR} or #{p1.warehouse_type,jdbcType=VARCHAR} is null or #{p1.warehouse_type,jdbcType=VARCHAR} = '')"
    })
    List<MWarehouseExportVo> exportAll(@Param("p1") MWarehouseVo searchCondition);

    /**
     * 仓库导出 部分
     *
     * @param searchCondition 查询参数
     * @return List<MWarehouseExportVo>
     */
    @Select({
            "<script>"
                    + export_select
                    + "  and true "
                    + "   <if test='p1 != null and p1.size!=0' >                                                       "
                    + "    and t.id in                                                                                "
                    + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>    "
                    + "         #{item.id,jdbcType=INTEGER}                                                            "
                    + "        </foreach>                                                                              "
                    + "   </if>                                                                                        "
                    + " </script>"
    })
    List<MWarehouseExportVo> export(@Param("p1") List<MWarehouseVo> searchCondition);

    /**
     * 查询仓库三大件
     * @param warehouse_code 仓库编码
     * @return
     */
    @Select("                                                                                                           "
            + "     SELECT                                                                                              "
            + "            t.id warehouse_id,                                                                           "
            + "            t.code as warehouse_code,                                                                    "
            + "            t1.id as location_id,                                                                        "
            + "            t1.code as location_code,                                                                    "
            + "            t2.id as bin_id,                                                                             "
            + "            t2.code as bin_code                                                                          "
            + "       FROM m_warehouse t                                                                                "
            + "  LEFT JOIN m_location  t1 ON t1.warehouse_id = t.id and t1.enable= '"+ SystemConstants.ENABLE_TRUE + "' "
            + "  LEFT JOIN m_bin t2 ON t2.warehouse_id = t.id and t2.location_id=t1.id and t2.enable= '"+ SystemConstants.ENABLE_TRUE + "'"
            + "     where true                                                                                          "
            + "     and t.enable = '"+ SystemConstants.ENABLE_TRUE +"'                                                  "
            + "     and t.enable_bin = '"+ SystemConstants.ENABLE_FALSE +"'                                             "
            + "     and t.code =  #{p1,jdbcType=VARCHAR}                                                                "
            + "     ")
    List<MBLWBo> selectBLWByCode(@Param("p1") String warehouse_code);

    /**
     * 获取穿梭框数据
     * @param searchCondition
     * @return list
     */
    @Select("                                                                                                           "
            + "      select t1.id AS `key`,                                                                             "
            + "             t1.name AS label                                                                            "
            + "        from b_warehouse_group t1                                                                        "
            + "       where true                                                                                        "
            + "    order by t1.name                                                                                     "
            + "            ")
    List<MWGroupTransferVo> getAllWarehouseGroupTransferList(@Param("p1") MWGroupTransferVo searchCondition);

    /**
     * 获取该仓库组下，全部仓库组
     */
    @Select("                                                                                                           "
            + "       	 select t3.id                                                                                   "
            + "       	   from b_warehouse_group_relation t1                                                           "
            + "        inner join m_warehouse t2                                                                        "
            + "                on t1.warehouse_id = t2.id                                                               "
            + "        inner join b_warehouse_group t3                                                                  "
            + "                on t1.warehouse_group_id = t3.id                                                         "
            + "       	    where (t2.id = #{p1.warehouse_id,jdbcType=INTEGER})                                         "
            + " ")
    List<Long> getWarehouseGroupTransferList(@Param("p1") MWGroupTransferVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Delete("                                                                                                           "
            + "   delete from b_warehouse_group_relation t4                                                             "
            + "     	  where exists (                                                                                "
            + "                select 1                                                                                 "
            + "                  from (                                                                                 "
            + "              	  	  	select t1.id                                                                    "
            + "       	   from b_warehouse_group_relation t1                                                           "
            + "        inner join m_warehouse t2                                                                        "
            + "                on t1.warehouse_id = t2.id                                                               "
            + "        inner join b_warehouse_group t3                                                                  "
            + "                on t1.warehouse_group_id = t3.id                                                         "
            + "       	    where (t2.id = #{p1.warehouse_id,jdbcType=INTEGER})                                         "
            + "   	                   and t4.id = t1.id                                                                "
            + "                          ) sub                                                                          "
            + "     	  )                                                                                             "
            + "      ")
    void deleteWarehouseGroupRelationByWarehouseId(@Param("p1") MWGroupTransferVo searchCondition);

    /**
     * 获取穿梭框数据
     * @param searchCondition
     * @return list
     */
    @Select("                                                                                                           "
            + "      select t1.id AS `key`,                                                                             "
            + "             t1.id AS id,                                                                                "
            + "             t1.name AS label,                                                                           "
            + "             t1.name AS name,                                                                            "
            + "             t1.short_name,                                                                              "
            + "             t1.code                                                                                     "
            + "        from m_warehouse t1                                                                              "
            + "       where true and t1.enable = '"+ SystemConstants.ENABLE_TRUE +"'                                    "
            + "    order by t1.name                                                                                     "
            + "            ")
    List<MWStaffTransferVo> getAllWarehouseStaffTransferList(@Param("p1") MWStaffTransferVo searchCondition);

    /**
     * 获取该仓库组下，全部仓库组
     */
    @Select("                                                                                                           "
            + " 	        SELECT                                                                                      "
            + " 	        	t2.*                                                                                    "
            + " 	        FROM                                                                                        "
            + " 	        	b_warehouse_relation t1                                                                 "
            + " 	        	INNER JOIN m_warehouse t2 ON t1.serial_id = t2.id                                       "
            + " 	        	AND t1.serial_type = 'm_warehouse'                                                      "
            + " 	        WHERE                                                                                       "
            + " 	        	(t1.staff_id = #{p1.staff_id,jdbcType=INTEGER})                                         "
            + " ")
    List<Long> getWarehouseStaffTransferList(@Param("p1") MWStaffTransferVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Delete("                                                                                                           "
            + "   delete from b_warehouse_relation t4                                                                   "
            + "     	  where exists (                                                                                "
            + "                select 1                                                                                 "
            + "                  from (                                                                                 "
            + "              	  	  	select t1.id                                                                    "
            + "       	   from b_warehouse_group_relation t1                                                           "
            + "        inner join m_warehouse t2                                                                        "
            + "                on t1.warehouse_id = t2.id                                                               "
            + "        inner join b_warehouse_group t3                                                                  "
            + "                on t1.warehouse_group_id = t3.id                                                         "
            + "       	    where (t2.id = #{p1.warehouse_id,jdbcType=INTEGER})                                         "
            + "   	                   and t4.id = t1.id                                                                "
            + "                          ) sub                                                                          "
            + "     	  )                                                                                             "
            + "      ")
    void deleteWarehouseStaffRelationByStaffId(@Param("p1") MWStaffTransferVo searchCondition);


    /**
     * 获取该员工所有授权仓库
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t2.*,                                                                                           "
            + "         2 type                                                                                          "
            + "		FROM                                                                                                "
            + "			b_warehouse_relation t1                                                                         "
            + "			INNER JOIN m_warehouse t2 ON t1.serial_id = t2.id                                                "
            + "			AND t1.serial_type = 'm_warehouse'                                                              "
            + "		WHERE                                                                                               "
            + "			TRUE and t1.staff_id  =  #{p1,jdbcType=INTEGER}                                                 "
            + " ")
    List<MWarehouseVo> getWarehouseByStaffId(@Param("p1") Integer staff_id);


    /**
     * 获取该员工所有授权仓库
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t4.*,                                                                                           "
            + "			1 type                                                                                          "
            + "		FROM                                                                                                "
            + "			b_warehouse_relation t1                                                                         "
            + "			INNER JOIN b_warehouse_group t2 ON t1.serial_id = t2.id                                         "
            + "			AND t1.serial_type = 'b_warehouse_group'                                                        "
            + "			INNER JOIN b_warehouse_group_relation t3 ON t3.warehouse_group_id = t2.id                       "
            + "			INNER JOIN m_warehouse t4 ON t4.id = t3.warehouse_id                                            "
            + "		WHERE                                                                                               "
            + "			TRUE and t1.staff_id  =  #{p1,jdbcType=INTEGER}                                                 "
            + " ")
    List<MWarehouseVo> getWarehouseByGroupStaffId(@Param("p1") Integer staff_id);

    /**
     * 获取该员工下所有的仓库组数据
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t2.*                                                                                            "
            + "		FROM                                                                                                "
            + "			b_warehouse_relation t1                                                                         "
            + "			INNER JOIN b_warehouse_group t2 ON t1.serial_id = t2.id                                         "
            + "			AND t1.serial_type = 'b_warehouse_group'                                                        "
            + "		WHERE                                                                                               "
            + "			TRUE and t1.staff_id  =  #{p1,jdbcType=INTEGER}                                                 "
            + " ")
    List<BWarehouseGroupVo> getWarehouseGroupByStaffId(@Param("p1") Integer staff_id);


    /**
     * 获取该岗位所有授权仓库
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t2.*,                                                                                           "
            + "         2 type                                                                                          "
            + "		FROM                                                                                                "
            + "			b_warehouse_relation t1                                                                         "
            + "			INNER JOIN m_warehouse t2 ON t1.serial_id = t2.id                                               "
            + "			AND t1.serial_type = 'm_warehouse'                                                              "
            + "		WHERE                                                                                               "
            + "			TRUE and t1.position_id  =  #{p1,jdbcType=INTEGER}                                              "
            + " ")
    List<MWarehouseVo> getWarehouseByPositionId(@Param("p1") Integer position_id);


    /**
     * 获取该岗位所有授权仓库
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t4.*,                                                                                           "
            + "			1 type                                                                                          "
            + "		FROM                                                                                                "
            + "			b_warehouse_relation t1                                                                         "
            + "			INNER JOIN b_warehouse_group t2 ON t1.serial_id = t2.id                                         "
            + "			AND t1.serial_type = 'b_warehouse_group'                                                        "
            + "			INNER JOIN b_warehouse_group_relation t3 ON t3.warehouse_group_id = t2.id                       "
            + "			INNER JOIN m_warehouse t4 ON t4.id = t3.warehouse_id                                            "
            + "		WHERE                                                                                               "
            + "			TRUE and t1.position_id  =  #{p1,jdbcType=INTEGER}                                              "
            + " ")
    List<MWarehouseVo> getWarehouseByGroupPositionId(@Param("p1") Integer position_id);

    /**
     * 获取该岗位下所有的仓库组数据
     */
    @Select(""
            + "		SELECT                                                                                              "
            + "			t2.*                                                                                            "
            + "		FROM                                                                                                "
            + "			b_warehouse_relation t1                                                                         "
            + "			INNER JOIN b_warehouse_group t2 ON t1.serial_id = t2.id                                         "
            + "			AND t1.serial_type = 'b_warehouse_group'                                                        "
            + "		WHERE                                                                                               "
            + "			TRUE and t1.position_id  =  #{p1,jdbcType=INTEGER}                                              "
            + " ")
    List<BWarehouseGroupVo> getWarehouseGroupByPositionId(@Param("p1") Integer position_id);

    /**
     * 检查仓库是否有库存记录
     * @param warehouse_id 仓库ID
     * @return 库存记录数量
     */
    @Select("SELECT COUNT(1) FROM m_inventory WHERE warehouse_id = #{warehouse_id}")
    Integer checkInventoryExists(@Param("warehouse_id") Integer warehouse_id);

    /**
     * 检查仓库是否有入库记录
     * @param warehouse_id 仓库ID
     * @return 入库记录数量
     */
    @Select("SELECT COUNT(1) FROM b_in WHERE warehouse_id = #{warehouse_id}")
    Integer checkInboundExists(@Param("warehouse_id") Integer warehouse_id);

    /**
     * 检查仓库是否有出库记录
     * @param warehouse_id 仓库ID
     * @return 出库记录数量
     */
    @Select("SELECT COUNT(1) FROM b_out WHERE warehouse_id = #{warehouse_id}")
    Integer checkOutboundExists(@Param("warehouse_id") Integer warehouse_id);

    /**
     * 检查仓库是否有库区记录
     * @param warehouse_id 仓库ID
     * @return 库区记录数量
     */
    @Select("SELECT COUNT(1) FROM m_location WHERE warehouse_id = #{warehouse_id}")
    Integer checkLocationExists(@Param("warehouse_id") Integer warehouse_id);

    /**
     * 检查仓库是否有库位记录
     * @param warehouse_id 仓库ID
     * @return 库位记录数量
     */
    @Select("SELECT COUNT(1) FROM m_bin WHERE warehouse_id = #{warehouse_id}")
    Integer checkBinExists(@Param("warehouse_id") Integer warehouse_id);


    /**
     * 综合检查仓库业务关联情况
     * @param warehouse_id 仓库ID
     * @return 关联业务信息
     */
    @Select("SELECT "
            + "  (SELECT COUNT(1) FROM m_inventory WHERE warehouse_id = #{warehouse_id}) as inventory_count, "
            + "  (SELECT COUNT(1) FROM b_in WHERE warehouse_id = #{warehouse_id}) as inbound_count, "
            + "  (SELECT COUNT(1) FROM b_out WHERE warehouse_id = #{warehouse_id}) as outbound_count, "
            + "  (SELECT COUNT(1) FROM m_location WHERE warehouse_id = #{warehouse_id}) as location_count, "
            + "  (SELECT COUNT(1) FROM m_bin WHERE warehouse_id = #{warehouse_id}) as bin_count")
    java.util.Map<String, Object> checkWarehouseBusinessAssociations(@Param("warehouse_id") Integer warehouse_id);

    /**
     * 导出专用查询方法，支持动态排序 (按照岗位模式设计)
     * 支持全部导出和选中导出两种模式
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @param orderByClause 排序子句
     * @return List<MWarehouseExportVo> 导出数据列表
     */
    @Select({
            "<script>"
                    + selectExportList_select
                    + "    and (t1.operate_company_id = #{p1.operate_company_id,jdbcType=INTEGER} or #{p1.operate_company_id,jdbcType=INTEGER} is null)      "
                    + "    and (t1.charge_company_id = #{p1.charge_company_id,jdbcType=INTEGER} or #{p1.charge_company_id,jdbcType=INTEGER} is null)        "
                    + "    and (CONCAT (ifnull(t1.code,''),ifnull(t1.name,''),ifnull(t1.short_name,''),ifnull(t1.name_pinyin,''),ifnull(t1.short_name_pinyin,''),ifnull(t1.name_pinyin_initial,''),ifnull(t1.short_name_pinyin_initial,'')) like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null or #{p1.combine_search_condition,jdbcType=VARCHAR} = '')        "
                    + "    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                            "
                    + "    and (t1.warehouse_type = #{p1.warehouse_type} or #{p1.warehouse_type} is null or #{p1.warehouse_type} = '')                       "
                    + "    and (t1.enable = #{p1.enable} or #{p1.enable} is null)                                                                            "
                    + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                                              "
                    + "     and t1.id in                                                                                                                   "
                    + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                    "
                    + "         #{item}                                                                                                                    "
                    + "        </foreach>                                                                                                                  "
                    + "   </if>                                                                                                                            "
                    + "  <if test=\"orderByClause != null and orderByClause != ''\">"
                    + "    ${orderByClause}"
                    + "  </if>"
                    + "  <if test=\"orderByClause == null or orderByClause == ''\">"
                    + "    ORDER BY t1.u_time DESC"
                    + "  </if>"
                    + "</script>"
    })
    List<MWarehouseExportVo> selectExportList(@Param("p1") MWarehouseVo searchCondition, @Param("orderByClause") String orderByClause);

}
