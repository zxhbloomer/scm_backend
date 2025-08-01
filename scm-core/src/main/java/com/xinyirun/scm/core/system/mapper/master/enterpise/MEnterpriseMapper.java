package com.xinyirun.scm.core.system.mapper.master.enterpise;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 */
@Repository
public interface MEnterpriseMapper extends BaseMapper<MEnterpriseEntity> {





    /**
     * 页面查询列表
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                            AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
            where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                <if test='p1.type_ids != null and p1.type_ids.length!=0' >
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:5-加工厂，4-承运商，3-仓储方，2-供应商，1-客户，0-主体企业
                   and tb2.type in
                    <foreach collection='p1.type_ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
                         )
                  </if>
            </script>
            """)
    IPage<MEnterpriseVo> selectPage(Page page, @Param("p1") MEnterpriseVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 2-供应商, 1-客户, 0-主体企业
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                            AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
            where true
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                and (t1.id != #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)
                and t1.name = #{p1.name,jdbcType=VARCHAR}
                <if test='p1.isSupplier != null and p1.isSupplier == true'>
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:2-供应商
                   and tb2.type = '2'
                         )
                  </if>
                <if test='p1.isCustomer != null and p1.isCustomer == true'>
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:1-客户
                   and tb2.type = '1'
                         )
                  </if>
                <if test='p1.isSysCompany != null and p1.isSysCompany == true'>
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:0-主体企业
                   and tb2.type = '0'
                         )
                  </if>
            </script>
            """)
    List<MEnterpriseVo> validateDuplicateName(@Param("p1") MEnterpriseVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     */
    @Select("""
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                            AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
            where true
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                and (t1.id <> #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)
                and t1.uscc = #{p1.uscc,jdbcType=VARCHAR}
            """)
    List<MEnterpriseVo> validateDuplicateUscc(@Param("p1") MEnterpriseVo searchCondition);


    /**
     * 查询企业信息，按企业信用代码
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     */
    @Select("""
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                            AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
            where true
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                and t1.uscc = #{p1,jdbcType=VARCHAR}
            """)
    MEnterpriseVo selectByUscc(@Param("p1") String uscc);
    /**
     * 没有分页，按id筛选条件
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
            where true
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                and t1.id in
                    <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>
                     #{item.id,jdbcType=INTEGER}
                    </foreach>
            </script>
            """)
    List<MEnterpriseEntity> selectIdsIn(@Param("p1") List<MEnterpriseVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * @param id
     * @return
     */
    @Select("""
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
            where true
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                and t1.id = #{p1,jdbcType=INTEGER}
            """)
    MEnterpriseVo selectId(@Param("p1") int id);


    /**
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
             @row_num:= @row_num+ 1 as no,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id,(select @row_num:=0) t7
            where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                <if test='p1.ids != null and p1.ids.length!=0' >
                   and t1.id in
                    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
                  </if>
                <if test='p1.type_ids != null and p1.type_ids.length!=0' >
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:5-加工厂，4-承运商，3-仓储方，2-供应商，1-客户，0-主体企业
                   and tb2.type in
                    <foreach collection='p1.type_ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
                         )
                  </if>
            </script>
            """)
    List<MEnterpiseExcelVo> exportList(@Param("p1") MEnterpriseVo searchConditionList);

    /**
     * 企业类型查询
     * SQL中使用的常量: m_enterprise_type(企业类型)
     */
    @Select("""
            select dict_value as dict_id ,label as dict_label from s_dict_data where code = 'm_enterprise_type'
            """)
    List<MEnterpriseVo> getType();

