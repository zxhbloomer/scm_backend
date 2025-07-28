package com.xinyirun.scm.core.system.mapper.business.warehouse.relation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.relation.MWarehouseRelationEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MRelationCountsVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MRelationTreeVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MWarehouseRelationVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MWarehouseTransferVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Repository
public interface MWarehouseRelationMapper extends BaseMapper<MWarehouseRelationEntity> {


    String warehouseListSql = "                                                                                             "
            + "                    select                                                                                   "
            + "                           t3.*,                                                                             "
            + "                           c_staff.name as c_name,                                                           "
            + "                           u_staff.name as u_name                                                            "
            + "                     FROM                                                                                    "
            + "                          b_warehouse_group t1                                                           "
            + "               inner JOIN b_warehouse_group_relation t2 ON t2.warehouse_group_id = t1.id                 "
            + "                LEFT JOIN m_warehouse t3  ON t3.id = t2.warehouse_id                                         "
            + "                LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                            "
            + "                LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                            "
            + "                    where true                                                                               "
            +"                                                                                                              ";

    /**
     * 左侧树查询
     */
    @Select("  <script>  "
            + "                  select t1.* ,                                                                              "
            + "                         t2.code,                                                                            "
            + "                         IFNULL(t3.name,'') as label,                                                        "
            + "                         t3.name,                                                                            "
            + "                         t5.warehouse_name,                                                                  "
            + "                         t3.short_name,                                                                      "
            + "                         IFNULL(t2.type,'0') as type,                                                        "
            + "                         t2.son_count,                                                                       "
            + "                         t2.u_time,                                                                          "
            + "                         t2.dbversion ,                                                                      "
            + "         <if test='p1.serial_id != null'>                                                                    "
            + "                         (case when t4.id is not null then true else false end) is_enable,                   "
            + "         </if>                                                                                               "
            + "                         t2.serial_id ,                                                                      "
            + "                         t2.serial_type                                                                      "
            + "                    from v_wh_relation t1                                                                    "
            + "               left join m_warehouse_relation t2 on t2.id = t1.id                                            "
            + "               left join b_warehouse_group t3 on t3.id = t2.serial_id                                        "
            + "         <if test='p1.serial_id != null'>                                                                    "
            + "               left join b_warehouse_relation t4                                                             "
            + "                      on t4.serial_id = #{p1.serial_id,jdbcType=INTEGER}                                     "
            + "                     and t4.serial_type = 'm_position'                                                       "
            + "                     and t4.warehouse_relation_code = t2.code                                                "
            + "         </if>                                                                                               "
            + "	              left join (                                                                                   "
            + "	                SELECT                                                                                      "
            + "	                  t1.warehouse_group_id,                                                                    "
            + "	                  concat(group_concat(t2.name),group_concat(t2.short_name)) warehouse_name                  "
            + "	                FROM                                                                                        "
            + "	                	b_warehouse_group_relation t1                                                           "
            + "	                	LEFT JOIN m_warehouse t2 ON t2.id = t1.warehouse_id                                     "
            + "	                	GROUP BY t1.warehouse_group_id ) t5 on  t5.warehouse_group_id = t3.id                   "
            + "                   where true                                                                                "
            + "                order by t2.code                                                                             "
            + "  </script>    ")
    List<MRelationTreeVo> getTreeList(@Param("p1") MRelationTreeVo searchCondition);

    /**
     * 查询添加的子结点是否合法
     * @return
     */
    @Select("                                                                                                        "
            + "           SELECT                                                                                         "
            + "           	count(1)                                                                                     "
            + "           FROM                                                                                           "
            + "           	m_warehouse_relation t1                                                                      "
            + "           WHERE true                                                                                     "
            + "           	and t1.CODE LIKE CONCAT (#{p1},'%')                                                          "
            + "           	and t1.type > #{p2}                                                                          "
            + "                                                                                                          ")
    Integer selectNodeInsertStatus(@Param("p1")String code, @Param("p2")String type);

