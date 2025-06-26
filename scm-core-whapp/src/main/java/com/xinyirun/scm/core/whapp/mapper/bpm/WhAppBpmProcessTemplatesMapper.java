package com.xinyirun.scm.core.whapp.mapper.bpm;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.bpm.BpmProcessTemplatesEntity;
import com.xinyirun.scm.bean.whapp.vo.business.bpm.WhAppBBpmProcessVo;
import com.xinyirun.scm.core.whapp.config.mybatis.typehandlers.JsonObjectTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * process_templates Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-11
 */
@Repository
public interface WhAppBpmProcessTemplatesMapper extends BaseMapper<BpmProcessTemplatesEntity> {

    /**
     * code查询审批流程模板
     */
    @Select("select *,IF(process,JSON_ARRAY(),JSON_EXTRACT(process, '$.props.assignedUser')) as orgUserVoList from bpm_process_templates where code = #{p1}")
    @Results({
            @Result(property = "orgUserVoList", column = "orgUserVoList", javaType = List.class ,typeHandler = JsonObjectTypeHandler.class),
    })
    WhAppBBpmProcessVo selByCode(@Param("p1")String code);

}
