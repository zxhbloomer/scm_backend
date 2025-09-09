package com.xinyirun.scm.core.system.mapper.master.warehouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.warehouse.MLocationEntity;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationExportVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MLocationVo;
import com.xinyirun.scm.common.constant.SystemConstants;
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
public interface MLocationMapper extends BaseMapper<MLocationEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.name as warehouse_name,                                          "
            + "            t3.id as warehouse_id,                                          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_location t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id                                 "
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
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
//            + "    and (CONCAT (t.code,t.name,t.short_name,t.name_pinyin,t.short_name_pinyin,t.name_pinyin_initial,t.short_name_pinyin_initial) like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '')       "
//            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                                                                                                                                                          "
//            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                                                                                                                            "
//            + "    and (t3.id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null)                                                                                                                                                                 "
            + "    and (t3.name like CONCAT ('%',#{p1.warehouse_name,jdbcType=VARCHAR},'%') or #{p1.warehouse_name,jdbcType=VARCHAR} is null or #{p1.warehouse_name,jdbcType=VARCHAR} = '') "
            + "    and (t3.code like CONCAT ('%',#{p1.warehouse_code,jdbcType=VARCHAR},'%') or #{p1.warehouse_code,jdbcType=VARCHAR} is null or #{p1.warehouse_code,jdbcType=VARCHAR} = '') "
            + "      ")
    IPage<MLocationVo> selectPage(Page page, @Param("p1") MLocationVo searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null) "
            + "    and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null) "
            + "    and (t.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null) "
            + "    and (t3.name like CONCAT ('%',#{p1.warehouse_name,jdbcType=VARCHAR},'%') or #{p1.warehouse_name,jdbcType=VARCHAR} is null) "
            + "    and (t3.id = #{p1.warehouse_id,jdbcType=INTEGER} or #{p1.warehouse_id,jdbcType=INTEGER} is null) "
            + "      ")
    List<MLocationVo> selectList(@Param("p1") MLocationVo searchCondition);

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
            + "      ")
    List<MLocationEntity> selectByName(@Param("p1") String name,@Param("p2")int warehouse_id);

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
            + "      ")
    List<MLocationEntity> selectByCode(@Param("p1") String code,@Param("p2")int warehouse_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param shortName
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.short_name =  #{p1,jdbcType=VARCHAR}"
            + "    and t.warehouse_id =  #{p2,jdbcType=INTEGER}"
            + "      ")
    List<MLocationEntity> selectByShortName(@Param("p1") String shortName,@Param("p2")int warehouse_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param warehouse_id
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.enable =  true"
            + "    and t.warehouse_id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    List<MLocationEntity> selectByWarehouseId(@Param("p1")int warehouse_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param warehouse_id
     * @return
     */
    @Select("    "
            + "  SELECT                                                                                                                                                   "
            + "         t2.*                                                                                                                                  "
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
    MLocationEntity selectLocationByWarehouseId(@Param("p1")int warehouse_id);

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
    List<MLocationEntity> selectIdsIn(@Param("p1") List<MLocationVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1,jdbcType=INTEGER}"
            + "      ")
    MLocationVo selectId(@Param("p1") int id);

    /**
     * 导出专用查询方法，支持动态排序
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @param orderByClause 动态排序子句
     * @return
     */
    @Select("""
        <script>
        SELECT                                                                                     
                @row_num := @row_num + 1 AS no,                                                       
                t.name,                                                          
                t.short_name,                                                          
                t3.name as warehouse_name,                                                          
                if(t.enable, '启用', '停用') as enable_status,                                                     
                t1.name as c_name,                                                                  
                t.c_time,                                                                           
                t2.name as u_name,                                                                   
                t.u_time                                                                           
           FROM                                                                                     
                  m_location t                                                                        
          LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                        
          LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                        
          LEFT JOIN m_warehouse t3 ON t.warehouse_id = t3.id,                                            
                (select @row_num:=0) t5                                                                                                    
          where true 
            and (t.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null or #{p1.name,jdbcType=VARCHAR} = '')
            and (t.short_name like CONCAT ('%',#{p1.short_name,jdbcType=VARCHAR},'%') or #{p1.short_name,jdbcType=VARCHAR} is null or #{p1.short_name,jdbcType=VARCHAR} = '')
            and (t3.name like CONCAT ('%',#{p1.warehouse_name,jdbcType=VARCHAR},'%') or #{p1.warehouse_name,jdbcType=VARCHAR} is null or #{p1.warehouse_name,jdbcType=VARCHAR} = '') 
            and (t3.code like CONCAT ('%',#{p1.warehouse_code,jdbcType=VARCHAR},'%') or #{p1.warehouse_code,jdbcType=VARCHAR} is null or #{p1.warehouse_code,jdbcType=VARCHAR} = '') 
            and (t.enable = #{p1.enable,jdbcType=BOOLEAN} or #{p1.enable,jdbcType=BOOLEAN} is null)
            <if test="p1.ids != null and p1.ids.length > 0">
            and t.id in
                <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                 #{item}
                </foreach>
            </if>
          <if test="orderByClause != null and orderByClause != ''">
            ${orderByClause}
          </if>
          <if test="orderByClause == null or orderByClause == ''">
            ORDER BY t.u_time DESC
          </if>
        </script>
        """)
    List<MLocationExportVo> selectExportList(@Param("p1") MLocationVo searchCondition, @Param("orderByClause") String orderByClause);

    /**
     * 检查库区是否有库存数据
     * @param location_id 库区ID
     * @return 库存记录数量
     */
    @Select("SELECT COUNT(1) FROM m_inventory WHERE location_id = #{location_id}")
    Integer checkInventoryExists(@Param("location_id") Integer location_id);

    /**
     * 检查库区是否有库位配置
     * @param location_id 库区ID  
     * @return 库位记录数量
     */
    @Select("SELECT COUNT(1) FROM m_bin WHERE location_id = #{location_id} AND enable = true")
    Integer checkBinExists(@Param("location_id") Integer location_id);

    /**
     * 检查库区是否有入库记录
     * @param location_id 库区ID
     * @return 入库记录数量
     */
    @Select("SELECT COUNT(1) FROM b_in WHERE location_id = #{location_id}")
    Integer checkInboundExists(@Param("location_id") Integer location_id);

    /**
     * 检查库区是否有出库记录  
     * @param location_id 库区ID
     * @return 出库记录数量
     */
    @Select("SELECT COUNT(1) FROM b_out WHERE location_id = #{location_id}")
    Integer checkOutboundExists(@Param("location_id") Integer location_id);
}