    /**
     * check逻辑，查看是否存在重复的子组织
     * @param vo
     * @return
     */
    @Select("                                                                                                        "
            + "           SELECT                                                                                         "
            + "           	count(1)                                                                                     "
            + "           FROM                                                                                           "
            + "           	m_warehouse_relation t1                                                                      "
            + "           WHERE true                                                                                     "
            + "           	AND t1.serial_type = #{p1.serial_type,jdbcType=VARCHAR}                                      "
            + "           	AND t1.serial_id = #{p1.serial_id,jdbcType=BIGINT}                                           "
            + "           	AND (t1.id  =  #{p2} or #{p2} is null)                                                       "
            + "                                                                                                          ")
    Integer getCountBySerial(@Param("p1") MWarehouseRelationVo vo, @Param("p2") Integer equal_id);


    /**
     * 获取单条数据
     * @param bean
     * @return
     */
    @Select("  <script>  "
            + "                  select t1.* ,                                                                              "
            + "                         t2.code,                                                                            "
            + "                         IFNULL(t3.name,'') as label,                                                        "
            + "                         t3.name,                                                                            "
            + "                         t3.short_name,                                                                      "
            + "                         IFNULL(t2.type,'0') as type,                                                        "
            + "                         t2.son_count,                                                                       "
            + "                         t2.u_time,                                                                          "
            + "                         t2.dbversion ,                                                                      "
            + "         <if test='p1.serial_id != null'>                                                                    "
            + "                         (case when t4.id is not null then true else false end) is_enable,                   "
            + "         </if>                                                                                               "
            + "                         t2.serial_id ,                                                                      "
            + "                         t2.serial_type                                                                      "
            + "                    from v_wh_relation t1                                                                    "
            + "               left join m_warehouse_relation t2 on t2.id = t1.id                                            "
            + "               left join b_warehouse_group t3 on t3.id = t2.serial_id                                        "
            + "         <if test='p1.serial_id != null'>                                                                    "
            + "               left join b_warehouse_relation t4                                                             "
            + "                      on t4.serial_id = #{p1.serial_id,jdbcType=INTEGER}                                     "
            + "                     and t4.serial_type = 'm_position'                                                       "
            + "                     and t4.warehouse_relation_code = t2.code                                                "
            + "         </if>                                                                                               "
            + "                   where true                                                                                "
            + "                     and (t1.id = #{p1.id,jdbcType=INTEGER})                                                 "
            + "            </script>                                                               ")
    MWarehouseRelationVo selectByid(@Param("p1") MWarehouseRelationVo bean);

    /**
     * 根据code，进行 like 'code%'，匹配当前结点以及子结点
     * @param vo
     * @return
     */
    @Select("                                                                                                        "
            + "        select *                                                                                          "
            + "          from m_warehouse_relation t1                                                                    "
            + "         where true                                                                                       "
            + "           and t1.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%')                                      "
            + "                                                                                                          ")
    List<MWarehouseRelationVo> getDataByCode(@Param("p1") MWarehouseRelationVo vo);

