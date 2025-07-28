package com.xinyirun.scm.core.system.mapper.business.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.project.BProjectAttachEntity;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectAttachVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 项目管理附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Repository
public interface BProjectAttachMapper extends BaseMapper<BProjectAttachEntity> {

    @Select("select * from b_project_attach where project_id = #{p1}")
    BProjectAttachVo selectByProjectId(@Param("p1") Integer id);
}
