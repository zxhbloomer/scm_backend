package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyVo;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyExportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 公司主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MCompanyMapper extends BaseMapper<MCompanyEntity> {
    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT 
            t1.id,
            t1.code,
            t1.company_no,
            t1.name,
            t1.simple_name,
            t1.address_id,
            t1.juridical_name,
            t1.register_capital,
            t1.type,
            t1.setup_date,
            t1.end_date,
            t1.descr,
            t1.is_del,
            t1.c_id,
            t1.c_time,
            t1.u_id,
            t1.u_time,
            t1.dbversion,
            t2.postal_code,
            t2.province_code,
            t2.city_code,
            t2.area_code,
            t2.detail_address,
            c_staff.name as c_name,
            u_staff.name as u_name,
            t3.label as type_name,
            f_get_org_simple_name(t4.code, 'm_group') group_simple_name
        FROM 
            m_company AS t1
            LEFT JOIN m_address AS t2 ON t1.address_id = t2.id
            LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id
            LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id
            -- 企业类型字典代码：sys_company_type
            LEFT JOIN v_dict_info AS t3 ON t3.code = 'sys_company_type' and t3.dict_value = t1.type
            LEFT JOIN v_org_relation t4 ON t4.serial_type = 'm_company' and t4.serial_id = t1.id
        WHERE true
            AND (t1.name LIKE CONCAT('%', #{p1.name,jdbcType=VARCHAR}, '%') OR #{p1.name,jdbcType=VARCHAR} IS NULL)
            AND (t1.company_no LIKE CONCAT('%', #{p1.company_no,jdbcType=VARCHAR}, '%') OR #{p1.company_no,jdbcType=VARCHAR} IS NULL)
            AND (#{p1.group_name,jdbcType=VARCHAR} IS NULL OR #{p1.group_name,jdbcType=VARCHAR} = '' OR f_get_org_simple_and_full_name(t4.code, 'm_group') LIKE CONCAT('%', #{p1.group_name,jdbcType=VARCHAR}, '%'))
            AND (t1.is_del = #{p1.is_del,jdbcType=VARCHAR} OR #{p1.is_del,jdbcType=VARCHAR} IS NULL)
            AND (t1.id = #{p1.id,jdbcType=BIGINT} OR #{p1.id,jdbcType=BIGINT} IS NULL)
            AND (
                CASE 
                    -- dataModel为'10'时显示组织架构中未使用的企业
                    WHEN #{p1.dataModel,jdbcType=VARCHAR} = '10' THEN
                        NOT EXISTS (
                            SELECT 1 
                            FROM m_org subt1 
                            -- 企业序列类型代码：m_company
                            WHERE subt1.serial_type = 'm_company'
                              AND t1.id = subt1.serial_id
                        )
                    ELSE true 
                END
            )
        """)
    IPage<MCompanyVo> selectPage(Page page, @Param("p1") MCompanyVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT 
            t1.id,
            t1.code,
            t1.company_no,
            t1.name,
            t1.simple_name,
            t1.address_id,
            t1.juridical_name,
            t1.register_capital,
            t1.type,
            t1.setup_date,
            t1.end_date,
            t1.descr,
            t1.is_del,
            t1.c_id,
            t1.c_time,
            t1.u_id,
            t1.u_time,
            t1.dbversion,
            t2.postal_code,
            t2.province_code,
            t2.city_code,
            t2.area_code,
            t2.detail_address,
            c_staff.name as c_name,
            u_staff.name as u_name,
            t3.label as is_del_name,
            t4.label as type_name,
            f_get_org_simple_name(t5.code, 'm_group') group_simple_name
        FROM 
            m_company AS t1
            LEFT JOIN m_address AS t2 ON t1.address_id = t2.id
            LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id
            LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id
            -- 删除状态字典代码：sys_delete_map
            LEFT JOIN v_dict_info AS t3 ON t3.code = 'sys_delete_map' and t3.dict_value = CONCAT('', t1.is_del)
            -- 企业类型字典代码：sys_company_type  
            LEFT JOIN v_dict_info AS t4 ON t4.code = 'sys_company_type' and t4.dict_value = t1.type
            LEFT JOIN v_org_relation t5 ON t5.serial_type = 'm_company' and t5.serial_id = t1.id
        WHERE true
            AND (t1.name LIKE CONCAT('%', #{p1.name,jdbcType=VARCHAR}, '%') OR #{p1.name,jdbcType=VARCHAR} IS NULL)
            AND (t1.is_del = #{p1.is_del,jdbcType=VARCHAR} OR #{p1.is_del,jdbcType=VARCHAR} IS NULL)
        """)
    List<MCompanyVo> select(@Param("p1") MCompanyVo searchCondition);


    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("""
        <script>
            SELECT t.*
            FROM m_company t
            WHERE true
                AND t.id IN
                <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>
                    #{item.id}
                </foreach>
        </script>
        """)
    List<MCompanyEntity> selectIdsIn(@Param("p1") List<MCompanyVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("""
        SELECT t.*
        FROM m_company t
        WHERE true
            AND t.code = #{p1}
            AND (t.id <> #{p2} OR #{p2} IS NULL)
        """)
    List<MCompanyEntity> selectByCode(@Param("p1") String code, @Param("p2") Long equal_id );

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("""
        SELECT t.*
        FROM m_company t
        WHERE true
            AND t.name = #{p1}
            AND (t.id <> #{p2} OR #{p2} IS NULL)
        """)
    List<MCompanyEntity> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("""
        SELECT t.*
        FROM m_company t
        WHERE true
            AND t.simple_name = #{p1}
            AND (t.id <> #{p2} OR #{p2} IS NULL)
        """)
    List<MCompanyEntity> selectBySimpleName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 查询在组织架构中是否存在有被使用的数据
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM m_org t
        WHERE true
            -- 企业序列类型代码：m_company
            AND t.serial_type = 'm_company'
            AND t.serial_id = #{p1.id,jdbcType=BIGINT}
        """)
    int isExistsInOrg(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在采购合同中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM b_po_contract t
        WHERE true
            AND t.purchaser_id = #{p1.id,jdbcType=BIGINT}
            AND t.is_del = false
        """)
    int isExistsInPoContract(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在销售合同中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM b_so_contract t
        WHERE true
            AND t.seller_id = #{p1.id,jdbcType=BIGINT}
            AND t.is_del = false
        """)
    int isExistsInSoContract(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在采购订单中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM b_po_order t
        WHERE true
            AND t.purchaser_id = #{p1.id,jdbcType=BIGINT}
            AND t.is_del = false
        """)
    int isExistsInPoOrder(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在销售订单中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM b_so_order t
        WHERE true
            AND t.seller_id = #{p1.id,jdbcType=BIGINT}
            AND t.is_del = false
        """)
    int isExistsInSoOrder(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在应付款中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM b_ap_pay t
        WHERE true
            AND t.purchaser_id = #{p1.id,jdbcType=BIGINT}
            AND t.status = '1'
        """)
    int isExistsInApPay(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在应收款中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM b_ar_receive t
        WHERE true
            AND t.seller_id = #{p1.id,jdbcType=BIGINT}
            AND t.status = '1'
        """)
    int isExistsInArReceive(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业在员工表中的关联情况
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM m_staff t
        WHERE true
            AND t.company_id = #{p1.id,jdbcType=BIGINT}
            AND t.is_del = false
        """)
    int isExistsInStaff(@Param("p1") MCompanyEntity searchCondition);

    /**
     * 检查主体企业下级组织关联情况（部门等下级实体）
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT COUNT(1)
        FROM m_org t
        WHERE true
            AND t.parent_id IN (
                SELECT subt1.id 
                FROM m_org subt1 
                WHERE subt1.serial_type = 'm_company'
                  AND subt1.serial_id = #{p1.id,jdbcType=BIGINT}
            )
        """)
    int isExistsInSubOrg(@Param("p1") MCompanyEntity searchCondition);

    /**
     *
     * 根据id获取数据
     *
     * @param id
     * @return
     */
    @Select("""
        SELECT 
            t1.id,
            t1.code,
            t1.name,
            t1.company_no,
            t1.simple_name,
            t1.address_id,
            t1.juridical_name,
            t1.register_capital,
            t1.type,
            t1.setup_date,
            t1.end_date,
            t1.descr,
            t1.is_del,
            t1.c_id,
            t1.c_time,
            t1.u_id,
            t1.u_time,
            t1.dbversion,
            t2.postal_code,
            t2.province_code,
            t2.city_code,
            t2.area_code,
            t2.detail_address,
            c_staff.name as c_name,
            u_staff.name as u_name
        FROM 
            m_company AS t1
            LEFT JOIN m_address AS t2 ON t1.address_id = t2.id
            LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id
            LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id
        WHERE true
            AND t1.id = #{p1}
        """)
    MCompanyVo selectId(@Param("p1") Long id);

    /**
     * 导出专用查询 - 标准化实现
     * @param searchCondition 查询条件
     * @param orderByClause 动态排序子句
     * @return 导出数据列表
     */
    @Select("""
        <script>
            SELECT (@row_num:= @row_num + 1) as no,
                   t1.code,
                   t1.company_no,
                   t1.name,
                   t1.simple_name,
                   t1.juridical_name,
                   t1.register_capital,
                   t3.label as type_name,
                   t1.setup_date,
                   t1.descr,
                   u_staff.name as u_name,
                   t1.u_time,
                   f_get_org_simple_name(t4.code, 'm_group') group_simple_name
              FROM m_company AS t1
              LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id
              LEFT JOIN v_dict_info AS t3 ON t3.code = 'sys_company_type' and t3.dict_value = t1.type
              LEFT JOIN v_org_relation t4 ON t4.serial_type = 'm_company' and t4.serial_id = t1.id
                   ,(SELECT @row_num := 0) r
            WHERE true
                AND (t1.name LIKE CONCAT('%', #{p1.name,jdbcType=VARCHAR}, '%') OR #{p1.name,jdbcType=VARCHAR} IS NULL)
                AND (t1.company_no LIKE CONCAT('%', #{p1.company_no,jdbcType=VARCHAR}, '%') OR #{p1.company_no,jdbcType=VARCHAR} IS NULL)
                AND (#{p1.group_name,jdbcType=VARCHAR} IS NULL OR #{p1.group_name,jdbcType=VARCHAR} = '' OR f_get_org_simple_and_full_name(t4.code, 'm_group') LIKE CONCAT('%', #{p1.group_name,jdbcType=VARCHAR}, '%'))
                AND (t1.is_del = #{p1.is_del,jdbcType=VARCHAR} OR #{p1.is_del,jdbcType=VARCHAR} IS NULL)
                AND (
                    <if test="p1.ids != null and p1.ids.length > 0">
                        t1.id IN
                        <foreach collection='p1.ids' item='id' index='index' open='(' separator=',' close=')'>
                            #{id}
                        </foreach>
                    </if>
                    <if test="p1.ids == null or p1.ids.length == 0">
                        true
                    </if>
                )
            ${orderByClause}
        </script>
        """)
    List<MCompanyExportVo> selectExportList(@Param("p1") MCompanyVo searchCondition, 
                                           @Param("orderByClause") String orderByClause);
}
