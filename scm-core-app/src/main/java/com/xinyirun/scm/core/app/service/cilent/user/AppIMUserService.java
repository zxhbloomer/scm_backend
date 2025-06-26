package com.xinyirun.scm.core.app.service.cilent.user;

import com.xinyirun.scm.bean.app.ao.result.AppInsertResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.app.vo.master.user.AppMUserVo;
import com.xinyirun.scm.bean.app.vo.master.user.AppPasswordVo;
import com.xinyirun.scm.bean.app.vo.master.user.AppUserInfoVo;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.core.app.service.base.v1.AppIBaseService;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jobob
 * @since 2019-06-24
 */
public interface AppIMUserService extends AppIBaseService<MUserEntity>, UserDetailsService {

    /**
     * 获取use的基本信息
     * @param userName
     * @return
     */
    AppUserInfoVo getUserInfo(String userName);


    /**
     * 获取userbean
     * @return
     */
    AppUserBo getUserBean(Long id, String loginOrStaffId);


    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    AppInsertResultAo<Integer> insert(MUserEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    AppUpdateResultAo<Integer> update(MUserEntity entity);

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    AppUpdateResultAo<Integer> updatePwd(AppPasswordVo vo);

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    AppMUserVo selectByid(Long id);

    /**
     * 根据登录的username获取entity
     *
     * @param username
     * @return
     */
    AppMUserVo getDataByName(String username);

 }
