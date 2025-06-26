package com.xinyirun.scm.core.bpm.mapper.business;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
@Repository
public interface BpmUsersMapper extends BaseMapper<BpmUsersEntity> {

    @Select("select * from bpm_users where user_code = #{p1}")
    BpmUsersEntity selectByCode(@Param("p1") String assignee);
}
