package com.xinyirun.scm.core.system.mapper.master.inventory.v2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiDailyInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.inventory.ApiInventoryVo;
import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import com.xinyirun.scm.bean.entity.master.inventory.v2.MInventoryV2Entity;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventorySumVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.MInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MMonitorInventoryVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 库存表 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MInventoryV2Mapper extends BaseMapper<MInventoryV2Entity> {

    String common_select = "  "
            + "     SELECT                                                               "
            + "            t.*,                                                          "
            + "            sum(t.qty_avaible) qty,                                       "
            + "            '"+ SystemConstants.INVENTORY_UNIT +"' as unit,               "
            + "            ifnull(t3.short_name,t3.name) as warehouse_name,              "
            + "            t4.spec,                                                      "
            + "            t4.pm,                                                        "
            + "            ifnull(t6.short_name,t6.name) as owner_name,                  "
            + "            ifnull(t7.short_name,t7.name) as location_name,               "
            + "            t8.name as bin_name,                                          "
            + "            t5.name as goods_name,                                        "
            + "            t5.code as goods_code,                                        "
            + "            t1.name as c_name,                                            "
            + "            t2.name as u_name,                                            "
            + "			   t12.name business_name,                                       "
            + "			   t11.name industry_name,                                       "
            + "			   t10.name category_name                                        "
            + "       FROM                                                               "
            + "  	       m_inventory t                                                 "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                  "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                  "
            + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                      "
            + "  LEFT JOIN m_goods_spec t4 ON t.sku_id = t4.id                           "
            + "  LEFT JOIN m_goods t5 ON t4.goods_id = t5.id                             "
            + "  LEFT JOIN m_owner t6 ON t.owner_id = t6.id                              "
            + "  LEFT JOIN m_location t7 ON t.location_id = t7.id                        "
            + "  LEFT JOIN m_bin t8 ON t.bin_id = t8.id                                  "
            + "  LEFT JOIN m_goods t9 ON t4.goods_id = t9.id                             "
            + "  LEFT JOIN m_category t10 ON t9.category_id = t10.id                     "
            + "  LEFT JOIN m_industry t11 ON t11.id = t10.industry_id                    "
            + "  LEFT JOIN m_business_type t12 ON t11.business_id = t12.id               "
            ;

    String sum_select = "  "
            + "     SELECT                                                               "
            + "            sum(t.qty_avaible) qty_avaible                                "
            + "       FROM                                                               "
            + "  	       m_inventory t                                                 "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                  "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                  "
            + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                      "
            + "  LEFT JOIN m_goods_spec t4 ON t.sku_id = t4.id                           "
            + "  LEFT JOIN m_goods t5 ON t4.goods_id = t5.id                             "
            + "  LEFT JOIN m_owner t6 ON t.owner_id = t6.id                              "
            + "  LEFT JOIN m_location t7 ON t.location_id = t7.id                        "
            + "  LEFT JOIN m_bin t8 ON t.bin_id = t8.id                                  "
            + "  LEFT JOIN m_goods t9 ON t4.goods_id = t9.id                             "
            + "  LEFT JOIN m_category t10 ON t9.category_id = t10.id                     "
            + "  LEFT JOIN m_industry t11 ON t11.id = t10.industry_id                    "
            + "  LEFT JOIN m_business_type t12 ON t11.business_id = t12.id               "
            ;

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t4.code like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null) "
            + "    or (t4.spec like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null)  "
            + "    or (t4.pm like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null)    "
            + "    or (t5.name like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null)  "
            + "    and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                 "
            + "    and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)     "
            + "      ")
    IPage<MInventoryVo> selectPage(Page page, @Param("p1") MInventoryVo searchCondition);

    /**
     * 页面查询列表-按货主
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select
            + "  where true "
            + "    and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                                                           "
            + "    and (concat(t6.name,t6.short_name) like concat('%',#{p1.owner_name,jdbcType=VARCHAR},'%')                                                                "
            + "        or #{p1.owner_name,jdbcType=VARCHAR} is null or #{p1.owner_name,jdbcType=VARCHAR} ='')                                                               "
            + "   <if test='p1.warehouse_ids != null and p1.warehouse_ids.length!=0' >                                                                                      "
            + "    and t.warehouse_id in                                                                                                                                    "
            + "        <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>                                                   "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            + "    and (CONCAT(ifnull(t4.name, ''),ifnull(t4.spec, ''),ifnull(t5.name, ''), ifnull(t5.code, '')) like CONCAT ('%',#{p1.sku_name,jdbcType=VARCHAR},'%') or #{p1.sku_name,jdbcType=VARCHAR} is null)"
            + "    and (t10.name like concat('%', #{p1.category_name,jdbcType=VARCHAR}, '%') or #{p1.category_name,jdbcType=VARCHAR} is null or #{p1.category_name,jdbcType=VARCHAR} = '')"
            + "   <if test='p1.owner_ids != null and p1.owner_ids.length != 0' >                                                                                            "
            + "    and t.owner_id in                                                                                                                                        "
            + "        <foreach collection='p1.owner_ids' item='item' index='index' open='(' separator=',' close=')'>                                                       "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                "
            + "    GROUP BY t.owner_id,t5.name                                                                                                                              "
            + "    </script>  ")
    IPage<MInventoryVo> selectPageByOwner(Page page, @Param("p1") MInventoryVo searchCondition);

    /**
     * 页面查询列表-按货主
     */
    @Select(" <script>   "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + sum_select
            + "  where true "
            + "    and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                                                           "
            + "    and (concat(t6.name,t6.short_name) like concat('%',#{p1.owner_name,jdbcType=VARCHAR},'%')                                                              "
            + "        or #{p1.owner_name,jdbcType=VARCHAR} is null or #{p1.owner_name,jdbcType=VARCHAR} ='')                                                               "
            + "   <if test='p1.warehouse_ids != null and p1.warehouse_ids.length!=0' >                                                                                      "
            + "    and t.warehouse_id in                                                                                                                                    "
            + "        <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>                                                   "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            + "    and (CONCAT(ifnull(t4.name, ''),ifnull(t4.spec, ''),ifnull(t5.name, ''), ifnull(t5.code, '')) like CONCAT ('%',#{p1.sku_name,jdbcType=VARCHAR},'%') or #{p1.sku_name,jdbcType=VARCHAR} is null)"
            + "    and (t10.name like concat('%', #{p1.category_name,jdbcType=VARCHAR}, '%') or #{p1.category_name,jdbcType=VARCHAR} is null or #{p1.category_name,jdbcType=VARCHAR} = '')"
            + "   <if test='p1.owner_ids != null and p1.owner_ids.length != 0' >                                                                                            "
            + "    and t.owner_id in                                                                                                                                        "
            + "        <foreach collection='p1.owner_ids' item='item' index='index' open='(' separator=',' close=')'>                                                       "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                                        "
            + "    </script>  ")
    MInventorySumVo selectSumByOwner(@Param("p1") MInventoryVo searchCondition);

    /**
     * 页面查询列表-按货主规格
     */
    @Select("  <script>  "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + common_select
            + "  where true "
            + "    and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                                                           "
            + "    and (concat(t6.name,t6.short_name) like concat('%',#{p1.owner_name,jdbcType=VARCHAR},'%')                                                              "
            + "        or #{p1.owner_name,jdbcType=VARCHAR} is null or #{p1.owner_name,jdbcType=VARCHAR} ='')                                                               "
            + "   <if test='p1.warehouse_ids != null and p1.warehouse_ids.length!=0' >                                                                                      "
            + "    and t.warehouse_id in                                                                                                                                    "
            + "        <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>                                                   "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            + "    and (CONCAT(ifnull(t4.name, ''),ifnull(t4.spec, ''),ifnull(t5.name, ''), ifnull(t5.code, '')) like CONCAT ('%',#{p1.sku_name,jdbcType=VARCHAR},'%') or #{p1.sku_name,jdbcType=VARCHAR} is null)"
            + "    and (t10.name like concat('%', #{p1.category_name,jdbcType=VARCHAR}, '%') or #{p1.category_name,jdbcType=VARCHAR} is null or #{p1.category_name,jdbcType=VARCHAR} = '')"
            + "   <if test='p1.owner_ids != null and p1.owner_ids.length != 0' >                                                                                            "
            + "    and t.owner_id in                                                                                                                                        "
            + "        <foreach collection='p1.owner_ids' item='item' index='index' open='(' separator=',' close=')'>                                                       "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                "
            + "    GROUP BY t.owner_id,t4.id                                                                                                                                "
            + "    </script>  ")
    IPage<MInventoryVo> selectPageByOwnerSpec(Page page, @Param("p1") MInventoryVo searchCondition);

    /**
     * 页面查询列表-按货主规格
     */
    @Select("  <script>  "
            + " ${p1.params.dataScopeAnnotation_with}                                                                                               "
            + sum_select
            + "  where true "
            + "    and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                                                           "
            + "    and (concat(t6.name,t6.short_name) like concat('%',#{p1.owner_name,jdbcType=VARCHAR},'%')                                                              "
            + "        or #{p1.owner_name,jdbcType=VARCHAR} is null or #{p1.owner_name,jdbcType=VARCHAR} ='')                                                               "
            + "   <if test='p1.warehouse_ids != null and p1.warehouse_ids.length!=0' >                                                                                      "
            + "    and t.warehouse_id in                                                                                                                                    "
            + "        <foreach collection='p1.warehouse_ids' item='item' index='index' open='(' separator=',' close=')'>                                                   "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            + "    and (CONCAT(ifnull(t4.name, ''),ifnull(t4.spec, ''),ifnull(t5.name, ''), ifnull(t5.code, '')) like CONCAT ('%',#{p1.sku_name,jdbcType=VARCHAR},'%') or #{p1.sku_name,jdbcType=VARCHAR} is null)"
            + "    and (t10.name like concat('%', #{p1.category_name,jdbcType=VARCHAR}, '%') or #{p1.category_name,jdbcType=VARCHAR} is null or #{p1.category_name,jdbcType=VARCHAR} = '')"
            + "   <if test='p1.owner_ids != null and p1.owner_ids.length != 0' >                                                                                            "
            + "    and t.owner_id in                                                                                                                                        "
            + "        <foreach collection='p1.owner_ids' item='item' index='index' open='(' separator=',' close=')'>                                                       "
            + "         #{item}                                                                                                                                             "
            + "        </foreach>                                                                                                                                           "
            + "   </if>                                                                                                                                                     "
            // 仓库权限
            + "     ${p1.params.dataScopeAnnotation}                                                                                                "
            + "    </script>  ")
    MInventorySumVo selectSumByOwnerSpec(@Param("p1") MInventoryVo searchCondition);

    /**
     * 页面查询列表-不分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and ((t4.code like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null) "
            + "    or (t4.spec like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null)  "
            + "    or (t4.pm like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null)    "
            + "    or (t5.name like CONCAT ('%',#{p1.key_word,jdbcType=VARCHAR},'%') or #{p1.key_word,jdbcType=VARCHAR} is null))  "
            + "    and (t.owner_id =  #{p1.owner_id,jdbcType=INTEGER} or #{p1.owner_id,jdbcType=INTEGER} is null)                 "
            + "    and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)     "
            + "    group by t.id                                                                             "
            + ""
            + "      ")
    List<MInventoryVo> selectList(@Param("p1") MInventoryVo searchCondition);


    @Select("    "
            + "      select *                                                                                        "
            + "        from m_inventory_v2 t1                                                                           "
            + "       where t1.bin_id = #{p1}                                                                        "
            + "         and t1.owner_id = #{p2}                                                                      "
            + "         and (t1.owner_id = #{p3} or #{p3} is null or #{p3} = '')                                     "
            + "         and t1.sku_id = #{p4}                                                                        "
            + "    order by lot asc                                                                        "
            + "      ")
    List<MInventoryV2Entity> select(@Param("p1") Integer bin_id,
                                  @Param("p2") Integer owner_id,
                                  @Param("p3") String lot,
                                  @Param("p4") Integer sku_id
    );

    @Select("    "
            + "      select *                                                                                        "
            + "        from m_inventory_v2 t1                                                                           "
            + "       where t1.bin_id = #{p1}                                                                        "
            + "         and t1.owner_id = #{p2}                                                                      "
            + "         and (t1.lot = #{p3} or #{p3} is null or #{p3} = '')                                     "
            + "         and t1.sku_id = #{p4}                                                                        "
            + "    order by lot asc                                                                                  "
            + "     for update                                                                                       "
            + "      ")
    List<MInventoryV2Entity> selectForUpdate(@Param("p1") Integer bin_id,
                                  @Param("p2") Integer owner_id,
                                  @Param("p3") String lot,
                                  @Param("p4") Integer sku_id
    );

    @Select("    "
            + "      select *                                                                                        "
            + "        from m_inventory_v2 t1                                                                           "
            + "       where t1.id = #{p1}                                                                            "
            + "      ")
    MInventoryV2Entity getInventoryEntityById(@Param("p1") Integer id);

    @Select("    "
            + "		SELECT		                                                    "
            + "			sum( t.qty_avaible ) qty_avaible,		                    "
            + "			t.owner_id,		                                            "
            + "			t.price,		                                            "
            + "			t.amount,		                                            "
            + "			t.warehouse_id,		                                        "
            + "			t.sku_id,		                                            "
            + "			t.sku_code 		                                            "
            + "		FROM		                                                    "
            + "			m_inventory t 		                                        "
            + "  where true                                                         "
            + "         and t.owner_id = #{p1.owner_id}                             "
            + "         and t.warehouse_id = #{p1.warehouse_id}                     "
            + "         and t.sku_id = #{p1.sku_id}                                 "
            + "		GROUP BY		                                                "
            + "			t.owner_id,		                                            "
            + "			t.warehouse_id,		                                        "
            + "			t.sku_id		                                            "
            + "      ")
    MInventoryVo getInventoryInfo(@Param("p1") MInventoryVo searchCondition);

    @Select("    "
            + "		SELECT		                                                    "
            + "			t.qty_avaible ,		                                        "
            + "			t.owner_id,		                                            "
            + "			t.price,		                                            "
            + "			t.amount,		                                            "
            + "			t.warehouse_id,		                                        "
            + "			t.sku_id,		                                            "
            + "			t.sku_code 		                                            "
            + "		FROM		                                                    "
            + "			m_inventory t 		                                        "
            + "  where true                                                         "
            + "         and t.owner_id = #{p1.owner_id}                             "
            + "         and t.sku_id = #{p1.sku_id}                                 "
            + "      ")
    List<MInventoryVo> getInventoryInfoList(@Param("p1") MInventoryVo searchCondition);

    /**
     * 根据 仓库ID 查询仓库库存量大于0, 锁定库存不等于0 的库存
     * @param searchCondition 仓库ID
     * @return List<MInventoryVo>
     */
    @Select({ " <script>                                                                                               "
            + "		SELECT		                                                                                       "
            + "         qty_avaible,                                                                                   "
            + "         qty_lock,                                                                                      "
            + "         warehouse_id                                                                                   "
            + "     from m_inventory                                                                                   "
            + "     where true                                                                                         "
            + "    <if test='p1 != null and p1.size != 0' >                                                            "
            + "         and warehouse_id in                                                                            "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>            "
            + "         #{item.id}                                                                                        "
            + "        </foreach>                                                                                      "
            + "    </if>                                                                                               "
            + "     and (qty_avaible &gt; 0  OR  qty_lock != 0)                                                                             "
            + "  </script>                                                                                             "
    })
    List<MInventoryVo> selectInventoryByWarehouse(@Param("p1") List<MWarehouseVo> searchCondition);

    String common_select1 = "  "
            + "     SELECT                                                              "
            + "            t.id,                                                        "
            + "            t.code,                                                      "
            + "            t.warehouse_id,                                              "
            + "            t.location_id,                                               "
            + "            t.bin_id,                                                    "
            + "            t.sku_id,                                                    "
            + "            t.sku_code,                                                  "
            + "            t.owner_id,                                                  "
            + "            t.lot,                                                       "
