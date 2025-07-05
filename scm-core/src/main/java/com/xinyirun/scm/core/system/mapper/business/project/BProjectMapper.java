package com.xinyirun.scm.core.system.mapper.business.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.project.BProjectEntity;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.ProjectDetailListTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 项目管理表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Repository
public interface BProjectMapper extends BaseMapper<BProjectEntity> {

    /**
     * 通用查询SQL语句
     * 包含项目基本信息、关联的商品明细、字典数据、企业信息、员工信息和附件信息
     */
    String common_select = """
            	SELECT
            		t1.*,
            		tab3.one_file as doc_att_file,
            		t8.label type_name,
            		t9.label status_name,
            		t10.label payment_method_name,
            		t11.label delivery_type_name,
            		t14.name as finance_name ,
            		tab2.detailListData ,
            		tab13.name as c_name,
            		tab14.name as u_name
            	FROM
            		b_project t1
            	    LEFT JOIN (select project_id,JSON_ARRAYAGG(
            	    JSON_OBJECT(
            	        'goods_id', goods_id,
            	        'goods_code', goods_code,
            	        'goods_name', goods_name,
            	        'sku_code', sku_code,
            	        'sku_name', sku_name,
            	        'origin', origin,
            	        'sku_id', sku_id,
            	        'unit_id', unit_id,
            	        'qty', qty,
            	        'price', price,
            	        'amount', amount,
            	        'tax_amount', tax_amount,
            	        'tax_rate', tax_rate
            	    )) as detailListData
            	     from b_project_goods GROUP BY project_id) tab2 ON t1.id = tab2.project_id
                 left join s_dict_data t8 ON t1.type = t8.dict_value
                        AND t8.code = 'b_project_type'
                 left join s_dict_data t9 ON t1.status = t9.dict_value
                        AND t9.code = 'b_project_status'
                 left join s_dict_data t10 ON t1.payment_method = t10.dict_value
                        AND t10.code = 'b_project_payment_method'
                 left join s_dict_data t11 ON t1.delivery_type = t11.dict_value
                        AND t11.code = 'b_project_delivery_type'
                 left join m_enterprise t14 ON t14.id = t1.finance_id
                LEFT JOIN m_staff tab13 ON tab13.id = t1.c_id
                LEFT JOIN m_staff tab14 ON tab14.id = t1.u_id
            		LEFT JOIN b_project_attach tab3 on t1.id = tab3.project_id
            """;    /**
     * 分页查询项目列表
     * 支持多种条件筛选：项目名称、编码、类型、状态、供应商、采购方、运输类型、时间范围等
     * 查询条件与querySum方法保持一致，确保数据一致性
     * 
     * @param page 分页参数
     * @param param 查询条件参数
     * @return 分页结果，包含项目详细信息和关联数据
     */
    @Select("""
            <script>
            """ + common_select + """
                  where t1.is_del = false
                  and (t1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')
                  and (t1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
                  and (t1.type like concat('%', #{p1.type}, '%') or #{p1.type} is null or #{p1.type} = '')
                  and (t1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
                  and (t1.supplier_id = #{p1.supplier_id} or #{p1.supplier_id} is null)
                  and (t1.purchaser_id = #{p1.purchaser_id} or #{p1.purchaser_id} is null)
               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and t1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
                  and (t1.delivery_type = #{p1.delivery_type}
                            or #{p1.delivery_type} is null or #{p1.delivery_type} = '')
                  and (t1.u_time &gt;= #{p1.start_time,jdbcType=DATE} or #{p1.start_time,jdbcType=DATE} is null)
                  and (t1.u_time &lt;= #{p1.over_time,jdbcType=DATE} or #{p1.over_time,jdbcType=DATE} is null)
            </script>
            """)    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = ProjectDetailListTypeHandler.class),
    })
    IPage<BProjectVo> selectPage(Page<BProjectVo> page, @Param("p1") BProjectVo param);

    /**
     * 根据ID查询项目详情（带script标签）
     * 
     * @param id 项目ID
     * @return 项目详细信息
     */
    @Select("""
            <script>
            """ + common_select + """
                  where t1.code =  #{p1}
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = ProjectDetailListTypeHandler.class),    })
    BProjectVo selectCode(@Param("p1") String code);

    /**
     * 根据ID查询项目详情（带script标签）
     *
     * @param id 项目ID
     * @return 项目详细信息
     */
    @Select("""
            <script>
            """ + common_select + """
                  where t1.id =  #{p1}
            </script>
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = ProjectDetailListTypeHandler.class),    })
    BProjectVo selectId(@Param("p1") Integer id);

    /**
     * 根据ID查询项目详情（不带script标签）
     * 
     * @param id 项目ID
     * @return 项目详细信息
     */
    @Select(common_select + """
                  where t1.id =  #{p1}
            """)
    @Results({
            @Result(property = "detailListData", column = "detailListData", javaType = List.class, typeHandler = ProjectDetailListTypeHandler.class),
    })
    BProjectVo get(@Param("p1") Integer id);

    /**
     * 校验项目编号是否重复
     * @param id 项目ID（更新时传入，新增时为null）
     * @param code 项目编号
     * @return 重复的项目记录
     */
    @Select("select * from b_project where is_del = false and (id <> #{id} or #{id} is null) and code = #{code}")
    List<BProjectEntity> validateDuplicateProjectCode(@Param("id") Integer id, @Param("code") String code);

    /**
     * 校验项目名称是否重复
     * @param id 项目ID（更新时传入，新增时为null）
     * @param name 项目名称
     * @return 重复的项目记录
     */
    @Select("select * from b_project where is_del = false and (id <> #{id} or #{id} is null) and name = #{name}")    List<BProjectEntity> validateDuplicateProjectName(@Param("id") Integer id, @Param("name") String name);
    
    /**
     * 按项目管理合计查询
     * 计算符合条件的项目总金额
     * 查询条件与selectPage方法保持完全一致，确保统计数据与列表数据的一致性
     * 
     * @param searchCondition 查询条件参数
     * @return 合计结果，包含总金额等统计信息
     */
    @Select("""
            <script>
            	SELECT
            		IF(SUM( tab2.amount ) is not null  ,SUM( tab2.amount ),0)  as  amount_sum
            	FROM
            		b_project tab1
            		LEFT JOIN b_project_goods tab2
            		ON tab1.id = tab2.project_id
            		LEFT JOIN m_enterprise tab3 ON tab3.id = tab1.supplier_id
            		LEFT JOIN m_enterprise tab4 ON tab4.id = tab1.purchaser_id
            		WHERE tab1.is_del = false
            		 AND (tab1.name like concat('%', #{p1.name}, '%') or #{p1.name} is null or #{p1.name} = '')
            		 AND (tab1.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')
            		 AND (tab1.type like concat('%', #{p1.type}, '%') or #{p1.type} is null or #{p1.type} = '')
            		 AND (tab1.status = #{p1.status} or #{p1.status} is null or #{p1.status} = '')
            		 AND (tab1.supplier_id = #{p1.supplier_id}  or #{p1.supplier_id} is null   )
            		 AND (tab3.name like concat('%', #{p1.supplier_name}, '%') or #{p1.supplier_name} is null or #{p1.supplier_name} = '')
            		 AND (tab1.purchaser_id = #{p1.purchaser_id}  or #{p1.purchaser_id} is null   )
            		 AND (tab4.name like concat('%', #{p1.customer_name}, '%') or #{p1.customer_name} is null or #{p1.customer_name} = '')
               <if test='p1.status_list != null and p1.status_list.length!=0' >
                and tab1.status in
                    <foreach collection='p1.status_list' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
               </if>
            		 AND (tab1.delivery_type = #{p1.delivery_type} or #{p1.delivery_type} is null or #{p1.delivery_type} = '')
            		 AND (tab1.u_time &gt;= #{p1.start_time,jdbcType=DATE} or #{p1.start_time,jdbcType=DATE} is null)
            		 AND (tab1.u_time &lt;= #{p1.over_time,jdbcType=DATE} or #{p1.over_time,jdbcType=DATE} is null)
              </script>
            """)
    BProjectVo querySum(@Param("p1") BProjectVo searchCondition);
}