    /**
     * 获取所有的组织以及子组织数量，仅仅是数量
     * @param searchCondition
     * @return
     */
    @Select("                                                                                                           "
            + "   select                                                                                                    "
            + "           IFNULL((                                                                                          "
            + "            select count(1) from ( " + warehouseListSql +")     tab2                                             "
            + "            ),0) as warehouse_count                                                                             "
            + "                                                                                                             ")
    MRelationCountsVo getAllRelationDataCount(@Param("p1") MWarehouseRelationVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("   <script>  "
            + "                  select t1.* ,                                                                              "
            + "                         t2.code,                                                                            "
            + "                         IFNULL(t3.name,'') as label,                                                        "
            + "                         t3.name,                                                                            "
            + "                         t3.short_name,                                                                      "
            + "                         IFNULL(t2.type,'0') as type,                                                        "
            + "                         t2.son_count,                                                                       "
            + "                         t2.u_time,                                                                          "
            + "                         t2.dbversion ,                                                                      "
            + "         <if test='p1.serial_id != null'>                                                                    "
            + "                         (case when t4.id is not null then true else false end) is_enable,                  "
            + "         </if>                                                                                               "
            + "                         t2.serial_id ,                                                                      "
            + "                         t2.serial_type                                                                      "
            + "                    from v_wh_relation t1                                                                    "
            + "               left join m_warehouse_relation t2 on t2.id = t1.id                                            "
            + "               left join b_warehouse_group t3 on t3.id = t2.serial_id                                        "
            + "         <if test='p1.serial_id != null'>                                                                    "
            + "               left join b_warehouse_relation t4                                                             "
            + "                      on t4.serial_id = #{p1.serial_id,jdbcType=INTEGER}                                            "
            + "                     and t4.serial_type = 'm_position'                                                       "
            + "                     and t4.warehouse_relation_code = t2.code                                                "
            + "         </if>                                                                                               "
            + "                   where true                                                                                              "
            + "                     and (t2.code like CONCAT (#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)    "
            + "     </script>  ")
    List<MRelationTreeVo> select(@Param("p1") MWarehouseRelationVo searchCondition);


    /**
     * 仓库查询列表
     * @param searchCondition
     * @return
     */
    @Select("    <script>                                                                                            "
            + "       SELECT distinct t3.*                                                                                  "
            + "         FROM m_warehouse_relation t1                                                                        "
            + "   inner JOIN b_warehouse_group_relation t2                                                                  "
            + "           ON t1.serial_id = t2.warehouse_group_id                                                           "
            + "   inner join m_warehouse t3                                                                                 "
            + "           on t3.id = t2.warehouse_id                                                                        "
            + "        where t1.serial_type = 'b_warehouse_group'                                                           "
            + "          and t1.code in                                                                                     "
            + "        <foreach collection='p1.relation_codes' item='item' index='index' open='(' separator=',' close=')'>  "
            + "            #{item.code,jdbcType=VARCHAR}                                                                    "
            + "        </foreach>                                                                                           "
            + "   </script>                                                                                                 ")
    IPage<MWarehouseVo> getAllWarehouseListByPosition(Page page, @Param("p1") MRelationTreeVo searchCondition);

    /**
     * 仓库查询列表
     * @param searchCondition
     * @return
     */
    @Select("    <script>                                                                                               "
            + "       select count(1)                                                                                   "
            + "         from                                                                                            "
            + "       (                                                                                                 "
            + "       SELECT distinct t3.*                                                                              "
            + "         FROM m_warehouse_relation t1                                                                    "
            + "   inner JOIN b_warehouse_group_relation t2                                                              "
            + "           ON t1.serial_id = t2.warehouse_group_id                                                       "
            + "   inner join m_warehouse t3                                                                             "
            + "           on t3.id = t2.warehouse_id                                                                    "
            + "   inner join b_warehouse_group t4                                                                       "
            + "           on t4.id = t2.warehouse_group_id                                                              "
            + "        where t1.serial_type = 'b_warehouse_group'                                                       "
            + "       	    and t4.code in                                                                              "
            + "        <foreach collection='p1.codes' item='item' index='index' open='(' separator=',' close=')'>       "
            + "         #{item,jdbcType=INTEGER}                                                                        "
            + "        </foreach>                                                                                       "
            + "        )   subt                                                                                         "
            + "   </script> ")
    Integer getAllWarehouseListByPositionCount( @Param("p1") MRelationTreeVo searchCondition);

    /**
     * 仓库查询列表
     * @param searchCondition
     * @return
     */
    @Select("  <script>                                                                                                 "
            + "       	 select t3.*,                                                                                   "
            + "       	        t4.type as b_warehouse_group_type,                                                      "
            + "       	        t4.name as warehouse_group_name                                                         "
            + "        from b_warehouse_group_relation t2                                                               "
            + "        inner join b_warehouse_group t4                                                                  "
            + "                on t4.id = t2.warehouse_group_id                                                         "
            + "        inner join m_warehouse t3                                                                        "
            + "                on t2.warehouse_id = t3.id                                                               "
            + "       	    where t4.code in                                                                            "
            + "        <foreach collection='p1.codes' item='item' index='index' open='(' separator=',' close=')'>       "
            + "         #{item,jdbcType=INTEGER}                                                                        "
            + "        </foreach>                                                                                       "
            + "  </script>                                                                                              ")
    IPage<MWarehouseVo> getWarehousePageList(Page page, @Param("p1") MRelationTreeVo searchCondition);

    /**
     * 仓库查询列表
     * @param searchCondition
     * @return
     */
    @Select("  <script>                                                                                                 "
            + "       	 select t3.*,                                                                                   "
            + "       	        t4.type as b_warehouse_group_type,                                                      "
            + "       	        t4.name as warehouse_group_name                                                         "
            + "       	   from m_warehouse_relation t1                                                                 "
            + "        inner join b_warehouse_group_relation t2                                                         "
            + "                on t1.serial_id = t2.warehouse_group_id                                                  "
            + "        inner join b_warehouse_group t4                                                                  "
            + "                on t4.id = t2.warehouse_group_id                                                         "
            + "        inner join m_warehouse t3                                                                        "
            + "                on t2.warehouse_id = t3.id                                                               "
            + "       	    where t4.code in                                                                            "
            + "        <foreach collection='p1.codes' item='item' index='index' open='(' separator=',' close=')'>       "
            + "         #{item,jdbcType=INTEGER}                                                                        "
            + "        </foreach>                                                                                       "
            + "  </script>                                                                                              ")
    List<MWarehouseVo> getWarehouseList(@Param("p1") MRelationTreeVo searchCondition);


    /**
     * 获取全部仓库
     * @param condition
     * @return
     */
    @Select("                                                                        "
            + "      select t1.id AS `key`,             "
            + "             t1.short_name AS label      "
            + "        from m_warehouse t1              "
            + "       where t1.enable  = true           "
            + "    order by t1.name                     "
            + "                                                                          ")
    List<MWarehouseTransferVo> getAllWarehouseTransferList(@Param("p1")MWarehouseTransferVo condition);

    /**
     * 获取该岗位下，全部员工
     * @param condition
     * @return
     */
    @Select("                                                                                                           "
            + "       	 select t3.id                                                                                   "
            + "            from b_warehouse_group_relation t2                                                           "
            + "        inner join m_warehouse t3                                                                        "
            + "                on t2.warehouse_id = t3.id                                                               "
            + "        inner join b_warehouse_group t4                                                                  "
            + "                on t4.id = t2.warehouse_group_id                                                         "
            + "       	    where (t4.code = #{p1.code,jdbcType=VARCHAR})                                               "
            + "       	        and t3.enable = true                                                                    "
            + "  ")
    List<Long> getUsedWarehouseTransferList(@Param("p1")MWarehouseTransferVo condition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Delete("                                                                                        "
            + "   delete from b_warehouse_group_relation t4                                          "
            + "     	  where exists (                                                             "
            + "                select 1                                                              "
            + "                  from (                                                              "
            + "              	  	  	select t2.id                                                 "
            + "   	  	   			  from m_warehouse_relation t1                                   "
            + "   	            inner join b_warehouse_group_relation t2                             "
            + "   	                    on t1.serial_id = t2.warehouse_group_id                      "
            + "   	            inner join m_warehouse t3                                            "
            + "   	                    on t2.warehouse_id = t3.id                                   "
            + "   	  	             where (t1.code like CONCAT (#{p1.code,jdbcType=VARCHAR},''))   "
            + "   	                   and t4.id = t2.id                                             "
            + "                          ) sub                                                       "
            + "     	  )                                                                          "
            + "      ")
    void realDeleteByCode(@Param("p1") MWarehouseTransferVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Delete("                                                                                        "
            + "   delete from b_warehouse_group_relation t1                                          "
            + "     	  where TRUE                                                                 "
            + "   	 and t1.warehouse_group_id = #{p1.warehouse_group_id,jdbcType=VARCHAR}           "
            + "      ")
    void realDeleteById(@Param("p1") MWarehouseTransferVo searchCondition);
}
