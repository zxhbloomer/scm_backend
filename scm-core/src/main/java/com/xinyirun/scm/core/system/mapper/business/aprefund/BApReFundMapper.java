package com.xinyirun.scm.core.system.mapper.business.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应付账款管理表（Accounts Payable） Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApReFundMapper extends BaseMapper<BApReFundEntity> {

    /**
     * 业务类型查询
     */
    @Select(" select dict_value as dict_id ,label as dict_label from s_dict_data where code = 'b_ap_refund_type' and is_del = false ")
    List<BApReFundVo> getType();

    /**
     * 分页查询
     */
    @Select("""
            SELECT                                                                                                                             
            	tab1.*,                                                                                                                          
            	0 as  not_pay_amount,                      
            	tab2.po_goods,
            	tab2.order_amount as source_order_amount,
            	tab2.refundable_amount_total,
            	tab2.refunded_amount_total,
            	tab2.refunding_amount_total,
            	tab2.unrefund_amount_total,
            	tab2.cancelrefund_amount_total,
            	tab3.ap_refund_id,
            	tab3.ap_refund_code,
            	tab3.bank_accounts_id,
            	tab3.bank_accounts_code,
            	tab3.refundable_amount,
            	tab3.refunded_amount,
            	tab3.refunding_amount,
            	tab3.unrefund_amount,
            	tab3.order_amount as detail_order_amount,
            	tab9.name,
            	tab9.bank_name,
            	tab9.account_number,
            	GROUP_CONCAT(tab10.NAME) AS accounts_purpose_type_name,                                                                                                             
            	tab4.name as c_name,                                                                                                             
              tab5.name as u_name,                                                                                                             
            	tab6.label as status_name,                                                                                                       
            	tab7.label as type_name,                                                                                                         
            	tab8.label as refund_status_name																	                                 
            FROM                                                                                                                               
            	b_ap_refund tab1                                                                                                                 
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                        
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                
            	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                    
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id  
            	LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_status' AND tab6.dict_value = tab1.status           
            	LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type               
            	LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_refund_pay_status' AND tab8.dict_value = tab1.refund_status
            	LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            	LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id  
            	WHERE TRUE
              AND tab1.is_del = false                                                                                                                                                                                                    
              AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')                                                                                                                                                     
              AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                                             
              AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                                                     
              AND (tab1.refund_status = #{p1.refund_status} or #{p1.refund_status} is null or  #{p1.refund_status} = '')                                                                                                                             
              AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                                                
                                                                                                                    
              AND (tab1.purchaser_name like concat('%', #{p1.purchaser_name}, '%') or #{p1.purchaser_name} is null or  #{p1.purchaser_name} = '')                                                            
              AND (tab1.supplier_name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or  #{p1.supplier_name} = '')
              GROUP BY tab1.code, tab3.code                                               
              """)
    IPage<BApReFundVo> selectPage(Page page, @Param("p1") BApReFundVo searchCondition);


    /**
     * 根据id查询
     */
    @Select("""
            SELECT                                                                                                                             
            	tab1.*,                                                                                                                          
            	0 as  not_pay_amount,                      
            	tab2.po_goods,
            	tab2.order_amount as source_order_amount,
            	tab2.refundable_amount_total,
            	tab2.refunded_amount_total,
            	tab2.refunding_amount_total,
            	tab2.unrefund_amount_total,
            	tab2.cancelrefund_amount_total,
            	tab2.advance_paid_total,
            	tab2.advance_refund_amount_total,
            	tab2.order_amount,
            	tab3.ap_refund_id,
            	tab3.ap_refund_code,
            	tab3.bank_accounts_id,
            	tab3.bank_accounts_code,
            	tab3.refundable_amount,
            	tab3.refunded_amount,
            	tab3.refunding_amount,
            	tab3.unrefund_amount,
            	tab3.order_amount as detail_order_amount,
            	tab9.name,
            	tab9.bank_name,
            	tab9.account_number,
            	GROUP_CONCAT(tab10.NAME) AS bank_type_name,                                                                                                             
            	tab4.name as c_name,                                                                                                             
              tab5.name as u_name,                                                                                                             
            	tab6.label as status_name,                                                                                                       
            	tab7.label as type_name,                                                                                                         
            	tab8.label as refund_status_name,
            	tabb1.one_file as doc_att_file
            FROM                                                                                                                               
            	b_ap_refund tab1                                                                                                                 
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                        
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                
            	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                    
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id  
            	LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_status' AND tab6.dict_value = tab1.status           
            	LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type               
            	LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_refund_pay_status' AND tab8.dict_value = tab1.refund_status
            	LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            	LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id  
                LEFT JOIN b_ap_refund_attach tabb1 on tab1.id = tabb1.ap_refund_id
            	WHERE TRUE
              AND tab1.id = #{p1}
              GROUP BY tab1.code, tab3.code
            """)
    BApReFundVo selectId(@Param("p1") Integer id);

    /**
     * 根据退款编号查询退款ID
     * @param code 退款编号
     * @return 退款ID
     */
    @Select("SELECT id FROM b_ap_refund WHERE code = #{p1} AND is_del = false")
    Integer selectIdByCode(@Param("p1") String code);

    @Select("""
            <script>	 SELECT                                                                                                                                                                              
            	@row_num:= @row_num+ 1 as no,                                                                                                                                                                    
            	tab1.*,                                                                                                                                                                       
            	0 as  not_pay_amount,                                                                                      
            	tab2.po_goods,
            	tab2.order_amount as source_order_amount,
            	tab2.refundable_amount_total,
            	tab2.refunded_amount_total,
            	tab2.refunding_amount_total,
            	tab2.unrefund_amount_total,
            	tab2.cancelrefund_amount_total,
            	tab3.ap_refund_id,
            	tab3.ap_refund_code,
            	tab3.bank_accounts_id,
            	tab3.bank_accounts_code,
            	tab3.refundable_amount,
            	tab3.refunded_amount,
            	tab3.refunding_amount,
            	tab3.unrefund_amount,
            	tab3.order_amount as detail_order_amount,
            	tab9.name,
            	tab9.bank_name,
            	tab9.account_number,
            	GROUP_CONCAT(tab10.NAME) AS accounts_purpose_type_name,    
            	tab4.name as c_name,                                                                                                                                                                             
              tab5.name as u_name,                                                                                                                                                                             
            	tab6.label as status_name,                                                                                                                                                                       
            	tab7.label as type_name,                                                                                                                                                                         
            	tab8.label as refund_status_name																	                                                                                                 
            FROM                                                                                                                                                                                               
            	b_ap_refund tab1                                                                                                                                                                                 
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                                                                                       
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                                                                                
            	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                                                                                    
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                                                                                    
            	LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_status' AND tab6.dict_value = tab1.status                                                                           
            	LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type                                                                               
            	LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_refund_pay_status' AND tab8.dict_value = tab1.refund_status,                                                                 
            	LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            	LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id,                                                               
            	(select @row_num:=0) tb11                                                                                                                                                                         
            	WHERE TRUE                                                                                                                                                                                       
              AND tab1.is_del = false                                                                                                                                                                          
              AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')                                                                                                                           
              AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                   
              AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                           
              AND (tab1.refund_status = #{p1.refund_status} or #{p1.refund_status} is null or  #{p1.refund_status} = '')                                                                                                   
              AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                      
                                                    
              AND (tab1.purchaser_name like concat('%', #{p1.purchaser_name}, '%') or #{p1.purchaser_name} is null or  #{p1.purchaser_name} = '')                                  
              AND (tab1.supplier_name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or  #{p1.supplier_name} = '')                      
               <if test='p1.ids != null and p1.ids.length != 0' >                                                                                                                                              
                and tab1.id in                                                                                                                                                                                 
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                                                                   
                     #{item}                                                                                                                                                                                   
                    </foreach>                                                                                                                                                                                 
               </if>
               GROUP BY tab1.code, tab3.code                                                                                                                                                                                           
            		  </script>                                                                                                                                                                                  
            """)
    List<BApReFundVo> selectExportList(@Param("p1") BApReFundVo param);

    @Select("""
           <script>	 SELECT                                                                                                                                                                              
           	count(tab1.id)																	                                                                                                 
           FROM                                                                                                                                                                                               
            	b_ap_refund tab1                                                                                                                                                                                 
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                                                                                       
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                                                                                
           	WHERE TRUE                                                                                                                                                                                       
              AND tab1.is_del = false                                                                                                                                                                           
              AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')                                                                                                                            
              AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                    
              AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                            
              AND (tab1.refund_status = #{p1.refund_status} or #{p1.refund_status} is null or  #{p1.refund_status} = '')                                                                                                    
              AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                       
              AND (tab1.purchaser_name like concat('%', #{p1.purchaser_name}, '%') or #{p1.purchaser_name} is null or  #{p1.purchaser_name} = '')                                   
              AND (tab1.supplier_name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or  #{p1.supplier_name} = '')                       
               <if test='p1.ids != null and p1.ids.length != 0' >                                                                                                                                               
                and tab1.id in                                                                                                                                                                                  
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                                                                    
                     #{item}                                                                                                                                                                                    
                    </foreach>                                                                                                                                                                                  
               </if>                                                                                                                                                                                            
           		  </script>                                                                                                                                                                                  
            """)
    Long selectExportCount(@Param("p1") BApReFundVo param);

    /**
     * 查询采购订单下 退款账单
     */
    @Select("select * from b_ap_refund tab1 left join b_po_order tab2 on tab1.po_order_code = tab2.code where tab2.id = #{p1} and tab1.is_del = false")
    List<BApReFundVo> selectByPoCode(@Param("p1")String code);

    /**
     * 查询采购订单下 退款账单
     */
    @Select("select * from b_ap_refund tab1 left join b_po_order tab2 on tab1.po_order_code = tab2.code where tab2.id = #{p1} and tab1.status != #{p2} and tab1.is_del = false")
    List<BApReFundVo> selByPoCodeNotByStatus(@Param("p1")Integer code,@Param("p2") String dictBApStatusFive);

    /**
     * 获取下推预付退款款数据
     */
    @Select("SELECT tab1.* FROM b_ap_refund tab1 WHERE tab1.is_del = false AND tab1.po_order_code = #{p1.po_order_code}")
    BApReFundVo getApRefund(@Param("p1")BApReFundVo searchCondition);

    /**
     * 汇总查询
     */
    @Select("""
            <script>
            SELECT 
                SUM(IFNULL(tabb2.refundable_amount_total, 0)) as refundable_amount_total,
                SUM(IFNULL(tabb2.refunded_amount_total, 0)) as refunded_amount_total,
                SUM(IFNULL(tabb2.refunding_amount_total, 0)) as refunding_amount_total,
                SUM(IFNULL(tabb2.unrefund_amount_total, 0)) as unrefund_amount_total,
                SUM(IFNULL(tabb2.cancelrefund_amount_total, 0)) as cancelrefund_amount_total
            FROM b_ap_refund tab1  
                LEFT JOIN b_ap_refund_total tabb2 on tab1.id = tabb2.ap_refund_id
            WHERE TRUE 
                AND tab1.is_del = false 
                AND (tab1.id = #{p1.id} OR #{p1.id} IS NULL) 
                AND (tab1.status = #{p1.status} OR #{p1.status} IS NULL OR #{p1.status} = '') 
                AND (tab1.type = #{p1.type} OR #{p1.type} IS NULL OR #{p1.type} = '') 
                AND (tab1.refund_status = #{p1.refund_status} OR #{p1.refund_status} IS NULL OR #{p1.refund_status} = '') 
                AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') OR #{p1.code} IS NULL OR #{p1.code} = '') 
                AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') OR #{p1.po_contract_code} IS NULL OR #{p1.po_contract_code} = '') 
                AND (tab1.po_order_code LIKE CONCAT('%', #{p1.po_order_code}, '%') OR #{p1.po_order_code} IS NULL OR #{p1.po_order_code} = '') 
                <if test='p1.status_list != null and p1.status_list.length!=0' > 
                  and tab1.status in 
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'> 
                      #{item} 
                    </foreach> 
                </if> 
            </script>
            """)
    BApReFundVo querySum(@Param("p1") BApReFundVo searchCondition);
}
