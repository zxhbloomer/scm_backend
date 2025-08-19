package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.org.MOrgCompanyDeptEntity;
import com.xinyirun.scm.bean.entity.master.org.MOrgEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 部门与部门关系表，多部门嵌套关系表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-05-15
 */
@Repository
public interface MOrgCompanyDeptMapper extends BaseMapper<MOrgCompanyDeptEntity> {

    /**
     * 集团关系，集团嵌套count - 增加组织类型过滤避免serial_id冲突
     * @return
     */
    @Select("""
        SELECT t1.*
          FROM m_org_company_dept t1
         INNER JOIN m_org t2 ON t1.current_id = t2.serial_id
         WHERE t1.current_id = #{p1}
           AND t2.serial_type = 'm_dept'
        """)
    MOrgCompanyDeptEntity getOCDEntityByCurrentId(@Param("p1") Long current_id);

    /**
     * 集团关系，集团嵌套count
     * @param searchCondition
     * @return
     */
    @Select("""
        SELECT count(*) count
          FROM m_org_company_dept t1
         WHERE t1.current_id = #{p1.serial_id,jdbcType=BIGINT}
        """)
    int getOCDRelationCount(@Param("p1") MOrgEntity searchCondition);

    @Delete("""
        delete from m_org_company_dept t
         where t.current_id = #{p1}
        """)
    int delOCDRelation(@Param("p1")Long id);

    /**
     * 保存嵌套时的儿子个数
     * @return
     */
    @Update("""
        update m_org_company_dept ut1
    inner join (
                 select t1.id,
                        count(1) over(partition by t1.root_id) as counts,
                        row_number() over(partition by t1.root_id) as sort
                   from m_org_company_dept t1
              left join (
                          SELECT sub.root_id
                            FROM m_org_company_dept sub
                           where sub.current_id = #{p1}
                        ) t2 on t1.root_id = t2.root_id
               ) ut2 on ut1.id = ut2.id
           set ut1.counts = ut2.counts,
               ut1.sort = ut2.sort
        """)
    int updateOCDCountAndSort(@Param("p1")Long id);

    /**
     * 保存嵌套时的儿子个数
     * @return
     */
    @Update("""
        update m_org_company_dept ut1
    inner join (
                 SELECT t1.id,
                        (
                         case
                         when t1.sort = 1 then t1.parent_id
                         when t1.sort > 1 then t2.parent_id
                         end
                        ) root_company_id
                   FROM m_org_company_dept t1
            left JOIN m_org_company_dept t2 on t1.root_id = t2.current_id
               ) ut2 on ut1.id = ut2.id
           set ut1.root_company_id = ut2.root_company_id
        """)
    int updateOCDParentData();

    @Delete("""
        delete from m_org_company_dept t
        """)
    int delAll();
}
