package com.xinyirun.scm.core.system.mapper.wms.in;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.in.BInEntity;
import com.xinyirun.scm.bean.system.vo.wms.in.BInVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 入库单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-26
 */
@Mapper
public interface BInMapper extends BaseMapper<BInEntity> {

    /**
     * 分页查询入库单
     */
    @Select("	<script>                                                                                                                        "
            +" SELECT                                                                                                                       "
            +"		tab1.*,                                                                                                                    "
            +"		t1.name as owner_name,                                                                                                     "
            +"		t2.name as consignor_name,                                                                                                 "
            +"		t3.name as warehouse_name,                                                                                                 "
            +"		t4.name as location_name,                                                                                                  "
            +"		t5.name as bin_name,                                                                                                       "
            +"		t6.name as sku_name,                                                                                                       "
            +"		t7.spec as sku_spec,                                                                                                       "
            +"		t8.name as unit_name,                                                                                                      "
            +"		tab3.label as status_name,                                                                                                 "
            +"		tab4.label as type_name,                                                                                                   "
            +"		tab13.name as c_name,                                                                                                      "
            +"		tab14.name as u_name                                                                                                       "
            +"	FROM                                                                                                                           "
            +"		b_in tab1                                                                                                                  "
            +"		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                        "
            +"		LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id                                                                    "
            +"		LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id                                                                     "
            +"		LEFT JOIN m_location t4 ON t4.id = tab1.location_id                                                                       "
            +"		LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id                                                                                 "
            +"		LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id                                                                          "
            +"		LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code                                                                      "
            +"		LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id                                                                               "
            +"		LEFT JOIN s_dict_data tab3 ON tab3.code = '" + DictConstant.DICT_B_IN_STATUS + "' AND tab3.dict_value = tab1.status                          "
            +"		LEFT JOIN s_dict_data tab4 ON tab4.code = '" + DictConstant.DICT_B_IN_TYPE + "' AND tab4.dict_value = tab1.type                              "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                              "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                              "
            +"		WHERE TRUE                                                                                                                 "
            +"		 AND (tab1.status = #{param.status} or #{param.status} is null or #{param.status} = '')                                 "
            +"		 AND (tab1.code = #{param.code} or #{param.code} is null or #{param.code} = '')                                         "
            +"		 AND (tab1.type = #{param.type} or #{param.type} is null or #{param.type} = '')                                         "
            +"		 AND (tab1.owner_id = #{param.owner_id} or #{param.owner_id} is null)                                                    "
            +"		 AND (tab1.consignor_id = #{param.consignor_id} or #{param.consignor_id} is null)                                        "
            +"		 AND (tab1.warehouse_id = #{param.warehouse_id} or #{param.warehouse_id} is null)                                        "
            +"		 AND (tab1.sku_id = #{param.sku_id} or #{param.sku_id} is null)                                                          "

            + "   <if test='param.status_list != null and param.status_list.length!=0' >                                                    "
            + "    and tab1.status in                                                                                                        "
            + "        <foreach collection='param.status_list' item='item' index='index' open='(' separator=',' close=')'>                 "
            + "         #{item}                                                                                                              "
            + "        </foreach>                                                                                                            "
            + "   </if>                                                                                                                      "

            + "   <if test='param.type_list != null and param.type_list.length!=0' >                                                       "
            + "    and tab1.type in                                                                                                         "
            + "        <foreach collection='param.type_list' item='item' index='index' open='(' separator=',' close=')'>                  "
            + "         #{item}                                                                                                              "
            + "        </foreach>                                                                                                            "
            + "   </if>                                                                                                                      "

            // 商品查询：goods_name
            + "   <if test='param.goods_name != null' >                                                                                      "
            + "   and (tab1.sku_code like CONCAT('%', #{param.goods_name}, '%') or t6.name like CONCAT('%', #{param.goods_name}, '%'))    "
            + "   </if>                                                                                                                      "

            +"		ORDER BY tab1.c_time DESC                                                                                                 "
            +" </script>                                                                                                                     ")
    IPage<BInVo> selectPage(Page<BInVo> page, @Param("param") BInVo searchCondition);

