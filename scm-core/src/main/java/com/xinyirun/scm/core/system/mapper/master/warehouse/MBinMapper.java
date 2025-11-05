package com.xinyirun.scm.core.system.mapper.master.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.warehouse.MBinEntity;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinExportVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MBinVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 库位 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface MBinMapper extends BaseMapper<MBinEntity> {

    String common_select = "  "
            + "     SELECT                                                                     "
            + "            t.*,                                                                "
            + "            t4.name as location_name,                                           "
            + "            t4.id as location_id,                                               "
            + "            t3.name as warehouse_name,                                          "
            + "            t3.code as warehouse_code,                                          "
            + "            t3.id as warehouse_id,                                              "
            + "            t3.short_name as warehouse_short_name,                              "
            + "            t3.enable_location as enable_location,                              "
            + "            t3.enable_bin as enable_bin,                                        "
            + "            t3.address as warehouse_address,                                    "
            + "            t3.enable warehouse_enable,                                         "
            + "            t1.name as c_name,                                                  "
            + "            t5.label warehouse_type_name,                                       "
            + "            t3.warehouse_type,                                                  "
            + "            t2.name as u_name                                                   "
            + "       FROM                                                                     "
            + "  	       m_bin t                                                             "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                        "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                        "
            + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                            "
            + "  LEFT JOIN m_location t4 ON t.location_id = t4.id                              "
            + "  LEFT JOIN v_dict_info t5 ON t5.code = '" + DictConstant.DICT_M_WAREHOUSE_TYPE + "' and t5.dict_value = t3.warehouse_type"
            + "                                                                                "
            ;

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("<script>    "
            + common_select
            + "  where true "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null)                                                                                                              "
            + "    and (t3.enable = #{p1.warehouse_enable,jdbcType=BOOLEAN} or #{p1.warehouse_enable,jdbcType=BOOLEAN} is null)                                                                                                              "
//            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                                                                                                "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                                                                "
            + "    and (t3.name like CONCAT ('%',#{p1.warehouse_name,jdbcType=VARCHAR},'%') or #{p1.warehouse_name,jdbcType=VARCHAR} is null or #{p1.warehouse_name,jdbcType=VARCHAR} = '')                                                                           "
            + "    and (t3.id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                                     "
            + "    and (t4.name like CONCAT ('%',#{p1.location_name,jdbcType=VARCHAR},'%') or #{p1.location_name,jdbcType=VARCHAR} is null)                                                                             "
            + "    and (t4.id = #{p1.location_id,jdbcType=INTEGER} or #{p1.location_id,jdbcType=INTEGER} is null)                                                                                                       "
            + "    and (CONCAT(t.code,t.name,t.name_pinyin,t.name_pinyin_initial)  like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '')        "
            // 全称，简称，拼音，简拼，所有的仓库、库区、库位
            + "    and (CONCAT(t3.name_pinyin,t3.short_name_pinyin,t3.name_pinyin_initial,t3.short_name_pinyin_initial,                                                                                                 "
            + "     t3.name,t3.short_name,t4.name,t4.short_name,t.name)  like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null)              "
            + "  <if test='p1.filterWarehouseType != null and p1.filterWarehouseType.length != 0'>"
            + "    and t3.warehouse_type not in "
            + "        <foreach collection='p1.filterWarehouseType' item='item' index='index' open='(' separator=',' close=')'>    "
            + "         #{item}                                                                                         "
            + "        </foreach>                                                                                       "
            + "  </if>                                                                                                  "
            + "</script>      ")
    IPage<MBinVo> selectPage(Page page, @Param("p1") MBinVo searchCondition);

    /**
     * 页面查询列表
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t3.name like CONCAT ('%',#{p1.warehouse_name,jdbcType=VARCHAR},'%') or #{p1.warehouse_name,jdbcType=VARCHAR} is null) "
            + "    and (t3.id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null) "
            + "    and (t4.name like CONCAT ('%',#{p1.location_name,jdbcType=VARCHAR},'%') or #{p1.location_name,jdbcType=VARCHAR} is null) "
            + "    and (t4.id = #{p1.location_id,jdbcType=INTEGER} or #{p1.location_id,jdbcType=INTEGER} is null) "
            + "      ")
    List<MBinVo> selectList(@Param("p1") MBinVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.name =  #{p1,jdbcType=VARCHAR}"
            + "    and t.warehouse_id =  #{p2,jdbcType=INTEGER}"
            + "    and t.location_id =  #{p3,jdbcType=INTEGER}"
            + "      ")
    List<MBinEntity> selectByName(@Param("p1") String name,@Param("p2")int warehouse_id,@Param("p3")int location_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.code =  #{p1,jdbcType=VARCHAR}"
            + "    and t.warehouse_id =  #{p2,jdbcType=INTEGER}"
            + "    and t.location_id =  #{p3,jdbcType=INTEGER}"
            + "      ")
    List<MBinVo> selectByCode(@Param("p1") String code,@Param("p2")int warehouse_id,@Param("p3")int location_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param warehouse_id
     * @param location_id
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.warehouse_id =  #{p1,jdbcType=INTEGER}"
            + "    and t.location_id =  #{p2,jdbcType=INTEGER}"
            + "    and t.enable =  true"
            + "      ")
    List<MBinEntity> selectByWarehouseId(@Param("p1")int warehouse_id,@Param("p2")int location_id);


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
    List<MBinEntity> selectIdsIn(@Param("p1") List<MBinVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MBinVo selectId(@Param("p1") int id);


    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MBinVo getWare_loc_bin(@Param("p1") MBinVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param warehouse_id,location_id
     * @return
     */
    @Select("    "
            + "  SELECT                                                                                                                                                   "
            + "         t3.*                                                                                                                                  "
            + "    FROM                                                                                                                                       "
            + "         m_warehouse t1                                                                                                                        "
            + "         LEFT JOIN m_location t2 ON t1.id = t2.warehouse_id AND t2.enable = '"+ SystemConstants.ENABLE_TRUE + "'                             "
            + "         LEFT JOIN m_bin t3 ON t3.warehouse_id = t1.id  AND t3.location_id = t2.id AND t3.enable = '"+ SystemConstants.ENABLE_TRUE + "'      "
            + "    WHERE                                                                                                                                      "
            + "         t1.id =  #{p1,jdbcType=INTEGER}                                                                                                      "
            + "         and t1.enable_location = '"+ SystemConstants.ENABLE_FALSE +"'                                                                       "
            + "         and t1.enable_bin = '"+ SystemConstants.ENABLE_FALSE +"'                                                                            "
            + "         and t1.enable = '"+ SystemConstants.ENABLE_TRUE +"'                                                                                 "
            + "      ")
    MBinEntity selecBinByWarehouseId(@Param("p1")int warehouse_id);

    /**
     * 导出查询列表
     * 用于库位管理的导出功能，支持全部导出和选择导出
     *
     * @param searchCondition 查询条件和导出参数
     * @return List<MBinExportVo>
     */
    @Select({" <script>                                                                                "
            + "     SELECT                                                                           "
                    + "            @row_num:= @row_num+ 1 as no,                                     "
                    + "            t3.name as warehouse_name,                                        "
                    + "            t4.name as location_name,                                         "
                    + "            t.name as name,                                                   "
                    + "            if(t.enable, '是', '否') enable,                                   "
                    + "            if(t.is_default, '是', '否') as is_default_status,                 "
                    + "            t1.name as c_name,                                                "
                    + "            t.c_time,                                                         "
                    + "            t2.name as u_name,                                                "
                    + "            t.u_time                                                          "
                    + "       FROM                                                                   "
                    + "  	       m_bin t                                                           "
                    + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                      "
                    + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                      "
                    + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                          "
                    + "  LEFT JOIN m_location t4 ON t.location_id = t4.id                            "
                    + " ,(select @row_num:=0) t5                                                     "
                    + "  where true                                                                  "
                    + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
                    + "    and (t3.enable = #{p1.warehouse_enable,jdbcType=BOOLEAN} or #{p1.warehouse_enable,jdbcType=BOOLEAN} is null) "
                    + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
                    + "    and (t3.name like CONCAT ('%',#{p1.warehouse_name,jdbcType=VARCHAR},'%') or #{p1.warehouse_name,jdbcType=VARCHAR} is null or #{p1.warehouse_name,jdbcType=VARCHAR} = '') "
                    + "    and (t3.id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null) "
                    + "    and (t4.name like CONCAT ('%',#{p1.location_name,jdbcType=VARCHAR},'%') or #{p1.location_name,jdbcType=VARCHAR} is null) "
                    + "    and (t4.id = #{p1.location_id,jdbcType=INTEGER} or #{p1.location_id,jdbcType=INTEGER} is null) "
                    + "    and (CONCAT(t.code,t.name,t.name_pinyin,t.name_pinyin_initial)  like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '') "
                    + "    and (CONCAT(t3.name_pinyin,t3.short_name_pinyin,t3.name_pinyin_initial,t3.short_name_pinyin_initial, "
                    + "     t3.name,t3.short_name,t4.name,t4.short_name,t.name)  like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null) "
                    + "  <if test='p1.filterWarehouseType != null and p1.filterWarehouseType.length != 0'>"
                    + "    and t3.warehouse_type not in "
                    + "        <foreach collection='p1.filterWarehouseType' item='item' index='index' open='(' separator=',' close=')'>    "
                    + "         #{item}                                                               "
                    + "        </foreach>                                                            "
                    + "  </if>                                                                       "
                    + "   <if test='p1.ids != null and p1.ids.length != 0' >                       "
                    + "    and t.id in                                                              "
                    + "        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>    "
                    + "         #{item}                                                             "
                    + "        </foreach>                                                          "
                    + "   </if>                                                                    "
            + "  ORDER BY t.u_time DESC, t.id DESC                                               "
            + " </script>                                                                        "
    })
    List<MBinExportVo> selectExportList(@Param("p1") MBinVo searchCondition);

    /**
     * ========= 删除校验相关查询方法 =========
     * 参考仓库管理MWarehouseMapper的校验模式
     */

    /**
     * 检查库位是否有库存记录
     * @param bin_id 库位ID
     * @return 库存记录数量
     */
    @Select("SELECT COUNT(1) FROM m_inventory WHERE bin_id = #{bin_id} AND is_del = 0")
    Integer checkInventoryExists(@Param("bin_id") Integer bin_id);

    /**
     * 检查库位是否有入库明细记录
     * @param bin_id 库位ID  
     * @return 入库明细记录数量
     */
    @Select("SELECT COUNT(1) FROM b_in_detail WHERE bin_id = #{bin_id} AND is_del = 0")
    Integer checkInboundExists(@Param("bin_id") Integer bin_id);

    /**
     * 检查库位是否有出库明细记录
     * @param bin_id 库位ID
     * @return 出库明细记录数量
     */
    @Select("SELECT COUNT(1) FROM b_out_detail WHERE bin_id = #{bin_id} AND is_del = 0")
    Integer checkOutboundExists(@Param("bin_id") Integer bin_id);
}
