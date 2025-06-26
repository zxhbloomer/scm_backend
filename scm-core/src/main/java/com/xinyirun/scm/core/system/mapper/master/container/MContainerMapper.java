package com.xinyirun.scm.core.system.mapper.master.container;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.container.MContainerEntity;
import com.xinyirun.scm.bean.system.vo.excel.container.MContainerExcelVo;
import com.xinyirun.scm.bean.system.vo.master.container.MContainerVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2023/5/30 16:15
 */

@Repository
public interface MContainerMapper extends BaseMapper<MContainerEntity> {

    String comm_select = "SELECT                                                                                        "
            + " t.id,                                                                                                   "
            + " t.code,                                                                                                 "
            + " t.c_time,                                                                                               "
            + " t.u_time,                                                                                               "
            + " t1.name c_name,                                                                                         "
            + " t2.name u_name                                                                                          "
            + " FROM m_container t                                                                                      "
            + " LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                  "
            + " LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                  "
            + " WHERE t.is_del = false                                                                                  ";
    /**
     * 分页查询
     * @param vo
     * @param pageCondition
     * @return
     */
    @Select(""
            + comm_select
            + " AND (t.code LIKE CONCAT ('%', #{p1.code}, '%') OR #{p1.code} IS NULL OR #{p1.code} = '')                "
    )
    IPage<MContainerVo> selectPageList(@Param("p1") MContainerVo vo, Page<MContainerVo> pageCondition);

    /**
     * 根据箱号 和 id 查询
     * @param id
     * @param code
     * @return
     */
    @Select(""
            + comm_select
            + "AND (t.is_del = false)                                                                                   "
            + "AND (t.id != #{p1} or #{p1} is null)                                                                     "
            + "AND t.code = #{p2}                                                                                       "
    )
    List<MContainerVo> selectByCode(@Param("p1") Integer id, @Param("p2") String code);

    /**
     * 根据 id 查询
     * @param id
     * @return
     */
    @Select(""
            + comm_select
            + " AND t.id = #{p1}                                                                                        "
    )
    MContainerVo getDetailById(@Param("p1") Integer id);

    /**
     * 导出查询
     * @param vo
     * @return
     */
    @Select("<script>                                                                                                   "
            + "SELECT                                                                                                   "
            + " @row_num:= @row_num+ 1 as no,                                                                           "
            + " t.id,                                                                                                   "
            + " t.code,                                                                                                 "
            + " t.c_time,                                                                                               "
            + " t.u_time,                                                                                               "
            + " t1.name c_name,                                                                                         "
            + " t2.name u_name                                                                                          "
            + " FROM m_container t                                                                                      "
            + " LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                  "
            + " LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                  "
            + " ,(select @row_num:=0) t5                                                                                "
            + " WHERE t.is_del = false                                                                                  "
            + " AND (t.code like concat('%', #{p1.code}, '%') or #{p1.code} is null or #{p1.code} = '')                 "
            + " <if test='p1.ids != null and p1.ids.size != 0'>                                                         "
            + "   AND t.id in                                                                                           "
            + "    <foreach collection='p1.ids' item='item' index='index' open='(' separator=',' close=')'>             "
            + "       #{item}                                                                                           "
            + "    </foreach>                                                                                           "
            + " </if>                                                                                                   "
            + " </script>                                                                                               "
    )
    List<MContainerExcelVo> selectExportList(@Param("p1") MContainerVo vo);
}