    /**
     * 根据ID查询入库单详情
     */
    @Select("	SELECT                                                                                                                          "
            +"		tab1.*,                                                                                                                     "
            +"		t1.name as owner_name,                                                                                                      "
            +"		t2.name as consignor_name,                                                                                                  "
            +"		t3.name as warehouse_name,                                                                                                  "
            +"		t4.name as location_name,                                                                                                   "
            +"		t5.name as bin_name,                                                                                                        "
            +"		t6.name as sku_name,                                                                                                        "
            +"		t7.spec as sku_spec,                                                                                                        "
            +"		t8.name as unit_name,                                                                                                       "
            +"		tab3.label as status_name,                                                                                                  "
            +"		tab4.label as type_name,                                                                                                    "
            +"		tab5.one_file as doc_att_file,                                                                                              "
            +"		tab13.name as c_name,                                                                                                       "
            +"		tab14.name as u_name                                                                                                        "
            +"	FROM                                                                                                                            "
            +"		b_in tab1                                                                                                                   "
            +"		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                         "
            +"		LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id                                                                     "
            +"		LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id                                                                      "
            +"		LEFT JOIN m_location t4 ON t4.id = tab1.location_id                                                                        "
            +"		LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id                                                                                  "
            +"		LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id                                                                           "
            +"		LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code                                                                       "
            +"		LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id                                                                                "
            +"		LEFT JOIN s_dict_data tab3 ON tab3.code = '" + DictConstant.DICT_B_IN_STATUS + "' AND tab3.dict_value = tab1.status                           "
            +"		LEFT JOIN s_dict_data tab4 ON tab4.code = '" + DictConstant.DICT_B_IN_TYPE + "' AND tab4.dict_value = tab1.type                               "
            +"		LEFT JOIN b_in_attach tab5 ON tab5.in_id = tab1.id                                                                         "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                               "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                               "
            +"		WHERE tab1.id = #{id}                                                                                                      ")
    BInVo selectById(@Param("id") Integer id);

    /**
     * 导出查询
     */
    @Select("	<script>  SELECT                                                                                                                          "
            +"		tab1.*,                                                                                                                     "
            +"		t1.name as owner_name,                                                                                                      "
            +"		t2.name as consignor_name,                                                                                                  "
            +"		t3.name as warehouse_name,                                                                                                  "
            +"		t4.name as location_name,                                                                                                   "
            +"		t5.name as bin_name,                                                                                                        "
            +"		t6.name as sku_name,                                                                                                        "
            +"		t7.spec as sku_spec,                                                                                                        "
            +"		t8.name as unit_name,                                                                                                       "
            +"		tab3.label as status_name,                                                                                                  "
            +"		tab4.label as type_name,                                                                                                    "
            +"		tab13.name as c_name,                                                                                                       "
            +"		tab14.name as u_name                                                                                                        "
            +"	FROM                                                                                                                           "
            +"		b_in tab1                                                                                                                  "
            +"		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                        "
            +"		LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id                                                                    "
            +"		LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id                                                                     "
            +"		LEFT JOIN m_location t4 ON t4.id = tab1.location_id                                                                       "
            +"		LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id                                                                                 "
            +"		LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id                                                                          "
            +"		LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code                                                                      "
            +"		LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id                                                                               "
            +"		LEFT JOIN s_dict_data tab3 ON tab3.code = '" + DictConstant.DICT_B_IN_STATUS + "' AND tab3.dict_value = tab1.status                          "
            +"		LEFT JOIN s_dict_data tab4 ON tab4.code = '" + DictConstant.DICT_B_IN_TYPE + "' AND tab4.dict_value = tab1.type                              "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                              "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                              "
            +"		WHERE TRUE                                                                                                                 "
            +"		 AND (tab1.status = #{param.status} or #{param.status} is null or #{param.status} = '')                                 "
            +"		 AND (tab1.code = #{param.code} or #{param.code} is null or #{param.code} = '')                                         "
            +"		 AND (tab1.type = #{param.type} or #{param.type} is null or #{param.type} = '')                                         "
            +"		 AND (tab1.owner_id = #{param.owner_id} or #{param.owner_id} is null)                                                    "
            +"		 AND (tab1.consignor_id = #{param.consignor_id} or #{param.consignor_id} is null)                                        "
            +"		 AND (tab1.warehouse_id = #{param.warehouse_id} or #{param.warehouse_id} is null)                                        "
            +"		 AND (tab1.sku_id = #{param.sku_id} or #{param.sku_id} is null)                                                          "

            + "   <if test='param.status_list != null and param.status_list.length!=0' >                                                    "
            + "    and tab1.status in                                                                                                        "
            + "        <foreach collection='param.status_list' item='item' index='index' open='(' separator=',' close=')'>                 "
            + "         #{item}                                                                                                              "
            + "        </foreach>                                                                                                            "
            + "   </if>                                                                                                                      "

            + "   <if test='param.type_list != null and param.type_list.length!=0' >                                                       "
            + "    and tab1.type in                                                                                                         "
            + "        <foreach collection='param.type_list' item='item' index='index' open='(' separator=',' close=')'>                  "
            + "         #{item}                                                                                                              "
            + "        </foreach>                                                                                                            "
            + "   </if>                                                                                                                      "

            // 商品查询：goods_name
            + "   <if test='param.goods_name != null' >                                                                                      "
            + "   and (tab1.sku_code like CONCAT('%', #{param.goods_name}, '%') or t6.name like CONCAT('%', #{param.goods_name}, '%'))    "
            + "   </if>                                                                                                                      "

            +"		ORDER BY tab1.c_time DESC                                                                                                 "
            +" </script>                                                                                                                     ")
    List<BInVo> selectExportList(@Param("param") BInVo param);