    /**
     * 获取企业详情
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     */
    @Select("""
            SELECT
            	t1.*,
            	t2.logo_id as logo_file,
            	t2.license_att_id as license_att_file,
            	t2.lr_id_front_att_id as lr_id_front_att_file,
            	t2.lr_id_back_att_id as lr_id_back_att_file,
            	t2.doc_att_id as doc_att_file,
            	t5.label as status_name,
            	t6.type_ids as type_ids_str ,
              t6.type_names ,
            	t7.name as c_name,
            	t8.name as u_name
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            		LEFT JOIN (
            		SELECT
            			subt1.enterprise_id,
            			GROUP_CONCAT( subt1.type ORDER BY subt1.enterprise_id SEPARATOR ',' ) AS type_ids ,
            			GROUP_CONCAT( subt2.label ORDER BY subt1.enterprise_id SEPARATOR ',' ) AS type_names
            		FROM
            			m_enterprise_types subt1
            			left join s_dict_data subt2 on subt1.type = subt2.dict_value and   subt2.code = 'm_enterprise_type'
            		GROUP BY
            			subt1.enterprise_id
            		) AS t6 ON t1.id = t6.enterprise_id
            	LEFT JOIN m_staff t7 ON t1.c_id = t7.id
            	LEFT JOIN m_staff t8 ON t1.u_id = t8.id
            	WHERE TRUE
            	  and t1.id = #{p1.id}
               -- 删除0-未删除，1-已删除
               AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
            """)
    MEnterpriseVo getDetail(@Param("p1") MEnterpriseVo searchCondition);

    /**
     * SQL中使用的常量: m_customer_status(客户状态)
     */
    @Select("""
            <script>
            SELECT
            	count(1)
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_customer_status' AND t5.dict_value = t1.status
            	LEFT JOIN (SELECT t1.enterprise_id,GROUP_CONCAT( t1.type ORDER BY t1.enterprise_id SEPARATOR ',' )AS type_ids
            						FROM
            							m_enterprise_types t1 GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
               -- 删除0-未删除，1-已删除
               AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                <if test='p1.type_ids != null and p1.type_ids.length!=0' >
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   and tb2.type in
                    <foreach collection='p1.type_ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
                         )
                  </if>
            </script>
            """)
    int selectExportNum(@Param("p1") MEnterpriseVo searchConditionList);