//            + "            sum(t.amount) amount,                                        "
            + "            sum(t.qty_avaible)*t9.price amount,                          "
            + "            t9.price,                                                    "
            + "            sum(t.qty_avaible) qty_avaible,                              "
            + "            sum(t.qty_lock) qty_lock,                                    "
            + "            t.c_time,                                                    "
            + "            t.u_time,                                                    "
            + "            t.c_id,                                                      "
            + "            t.u_id,                                                      "
            + "            ifnull(t3.short_name,t3.name) as warehouse_name,            "
            + "            t4.spec,                                          "
            + "            t4.pm,                                          "
            + "            ifnull(t6.short_name,t6.name) as owner_name,            "
            + "            t6.code as owner_code,            "
            + "            ifnull(t7.short_name,t7.name) as location_name,            "
            + "            t8.name as bin_name,                                          "
            + "            t5.name as goods_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_inventory t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                                 "
            + "  LEFT JOIN m_goods_spec t4 ON t.sku_id = t4.id                "
            + "  LEFT JOIN m_goods t5 ON t4.goods_id = t5.id                "
            + "  LEFT JOIN m_owner t6 ON t.owner_id = t6.id                "
            + "  LEFT JOIN m_location t7 ON t.location_id = t7.id                "
            + "  LEFT JOIN m_bin t8 ON t.bin_id = t8.id                "
            + "             LEFT JOIN(                                                                                                                                   "
            + "              SELECT                                                                                                                                      "
            + "              	sku_id,                                                                                                                                  "
            + "              	price,                                                                                                                                   "
            + "              	price_dt,                                                                                                                                "
            + "              	c_time                                                                                                                                   "
            + "              FROM                                                                                                                                        "
            + "              	b_goods_price                                                                                                                            "
            + "              ORDER BY price_dt DESC,c_time DESC LIMIT 1                                                                                                  "
            + "            )t9 on t9.sku_id = t.sku_id                                                                                                                   "
            ;

    @Select("    "
            + common_select1
            + "  where true "
            + "    and (t.warehouse_id =  #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null) "
            + "    and (t.sku_code =  #{p1.sku_code,jdbcType=VARCHAR} or #{p1.sku_code,jdbcType=VARCHAR} is null) "
            + "    and (t6.code =  #{p1.owner_code,jdbcType=VARCHAR} or #{p1.owner_code,jdbcType=VARCHAR} is null) "
            + "    and (t6.name = #{p1.owner_name,jdbcType=VARCHAR} or #{p1.owner_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.name = #{p1.goods_name,jdbcType=VARCHAR}  or #{p1.goods_name,jdbcType=VARCHAR} is null) "
            + "    and (t4.name = #{p1.spec,jdbcType=VARCHAR}  or #{p1.spec,jdbcType=VARCHAR} is null) "
            + "    and (t3.name = #{p1.warehouse_name,jdbcType=VARCHAR}  or #{p1.warehouse_name,jdbcType=VARCHAR} is null) "
            + "    GROUP BY t.bin_id,t.sku_id,t.owner_id  ")
    IPage<ApiInventoryVo> getInventory(Page page, @Param("p1") ApiInventoryVo vo);

    @Select("    "
            + "		SELECT                                                                                                  "
            + "			t1.id,                                                                                              "
            + "			t1.dt,                                                                                              "
            + "			t2.id owner_id,                                                                                     "
            + "			t2.name owner_name,                                                                                 "
            + "			t2.code owner_code,                                                                                 "
            + "			t2.short_name owner_simple_name,                                                                    "
            + "			t3.id warehouse_id,                                                                                 "
            + "			t3.name warehouse_name,                                                                             "
            + "			t3.short_name warehouse_simple_name,                                                                "
            + "			t4.id location_id,                                                                                  "
            + "			t4.name location_name,                                                                              "
            + "			t4.short_name location_simple_name,                                                                 "
            + "			t5.id bin_id,                                                                                       "
            + "			t5.name bin_name,                                                                                   "
            + "			t5.name bin_simple_name,                                                                            "
            + "			t6.id sku_id,                                                                                       "
            + "			t6.code sku_code,                                                                                   "
            + "			t6.name sku_name,                                                                                   "
            + "			t7.id goods_id,                                                                                     "
            + "			t7.code goods_code,                                                                                 "
            + "			t7.name goods_name,                                                                                 "
            + "			t8.id category_id,                                                                                  "
            + "			t8.code category_code,                                                                              "
            + "			t8.name category_name,                                                                              "
            + "			t9.id industry_id,                                                                                  "
            + "			t9.code industry_code,                                                                              "
            + "			t9.name industry_name,                                                                              "
            + "			t10.id business_id,                                                                                 "
            + "			t10.code business_code,                                                                             "
            + "			t10.name business_name,                                                                             "
            + "			t11.code prop_id,                                                                                   "
            + "			t11.name prop_name,                                                                                 "
            + "			t1.qty,                                                                                             "
            + "			t1.qty_in,                                                                                          "
            + "			t1.qty_out,                                                                                         "
            + "			t1.qty_adjust,                                                                                      "
            + "			t1.qty_in-t1.qty_out+t1.qty_adjust qty_diff,                                                        "
            + "			ifnull(t1.price, 0) price,                                                                          "
            + "			ifnull(t1.inventory_amount, 0) amount                                                               "
            + "		FROM                                                                                                    "
            + "			b_daily_inventory t1                                                                                "
            + "			left join m_owner t2 on t1.owner_id = t2.id                                                         "
            + "			left join m_warehouse t3 on t1.warehouse_id = t3.id                                                 "
            + "			left join m_location t4 on t1.location_id = t4.id                                                   "
            + "			left join m_bin t5 on t1.bin_id = t5.id                                                             "
            + "			left join m_goods_spec t6 on t6.id = t1.sku_id                                                      "
            + "			left join m_goods t7 on t7.id = t6.goods_id                                                         "
            + "			left join m_category t8 on t8.id = t7.category_id                                                   "
            + "			left join m_industry t9 on t9.id = t8.industry_id                                                   "
            + "			left join m_business_type t10 on t10.id = t9.business_id                                            "
            + "			left join m_goods_spec_prop t11 on t11.id = t6.prop_id                                              "
    )
    List<ApiDailyInventoryVo> getDailyInventory();

    @Select("    "
            + "	SELECT                                                                                                                                                                                  "
            + "		t1.CODE,                                                                                                                                                                            "
            + "		t1.source_sku_id,                                                                                                                                                                   "
            + "		sum( t1.amount ) amount,                                                                                                                                                            "
            + "		sum( t1.qty ) qty,                                                                                                                                                                  "
            + "		ifnull( sum( t1.amount ) / sum( t1.qty ), 0 ) price,                                                                                                                                "
            + "		t2.id owner_id,                                                                                                                                                                     "
            + "		t2.NAME owner_name,                                                                                                                                                                 "
            + "		t2.CODE owner_code,                                                                                                                                                                 "
            + "		t2.short_name owner_simple_name,                                                                                                                                                    "
            + "		t3.id warehouse_id,                                                                                                                                                                 "
            + "		t3.NAME warehouse_name,                                                                                                                                                             "
            + "		t3.short_name warehouse_simple_name,                                                                                                                                                "
            + "		t4.id location_id,                                                                                                                                                                  "
            + "		t4.NAME location_name,                                                                                                                                                              "
            + "		t4.short_name location_simple_name,                                                                                                                                                 "
            + "		t5.id bin_id,                                                                                                                                                                       "
            + "		t5.NAME bin_name,                                                                                                                                                                   "
            + "		t5.NAME bin_simple_name,                                                                                                                                                            "
            + "		t6.id sku_id,                                                                                                                                                                       "
            + "		t6.CODE sku_code,                                                                                                                                                                   "
            + "		t6.NAME sku_name,                                                                                                                                                                   "
            + "		t7.id goods_id,                                                                                                                                                                     "
            + "		t7.CODE goods_code,                                                                                                                                                                 "
            + "		t7.NAME goods_name,                                                                                                                                                                 "
            + "		t8.id category_id,                                                                                                                                                                  "
            + "		t8.CODE category_code,                                                                                                                                                              "
            + "		t8.NAME category_name,                                                                                                                                                              "
            + "		t9.id industry_id,                                                                                                                                                                  "
            + "		t9.CODE industry_code,                                                                                                                                                              "
            + "		t9.NAME industry_name,                                                                                                                                                              "
            + "		t10.id business_id,                                                                                                                                                                 "
            + "		t10.CODE business_code,                                                                                                                                                             "
            + "		t10.NAME business_name,                                                                                                                                                             "
            + "		t11.CODE prop_id,                                                                                                                                                                   "
            + "		t11.NAME prop_name,                                                                                                                                                                 "
            + "		t1.c_time                                                                                                                                                                           "
            + "	FROM                                                                                                                                                                                    "
            + "		(                                                                                                                                                                                   "
            + "		SELECT                                                                                                                                                                              "
            + "			tt1.warehouse_id,                                                                                                                                                               "
            + "			tt1.location_id,                                                                                                                                                                "
            + "			tt1.bin_id,                                                                                                                                                                     "
            + "			tt1.owner_id,                                                                                                                                                                   "
            + "			tt2.target_sku_id sku_id,                                                                                                                                                       "
            + "			tt2.source_sku_id,                                                                                                                                                              "
            + "			tt1.dt,                                                                                                                                                                         "
            + "			tt2.c_time,                                                                                                                                                                     "
            + "			tt2.CODE,                                                                                                                                                                       "
            + "			sum( ifnull( tt1.qty_in, 0 ) ) over ( PARTITION BY tt1.sku_id, tt1.warehouse_id, tt1.owner_id ORDER BY tt1.dt ROWS BETWEEN 14 PRECEDING AND 0 FOLLOWING ) qty,                  "
            + "			sum( tt1.qty_in * ifnull( tt1.price, 0 ) ) over ( PARTITION BY tt1.sku_id, tt1.warehouse_id, tt1.owner_id ORDER BY tt1.dt ROWS BETWEEN 14 PRECEDING AND 0 FOLLOWING ) amount    "
            + "		FROM                                                                                                                                                                                "
            + "			b_daily_inventory tt1                                                                                                                                                           "
            + "			INNER JOIN (                                                                                                                                                                    "
            + "			SELECT                                                                                                                                                                          "
            + "				t1.CODE,                                                                                                                                                                    "
            + "				t2.target_sku_id,                                                                                                                                                           "
            + "				t2.source_sku_id,                                                                                                                                                           "
            + "				t1.warehouse_id,                                                                                                                                                            "
            + "				t1.owner_id,                                                                                                                                                                "
            + "				t1.c_time                                                                                                                                                                   "
            + "			FROM                                                                                                                                                                            "
            + "				b_material_convert t1                                                                                                                                                       "
            + "				INNER JOIN b_material_convert_detail t2 ON t2.material_convert_id = t1.id                                                                                                   "
            + "			) tt2 ON tt2.source_sku_id = tt1.sku_id                                                                                                                                         "
            + "			AND tt2.warehouse_id = tt1.warehouse_id                                                                                                                                         "
            + "			AND tt2.owner_id = tt1.owner_id                                                                                                                                                 "
            + "		GROUP BY                                                                                                                                                                            "
            + "			tt1.dt,                                                                                                                                                                         "
            + "			tt1.sku_id,                                                                                                                                                                     "
            + "			tt1.warehouse_id,                                                                                                                                                               "
            + "			tt1.owner_id                                                                                                                                                                    "
            + "		) t1                                                                                                                                                                                "
            + "		LEFT JOIN m_owner t2 ON t1.owner_id = t2.id                                                                                                                                         "
            + "		LEFT JOIN m_warehouse t3 ON t1.warehouse_id = t3.id                                                                                                                                 "
            + "		LEFT JOIN m_location t4 ON t1.location_id = t4.id                                                                                                                                   "
            + "		LEFT JOIN m_bin t5 ON t1.bin_id = t5.id                                                                                                                                             "
            + "		LEFT JOIN m_goods_spec t6 ON t6.id = t1.sku_id                                                                                                                                      "
            + "		LEFT JOIN m_goods t7 ON t7.id = t6.goods_id                                                                                                                                         "
            + "		LEFT JOIN m_category t8 ON t8.id = t7.category_id                                                                                                                                   "
            + "		LEFT JOIN m_industry t9 ON t9.id = t8.industry_id                                                                                                                                   "
            + "		LEFT JOIN m_business_type t10 ON t10.id = t9.business_id                                                                                                                            "
            + "		LEFT JOIN m_goods_spec_prop t11 ON t11.id = t6.prop_id                                                                                                                              "
            + "	WHERE                                                                                                                                                                                   "
            + "		DATE_FORMAT( t1.dt, '%Y-%m-%d' ) = DATE_FORMAT( DATE_SUB(CURDATE(), INTERVAL 1 DAY), '%Y-%m-%d')                                                                                    "
            + "	GROUP BY                                                                                                                                                                                "
            + "		t1.sku_id,                                                                                                                                                                          "
            + "		t1.warehouse_id,                                                                                                                                                                    "
            + "		t1.owner_id                                                                                                                                                                         "
    )
    List<ApiMaterialConvertPriceVo> getMaterialConvertPrice();

    /**
     * 库存查询查询
     * @param param
     * @param page
     * @return
     */
    @Select("       SELECT                                                                                                                      "
            +  "    	concat(tab1.warehouse_id, '_', tab1.sku_id, '_', tab1.owner_id) id,                                                     "
            +  "    	tab1.warehouse_name,                                                                                                    "
            +  "    	tab1.owner_name,                                                                                                        "
            +  "    	tab1.sku_name,                                                                                                          "
            +  "    	tab1.sku_code,                                                                                                          "
            +  "    	tab1.actual_weight in_weight,                                                                                           "
            +  "    	tab2.actual_weight out_weight,                                                                                          "
            +  "    	tab3.qty_avaible,                                                                                                       "
            +  "    	tab4.qty_diff,                                                                                                          "
            +  "    	tab3.qty_lock,                                                                                                          "
            +  "    	IFNULL( tab1.actual_weight, 0 ) - IFNULL( tab2.actual_weight, 0 ) + IFNULL( tab4.qty_diff, 0 ) AS calculation_avaible,  "
            +  "    	IFNULL( tab3.qty_avaible, 0 ) + IFNULL( tab3.qty_lock, 0 ) AS sum_avaible                                               "
            +  "    FROM                                                                                                                        "
            +  "    	(                                                                                                                       "
            +  "    	SELECT                                                                                                                  "
            +  "    		t1.warehouse_id,                                                                                                    "
            +  "    		t4.`name` warehouse_name,                                                                                           "
            +  "    		t1.owner_id,                                                                                                        "
            +  "    		t3.`name` owner_name,                                                                                               "
            +  "    		t1.sku_id,                                                                                                          "
            +  "    		t2.`name` sku_name,                                                                                                 "
            +  "    		t2.`code` sku_code,                                                                                                 "
            +  "    		t5.`code` goods_code,                                                                                               "
            +  "    		t5.`name` goods_name,                                                                                               "
            +  "    		sum( t1.actual_weight ) actual_weight                                                                               "
            +  "    	FROM                                                                                                                    "
            +  "    		b_in t1                                                                                                             "
            +  "    	LEFT JOIN m_goods_spec t2 ON t1.sku_id = t2.id                                                                          "
            +  "    	LEFT JOIN m_goods t5 ON t2.goods_id = t5.id                                                                             "
            +  "    	LEFT JOIN m_owner t3 ON t3.id = t1.owner_id                                                                             "
            +  "    	LEFT JOIN m_warehouse t4 ON t1.warehouse_id = t4.id                                                                     "
            +  "    	WHERE                                                                                                                   "
            +  "    		t1.STATUS IN ( '"+ DictConstant.DICT_B_IN_STATUS_SUBMITTED +"', '"+ DictConstant.DICT_B_IN_STATUS_PASSED +"')       "
            +  "    	GROUP BY                                                                                                                "
            +  "    		t1.warehouse_id,                                                                                                    "
            +  "    		t1.owner_id,                                                                                                        "
            +  "    		t1.sku_id                                                                                                           "
            +  "    	) tab1                                                                                                                  "
            +  "    	LEFT JOIN (                                                                                                             "
            +  "    	SELECT                                                                                                                  "
            +  "    		t1.warehouse_id,                                                                                                    "
            +  "    		t1.owner_id,                                                                                                        "
            +  "    		t1.sku_id,                                                                                                          "
            +  "    		sum( t1.actual_weight ) actual_weight                                                                               "
            +  "    	FROM                                                                                                                    "
            +  "    		b_out t1                                                                                                            "
            +  "    	WHERE                                                                                                                   "
            +  "    		t1.STATUS IN ( '"+ DictConstant.DICT_B_OUT_STATUS_SUBMITTED +"', '"+ DictConstant.DICT_B_OUT_STATUS_PASSED +"')     "
            +  "    	GROUP BY                                                                                                                "
            +  "    		t1.warehouse_id,                                                                                                    "
            +  "    		t1.owner_id,                                                                                                        "
            +  "    		t1.sku_id                                                                                                           "
            +  "    	) tab2 ON tab1.warehouse_id = tab2.warehouse_id                                                                         "
            +  "    	AND tab1.owner_id = tab2.owner_id                                                                                       "
            +  "    	AND tab1.sku_id = tab2.sku_id                                                                                           "
            +  "    	LEFT JOIN m_inventory tab3 ON tab1.warehouse_id = tab3.warehouse_id                                                     "
            +  "    	AND tab1.owner_id = tab3.owner_id                                                                                       "
            +  "    	AND tab1.sku_id = tab3.sku_id                                                                                           "
            +  "    	LEFT JOIN (                                                                                                             "
            +  "    	SELECT                                                                                                                  "
            +  "    		t1.sku_id,                                                                                                          "
            +  "    		t1.warehouse_id,                                                                                                    "
            +  "    		t2.owner_id,                                                                                                        "
            +  "    		sum( t1.qty_diff ) qty_diff                                                                                         "
            +  "    	FROM                                                                                                                    "
            +  "    		b_adjust_detail t1                                                                                                  "
            +  "    		LEFT JOIN b_adjust t2 ON t2.id = t1.adjust_id                                                                       "
            +  "    	GROUP BY                                                                                                                "
            +  "    		t1.warehouse_id,                                                                                                    "
            +  "    		t2.owner_id,                                                                                                        "
            +  "    		t1.sku_id                                                                                                           "
            +  "    	) tab4 ON tab1.warehouse_id = tab4.warehouse_id                                                                         "
            +  "    	AND tab1.owner_id = tab4.owner_id                                                                                       "
            +  "    	AND tab1.sku_id = tab4.sku_id                                                                                           "
            +  "    WHERE TRUE                                                                                                                  "
            +  "    AND (tab1.warehouse_id = #{p1.warehouse_id} or #{p1.warehouse_id} is null)                                                  "
            +  "    AND (tab1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)                                                              "
            +  "    AND (concat(tab1.sku_name, '_', tab1.sku_code, '_', tab1.goods_name, '_', tab1.goods_code) like concat('%', #{p1.sku_name}, '%') or #{p1.sku_name} is null or #{p1.sku_name} = '')"
            +  "    HAVING                                                                                             "
            +  "    	calculation_avaible <> sum_avaible                                                             "
    )
    IPage<MMonitorInventoryVo> selectInventoryDiff(@Param("p1") MMonitorInventoryVo param, Page<MInventoryV2Entity> page);

    /**
     * 查询可用库存
     *
     * @param skuId       商品ID
     * @param warehouseId 仓库ID
     * @param ownerId     货主ID
     * @return
     */
    @Select(""
            +  "  SELECT                                                                                                "
            +  "  	ifnull(SUM( t1.qty_avaible), 0)                                                                     "
            +  "  FROM                                                                                                  "
            +  "  	m_inventory t1                                                                                      "
            +  "  WHERE                                                                                                 "
            +  "  TRUE                                                                                                  "
            +  "  	AND t1.sku_id = #{p1}                                                                               "
            +  "  	AND t1.warehouse_id = #{p2}                                                                         "
            +  "  	AND t1.owner_id = #{p3}                                                                             "
            +  "  GROUP BY                                                                                              "
            +  "  	t1.sku_id,                                                                                          "
            +  "  	t1.warehouse_id,                                                                                    "
            +  "  	t1.owner_id                                                                                         "
    )
    BigDecimal getQtyAvaibleBySWO(@Param("p1") Integer skuId,@Param("p2") Integer warehouseId,@Param("p3") Integer ownerId);
}
