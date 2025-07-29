package com.xinyirun.scm.core.system.mapper.business.po.aprefund;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.po.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundDetailVo;
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
    @Select("""
            -- 查询应付退款业务类型字典数据
            select dict_value as dict_id ,label as dict_label 
            from s_dict_data 
            -- code = 'b_ap_refund_type': 应付退款类型字典编码
            where code = 'b_ap_refund_type' 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            and is_del = false
            """)
    List<BApReFundVo> getType();

    /**
     * 分页查询
     */
    @Select("""
            -- 应付退款分页查询，包含退款来源、明细、银行账户等关联信息
            SELECT                                                                                                                             
            	-- 主表所有字段：应付退款基本信息
            	tab1.*,                                                                                                                          
            	-- not_pay_amount: 固定为0，保持接口兼容性
            	0 as  not_pay_amount,                      
            	-- po_goods: 采购商品信息（GROUP_CONCAT聚合）
            	tab2.po_goods,
            	-- source_order_amount: 来源单据本次申请金额
            	tab2.order_amount as source_order_amount,
            	-- refundable_amount_total: 退款金额
            	tab2.refundable_amount_total,
            	-- refunded_amount_total: 已退款金额
            	tab2.refunded_amount_total,
            	-- refunding_amount_total: 退款中金额
            	tab2.refunding_amount_total,
            	-- unrefund_amount_total: 未退款金额
            	tab2.unrefund_amount_total,
            	-- cancelrefund_amount_total: 退款取消金额
            	tab2.cancelrefund_amount_total,
            	-- 退款明细信息：银行账户相关字段
            	tab3.ap_refund_id,
            	tab3.ap_refund_code,
            	tab3.bank_accounts_id,
            	tab3.bank_accounts_code,
            	-- refundable_amount: 计划退款金额
            	tab3.refundable_amount,
            	-- refunded_amount: 实退金额
            	tab3.refunded_amount,
            	-- refunding_amount: 退款中金额
            	tab3.refunding_amount,
            	-- unrefund_amount: 未退款金额
            	tab3.unrefund_amount,
            	-- detail_order_amount: 本次退款金额
            	tab3.order_amount as detail_order_amount,
            	-- 银行账户信息
            	tab9.name,
            	tab9.bank_name,
            	tab9.account_number,
            	-- accounts_purpose_type_name: 银行账户用途类型名称（GROUP_CONCAT聚合）
            	GROUP_CONCAT(tab10.NAME) AS accounts_purpose_type_name,                                                                                                             
            	-- 人员信息：创建人和修改人姓名
            	tab4.name as c_name,                                                                                                             
              tab5.name as u_name,                                                                                                             
            	-- 字典翻译：状态、类型、退款状态的显示名称
            	tab6.label as status_name,                                                                                                       
            	tab7.label as type_name,                                                                                                         
            	tab8.label as refund_status_name																	                                 
            FROM                                                                                                                               
            	-- 主表：应付退款表
            	b_ap_refund tab1                                                                                                                 
            	-- 关联来源预付表：获取退款来源信息和金额汇总
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                        
            	-- 关联退款明细表：获取银行账户和退款明细金额
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                
            	-- 关联员工表：获取创建人姓名
            	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                    
              -- 关联员工表：获取修改人姓名
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id  
            	-- 关联字典表：获取应付退款状态显示名称
            	LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_status' AND tab6.dict_value = tab1.status           
            	-- 关联字典表：获取应付退款类型显示名称
            	LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type               
            	-- 关联字典表：获取应付退款支付状态显示名称
            	LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_refund_pay_status' AND tab8.dict_value = tab1.refund_status
            	-- 关联银行账户表：获取银行账户详细信息
            	LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            	-- 关联银行账户类型表：获取账户用途类型
            	LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id  
            	WHERE TRUE
              -- is_del = false: 查询未删除的记录
              AND tab1.is_del = false                                                                                                                                                                                                    
              -- #{p1.code}: 应付退款编号模糊查询
              AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')                                                                                                                                                     
              -- #{p1.status}: 应付退款状态精确匹配
              AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                                             
              -- #{p1.type}: 应付退款类型精确匹配
              AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                                                     
              -- #{p1.refund_status}: 应付退款支付状态精确匹配
              AND (tab1.refund_status = #{p1.refund_status} or #{p1.refund_status} is null or  #{p1.refund_status} = '')                                                                                                                             
              -- #{p1.po_contract_code}: 采购合同编号模糊查询
              AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                                                
              -- #{p1.purchaser_name}: 购买方名称模糊查询
              AND (tab1.purchaser_name like concat('%', #{p1.purchaser_name}, '%') or #{p1.purchaser_name} is null or  #{p1.purchaser_name} = '')                                                            
              -- #{p1.supplier_name}: 供应商名称模糊查询
              AND (tab1.supplier_name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or  #{p1.supplier_name} = '')
              -- GROUP BY: 按应付退款编号和明细编号分组，避免重复数据
              GROUP BY tab1.code, tab3.code                                               
              """)
    IPage<BApReFundVo> selectPage(Page page, @Param("p1") BApReFundVo searchCondition);


    /**
     * 根据id查询
     */
    @Select("""
            -- 根据ID查询应付退款详细信息，包含所有关联数据和附件信息
            SELECT                                                                                                                             
            	-- 主表所有字段：应付退款基本信息
            	tab1.*,                                                                                                                          
            	-- not_pay_amount: 固定为0，保持接口兼容性
            	0 as  not_pay_amount,                      
            	-- po_goods: 采购商品信息（GROUP_CONCAT聚合）
            	tab2.po_goods,
            	-- source_order_amount: 来源单据本次申请金额
            	tab2.order_amount as source_order_amount,
            	-- 来源预付表的金额统计信息
            	tab2.refundable_amount_total,
            	tab2.refunded_amount_total,
            	tab2.refunding_amount_total,
            	tab2.unrefund_amount_total,
            	tab2.cancelrefund_amount_total,
            	-- advance_paid_total: 预付款已付金额
            	tab2.advance_paid_total,
            	-- advance_refund_amount_total: 可退金额
            	tab2.advance_refund_amount_total,
            	-- order_amount: 本次申请金额（来源预付表）
            	tab2.order_amount,
            	-- 退款明细信息：银行账户相关字段
            	tab3.ap_refund_id,
            	tab3.ap_refund_code,
            	tab3.bank_accounts_id,
            	tab3.bank_accounts_code,
            	-- 明细表的退款金额字段
            	tab3.refundable_amount,
            	tab3.refunded_amount,
            	tab3.refunding_amount,
            	tab3.unrefund_amount,
            	-- detail_order_amount: 本次退款金额（明细表）
            	tab3.order_amount as detail_order_amount,
            	-- 银行账户信息
            	tab9.name,
            	tab9.bank_name,
            	tab9.account_number,
            	-- bank_type_name: 银行账户类型名称（GROUP_CONCAT聚合）
            	GROUP_CONCAT(tab10.NAME) AS bank_type_name,                                                                                                             
            	-- 人员信息：创建人和修改人姓名
            	tab4.name as c_name,                                                                                                             
              tab5.name as u_name,                                                                                                             
            	-- 字典翻译：状态、类型、退款状态的显示名称
            	tab6.label as status_name,                                                                                                       
            	tab7.label as type_name,                                                                                                         
            	tab8.label as refund_status_name,
            	-- doc_att_file: 文档附件文件信息
            	tabb1.one_file as doc_att_file
            FROM                                                                                                                               
            	-- 主表：应付退款表
            	b_ap_refund tab1                                                                                                                 
            	-- 关联来源预付表：获取退款来源信息和金额汇总
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                        
            	-- 关联退款明细表：获取银行账户和退款明细金额
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                
            	-- 关联员工表：获取创建人姓名
            	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                    
              -- 关联员工表：获取修改人姓名
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id  
            	-- 关联字典表：获取应付退款状态显示名称
            	LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_status' AND tab6.dict_value = tab1.status           
            	-- 关联字典表：获取应付退款类型显示名称
            	LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type               
            	-- 关联字典表：获取应付退款支付状态显示名称
            	LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_refund_pay_status' AND tab8.dict_value = tab1.refund_status
            	-- 关联银行账户表：获取银行账户详细信息
            	LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id
            	-- 关联银行账户类型表：获取账户用途类型
            	LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id  
                -- 关联附件表：获取应付退款附件文件信息
                LEFT JOIN b_ap_refund_attach tabb1 on tab1.id = tabb1.ap_refund_id
            	WHERE TRUE
              -- #{p1}: 应付退款主表ID精确匹配
              AND tab1.id = #{p1}
              -- GROUP BY: 按应付退款编号和明细编号分组，避免重复数据
              GROUP BY tab1.code, tab3.code
            """)
    BApReFundVo selectId(@Param("p1") Integer id);

    /**
     * 根据退款编号查询退款ID
     * @param code 退款编号
     * @return 退款ID
     */
    @Select("""
            -- 根据退款编号查询退款ID
            SELECT id FROM b_ap_refund 
            -- #{p1}: 退款编号
            WHERE code = #{p1} 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            AND is_del = false
            """)
    Integer selectIdByCode(@Param("p1") String code);

    @Select("""
            <script>
            -- 应付退款导出查询，包含行号和完整的关联信息
            SELECT
            	-- no: 自增行号，用于导出时显示序号
            	@row_num:= @row_num+ 1 as no,                                                                                                                                                                    
            	-- 主表所有字段：应付退款基本信息
            	tab1.*,                                                                                                                                                                       
            	-- not_pay_amount: 固定为0，保持接口兼容性
            	0 as  not_pay_amount,                                                                                      
            	-- po_goods: 采购商品信息（GROUP_CONCAT聚合）
            	tab2.po_goods,
            	-- source_order_amount: 来源单据本次申请金额
            	tab2.order_amount as source_order_amount,
            	-- 来源预付表的金额统计信息
            	tab2.refundable_amount_total,
            	tab2.refunded_amount_total,
            	tab2.refunding_amount_total,
            	tab2.unrefund_amount_total,
            	tab2.cancelrefund_amount_total,
            	-- 退款明细信息：银行账户相关字段
            	tab3.ap_refund_id,
            	tab3.ap_refund_code,
            	tab3.bank_accounts_id,
            	tab3.bank_accounts_code,
            	-- 明细表的退款金额字段
            	tab3.refundable_amount,
            	tab3.refunded_amount,
            	tab3.refunding_amount,
            	tab3.unrefund_amount,
            	-- detail_order_amount: 本次退款金额（明细表）
            	tab3.order_amount as detail_order_amount,
            	-- 银行账户信息
            	tab9.name,
            	tab9.bank_name,
            	tab9.account_number,
            	-- accounts_purpose_type_name: 银行账户用途类型名称（GROUP_CONCAT聚合）
            	GROUP_CONCAT(tab10.NAME) AS accounts_purpose_type_name,    
            	-- 人员信息：创建人和修改人姓名
            	tab4.name as c_name,                                                                                                                                                                             
              tab5.name as u_name,                                                                                                                                                                             
            	-- 字典翻译：状态、类型、退款状态的显示名称
            	tab6.label as status_name,                                                                                                                                                                       
            	tab7.label as type_name,                                                                                                                                                                         
            	tab8.label as refund_status_name																	                                                                                                 
            FROM                                                                                                                                                                                               
            	-- 主表：应付退款表
            	b_ap_refund tab1                                                                                                                                                                                 
            	-- 关联来源预付表：获取退款来源信息和金额汇总
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                                                                                       
            	-- 关联退款明细表：获取银行账户和退款明细金额
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                                                                                
            	-- 关联员工表：获取创建人姓名
            	LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id                                                                                                                                                    
              -- 关联员工表：获取修改人姓名
              LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id                                                                                                                                                    
            	-- 关联字典表：获取应付退款状态显示名称
            	LEFT JOIN s_dict_data tab6 ON tab6.code = 'b_ap_refund_status' AND tab6.dict_value = tab1.status                                                                           
            	-- 关联字典表：获取应付退款类型显示名称
            	LEFT JOIN s_dict_data tab7 ON tab7.code = 'b_ap_refund_type' AND tab7.dict_value = tab1.type                                                                               
            	-- 关联字典表：获取应付退款支付状态显示名称
            	LEFT JOIN s_dict_data tab8 ON tab8.code = 'b_ap_refund_pay_status' AND tab8.dict_value = tab1.refund_status,                                                                 
            	-- 关联银行账户表：获取银行账户详细信息
            	LEFT JOIN m_bank_accounts tab9 ON tab3.bank_accounts_id = tab9.id,
            	-- 关联银行账户类型表：获取账户用途类型
            	LEFT JOIN m_bank_accounts_type tab10 ON tab9.id = tab10.bank_id,                                                               
            	-- 初始化行号变量，用于生成自增序号
            	(select @row_num:=0) tb11                                                                                                                                                                         
            	WHERE TRUE                                                                                                                                                                                       
              -- is_del = false: 查询未删除的记录
              AND tab1.is_del = false                                                                                                                                                                          
              -- 查询条件：支持空值和空字符串的处理
              -- #{p1.code}: 应付退款编号模糊查询
              AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')                                                                                                                           
              -- #{p1.status}: 应付退款状态精确匹配
              AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                   
              -- #{p1.type}: 应付退款类型精确匹配
              AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                           
              -- #{p1.refund_status}: 应付退款支付状态精确匹配
              AND (tab1.refund_status = #{p1.refund_status} or #{p1.refund_status} is null or  #{p1.refund_status} = '')                                                                                                   
              -- #{p1.po_contract_code}: 采购合同编号模糊查询
              AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                      
              -- #{p1.purchaser_name}: 购买方名称模糊查询
              AND (tab1.purchaser_name like concat('%', #{p1.purchaser_name}, '%') or #{p1.purchaser_name} is null or  #{p1.purchaser_name} = '')                                  
              -- #{p1.supplier_name}: 供应商名称模糊查询
              AND (tab1.supplier_name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or  #{p1.supplier_name} = '')                      
               -- MyBatis动态SQL：当p1.ids非空时，按ID集合过滤
               <if test='p1.ids != null and p1.ids.length != 0' >                                                                                                                                              
                and tab1.id in                                                                                                                                                                                 
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>                                                                                                   
                     #{item}                                                                                                                                                                                   
                    </foreach>                                                                                                                                                                                 
               </if>
               -- GROUP BY: 按应付退款编号和明细编号分组，避免重复数据
               GROUP BY tab1.code, tab3.code                                                                                                                                                                                           
            		  </script>                                                                                                                                                                                  
            """)
    List<BApReFundVo> selectExportList(@Param("p1") BApReFundVo param);

    @Select("""
           <script>
           -- 应付退款导出计数查询，用于统计导出数据的总数量
           SELECT
           	-- 统计符合条件的应付退款记录数量
           	count(tab1.id)																	                                                                                                 
           FROM                                                                                                                                                                                               
            	-- 主表：应付退款表
            	b_ap_refund tab1                                                                                                                                                                                 
            	-- 关联来源预付表：用于支持复合查询条件
            	LEFT JOIN b_ap_refund_source_advance tab2 ON tab1.id = tab2.ap_refund_id                                                                                                                       
            	-- 关联退款明细表：用于支持复合查询条件
            	LEFT JOIN b_ap_refund_detail tab3 ON tab1.id = tab3.ap_refund_id                                                                                                                                
           	WHERE TRUE                                                                                                                                                                                       
              -- is_del = false: 查询未删除的记录
              AND tab1.is_del = false                                                                                                                                                                           
              -- 查询条件：与导出查询保持一致，支持空值和空字符串处理
              -- #{p1.code}: 应付退款编号模糊查询
              AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') or #{p1.code} is null or  #{p1.code} = '')                                                                                                                            
              -- #{p1.status}: 应付退款状态精确匹配
              AND (tab1.status = #{p1.status} or #{p1.status} is null or  #{p1.status} = '')                                                                                                                    
              -- #{p1.type}: 应付退款类型精确匹配
              AND (tab1.type = #{p1.type} or #{p1.type} is null or  #{p1.type} = '')                                                                                                                            
              -- #{p1.refund_status}: 应付退款支付状态精确匹配
              AND (tab1.refund_status = #{p1.refund_status} or #{p1.refund_status} is null or  #{p1.refund_status} = '')                                                                                                    
              -- #{p1.po_contract_code}: 采购合同编号模糊查询
              AND (tab1.po_contract_code like concat('%', #{p1.po_contract_code}, '%') or #{p1.po_contract_code} is null or  #{p1.po_contract_code} = '')                                                       
              -- #{p1.purchaser_name}: 购买方名称模糊查询
              AND (tab1.purchaser_name like concat('%', #{p1.purchaser_name}, '%') or #{p1.purchaser_name} is null or  #{p1.purchaser_name} = '')                                   
              -- #{p1.supplier_name}: 供应商名称模糊查询
              AND (tab1.supplier_name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or  #{p1.supplier_name} = '')                       
               -- MyBatis动态SQL：当p1.ids非空时，按ID集合过滤
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
    @Select("""
            -- 根据采购订单ID查询对应的退款账单
            select * from b_ap_refund tab1 
            -- 关联采购订单表，通过订单编号关联
            left join b_po_order tab2 on tab1.po_order_code = tab2.code 
            -- #{p1}: 采购订单ID
            where tab2.id = #{p1} 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            and tab1.is_del = false
            """)
    List<BApReFundVo> selectByPoCode(@Param("p1")String code);

    /**
     * 查询采购订单下 退款账单
     */
    @Select("""
            -- 根据采购订单ID查询退款账单，排除指定状态
            select * from b_ap_refund tab1 
            -- 关联采购订单表，通过订单编号关联
            left join b_po_order tab2 on tab1.po_order_code = tab2.code 
            -- #{p1}: 采购订单ID
            where tab2.id = #{p1} 
            -- #{p2}: 需要排除的状态（通常为'5'-已作废）
            and tab1.status != #{p2} 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            and tab1.is_del = false
            """)
    List<BApReFundVo> selByPoCodeNotByStatus(@Param("p1")Integer code,@Param("p2") String dictBApStatusFive);

    /**
     * 获取下推预付退款款数据
     */
    @Select("""
            -- 根据采购订单编号获取下推预付退款数据
            SELECT tab1.* FROM b_ap_refund tab1 
            -- is_del = false: 未删除的记录（0-未删除，1-已删除）
            WHERE tab1.is_del = false 
            -- #{p1.po_order_code}: 采购订单编号
            AND tab1.po_order_code = #{p1.po_order_code}
            """)
    BApReFundVo getApRefund(@Param("p1")BApReFundVo searchCondition);

    /**
     * 汇总查询
     */
    @Select("""
            <script>
            -- 应付退款金额汇总查询，统计各种退款状态的金额合计
            SELECT 
                -- 退款金额汇总：使用IFNULL处理空值，默认为0
                SUM(IFNULL(tabb2.refundable_amount_total, 0)) as refundable_amount_total,
                -- 已退款金额汇总
                SUM(IFNULL(tabb2.refunded_amount_total, 0)) as refunded_amount_total,
                -- 退款中金额汇总
                SUM(IFNULL(tabb2.refunding_amount_total, 0)) as refunding_amount_total,
                -- 未退款金额汇总
                SUM(IFNULL(tabb2.unrefund_amount_total, 0)) as unrefund_amount_total,
                -- 退款取消金额汇总
                SUM(IFNULL(tabb2.cancelrefund_amount_total, 0)) as cancelrefund_amount_total
            FROM 
                -- 主表：应付退款表
                b_ap_refund tab1  
                -- 关联退款汇总表：获取各种退款状态的金额统计
                LEFT JOIN b_ap_refund_total tabb2 on tab1.id = tabb2.ap_refund_id
            WHERE TRUE 
                -- is_del = false: 查询未删除的记录
                AND tab1.is_del = false 
                -- 查询条件：支持空值判断和空字符串处理
                -- #{p1.id}: 应付退款主ID精确匹配
                AND (tab1.id = #{p1.id} OR #{p1.id} IS NULL) 
                -- #{p1.status}: 应付退款状态精确匹配
                AND (tab1.status = #{p1.status} OR #{p1.status} IS NULL OR #{p1.status} = '') 
                -- #{p1.type}: 应付退款类型精确匹配
                AND (tab1.type = #{p1.type} OR #{p1.type} IS NULL OR #{p1.type} = '') 
                -- #{p1.refund_status}: 应付退款支付状态精确匹配
                AND (tab1.refund_status = #{p1.refund_status} OR #{p1.refund_status} IS NULL OR #{p1.refund_status} = '') 
                -- #{p1.code}: 应付退款编号模糊查询
                AND (tab1.code LIKE CONCAT('%', #{p1.code}, '%') OR #{p1.code} IS NULL OR #{p1.code} = '') 
                -- #{p1.po_contract_code}: 采购合同编号模糊查询
                AND (tab1.po_contract_code LIKE CONCAT('%', #{p1.po_contract_code}, '%') OR #{p1.po_contract_code} IS NULL OR #{p1.po_contract_code} = '') 
                -- #{p1.po_order_code}: 采购订单编号模糊查询
                AND (tab1.po_order_code LIKE CONCAT('%', #{p1.po_order_code}, '%') OR #{p1.po_order_code} IS NULL OR #{p1.po_order_code} = '') 
                -- MyBatis动态SQL：当p1.status_list非空时，按状态列表过滤
                <if test='p1.status_list != null and p1.status_list.length!=0' > 
                  and tab1.status in 
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'> 
                      #{item} 
                    </foreach> 
                </if> 
            </script>
            """)
    BApReFundVo querySum(@Param("p1") BApReFundVo searchCondition);

    /**
     * 获取应付退款明细信息（银行账号）
     * 根据BApMapper.getApDetail方法学习实现
     * @param id 应付退款ID
     * @return 应付退款明细信息
     */
    @Select("""
        -- 根据应付退款ID查询退款明细信息，包含银行账户和类型信息
        SELECT
            -- t1.*: 退款明细表所有字段（包含各种退款金额）
            t1.*,
            -- 银行账户基本信息
            t2.name,
            t2.bank_name,
            t2.account_number,
            -- bank_type_name: 银行账户类型名称（GROUP_CONCAT聚合）
            GROUP_CONCAT(t3.name) AS bank_type_name
        FROM 
            -- 主表：应付退款明细表
            b_ap_refund_detail t1
        -- 关联银行账户表：获取银行账户基本信息
        LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
        -- 关联银行账户类型表：获取账户用途类型
        LEFT JOIN m_bank_accounts_type t3 ON t2.id = t3.bank_id
        WHERE 
            -- #{p1}: 应付退款主ID，精确匹配对应的退款明细
            t1.ap_refund_id = #{p1}
        -- GROUP BY: 按退款明细的主要字段分组，防止GROUP_CONCAT造成的数据重复
        GROUP BY t1.id, t1.code, t1.ap_refund_id, t1.ap_refund_code, t1.bank_accounts_id, t1.bank_accounts_code, 
                 t1.refundable_amount, t1.refunded_amount, t1.refunding_amount, t1.unrefund_amount, 
                 t1.cancel_amount, t1.order_amount, t2.name, t2.bank_name, t2.account_number
        """)
    List<BApReFundDetailVo> getApRefundDetail(@Param("p1") Integer id);
}