    /**
     * 全部导出
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
             @row_num:= @row_num+ 1 as no,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id,(select @row_num:=0) t7
              where true
               -- 删除0-未删除，1-已删除
               AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                <if test='p1.type_ids != null and p1.type_ids.length!=0' >
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   and tb2.type in
                    <foreach collection='p1.type_ids' item='item' index='index' open='(' separator=',' close=')'>
                     #{item}
                    </foreach>
                         )
                  </if>
            </script>
            """)
    List<MEnterpiseExcelVo> exportAll(@Param("p1") MEnterpriseVo searchConditionList);


    @Select("""
            SELECT
            	max(t1.version) as version
            FROM
            	m_enterprise_his t1
            WHERE
            	true
            	 and t1.enterprise_id = #{p1}
            group by t1.enterprise_id
            """)
    Integer getMaxEnterpriseHisVersionByid(@Param("p1") Integer id);

    /**
     * 获取企业下拉列表数据（交易对手、供应商）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 2-供应商
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                and (CONCAT(t1.name,t1.name_pinyin,t1.name_short_pinyin,t1.legal_person, t1.legal_person_pinyin ,
                 t1.legal_person_short_pinyin)  like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null or #{p1.combine_search_condition,jdbcType=VARCHAR} ='')
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:2-供应商
                   and tb2.type = '2' )
            </script>
            """)
    IPage<MEnterpriseVo> selectCounterpartySupplierGridData(Page page, @Param("p1") MEnterpriseVo searchCondition);


    /**
     * 获取企业下拉列表数据（交易对手、买方、客户、经销商）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 1-客户
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                and (CONCAT(t1.name,t1.name_pinyin,t1.name_short_pinyin,t1.legal_person, t1.legal_person_pinyin ,
                 t1.legal_person_short_pinyin)  like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null or #{p1.combine_search_condition,jdbcType=VARCHAR} ='')
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:1-客户
                   and tb2.type = '1' )
            </script>
            """)
    IPage<MEnterpriseVo> selectCounterpartyCustomerGridData(Page page, @Param("p1") MEnterpriseVo searchCondition);

    /**
     * 获取企业下拉列表数据（主体企业、系统企业、供应商）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 0-主体企业
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = true
                and (CONCAT(t1.name,t1.name_pinyin,t1.name_short_pinyin,t1.legal_person, t1.legal_person_pinyin ,
                 t1.legal_person_short_pinyin)  like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null or #{p1.combine_search_condition,jdbcType=VARCHAR} ='')
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:0-主体企业
                   and tb2.type = '0' )
            </script>
            """)
    IPage<MEnterpriseVo> selectSystemEnterpriseSupplierGridData(Page page, @Param("p1") MEnterpriseVo searchCondition);

    /**
     * 获取企业下拉列表数据（主体企业、系统企业、客户）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 0-主体企业
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = true
                and (CONCAT(t1.name,t1.name_pinyin,t1.name_short_pinyin,t1.legal_person, t1.legal_person_pinyin ,
                 t1.legal_person_short_pinyin)  like CONCAT ('%',#{p1.combine_search_condition,jdbcType=VARCHAR},'%') or #{p1.combine_search_condition,jdbcType=VARCHAR} is null or #{p1.combine_search_condition,jdbcType=VARCHAR} ='')
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:0-主体企业
                   and tb2.type = '0' )
            </script>
            """)
    IPage<MEnterpriseVo> selectSystemEnterpriseCustomerGridData(Page page, @Param("p1") MEnterpriseVo searchCondition);

    /**
     * 根据查询条件，获取企业列表（交易对手、供应商）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 2-供应商
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:2-供应商
                   and tb2.type = '2' )
            </script>
            """)
    IPage<MEnterpriseVo> getCounterpartySupplierList(Page page, @Param("p1") MEnterpriseVo searchCondition);


    /**
     * 根据查询条件，获取企业列表（主体企业、系统企业、买方、经销商）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 0-主体企业（既可以是客户也可以是供应商）
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = true
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:0-主体企业
                   and tb2.type = '0' )
            </script>
            """)
    IPage<MEnterpriseVo> getSystemEnterpriseCustomerList(Page page, @Param("p1") MEnterpriseVo searchCondition);

    /**
     * 根据查询条件，获取企业列表（交易对手、供应商、主体企业）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 0-主体企业, 2-供应商
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = true
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:0-主体企业, 2-供应商
                   and tb2.type in ('0', '2') )
            </script>
            """)
    IPage<MEnterpriseVo> getSystemEnterpriseSupplierList(Page page, @Param("p1") MEnterpriseVo searchCondition);

    /**
     * 根据查询条件，获取企业列表（系统企业、买方、经销商）
     * SQL中使用的常量: m_enterprise_status(企业状态), m_enterprise_type(企业类型)
     * type值: 1-客户
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
            <script>
            SELECT
            	t1.*,
            	t3.name as c_name,
            	t4.name as u_name,
            	t5.label as status_name,
            	t6.type_names as type_names,
            	t1.bpm_instance_code as process_code
            FROM
            	m_enterprise t1
            	LEFT JOIN m_enterprise_attach t2 ON t1.id = t2.enterprise_id
            	LEFT JOIN m_staff t3 ON t1.c_id = t3.id
            	LEFT JOIN m_staff t4 ON t1.u_id = t4.id
            	LEFT JOIN s_dict_data t5 ON t5.code = 'm_enterprise_status' AND t5.dict_value = t1.status
            	LEFT JOIN (select t1.enterprise_id, GROUP_CONCAT(t2.label ORDER BY t1.enterprise_id SEPARATOR ', ') as type_names
            				from m_enterprise_types t1 left join s_dict_data t2 on t2.code = 'm_enterprise_type'
                         AND t2.dict_value = t1.type GROUP BY t1.enterprise_id ) AS t6 on t1.id = t6.enterprise_id
              where true
                AND (t1.uscc like CONCAT ('%',#{p1.uscc,jdbcType=VARCHAR},'%') or #{p1.uscc,jdbcType=VARCHAR} is null)
                AND (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null )
                -- 审批状态 0-待审批 1-审批中 2-审批通过 3-驳回
                AND (t1.status = #{p1.status,jdbcType=VARCHAR} or #{p1.status,jdbcType=VARCHAR} is null )
                -- 删除0-未删除，1-已删除
                AND t1.is_del = false
                -- 主体企业：0-false（不是）、1-true（是）
                AND t1.is_sys_company = false
                AND exists (
                  select 1 from m_enterprise tb1 left join m_enterprise_types tb2 on tb1.id = tb2.enterprise_id where true
                   and t1.id = tb1.id
                   -- 类型:1-客户
                   and tb2.type = '1' )
            </script>
            """)
    IPage<MEnterpriseVo> getCounterpartyCustomerList(Page page, @Param("p1") MEnterpriseVo searchCondition);

}
