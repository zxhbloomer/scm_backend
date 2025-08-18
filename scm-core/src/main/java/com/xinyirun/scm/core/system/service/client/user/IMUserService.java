package com.xinyirun.scm.core.system.service.client.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.bean.system.vo.master.user.PasswordVo;
import com.xinyirun.scm.bean.system.vo.master.user.SsoUserInfoVo;
import com.xinyirun.scm.bean.system.vo.master.user.UserInfoVo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-24
 */
public interface IMUserService extends IService<MUserEntity> , UserDetailsService {

    /**
     * 获取use的基本信息
     * @param userName
     * @return
     */
    UserInfoVo getUserInfo(String userName);

    /**
     * 获取use的基本信息
     * @param md5UserName
     * @return
     */
    UserDetails loadUserByMd5Username(String md5UserName);

    /**
     * 获取use的基本信息
     * @param vo
     * @return
     */
    SsoUserInfoVo ssoUserValidate(SsoUserInfoVo vo);


    /**
     * 获取use的基本信息
     * @return
     */
    UserInfoVo getUserInfo();

    /**
     * 获取userbean
     * @return
     */
    UserSessionBo getUserBean(Long id, String loginOrStaffId);


    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    InsertResultAo<Integer> insert(MUserEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    UpdateResultAo<Integer> update(MUserEntity entity);

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    MUserVo selectUserById(Long id);

    /**
     * 根据登录的username获取entity
     *
     * @param username
     * @return
     */
    MUserVo getDataByName(String username);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    UpdateResultAo<Integer> updatePwd(PasswordVo vo);


    /**
     * 更新最后登出时间
     */
    void updateLastLogoutDate();

    /**
     * 执行完整的用户登出流程
     * 包括：更新登出时间 + 清理租户共享缓存
     * 注意：保留用户个人缓存（菜单收藏、搜索历史等）
     */
    void performLogout();

    /**
     * 更新最后登录时间
     */
     void updateLoginDate(Long id);

    /**
     * 获取当前登录用户密码是否过期
     */
    MUserVo getUserPwdWarning(Long id);

    /**
     * 根据微信unionid获取用户信息
     * @param wxUnionid
     * @return
     */
    MUserEntity getDataByWxUnionid(String wxUnionid);

    /**
     * 根据用户id获取用户信息
     * @param id
     * @return
     */
    MUserEntity getDataById(Integer id);

}
