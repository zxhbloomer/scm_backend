package com.xinyirun.scm.core.system.mapper.master.org;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.org.MOrgDeptPositionEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 岗位与部门关系表 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-05-15
 */
@Repository
public interface MOrgDeptPositionMapper extends BaseMapper<MOrgDeptPositionEntity> {

    @Delete("                                                                        "
        + "     delete from m_org_dept_position t                                     "
        + "      where t.current_id = #{p1}                                          "
    )
    int delODPRelation(@Param("p1")Long id);

    @Delete("                                                                        "
        + "     delete from m_org_dept_position t                                      "
//        + "      where t.tenant_id = #{p1}                                           "
    )
    int delAll();
}
