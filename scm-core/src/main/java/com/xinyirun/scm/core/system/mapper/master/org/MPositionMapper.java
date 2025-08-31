package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.user.MPositionInfoVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.RoleItemListTypeHandler;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.PermissionItemListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 岗位主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MPositionMapper extends BaseMapper<MPositionEntity> {


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    // 删除状态字典类型：sys_delete_type
    // 岗位组织类型：m_position
    // 显示未使用状态：10
    @Select("""
        SELECT                                                                                               
               t1.*,                                                                                         
               c_staff.name as c_name,                                                                       
               u_staff.name as u_name,                                                                       
               t2.label as is_del_name,                                                                      
               t3.staff_count,                                                                               
               t4.role_count,                                                                                
               tt4.warehouse_count,                                                                          
               tt5.warehouse_count warehouse_count1,                                                         
               f_get_org_simple_name(vor.code, 'm_group') group_simple_name,                            
               f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                         
               f_get_org_simple_name ( vor.CODE, 'm_dept' ) parent_dept_simple_name,                           
               tt_roles.roleList,                                                                               
               tt_permissions.permission_count,                                                               
               tt_permissions.permissionList                                                                  
          FROM m_position t1                                                                                 
     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       
     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       
     LEFT JOIN v_dict_info AS t2 ON t2.code = 'sys_delete_type'                    
    and t2.dict_value = CONCAT('', t1.is_del)                                                           
     left join (                                                                                             
                  select count(1) staff_count,                                                               
                         subt.serial_id,                                                                       
                         subt.serial_type                                                                    
                    from m_staff_org subt                                                                    
                group by subt.serial_id, subt.serial_type                                                    
                )  t3 on t3.serial_id = t1.id                                                                
           and t3.serial_type = 'm_position'            
     left join (                                                                                             
	            SELECT                                                                                          
	            	count( 1 ) role_count,                                                                      
	            	subt2.position_id                                                                           
	            FROM                                                                                            
	            	s_role subt1                                                                                
	            	INNER JOIN m_role_position subt2 ON subt1.id = subt2.role_id                                
	            WHERE                                                                                           
	            	subt1.is_del = false                                                                        
	            GROUP BY                                                                                        
	            	subt2.position_id                                                                           
                )  t4 on t4.position_id = t1.id                                                              
     left join (                                                                                             
                 select count(1) as warehouse_count,                                                         
                        ttab.serial_id                                                                       
                   from (                                                                                    
                             SELECT distinct com_t6.id,                                                      
                                    com_t1.serial_id                                                         
                               FROM b_warehouse_relation com_t1                                                       
                         inner JOIN m_position com_t2                                                        
                                 ON com_t1.serial_id = com_t2.id                                             
                                AND com_t1.serial_type = 'm_position'                                        
                         inner JOIN b_warehouse_relation com_t3                                              
                                 ON com_t3.serial_id = com_t2.id                                             
                                AND com_t3.serial_type = 'm_position'                                        
                         inner JOIN m_warehouse_relation com_t4                                              
                                 ON com_t3.warehouse_relation_code = com_t4.code                             
                                AND com_t4.serial_type = 'b_warehouse_group'                                 
                         inner JOIN b_warehouse_group_relation com_t5                                        
                                 ON com_t4.serial_id = com_t5.warehouse_group_id                             
                         inner join m_warehouse com_t6                                                       
                                 on com_t6.id = com_t5.warehouse_id                                          
                         inner join m_position com_t7 on com_t7.id = com_t1.serial_id                        
                          where com_t1.serial_type = 'm_position'                                        
                           ) ttab                                                                            
                          group by ttab.serial_id                                                            
                )  tt4 on tt4.serial_id = t1.id                                                              
     left join (                                                                                             
                 select count(1) as warehouse_count,                                                         
                        ttab.serial_id                                                                       
                   from (                                                                                    
                             SELECT distinct com_t1.serial_id,                                               
                                    com_t1.warehouse_id                                                      
                               FROM b_warehouse_position com_t1                                              
                           ) ttab                                                                            
                          group by ttab.serial_id                                                            
                )  tt5 on tt5.serial_id = t1.id                                                              
      LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id               
      LEFT JOIN (                                                                                           
        SELECT JSON_ARRAYAGG(JSON_OBJECT('id', sr.id, 'code', sr.code, 'name', sr.name, 'key', sr.name, 'label', sr.name)) roleList, mrp.position_id 
        FROM m_role_position mrp                                                                            
        INNER JOIN s_role sr ON sr.id = mrp.role_id AND sr.is_del = false                                 
        GROUP BY mrp.position_id                                                                           
      ) tt_roles ON tt_roles.position_id = t1.id                                                          
      LEFT JOIN (                                                                                           
        SELECT COUNT(1) permission_count,                                                                  
               mpp.position_id,                                                                             
               JSON_ARRAYAGG(JSON_OBJECT('id', mp.id, 'key', mp.name, 'label', mp.name)) permissionList   
        FROM m_permission_position mpp                                                                      
        INNER JOIN m_permission mp ON mpp.permission_id = mp.id                                            
        GROUP BY mpp.position_id                                                                            
      ) tt_permissions ON tt_permissions.position_id = t1.id                                               
  where true                                                              
    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)  
    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)  
    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)                 
    and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)                              
    and (                                                                                                 
       case when #{p1.dataModel,jdbcType=VARCHAR} = '10' then   
           not exists(                                                                                    
                     select 1                                                                             
                       from m_org subt1                                                                   
                      where subt1.serial_type = 'm_position'    
                        and t1.id = subt1.serial_id                                                        
           )                                                                                              
       else true                                                                                          
       end                                                                                                
        )                                                                                                 
      """)
    @Results({
        @Result(property = "roleList", column = "roleList", javaType = List.class, typeHandler = RoleItemListTypeHandler.class),
        @Result(property = "permissionList", column = "permissionList", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    IPage<MPositionVo> selectPage(Page page, @Param("p1") MPositionVo searchCondition);

    /**
     * 获取单条岗位详情
     * @param searchCondition
     * @return
     */
    // 删除状态字典类型：sys_delete_type
    @Select("""
        SELECT                                                                                               
               t1.*,                                                                                         
               c_staff.name as c_name,                                                                       
               u_staff.name as u_name,                                                                       
               t2.label as is_del_name,                                                                      
               t3.staff_count,                                                                               
               t4.role_count,                                                                                
               tt4.warehouse_count,                                                                          
               tt5.warehouse_count warehouse_count1,                                                         
               f_get_org_simple_name(vor.code, 'm_group') group_simple_name,                            
               f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                         
               f_get_org_simple_name ( vor.CODE, 'm_dept' ) parent_dept_simple_name,                           
               tt_roles.roleList,                                                                               
               tt_permissions.permission_count,                                                               
               tt_permissions.permissionList                                                                  
          FROM m_position t1                                                                                 
     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       
     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       
     LEFT JOIN v_dict_info AS t2 ON t2.code = 'sys_delete_type'                    
    and t2.dict_value = CONCAT('', t1.is_del)                                                           
     left join (                                                                                             
                  select count(1) staff_count,                                                               
                         subt.serial_id,                                                                       
                         subt.serial_type                                                                    
                    from m_staff_org subt                                                                    
                group by subt.serial_id, subt.serial_type                                                    
                )  t3 on t3.serial_id = t1.id                                                                
           and t3.serial_type = 'm_position'            
     left join (                                                                                             
	            SELECT                                                                                          
	            	count( 1 ) role_count,                                                                      
	            	subt2.position_id                                                                           
	            FROM                                                                                            
	            	s_role subt1                                                                                
	            	INNER JOIN m_role_position subt2 ON subt1.id = subt2.role_id                                
	            WHERE                                                                                           
	            	subt1.is_del = false                                                                        
	            GROUP BY                                                                                        
	            	subt2.position_id                                                                           
                )  t4 on t4.position_id = t1.id                                                              
     left join (                                                                                             
                 select count(1) as warehouse_count,                                                         
                        ttab.serial_id                                                                       
                   from (                                                                                    
                             SELECT distinct com_t6.id,                                                      
                                    com_t1.serial_id                                                         
                               FROM b_warehouse_relation com_t1                                                       
                         inner JOIN m_position com_t2                                                        
                                 ON com_t1.serial_id = com_t2.id                                             
                                AND com_t1.serial_type = 'm_position'                                        
                         inner JOIN b_warehouse_relation com_t3                                              
                                 ON com_t3.serial_id = com_t2.id                                             
                                AND com_t3.serial_type = 'm_position'                                        
                         inner JOIN m_warehouse_relation com_t4                                              
                                 ON com_t3.warehouse_relation_code = com_t4.code                             
                                AND com_t4.serial_type = 'b_warehouse_group'                                 
                         inner JOIN b_warehouse_group_relation com_t5                                        
                                 ON com_t4.serial_id = com_t5.warehouse_group_id                             
                         inner join m_warehouse com_t6                                                       
                                 on com_t6.id = com_t5.warehouse_id                                          
                         inner join m_position com_t7 on com_t7.id = com_t1.serial_id                        
                          where com_t1.serial_type = 'm_position'                                        
                           ) ttab                                                                            
                          group by ttab.serial_id                                                            
                )  tt4 on tt4.serial_id = t1.id                                                              
     left join (                                                                                             
                 select count(1) as warehouse_count,                                                         
                        ttab.serial_id                                                                       
                   from (                                                                                    
                             SELECT distinct com_t1.serial_id,                                               
                                    com_t1.warehouse_id                                                      
                               FROM b_warehouse_position com_t1                                              
                           ) ttab                                                                            
                          group by ttab.serial_id                                                            
                )  tt5 on tt5.serial_id = t1.id                                                              
      LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id               
      LEFT JOIN (                                                                                           
        SELECT JSON_ARRAYAGG(JSON_OBJECT('id', sr.id, 'code', sr.code, 'name', sr.name, 'key', sr.name, 'label', sr.name)) roleList, mrp.position_id 
        FROM m_role_position mrp                                                                            
        INNER JOIN s_role sr ON sr.id = mrp.role_id AND sr.is_del = false                                 
        GROUP BY mrp.position_id                                                                           
      ) tt_roles ON tt_roles.position_id = t1.id                                                          
      LEFT JOIN (                                                                                           
        SELECT COUNT(1) permission_count,                                                                  
               mpp.position_id,                                                                             
               JSON_ARRAYAGG(JSON_OBJECT('id', mp.id, 'key', mp.name, 'label', mp.name)) permissionList   
        FROM m_permission_position mpp                                                                      
        INNER JOIN m_permission mp ON mpp.permission_id = mp.id                                            
        GROUP BY mpp.position_id                                                                            
      ) tt_permissions ON tt_permissions.position_id = t1.id                                               
  where true                                                                                             
  and t1.id = #{p1.id,jdbcType=BIGINT}                                                                   
   """)
    @Results({
        @Result(property = "roleList", column = "roleList", javaType = List.class, typeHandler = RoleItemListTypeHandler.class),
        @Result(property = "permissionList", column = "permissionList", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    MPositionVo getDetail(@Param("p1") MPositionVo searchCondition);

    /**
     * 导出专用查询方法，支持动态排序
     * @param searchCondition 查询条件（可包含ids数组用于选中导出）
     * @param orderByClause 动态排序子句
     * @return
     */
    // 删除状态字典类型：sys_delete_type
    @Select("""
        <script>
		SELECT                                                                                                                                                             
			@row_num := @row_num + 1 AS NO,                                                                                                                                
			t1.simple_name,                                                                                                                                                
         t1.name,                                                                                                                                                       
         t1.code,                                                                                                                                                       
			u_staff.NAME AS u_name,                                                                                                                                        
			t2.label AS delete_status,                                                                                                                                       
			COALESCE(tt6.role_concat_name, '') role_concat_name,                                                                                                                                
			COALESCE(tt6.role_json_data, '[]') roleList,                                                                                                                     
			COALESCE(tt7.permission_count, 0) permission_count,                                                                                                                
			COALESCE(tt7.permission_concat_name, '') permission_concat_name,                                                                                               
			COALESCE(tt7.permission_json_data, '[]') permissionList,                                                                                                       
			t1.u_time,                                                                                                                                                     
			f_get_org_simple_name(vor.code, 'm_group') group_simple_name,                                                                                             
			f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                                                                                          
			f_get_org_simple_name ( vor.CODE, 'm_dept' ) parent_dept_simple_name                                                                                             
		FROM                                                                                                                                                               
			m_position t1                                                                                                                                                  
			LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                                                                              
			LEFT JOIN v_dict_info AS t2 ON t2.CODE = 'sys_delete_type'                                                                                                     
			AND t2.dict_value = CONCAT('', t1.is_del)                                                                                                            
  LEFT JOIN (                                                                                                                                                               
    SELECT GROUP_CONCAT(sr.name SEPARATOR ', ') role_concat_name,
           JSON_ARRAYAGG(JSON_OBJECT('id', sr.id, 'code', sr.code, 'name', sr.name, 'key', sr.name, 'label', sr.name)) role_json_data,
           mrp.position_id                  
    FROM m_role_position mrp                                                                                                                                            
    INNER JOIN s_role sr ON sr.id = mrp.role_id AND sr.is_del = false                                                                                                  
    GROUP BY mrp.position_id
  ) tt6 ON tt6.position_id = t1.id                                                                                                                                      
  LEFT JOIN (                                                                                                                                                          
    SELECT COUNT(1) permission_count,                                                                                                                                  
           GROUP_CONCAT(mp.name SEPARATOR ', ') permission_concat_name,
           JSON_ARRAYAGG(JSON_OBJECT('id', mp.id, 'key', mp.name, 'label', mp.name)) permission_json_data,
           mpp.position_id                                                                                                                                             
    FROM m_permission_position mpp                                                                                                                                      
    INNER JOIN m_permission mp ON mpp.permission_id = mp.id                                                                                                            
    GROUP BY mpp.position_id                                                                                                                                            
  ) tt7 ON tt7.position_id = t1.id                                                                                                                                      
			LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id,                                                                      
			( SELECT @row_num := 0 ) t6                                                                                                                                    
  where true 
    and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)                                                              
    and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)                                                              
    and (t1.is_del =#{p1.is_del,jdbcType=VARCHAR} or #{p1.is_del,jdbcType=VARCHAR} is null)
    <if test="p1.ids != null and p1.ids.length > 0">
        and t1.id in
        <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
         #{item}
        </foreach>
    </if>
  <if test="orderByClause != null and orderByClause != ''">
    ${orderByClause}
  </if>
  <if test="orderByClause == null or orderByClause == ''">
    ORDER BY t1.u_time DESC
  </if>
        </script>
      """)
    @Results({
        @Result(property = "roleList", column = "role_json_data", javaType = List.class, typeHandler = RoleItemListTypeHandler.class),
        @Result(property = "permissionList", column = "permission_json_data", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    List<MPositionExportVo> selectExportList(@Param("p1") MPositionVo searchCondition, @Param("orderByClause") String orderByClause);



    /**
     * 按ID列表查询实体
     * @param searchCondition
     * @return
     */
    @Select("""
        <script>
        select t.* 
           from m_position t 
          where true 
            and t.id in 
                <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>
                 #{item.id}  
                </foreach>
          </script>""")
    List<MPositionEntity> selectIdsIn(@Param("p1") List<MPositionVo> searchCondition);

    /**
     * 按编码查询（排除指定ID）
     * @param code
     * @return
     */
    // 未删除状态：0
    @Select("""
        select t.* 
           from m_position t 
          where true 
            and t.code =  #{p1}   
            and (t.id  <>  #{p2} or #{p2} is null)   
            and t.is_del =  0   
              """)
    List<MPositionEntity> selectByCode(@Param("p1") String code, @Param("p2") Long equal_id);

    /**
     * 按名称查询（排除指定ID）
     * @param name
     * @return
     */
    // 未删除状态：0
    @Select("""
        select t.* 
           from m_position t 
          where true 
            and t.name =  #{p1}   
            and (t.id  <>  #{p2} or #{p2} is null )   
            and t.is_del =  0   
              """)
    List<MPositionEntity> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按简称查询（排除指定ID）
     * @param name
     * @return
     */
    // 未删除状态：0
    @Select("""
        select t.* 
           from m_position t 
          where true 
            and t.simple_name =  #{p1}   
            and (t.id  <>  #{p2} or #{p2} is null)   
            and t.is_del =  0   
              """)
    List<MPositionEntity> selectBySimpleName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按ID获取单条详细数据
     * @param searchCondition
     * @return
     */
    // 删除状态字典类型：sys_delete_type
    @Select("""
        SELECT                                                                                               
               t1.*,                                                                                         
               c_staff.name as c_name,                                                                       
               u_staff.name as u_name,                                                                       
               t2.label as is_del_name,                                                                      
               t3.staff_count,                                                                               
               t4.role_count,                                                                                
               tt4.warehouse_count,                                                                          
               tt5.warehouse_count warehouse_count1,                                                         
               f_get_org_simple_name(vor.code, 'm_group') group_simple_name,                            
               f_get_org_simple_name ( vor.CODE, 'm_company' )  company_simple_name,                         
               f_get_org_simple_name ( vor.CODE, 'm_dept' ) parent_dept_simple_name,                           
               tt_roles.roleList,                                                                               
               tt_permissions.permission_count,                                                               
               tt_permissions.permissionList                                                                  
          FROM m_position t1                                                                                 
     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                       
     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                       
     LEFT JOIN v_dict_info AS t2 ON t2.code = 'sys_delete_type'                    
    and t2.dict_value = CONCAT('', t1.is_del)                                                           
     left join (                                                                                             
                  select count(1) staff_count,                                                               
                         subt.serial_id,                                                                       
                         subt.serial_type                                                                    
                    from m_staff_org subt                                                                    
                group by subt.serial_id, subt.serial_type                                                    
                )  t3 on t3.serial_id = t1.id                                                                
           and t3.serial_type = 'm_position'            
     left join (                                                                                             
	            SELECT                                                                                          
	            	count( 1 ) role_count,                                                                      
	            	subt2.position_id                                                                           
	            FROM                                                                                            
	            	s_role subt1                                                                                
	            	INNER JOIN m_role_position subt2 ON subt1.id = subt2.role_id                                
	            WHERE                                                                                           
	            	subt1.is_del = false                                                                        
	            GROUP BY                                                                                        
	            	subt2.position_id                                                                           
                )  t4 on t4.position_id = t1.id                                                              
     left join (                                                                                             
                 select count(1) as warehouse_count,                                                         
                        ttab.serial_id                                                                       
                   from (                                                                                    
                             SELECT distinct com_t6.id,                                                      
                                    com_t1.serial_id                                                         
                               FROM b_warehouse_relation com_t1                                                       
                         inner JOIN m_position com_t2                                                        
                                 ON com_t1.serial_id = com_t2.id                                             
                                AND com_t1.serial_type = 'm_position'                                        
                         inner JOIN b_warehouse_relation com_t3                                              
                                 ON com_t3.serial_id = com_t2.id                                             
                                AND com_t3.serial_type = 'm_position'                                        
                         inner JOIN m_warehouse_relation com_t4                                              
                                 ON com_t3.warehouse_relation_code = com_t4.code                             
                                AND com_t4.serial_type = 'b_warehouse_group'                                 
                         inner JOIN b_warehouse_group_relation com_t5                                        
                                 ON com_t4.serial_id = com_t5.warehouse_group_id                             
                         inner join m_warehouse com_t6                                                       
                                 on com_t6.id = com_t5.warehouse_id                                          
                         inner join m_position com_t7 on com_t7.id = com_t1.serial_id                        
                          where com_t1.serial_type = 'm_position'                                        
                           ) ttab                                                                            
                          group by ttab.serial_id                                                            
                )  tt4 on tt4.serial_id = t1.id                                                              
     left join (                                                                                             
                 select count(1) as warehouse_count,                                                         
                        ttab.serial_id                                                                       
                   from (                                                                                    
                             SELECT distinct com_t1.serial_id,                                               
                                    com_t1.warehouse_id                                                      
                               FROM b_warehouse_position com_t1                                              
                           ) ttab                                                                            
                          group by ttab.serial_id                                                            
                )  tt5 on tt5.serial_id = t1.id                                                              
      LEFT JOIN v_org_relation vor ON vor.serial_type = 'm_position' and vor.serial_id = t1.id               
      LEFT JOIN (                                                                                           
        SELECT JSON_ARRAYAGG(JSON_OBJECT('id', sr.id, 'code', sr.code, 'name', sr.name, 'key', sr.name, 'label', sr.name)) roleList, mrp.position_id 
        FROM m_role_position mrp                                                                            
        INNER JOIN s_role sr ON sr.id = mrp.role_id AND sr.is_del = false                                 
        GROUP BY mrp.position_id                                                                           
      ) tt_roles ON tt_roles.position_id = t1.id                                                          
      LEFT JOIN (                                                                                           
        SELECT COUNT(1) permission_count,                                                                  
               mpp.position_id,                                                                             
               JSON_ARRAYAGG(JSON_OBJECT('id', mp.id, 'key', mp.name, 'label', mp.name)) permissionList   
        FROM m_permission_position mpp                                                                      
        INNER JOIN m_permission mp ON mpp.permission_id = mp.id                                            
        GROUP BY mpp.position_id                                                                            
      ) tt_permissions ON tt_permissions.position_id = t1.id                                               
  where true                                                                                             
    and (t1.id = #{p1.id,jdbcType=BIGINT})                                                               
                                                                          """)
    @Results({
        @Result(property = "roleList", column = "roleList", javaType = List.class, typeHandler = RoleItemListTypeHandler.class),
        @Result(property = "permissionList", column = "permissionList", javaType = List.class, typeHandler = PermissionItemListTypeHandler.class),
    })
    MPositionVo selectByid(@Param("p1") MPositionVo searchCondition);

    /**
     * 查询岗位在组织架构中是否已被使用
     * @param searchCondition
     * @return
     */
    // 岗位组织类型：m_position
    @Select("""
        select count(1)                                                                                     
           from m_org t                                                                                      
          where true                                                                                         
            and t.serial_type = 'm_position'     
            and t.serial_id = #{p1.id,jdbcType=BIGINT}                                                       
                                                                                                     """)
    int isExistsInOrg(@Param("p1") MPositionEntity searchCondition);

    /**
     * 通过页面代码获取拥有权限的岗位信息
     * @param page_code
     * @return
     */
    // 未删除状态：0  启用状态：1
    @Select("""
			SELECT                                                                                 
				t5.*                                                                               
			FROM                                                                                   
				m_permission_pages t1                                                              
				INNER JOIN m_permission_role t2 ON t1.permission_id = t2.permission_id             
				INNER JOIN m_role_position t3 ON t2.role_id = t3.role_id                           
				INNER JOIN s_role t4 ON t2.role_id = t4.id                                         
				INNER JOIN m_position t5 ON t3.position_id = t5.id                                 
			WHERE                                                                                  
			TRUE                                                                                   
				AND t4.is_del = 0                                                                  
				AND t4.is_enable = 1                                                               
				AND t5.is_del = 0                                                                  
				AND t1.CODE = #{p1,jdbcType=VARCHAR}                                               
              """)
    List<MPositionVo> selectPositionByPageCode(@Param("p1") String page_code);

    /**
     * 通过权限标识获取拥有该权限的岗位信息
     * @return
     */
    // 未删除状态：0  启用状态：1  权限启用：true
    @Select("""
			SELECT                                                                                 
				t5.*                                                                               
			FROM                                                                                   
				m_permission_operation t1                                                          
				INNER JOIN m_permission_role t2 ON t1.permission_id = t2.permission_id             
				INNER JOIN m_role_position t3 ON t2.role_id = t3.role_id                           
				INNER JOIN s_role t4 ON t2.role_id = t4.id                                         
				INNER JOIN m_position t5 ON t3.position_id = t5.id                                 
			WHERE                                                                                  
			TRUE                                                                                   
				AND t1.is_enable = true                                                            
				AND t4.is_del = 0                                                                  
				AND t4.is_enable = 1                                                               
				AND t5.is_del = 0                                                                  
				AND t1.perms= #{p1,jdbcType=VARCHAR}                                               
              """)
    List<MPositionVo> selectPositionByPerms(@Param("p1") String perms);


    /**
     * 获取员工关联的岗位列表
     * @param condition
     * @return
     */
    // 岗位组织类型：m_position
    @Select("""
	SELECT                                                                                                  
		subt2.id position_id,                                                                               
		subt2.NAME position_name,                                                                           
		subt2.simple_name position_simple_name,                                                             
		subt1.staff_id                                                                                      
	FROM                                                                                                    
		m_staff_org subt1                                                                                   
		INNER JOIN m_position subt2 ON subt1.serial_type = 'm_position'                                     
		AND subt1.serial_id = subt2.id                                                                      
    and (subt1.staff_id =#{p1.staff_id,jdbcType=BIGINT} or #{p1.staff_id,jdbcType=BIGINT} is null)       
 """)
    List<MPositionInfoVo> getAllPositionList(@Param("p1") MPositionInfoVo condition);

    /**
     * 查询岗位仓库组列表
     */
    // 仓库组类型：b_warehouse_group
    @Select("""
        SELECT                                                                                                  
        	t2.id serial_id,                                                                                    
        	'b_warehouse_group' serial_type,                                                                    
        	concat(t2.name,'(仓库组)') label,                                                                    
        	t2.CODE serial_code                                                                                 
        FROM                                                                                                    
        	b_warehouse_relation t1                                                                             
        	INNER JOIN b_warehouse_group t2 ON t2.id = t1.serial_id                                             
        	AND t1.serial_type = 'b_warehouse_group'                                                            
        WHERE                                                                                                   
        TRUE                                                                                                    
        	AND t1.position_id = #{p1}                                                                          
        """)
    List<TreeDataVo> selectWarehouseGroupList(@Param("p1") Long position_id);

    /**
     * 查询仓库组下的仓库列表
     */
    // 仓库类型：m_warehouse
    @Select("""
	SELECT                                                                                                  
		t2.id serial_id,                                                                                    
		'm_warehouse' serial_type,                                                                          
		concat(t2.name,'(仓库)') label,                                                                      
		t2.CODE serial_code                                                                                 
	FROM                                                                                                    
		b_warehouse_group_relation t1                                                                       
		INNER JOIN m_warehouse t2 ON t2.id = t1.warehouse_id                                                
	WHERE                                                                                                   
	TRUE                                                                                                    
		AND t1.warehouse_group_id = #{p1}                                                                   
        """)
    List<TreeDataVo> selectWarehouseListByGroupId(@Param("p1") Long warehouse_group_id);

    /**
     * 查询岗位直接关联的仓库列表
     */
    // 仓库类型：m_warehouse
    @Select("""
	SELECT                                                                                                  
		t2.id serial_id,                                                                                    
		'm_warehouse' serial_type,                                                                          
		concat(t2.name,'(仓库)') label,                                                                      
		t2.CODE serial_code                                                                                 
	FROM                                                                                                    
		b_warehouse_relation t1                                                                             
		INNER JOIN m_warehouse t2 ON t2.id = t1.serial_id                                                   
		AND t1.serial_type = 'm_warehouse'                                                                  
	WHERE                                                                                                   
	TRUE                                                                                                    
		AND t1.position_id = #{p1}                                                                          
        """)
    List<TreeDataVo> selectWarehouseListByPositionId(@Param("p1") Long position_id);

    /**
     * 统计指定岗位分配的员工数量（删除校验专用）
     * @param positionId
     * @return
     */
    @Select("""
            SELECT COUNT(1) 
              FROM m_staff_org t1
              JOIN m_staff t2 ON t1.staff_id = t2.id
             WHERE t1.serial_type = 'm_position'
               AND t1.serial_id = #{positionId}
               AND t2.is_del = false
                                                                                    """)
    Long countStaffByPositionId(@Param("positionId") Long positionId);

    /**
     * 统计指定岗位关联的角色数量（删除校验专用）
     * @param positionId
     * @return
     */
    @Select("""
            SELECT COUNT(1) 
              FROM m_role_position t1
              JOIN s_role t2 ON t1.role_id = t2.id
             WHERE t1.position_id = #{positionId}
               AND t2.is_del = false
                                                                                    """)
    Long countRolesByPositionId(@Param("positionId") Long positionId);

    /**
     * 统计指定岗位配置的权限数量（删除校验专用）
     * @param positionId
     * @return
     */
    @Select("""
            SELECT COUNT(1) 
              FROM m_permission_position t1
              JOIN m_permission t2 ON t1.permission_id = t2.id
             WHERE t1.position_id = #{positionId}
               AND t2.is_del = false
                                                                                    """)
    Long countPermissionsByPositionId(@Param("positionId") Long positionId);

}
