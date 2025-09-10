package com.xinyirun.scm.core.system.mapper.master.goods;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.goods.MGoodsEntity;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsExportVo;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsVo;
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
public interface MGoodsMapper extends BaseMapper<MGoodsEntity> {
    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t5.name as category_name,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_goods t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_category t5 ON t.category_id = t5.id                "
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
            + "    and (t5.name like CONCAT ('%',#{p1.category_name,jdbcType=VARCHAR},'%') or #{p1.category_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.id = #{p1.category_id} or #{p1.category_id} is null) "
            + "      ")
    IPage<MGoodsVo> selectPage(Page page, @Param("p1") MGoodsVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.enable =  true "
            + "    and t.name =  #{p1} "
            + "      ")
    List<MGoodsEntity> selectByName(@Param("p1") String name);


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
    List<MGoodsEntity> selectIdsIn(@Param("p1") List<MGoodsVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MGoodsVo selectId(@Param("p1") int id);

    /**
     * 导出
     *
     * @param searchConditionList 入参
     * @return List<MGoodsExportVo>
     */
    @Select({"<script>                                                                                                 "
            + "     SELECT                                                                                             "
            + "            @row_num:= @row_num+ 1 as no,                                                               "
            + "            t.name,                                                                                     "
            + "            t.code,                                                                                     "
            + "            if(t.enable, '是', '否') enable,                                                             "
            + "            t.c_time,                                                                                   "
            + "            t.u_time,                                                                                   "
            + "            t5.name as category_name,                                                                   "
            + "            t1.name as c_name,                                                                          "
            + "            t2.name as u_name                                                                           "
            + "       FROM                                                                                             "
            + "  	       m_goods t                                                                                   "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                "
            + "  LEFT JOIN m_category t5 ON t.category_id = t5.id                                                      "
            + " ,(select @row_num:=0) t6                                                                               "
            + "  where true                                                                                            "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t5.name like CONCAT ('%',#{p1.category_name,jdbcType=VARCHAR},'%') or #{p1.category_name,jdbcType=VARCHAR} is null) "
            + "    and (t5.id = #{p1.category_id} or #{p1.category_id} is null) "
            + "   <if test='p1.ids != null and p1.ids.length != 0' >                                                   "
            + "    and t.id in                                                                                         "
            + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>        "
            + "         #{item}                                                                                        "
            + "        </foreach>                                                                                      "
            + "   </if>                                                                                                "
            + " </script>                                                                                              "

    })
    List<MGoodsExportVo> exportList(@Param("p1") MGoodsVo searchConditionList);

    /**
     * 检查物料是否有库存记录
     * @param goods_id 物料ID
     * @return 库存记录数量
     */
    @Select("SELECT COUNT(1) FROM m_inventory WHERE goods_id = #{goods_id}")
    Integer checkInventoryExists(@Param("goods_id") Integer goods_id);

    /**
     * 检查物料是否有入库记录
     * @param goods_id 物料ID
     * @return 入库记录数量
     */
    @Select("SELECT COUNT(1) FROM b_in_detail WHERE goods_id = #{goods_id}")
    Integer checkInboundExists(@Param("goods_id") Integer goods_id);

    /**
     * 检查物料是否有出库记录
     * @param goods_id 物料ID
     * @return 出库记录数量
     */
    @Select("SELECT COUNT(1) FROM b_out_detail WHERE goods_id = #{goods_id}")
    Integer checkOutboundExists(@Param("goods_id") Integer goods_id);

    /**
     * 检查物料是否有采购订单记录
     * @param goods_id 物料ID
     * @return 采购订单记录数量
     */
    @Select("SELECT COUNT(1) FROM b_po_order_detail WHERE goods_id = #{goods_id}")
    Integer checkPurchaseOrderExists(@Param("goods_id") Integer goods_id);

    /**
     * 检查物料是否有销售订单记录
     * @param goods_id 物料ID
     * @return 销售订单记录数量
     */
    @Select("SELECT COUNT(1) FROM b_so_order_detail WHERE goods_id = #{goods_id}")
    Integer checkSalesOrderExists(@Param("goods_id") Integer goods_id);

    /**
     * 综合检查物料业务关联情况
     * @param goods_id 物料ID
     * @return 关联业务信息
     */
    @Select("SELECT "
            + "  (SELECT COUNT(1) FROM m_inventory WHERE goods_id = #{goods_id}) as inventory_count, "
            + "  (SELECT COUNT(1) FROM b_in_detail WHERE goods_id = #{goods_id}) as inbound_count, "
            + "  (SELECT COUNT(1) FROM b_out_detail WHERE goods_id = #{goods_id}) as outbound_count, "
            + "  (SELECT COUNT(1) FROM b_po_order_detail WHERE goods_id = #{goods_id}) as purchase_order_count, "
            + "  (SELECT COUNT(1) FROM b_so_order_detail WHERE goods_id = #{goods_id}) as sales_order_count")
    java.util.Map<String, Object> checkGoodsBusinessAssociations(@Param("goods_id") Integer goods_id);

}