    /**
     * 合计查询
     */
    @Select("	<script>  SELECT                                                                                                                          "
            +"		SUM(tab1.qty) as qty_total,                                                                                 "
            +"		SUM(tab1.amount) as amount_total                                                                                              "
            +"	FROM                                                                                                                           "
            +"		b_in tab1                                                                                                                  "
            +"		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                        "
            +"		LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id                                                                    "
            +"		LEFT JOIN m_warehouse t3 ON t3.id = tab1.warehouse_id                                                                     "
            +"		LEFT JOIN m_location t4 ON t4.id = tab1.location_id                                                                       "
            +"		LEFT JOIN m_bin t5 ON t5.id = tab1.bin_id                                                                                 "
            +"		LEFT JOIN m_goods_spec t6 ON t6.id = tab1.sku_id                                                                          "
            +"		LEFT JOIN m_goods_spec t7 ON t7.code = tab1.sku_code                                                                      "
            +"		LEFT JOIN m_unit t8 ON t8.id = tab1.unit_id                                                                               "
            +"		LEFT JOIN s_dict_data tab3 ON tab3.code = '" + DictConstant.DICT_B_IN_STATUS + "' AND tab3.dict_value = tab1.status                          "
            +"		LEFT JOIN s_dict_data tab4 ON tab4.code = '" + DictConstant.DICT_B_IN_TYPE + "' AND tab4.dict_value = tab1.type                              "
            +"    LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                              "
            +"    LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                              "
            +"		WHERE TRUE                                                                                                                 "
            +"		 AND (tab1.status = #{param.status} or #{param.status} is null or #{param.status} = '')                                 "
            +"		 AND (tab1.code = #{param.code} or #{param.code} is null or #{param.code} = '')                                         "
            +"		 AND (tab1.type = #{param.type} or #{param.type} is null or #{param.type} = '')                                         "
            +"		 AND (tab1.owner_id = #{param.owner_id} or #{param.owner_id} is null)                                                    "
            +"		 AND (tab1.consignor_id = #{param.consignor_id} or #{param.consignor_id} is null)                                        "
            +"		 AND (tab1.warehouse_id = #{param.warehouse_id} or #{param.warehouse_id} is null)                                        "
            +"		 AND (tab1.sku_id = #{param.sku_id} or #{param.sku_id} is null)                                                          "

            + "   <if test='param.status_list != null and param.status_list.length!=0' >                                                    "
            + "    and tab1.status in                                                                                                        "
            + "        <foreach collection='param.status_list' item='item' index='index' open='(' separator=',' close=')'>                 "
            + "         #{item}                                                                                                              "
            + "        </foreach>                                                                                                            "
            + "   </if>                                                                                                                      "

            + "   <if test='param.type_list != null and param.type_list.length!=0' >                                                       "
            + "    and tab1.type in                                                                                                         "
            + "        <foreach collection='param.type_list' item='item' index='index' open='(' separator=',' close=')'>                  "
            + "         #{item}                                                                                                              "
            + "        </foreach>                                                                                                            "
            + "   </if>                                                                                                                      "

            // 商品查询：goods_name
            + "   <if test='param.goods_name != null' >                                                                                      "
            + "   and (tab1.sku_code like CONCAT('%', #{param.goods_name}, '%') or t6.name like CONCAT('%', #{param.goods_name}, '%'))    "
            + "   </if>                                                                                                                      "

            +"		ORDER BY tab1.c_time DESC                                                                                                 "
            +" </script>                                                                                                                     ")
    BInVo querySum(@Param("param") BInVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     *
     * @param id
     * @return
     */
    @Select("    "
            + "  select count(1)                 "
            + "    from b_in t1                  "
            + "   where t1.lot = #{p1}           "
            + "                                  ")
    Integer countLot(@Param("p1") String id);

    /**
     * 悲观锁
     *
     * @param id
     * @return
     */
    @Select("    "
            + "  select *                        "
            + "    from b_in t1                  "
            + "   where t1.id = #{p1}            "
            + "     for update                   "
            + "                                  ")
    BInEntity setBillInForUpdate(@Param("p1") Integer id);
}
