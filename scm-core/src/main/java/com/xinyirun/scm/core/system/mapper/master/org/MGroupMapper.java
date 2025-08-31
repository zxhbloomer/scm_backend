package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupVo;
import com.xinyirun.scm.bean.system.vo.master.org.MGroupExportVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 集团主表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MGroupMapper extends BaseMapper<MGroupEntity> {
    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("""
        select t1.*,
               t2.name as c_name,
               t3.name as u_name,
               f_get_org_simple_name(t5.code, 'm_group') as parent_group_simple_name
          from m_group t1
         LEFT JOIN m_staff t2 ON t1.c_id = t2.id
         LEFT JOIN m_staff t3 ON t1.u_id = t3.id
         LEFT JOIN v_org_relation t5 ON t5.serial_type = 'm_group' and t5.serial_id = t1.id and t5.parent_serial_type = 'm_group'
         where true
           and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)
           and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)
           and t1.is_del = false
           and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)
           and (#{p1.parent_group_name,jdbcType=VARCHAR} IS NULL 
                OR #{p1.parent_group_name,jdbcType=VARCHAR} = '' 
                OR f_get_org_simple_and_full_name(t5.code, 'm_group') LIKE CONCAT('%', #{p1.parent_group_name,jdbcType=VARCHAR}, '%'))
           and (
              /* dataModel='10'时查询组织架构中未使用的集团，对应DICT_ORG_USED_TYPE_SHOW_UNUSED常量 */
              case when #{p1.dataModel,jdbcType=VARCHAR} = '10' then
                  not exists(
                            select 1
                              from m_org subt1
                             /* serial_type='m_group'对应DICT_SYS_CODE_TYPE_M_GROUP常量 */
                             where subt1.serial_type = 'm_group'
                               and t1.id = subt1.serial_id
                  )
              else true
              end
               )
        """)
    IPage<MGroupVo> selectPage(Page page, @Param("p1") MGroupVo searchCondition);

    /**
     * 导出专用查询方法，支持动态排序
     * @param param 查询条件
     * @param orderByClause 排序子句
     * @return
     */
    @Select("""
        <script>
        SELECT (@row_num:= @row_num + 1) as no,
               t1.*,
               t2.name as c_name,
               t3.name as u_name,
               t4.label as is_del_name,
               f_get_org_simple_name(t5.code, 'm_group') as parent_group_simple_name
          from m_group t1
         LEFT JOIN m_staff t2 ON t1.c_id = t2.id
         LEFT JOIN m_staff t3 ON t1.u_id = t3.id
         /* dict_code='sys_delete_type'获取删除状态字典标签，对应DICT_SYS_DELETE_MAP常量 */
         LEFT JOIN v_dict_info AS t4 ON t4.code = 'sys_delete_type' and t4.dict_value = CONCAT('', t1.is_del)
         LEFT JOIN v_org_relation t5 ON t5.serial_type = 'm_group' and t5.serial_id = t1.id and t5.parent_serial_type = 'm_group'
               ,(SELECT @row_num := 0) r
         where true
           and (t1.code like CONCAT ('%',#{p1.code,jdbcType=VARCHAR},'%') or #{p1.code,jdbcType=VARCHAR} is null)
           and (t1.name like CONCAT ('%',#{p1.name,jdbcType=VARCHAR},'%') or #{p1.name,jdbcType=VARCHAR} is null)
           and t1.is_del = false
           and (t1.id =#{p1.id,jdbcType=BIGINT} or #{p1.id,jdbcType=BIGINT} is null)
           and (#{p1.parent_group_name,jdbcType=VARCHAR} IS NULL 
                OR #{p1.parent_group_name,jdbcType=VARCHAR} = '' 
                OR f_get_org_simple_and_full_name(t5.code, 'm_group') LIKE CONCAT('%', #{p1.parent_group_name,jdbcType=VARCHAR}, '%'))
           <if test='p1.ids != null and p1.ids.length > 0'>
           and t1.id in
           <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>
                #{item}
           </foreach>
           </if>
        ${orderByClause}
        </script>
        """)
    List<MGroupExportVo> selectExportList(@Param("p1") MGroupVo param, @Param("orderByClause") String orderByClause);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("""
        <script>
        select t1.*
          from m_group t1
         where true
           and t1.id in
           <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>
                #{item.id}
           </foreach>
        </script>
        """)
    List<MGroupEntity> selectIdsIn(@Param("p1") List<MGroupVo> searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param code
     * @return
     */
    @Select("""
        select t1.*
          from m_group t1
         where true
           and t1.code = #{p1}
           and (t1.id <> #{p2} or #{p2} is null)
        """)
    List<MGroupEntity> selectByCode(@Param("p1") String code, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("""
        select t1.*
          from m_group t1
         where true
           and t1.name = #{p1}
           and (t1.id <> #{p2} or #{p2} is null)
        """)
    List<MGroupEntity> selectByName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param name
     * @return
     */
    @Select("""
        select t1.*
          from m_group t1
         where true
           and (t1.simple_name = #{p1} or #{p1} is null or #{p1} = '')
           and (t1.id <> #{p2} or #{p2} is null)
        """)
    List<MGroupEntity> selectBySimpleName(@Param("p1") String name, @Param("p2") Long equal_id);

    /**
     * 查询在组织架构中是否存在有被使用的数据
     * @param searchCondition
     * @return
     */
    @Select("""
        select count(1)
          from m_org t1
         where true
           /* serial_type='m_group'检查集团在组织架构中的使用情况，对应DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE常量 */
           and t1.serial_type = 'm_group'
           and t1.serial_id = #{p1.id,jdbcType=BIGINT}
        """)
    int isExistsInOrg(@Param("p1") MGroupEntity searchCondition);

    /**
     *
     * 根据id获取数据
     *
     * @param id
     * @return
     */
    @Select("""
        select t1.*,
               t2.name as c_name,
               t3.name as u_name
          from m_group t1
         LEFT JOIN m_staff t2 ON t1.c_id = t2.id
         LEFT JOIN m_staff t3 ON t1.u_id = t3.id
         where true
           and (t1.id = #{p1})
        """)
    MGroupVo selectId(@Param("p1") Long id);
}
