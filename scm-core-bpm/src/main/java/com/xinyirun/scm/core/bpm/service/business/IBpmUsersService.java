package com.xinyirun.scm.core.bpm.service.business;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.bpm.BpmUsersEntity;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
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
    @Select("select * from bpm_users where user_code = #{p1}")
    BpmUsersEntity selectByCode(@Param("p1")String assignee);

    /**
     * 新增BPM用户 - 对应staff新增时调用
     * @param staffEntity 员工实体
     * @param userEntity 用户实体（可能为空）
     * @return 新增结果
     */
    InsertResultAo<Integer> insertBpmUser(MStaffEntity staffEntity, MUserEntity userEntity);
    
    /**
     * 更新BPM用户 - 对应staff更新时调用
     * @param staffEntity 员工实体
     * @param userEntity 用户实体（可能为空）
     * @return 更新结果
     */
    UpdateResultAo<Integer> updateBpmUser(MStaffEntity staffEntity, MUserEntity userEntity);
    
    /**
     * 删除BPM用户 - 对应staff删除时调用
     * @param staffId 员工ID
     * @return 删除结果
     */
    DeleteResultAo<Integer> deleteBpmUser(Long staffId);
    
    /**
     * 根据员工ID查询BPM用户
     * @param staffId 员工ID
     * @return BPM用户信息
     */
    BpmUsersEntity selectByStaffId(Long staffId);
}
