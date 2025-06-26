package com.xinyirun.scm.core.bpm.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2024-10-08
 */
public interface IBpmUsersService extends IService<BpmUsersEntity> {


    /**
     * 根据用户编码查询用户信息
     */
    @Select("select * from bpm_users where code = #{p1}")
    BpmUsersEntity selectByCode(@Param("p1")String assignee);
}
