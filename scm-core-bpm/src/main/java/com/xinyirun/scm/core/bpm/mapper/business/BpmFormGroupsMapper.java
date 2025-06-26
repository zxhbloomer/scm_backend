package com.xinyirun.scm.core.bpm.mapper.business;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.bpm.BpmFormGroupsEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmGroupVo;
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
public interface BpmFormGroupsMapper extends BaseMapper<BpmFormGroupsEntity> {


    @Select("select * from bpm_form_groups")
    List<BBpmGroupVo> getGroup();
}
