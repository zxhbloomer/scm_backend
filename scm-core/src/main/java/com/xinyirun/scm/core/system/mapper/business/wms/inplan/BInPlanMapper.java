package com.xinyirun.scm.core.system.mapper.business.wms.inplan;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.wms.inplan.BInPlanEntity;
import com.xinyirun.scm.bean.system.vo.business.wms.inplan.BInPlanDetailVo;
import com.xinyirun.scm.bean.system.vo.business.wms.inplan.BInPlanVo;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.InPlanDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 入库计划 Mapper 接口
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Repository
public interface BInPlanMapper extends BaseMapper<BInPlanEntity> {

    /**
     * 分页查询
     */
    @Select("""
            <script>                                                                                                                                         
            SELECT                                                                                                                                     
           		tab1.*,                                                                                                                                     
           		t1.name as owner_name,                                                                                                                      
           		t2.name as consignor_name,                                                                                                                  
           		tab3.label as status_name,                                                                                                                  
           		tab4.label as type_name,                                                                                                                    
           		tab2.detailListData ,                                                                                                                       
           		tab1.bpm_instance_code as process_code,                                                                                                         
           		tab13.name as c_name,                                                                                                                       
           		tab14.name as u_name                                                                                                                        
           	FROM                                                                                                                                            
           		b_in_plan tab1                                                                                                                              
           		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                                         
           		LEFT JOIN m_enterprise t2 ON t2.id = tab1.consignor_id                                                                                     
           	    LEFT JOIN (SELECT tt1.in_plan_id, JSON_ARRAYAGG(                                                                                           
           	        JSON_OBJECT(                                                                                                                            
                     'id', tt1.id,                                                                                                                      
                     'code', tt1.code,                                                                                                                  
                     'no', tt1.no,                                                                                                                      
                     'in_plan_id', tt1.in_plan_id,                                                                                                      
                     'serial_id', tt1.serial_id,                                                                                                        
                     'serial_code', tt1.serial_code,                                                                                                    
                     'serial_type', tt1.serial_type,                                                                                                    
                     'project_code', tt1.project_code,                                                                                                  
                     'contract_id', tt1.contract_id,                                                                                                    
                     'contract_code', tt1.contract_code,                                                                                                
                     'order_id', tt1.order_id,                                                                                                          
                     'order_code', tt1.order_code,                                                                                                      
                     'order_detail_id', tt1.order_detail_id,                                                                                            
                     'goods_code', tt1.goods_code,                                                                                                      
                     'goods_id', tt1.goods_id,                                                                                                          
                     'sku_id', tt1.sku_id,                                                                                                              
                     'sku_code', tt1.sku_code,                                                                                                          
                     'price', tt1.price,                                                                                                                
                     'qty', tt1.qty,                                                                                                                    
                     'weight', tt1.weight,                                                                                                              
                     'volume', tt1.volume,                                                                                                              
                     'warehouse_id', tt1.warehouse_id,                                                                                                  
                     'location_id', tt1.location_id,                                                                                                    
                     'bin_id', tt1.bin_id,                                                                                                              
                     'supplier_id', tt1.supplier_id,                                                                                                    
                     'supplier_code', tt1.supplier_code,                                                                                                
                     'unit_id', tt1.unit_id,                                                                                                            
                     'processing_qty', IFNULL(tt1.processing_qty, 0),                                                                                              
                     'processing_weight', IFNULL(tt1.processing_weight, 0),                                                                                        
                     'processing_volume', IFNULL(tt1.processing_volume, 0),                                                                                        
                     'unprocessed_qty', IFNULL(tt1.unprocessed_qty, 0),                                                                                            
                     'unprocessed_weight', IFNULL(tt1.unprocessed_weight, 0),                                                                                      
                     'unprocessed_volume', IFNULL(tt1.unprocessed_volume, 0),                                                                                      
                     'processed_qty', IFNULL(tt1.processed_qty, 0),                                                                                                
                     'processed_weight', IFNULL(tt1.processed_weight, 0),                                                                                          
                     'processed_volume', IFNULL(tt1.processed_volume, 0),                                                                                          
                     'remark', tt1.remark,                                                                                                              
                     'goods_name', tt2.name,                                                                                                            
                     'sku_name', tt2.spec,                                                                                                              
                     'order_qty', tt1.order_qty,                                                                                                        
                     'order_price', tt1.order_price,                                                                                                    
                     'order_amount', tt1.order_amount,                                                                                                  
                     'supplier_name', tt5.name,                                                                                                         
                     'warehouse_name', tt4.name,                                                                                                        
                     'location_name', tt6.name,                                                                                                         
                     'bin_name', tt7.name                                                                                                               
                 )) as detailListData                                                                                                                   
           	     FROM b_in_plan_detail tt1 LEFT JOIN m_goods_spec tt2 ON tt1.goods_code = tt2.goods_code AND tt1.sku_code = tt2.code                     
                 LEFT JOIN b_po_order_total tt3 ON tt1.order_id = tt3.po_order_id                                                                      
                 LEFT JOIN m_warehouse tt4 ON tt4.id = tt1.warehouse_id                                                                                
                 LEFT JOIN m_enterprise tt5 ON tt1.supplier_id = tt5.id                                                                                
                 LEFT JOIN m_location tt6 ON tt6.id = tt1.location_id                                                                                  
                 LEFT JOIN m_bin tt7 ON tt7.id = tt1.bin_id                                                                                            
             GROUP BY tt1.in_plan_id) tab2 ON tab1.id = tab2.in_plan_id                                                                                
           		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_in_plan_status' AND tab3.dict_value = tab1.status                  
           		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_in_plan_type' AND tab4.dict_value = tab1.type                      
                                                                       
           LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                               
           LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                               
           		WHERE TRUE                                                                                                                                  
           		 AND tab1.is_del = false                                                                                                                    
           		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              
           		 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                                               
           		 AND (tab1.owner_id = #{p1.owner_id}  or #{p1.owner_id} is null   )                                                                        
           		 AND (tab1.consignor_id = #{p1.consignor_id}  or #{p1.consignor_id} is null   )                                                            

              <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              
               and tab1.status in                                                                                                                                              
                   <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         
                    #{item}                                                                                                                                                 
                   </foreach>                                                                                                                                               
              </if>                                                                                                                                                         

              <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              
               and tab1.type in                                                                                                                                              
                   <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         
                    #{item}                                                                                                                                                 
                   </foreach>                                                                                                                                               
              </if>                                                                                                                                                         

              <if test='p1.plan_times != null and p1.plan_times.length == 2' >                                                                                         
               and tab1.plan_time >= #{p1.plan_times[0]} and tab1.plan_time &lt;= #{p1.plan_times[1]}                                                                  
              </if>                                                                                                                                                     

              <if test='p1.contract_code != null and p1.contract_code != ""' >                                                                                       
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.contract_code like CONCAT('%', #{p1.contract_code}, '%')                                                                            
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.order_code != null and p1.order_code != ""' >                                                                                           
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.order_code like CONCAT('%', #{p1.order_code}, '%')                                                                                 
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.warehouse_id != null' >                                                                                                                    
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.warehouse_id = #{p1.warehouse_id}                                                                                                   
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.supplier_id != null' >                                                                                                                     
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.supplier_id = #{p1.supplier_id}                                                                                                     
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.project_code != null and p1.project_code != ""' >                                                                                       
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.project_code like CONCAT('%', #{p1.project_code}, '%')                                                                             
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.goods_name != null and p1.goods_name != ""' >                                                                                           
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     LEFT JOIN m_goods_spec gs ON subt1.goods_code = gs.goods_code AND subt1.sku_code = gs.code                                                     
                     where (subt1.sku_code like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_code like CONCAT('%', #{p1.goods_name}, '%')                   
                            or gs.name like CONCAT('%', #{p1.goods_name}, '%') or gs.spec like CONCAT('%', #{p1.goods_name}, '%'))                                
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.goods_code != null and p1.goods_code != ""' >                                                                                           
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.goods_code like CONCAT('%', #{p1.goods_code}, '%')                                                                                 
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.sku_code != null and p1.sku_code != ""' >                                                                                               
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.sku_code like CONCAT('%', #{p1.sku_code}, '%')                                                                                     
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

           	GROUP BY                                                                                                                                        
           		tab2.in_plan_id                                                                                                                             
            </script>                                                                                                                                       
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = InPlanDetailListTypeHandler.class),
    })
    IPage<BInPlanVo> selectPage(Page<BInPlanVo> page, @Param("p1") BInPlanVo searchCondition);


    /**
     * id查询
     */
    @Select("""
            	SELECT                                                                                                                                          
           		tab1.*,                                                                                                                                     
           		t1.name as owner_name,                                                                                                                      
           		tab2.detailListData,                                                                                                                        
           		tab3.one_file as doc_att_file,                                                                                                             
           		tab6.label as status_name,                                                                                                                  
           		tab7.label as type_name,                                                                                                                    
           		tab13.name as c_name,                                                                                                                       
           		tab14.name as u_name                                                                                                                        
           	FROM                                                                                                                                            
           		b_in_plan tab1                                                                                                                              
           		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                                         
           	    LEFT JOIN (SELECT tt1.in_plan_id, JSON_ARRAYAGG(                                                                                           
           	        JSON_OBJECT(                                                                                                                            
                     'id', tt1.id,                                                                                                                      
                     'code', tt1.code,                                                                                                                  
                     'no', tt1.no,                                                                                                                      
                     'in_plan_id', tt1.in_plan_id,                                                                                                      
                     'serial_id', tt1.serial_id,                                                                                                        
                     'serial_code', tt1.serial_code,                                                                                                    
                     'serial_type', tt1.serial_type,                                                                                                    
                     'project_code', tt1.project_code,                                                                                                  
                     'contract_id', tt1.contract_id,                                                                                                    
                     'contract_code', tt1.contract_code,                                                                                                
                     'order_id', tt1.order_id,                                                                                                          
                     'order_code', tt1.order_code,                                                                                                      
                     'order_detail_id', tt1.order_detail_id,                                                                                            
                     'goods_code', tt1.goods_code,                                                                                                      
                     'goods_id', tt1.goods_id,                                                                                                          
                     'sku_id', tt1.sku_id,                                                                                                              
                     'sku_code', tt1.sku_code,                                                                                                          
                     'price', tt1.price,                                                                                                                
                     'qty', tt1.qty,                                                                                                                    
                     'weight', tt1.weight,                                                                                                              
                     'volume', tt1.volume,                                                                                                              
                     'warehouse_id', tt1.warehouse_id,                                                                                                  
                     'location_id', tt1.location_id,                                                                                                    
                     'bin_id', tt1.bin_id,                                                                                                              
                     'supplier_id', tt1.supplier_id,                                                                                                    
                     'supplier_code', tt1.supplier_code,                                                                                                
                     'unit_id', tt1.unit_id,                                                                                                            
                     'processing_qty', IFNULL(tt1.processing_qty, 0),                                                                                              
                     'processing_weight', IFNULL(tt1.processing_weight, 0),                                                                                        
                     'processing_volume', IFNULL(tt1.processing_volume, 0),                                                                                        
                     'unprocessed_qty', IFNULL(tt1.unprocessed_qty, 0),                                                                                            
                     'unprocessed_weight', IFNULL(tt1.unprocessed_weight, 0),                                                                                      
                     'unprocessed_volume', IFNULL(tt1.unprocessed_volume, 0),                                                                                      
                     'processed_qty', IFNULL(tt1.processed_qty, 0),                                                                                                
                     'processed_weight', IFNULL(tt1.processed_weight, 0),                                                                                          
                     'processed_volume', IFNULL(tt1.processed_volume, 0),                                                                                          
                     'remark', tt1.remark,                                                                                                              
                     'goods_name', tt2.name,                                                                                                            
                     'sku_name', tt2.spec,                                                                                                              
                     'order_qty', tt1.order_qty,                                                                                                        
                     'order_price', tt1.order_price,                                                                                                    
                     'order_amount', tt1.order_amount,                                                                                                  
                     'supplier_name', tt5.name,                                                                                                         
                     'warehouse_name', tt4.name,                                                                                                        
                     'location_name', tt6.name,                                                                                                         
                     'bin_name', tt7.name                                                                                                               
                 )) as detailListData                                                                                                                   
           	     FROM b_in_plan_detail tt1 LEFT JOIN m_goods_spec tt2 ON tt1.goods_code = tt2.goods_code AND tt1.sku_code = tt2.code                     
                 LEFT JOIN b_po_order_total tt3 ON tt1.order_id = tt3.po_order_id                                                                      
                 LEFT JOIN m_warehouse tt4 ON tt4.id = tt1.warehouse_id                                                                                
                 LEFT JOIN m_enterprise tt5 ON tt1.supplier_id = tt5.id                                                                                
                 LEFT JOIN m_location tt6 ON tt6.id = tt1.location_id                                                                                  
                 LEFT JOIN m_bin tt7 ON tt7.id = tt1.bin_id                                                                                            
             GROUP BY tt1.in_plan_id) tab2 ON tab1.id = tab2.in_plan_id                                                                                
           		LEFT JOIN b_in_plan_attach tab3 on tab1.id = tab3.in_plan_id                                                                               
           		LEFT JOIN s_dict_data  tab6 ON tab6.code = 'b_in_plan_status' AND tab6.dict_value = tab1.status                  
           		LEFT JOIN s_dict_data  tab7 ON tab7.code = 'b_in_plan_type' AND tab7.dict_value = tab1.type                      
           		LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                             
           		LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                             
           		WHERE TRUE AND tab1.id = #{p1}                                                                                                              
           		 AND tab1.is_del = false                                                                                                                    
           	GROUP BY                                                                                                                                        
           		tab2.in_plan_id                                                                                                                             
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = InPlanDetailListTypeHandler.class),
    })
    BInPlanVo selectId(@Param("p1") Integer id);

    /**
     * 查询合计信息
     */
    @Select("""
            	<script>                                                                                                                                           
            	SELECT 	                                                                                                                                          
           		SUM( IFNULL(tab2.processing_qty_total,0) )  as  processing_qty_total,                                                                      
           		SUM( IFNULL(tab2.processing_weight_total,0) )  as  processing_weight_total,                                                                
           		SUM( IFNULL(tab2.processing_volume_total,0) )  as  processing_volume_total,                                                                
           		SUM( IFNULL(tab2.unprocessed_qty_total,0) )  as  unprocessed_qty_total,                                                                   
           		SUM( IFNULL(tab2.unprocessed_weight_total,0) )  as  unprocessed_weight_total,                                                             
           		SUM( IFNULL(tab2.unprocessed_volume_total,0) )  as  unprocessed_volume_total,                                                             
           		SUM( IFNULL(tab2.processed_qty_total,0) )  as  processed_qty_total,                                                                       
           		SUM( IFNULL(tab2.processed_weight_total,0) )  as  processed_weight_total,                                                                 
           		SUM( IFNULL(tab2.processed_volume_total,0) )  as  processed_volume_total                                                                  
           	FROM                                                                                                                                             
           		b_in_plan tab1                                                                                                                              
           		LEFT JOIN b_in_plan_total tab2  ON tab1.id = tab2.in_plan_id                                                                               
           		WHERE TRUE                                                                                                                                   
           		 AND tab1.is_del = false                                                                                                                     
           		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                               
           		 AND (tab1.code like CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                                               
           		 AND (tab1.owner_id = #{p1.owner_id}  or #{p1.owner_id} is null   )                                                                        
           		 AND (tab1.consignor_id = #{p1.consignor_id}  or #{p1.consignor_id} is null   )                                                            

              <if test='p1.status_list != null and p1.status_list.length!=0' >                                                                                              
               and tab1.status in                                                                                                                                              
                   <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>                                                         
                    #{item}                                                                                                                                                 
                   </foreach>                                                                                                                                               
              </if>                                                                                                                                                         

              <if test='p1.type_list != null and p1.type_list.length!=0' >                                                                                              
               and tab1.type in                                                                                                                                              
                   <foreach collection='p1.type_list' item='item' index='index' open='(' separator=',' close=')'>                                                         
                    #{item}                                                                                                                                                 
                   </foreach>                                                                                                                                               
              </if>                                                                                                                                                         

              <if test='p1.plan_times != null and p1.plan_times.length == 2' >                                                                                         
               and tab1.plan_time >= #{p1.plan_times[0]} and tab1.plan_time &lt;= #{p1.plan_times[1]}                                                                  
              </if>                                                                                                                                                     

              <if test='p1.contract_code != null and p1.contract_code != ""' >                                                                                       
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.contract_code like CONCAT('%', #{p1.contract_code}, '%')                                                                            
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.order_code != null and p1.order_code != ""' >                                                                                           
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.order_code like CONCAT('%', #{p1.order_code}, '%')                                                                                 
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.warehouse_id != null' >                                                                                                                    
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.warehouse_id = #{p1.warehouse_id}                                                                                                   
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.supplier_id != null' >                                                                                                                     
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.supplier_id = #{p1.supplier_id}                                                                                                     
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.project_code != null and p1.project_code != ""' >                                                                                       
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.project_code like CONCAT('%', #{p1.project_code}, '%')                                                                             
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.goods_name != null and p1.goods_name != ""' >                                                                                           
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     LEFT JOIN m_goods_spec gs ON subt1.goods_code = gs.goods_code AND subt1.sku_code = gs.code                                                     
                     where (subt1.sku_code like CONCAT('%', #{p1.goods_name}, '%') or subt1.goods_code like CONCAT('%', #{p1.goods_name}, '%')                   
                            or gs.name like CONCAT('%', #{p1.goods_name}, '%') or gs.spec like CONCAT('%', #{p1.goods_name}, '%'))                                
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.goods_code != null and p1.goods_code != ""' >                                                                                           
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.goods_code like CONCAT('%', #{p1.goods_code}, '%')                                                                                 
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

              <if test='p1.sku_code != null and p1.sku_code != ""' >                                                                                               
              and exists(                                                                                                                                               
                     select 1 from b_in_plan_detail subt1                                                                                                             
                     INNER JOIN b_in_plan subt2 ON subt1.in_plan_id = subt2.id                                                                                       
                     where subt1.sku_code like CONCAT('%', #{p1.sku_code}, '%')                                                                                     
                       and subt2.id = tab1.id                                                                                                                         
                    )                                                                                                                                                 
              </if>                                                                                                                                                   

             </script>                                                                                                                                  
            """)
    BInPlanVo querySum(@Param("p1") BInPlanVo searchCondition);

    /**
     * 校验计划编号是否重复
     */
    @Select("""
            select * from b_in_plan where true and is_del = false                           
            and (id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)         
            and code = #{p1.code}                                            
            """)
    List<BInPlanVo> validateDuplicateCode(@Param("p1")BInPlanVo bean);


    /**
     * 导出查询
     */
    @Select("""
            <script>	                                                                                                                                        
            SELECT @row_num:= @row_num+ 1 as no,tb1.* from (                                                                                                  
               SELECT                                                                                                                                         
              		tab1.*,                                                                                                                                     
              		t1.name as owner_name,                                                                                                                      
              		tab3.label as status_name,                                                                                                                  
              		tab4.label as type_name,                                                                                                                    
              		tab2.detailListData ,                                                                                                                       
              		tab1.bpm_instance_code as process_code,                                                                                                         
              		tab13.name as c_name,                                                                                                                       
              		tab14.name as u_name                                                                                                                        
              	FROM                                                                                                                                            
              		b_in_plan tab1                                                                                                                              
              		LEFT JOIN m_enterprise t1 ON t1.id = tab1.owner_id                                                                                         
              	    LEFT JOIN (SELECT tt1.in_plan_id, JSON_ARRAYAGG(                                                                                           
              	        JSON_OBJECT(                                                                                                                            
                            'id', tt1.id,                                                                                                                      
                            'code', tt1.code,                                                                                                                  
                            'no', tt1.no,                                                                                                                      
                            'in_plan_id', tt1.in_plan_id,                                                                                                      
                            'serial_id', tt1.serial_id,                                                                                                        
                            'serial_code', tt1.serial_code,                                                                                                    
                            'serial_type', tt1.serial_type,                                                                                                    
                            'project_code', tt1.project_code,                                                                                                  
                            'contract_id', tt1.contract_id,                                                                                                    
                            'contract_code', tt1.contract_code,                                                                                                
                            'order_id', tt1.order_id,                                                                                                          
                            'order_code', tt1.order_code,                                                                                                      
                            'order_detail_id', tt1.order_detail_id,                                                                                            
                            'goods_code', tt1.goods_code,                                                                                                      
                            'goods_id', tt1.goods_id,                                                                                                          
                            'sku_id', tt1.sku_id,                                                                                                              
                            'sku_code', tt1.sku_code,                                                                                                          
                            'price', tt1.price,                                                                                                                
                            'qty', tt1.qty,                                                                                                                    
                            'weight', tt1.weight,                                                                                                              
                            'volume', tt1.volume,                                                                                                              
                            'warehouse_id', tt1.warehouse_id,                                                                                                  
                            'location_id', tt1.location_id,                                                                                                    
                            'bin_id', tt1.bin_id,                                                                                                              
                            'supplier_id', tt1.supplier_id,                                                                                                    
                            'supplier_code', tt1.supplier_code,                                                                                                
                            'unit_id', tt1.unit_id,                                                                                                            
                            'processing_qty', IFNULL(tt1.processing_qty, 0),                                                                                              
                            'processing_weight', IFNULL(tt1.processing_weight, 0),                                                                                        
                            'processing_volume', IFNULL(tt1.processing_volume, 0),                                                                                        
                            'unprocessed_qty', IFNULL(tt1.unprocessed_qty, 0),                                                                                            
                            'unprocessed_weight', IFNULL(tt1.unprocessed_weight, 0),                                                                                      
                            'unprocessed_volume', IFNULL(tt1.unprocessed_volume, 0),                                                                                      
                            'processed_qty', IFNULL(tt1.processed_qty, 0),                                                                                                
                            'processed_weight', IFNULL(tt1.processed_weight, 0),                                                                                          
                            'processed_volume', IFNULL(tt1.processed_volume, 0),                                                                                          
                            'remark', tt1.remark,                                                                                                              
                            'goods_name', tt2.name,                                                                                                            
                            'sku_name', tt2.spec,                                                                                                              
                            'order_qty', tt1.order_qty,                                                                                                        
                            'order_price', tt1.order_price,                                                                                                    
                            'order_amount', tt1.order_amount,                                                                                                  
                            'supplier_name', tt5.name,                                                                                                         
                            'warehouse_name', tt4.name,                                                                                                        
                            'location_name', tt6.name,                                                                                                         
                            'bin_name', tt7.name                                                                                                               
                        )) as detailListData                                                                                                                   
              	     FROM b_in_plan_detail tt1 LEFT JOIN m_goods_spec tt2 ON tt1.goods_code = tt2.goods_code AND tt1.sku_code = tt2.code                     
                       LEFT JOIN b_po_order_total tt3 ON tt1.order_id = tt3.po_order_id                                                                      
                       LEFT JOIN m_warehouse tt4 ON tt4.id = tt1.warehouse_id                                                                                
                       LEFT JOIN m_enterprise tt5 ON tt1.supplier_id = tt5.id                                                                                
                       LEFT JOIN m_location tt6 ON tt6.id = tt1.location_id                                                                                  
                       LEFT JOIN m_bin tt7 ON tt7.id = tt1.bin_id                                                                                            
                   GROUP BY tt1.in_plan_id) tab2 ON tab1.id = tab2.in_plan_id                                                                                
              		LEFT JOIN s_dict_data  tab3 ON tab3.code = 'b_in_plan_status' AND tab3.dict_value = tab1.status                  
              		LEFT JOIN s_dict_data  tab4 ON tab4.code = 'b_in_plan_type' AND tab4.dict_value = tab1.type                      
               LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id                                                                                               
               LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id                                                                                               
              		WHERE TRUE                                                                                                                                  
              		 AND tab1.is_del = false                                                                                                                    
              		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              
              		 AND (tab1.code = #{p1.code} or #{p1.code} is null or #{p1.code} = '')                                                                      
                  <if test='p1.ids != null and p1.ids.length != 0' >                                                                                            
                   and tab1.id in                                                                                                                               
                       <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                 
                        #{item}                                                                                                                                 
                       </foreach>                                                                                                                               
                  </if>                                                                                                                                         
              	GROUP BY                                                                                                                                        
              		tab2.in_plan_id) as tb1,(select @row_num:=0) tb2                                                                                            
              		  </script>                                                                                                                                 
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = InPlanDetailListTypeHandler.class),
    })
    List<BInPlanVo> selectExportList(@Param("p1")BInPlanVo param);

    @Select("""
            SELECT                                                                                                                                    
           	count(tab1.id)                                                                                                                                  
           	FROM                                                                                                                                            
           		b_in_plan tab1                                                                                                                              
           		WHERE TRUE                                                                                                                                  
           		 AND tab1.is_del = false                                                                                                                    
           		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')                                                              
           		 AND (tab1.code = #{p1.code} or #{p1.code} is null or #{p1.code} = '')                                  
            """)
    Long selectExportCount(@Param("p1")BInPlanVo param);

    /**
     * 根据code查询入库计划
     */
    @Select("select * from b_in_plan where code = #{code} and is_del = false")
    BInPlanVo selectByCode(@Param("code") String code);

    /**
     * 初始化计划数据
     */
    @Select("""
            SELECT                                                                             
              t1.po_contract_code AS contract_code,                                            
              t1.po_contract_id AS contract_id,                                                
              t1.id AS order_id,                                                               
              t1.CODE AS order_code,                                                           
              t2.id AS order_detail_id,                                                        
              t2.goods_code,                                                                   
              t2.goods_id,                                                                     
              t2.goods_name,                                                                   
              t2.sku_id,                                                                       
              t2.sku_code,                                                                     
              t2.price AS order_price,                                                         
              t2.amount AS order_amount,                                                       
              t2.sku_name,                                                                     
              t2.qty AS order_qty,                                                             
              t3.processed_qty,                                                                
              t3.unprocessed_qty,                                                              
              t1.supplier_id,                                                                  
              t1.supplier_code,                                                                
              t4.name AS supplier_name                                                         
            FROM                                                                               
              b_po_order t1                                                                    
              LEFT JOIN b_po_order_detail t2 ON t1.id = t2.po_order_id                        
              LEFT JOIN (                                                                      
                SELECT                                                                         
                  order_detail_id,                                                             
                  SUM(processed_qty) AS processed_qty,                                         
                  SUM(unprocessed_qty) AS unprocessed_qty                                      
                FROM b_in_plan_detail                                                          
                GROUP BY order_detail_id                                                       
              ) t3 ON t2.id = t3.order_detail_id                                              
              LEFT JOIN m_enterprise AS t4 ON t1.supplier_id = t4.id                          
            WHERE                                                                              
              t1.is_del = FALSE                                                                
              AND t1.id = #{searchCondition.order_id,jdbcType=INTEGER}                        
            ORDER BY                                                                           
              t1.id,                                                                           
              t2.id                                                                            
            """)
    List<BInPlanDetailVo> initPlanData(@Param("searchCondition") BInPlanDetailVo searchCondition);

}
