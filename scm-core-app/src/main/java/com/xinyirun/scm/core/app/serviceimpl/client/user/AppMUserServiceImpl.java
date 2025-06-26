package com.xinyirun.scm.core.app.serviceimpl.client.user;

import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppInsertResultAo;
import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppUserBo;
import com.xinyirun.scm.bean.app.bo.user.login.AppMUserBo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppCheckResultUtil;
import com.xinyirun.scm.bean.app.result.utils.v1.AppInsertResultUtil;
import com.xinyirun.scm.bean.app.result.utils.v1.AppUpdateResultUtil;
import com.xinyirun.scm.bean.app.vo.master.user.AppMStaffVo;
import com.xinyirun.scm.bean.app.vo.master.user.AppMUserVo;
import com.xinyirun.scm.bean.app.vo.master.user.AppPasswordVo;
import com.xinyirun.scm.bean.app.vo.master.user.AppUserInfoVo;
import com.xinyirun.scm.bean.app.vo.master.user.jwt.AppMUserJwtTokenVo;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.app.AppResultEnum;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.app.mapper.client.user.AppMUserMapper;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserLiteService;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserService;
import com.xinyirun.scm.core.app.service.master.user.AppIMStaffService;
import com.xinyirun.scm.core.app.service.master.user.jwt.AppIMUserJwtTokenService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-05-17
 */
@Slf4j
@Service
public class AppMUserServiceImpl extends AppBaseServiceImpl<AppMUserMapper, MUserEntity> implements AppIMUserService {

    @Autowired
    private AppMUserMapper mUserMapper;

    @Autowired
    private AppIMStaffService imStaffService;

    @Autowired
    private AppIMUserLiteService imUserLiteService;

    @Autowired
    private AppIMUserJwtTokenService appIMUserJwtTokenService;

    /**
     * app登录入口
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppMUserVo user = getDataByName(username);

        if (user == null) {
            log.warn("您输入的用户名不存在！");
            throw new UsernameNotFoundException("您输入的用户名不存在！");
        }

        //        Clerk clerk = clerkMapper.selectByPrimaryKey(user.getId());
        //        if (clerk == null) {
        //            throw new ClerkNotFoundException("Couldn't found clerk in system");
        //        }
        //
        //        List<Role> roles = userMapper.selectRoles(user.getId());
        //
        List<String> permissions = new ArrayList<>();
//        permissions.addAll(CollectionUtils.arrayToList(new String[]{"ROLE_USER"}));
        permissions.addAll(Arrays.asList(new String[]{"ROLE_USER"}));
        //        for (Role role : roles) {
        //            permissions.addAll(CollectionUtils.arrayToList(role.getPermissions()));
        //        }

        return new AppMUserBo(
                user.getId(),
                username,
                user.getPwd(),
                AuthorityUtils.createAuthorityList(permissions.toArray(new String[]{})))   // 加载权限的关键部分
                .setUser(user);
    }

    /**
     * 获取use的基本信息
     * @param userName
     * @return
     */
    @Override
    public AppUserInfoVo getUserInfo(String userName){

        // TODO 测试bean
        AppUserInfoVo ui = new AppUserInfoVo();
        ui.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        ui.setIntroduction("我是超级管理员");
        ui.setName("超级管理员");
        ui.setRoles(new String[]{"admin"});
        return ui;
    }

    /**
     * 获取userbean
     *
     * 1：用户 信息
     * 2：员工 信息
     * 3：租户 信息
     *
     * @param loginOrStaffId
     * @return
     */
    @Override
    public AppUserBo getUserBean(Long id, String loginOrStaffId){
        MUserEntity mUserEntity = null;
        AppMStaffVo mStaffVo = null;
        if(loginOrStaffId.equals(SystemConstants.LOGINUSER_OR_STAFF_ID.LOGIN_USER_ID)){
            mUserEntity = mUserMapper.selectById(id);
            mStaffVo = imStaffService.selectById(mUserEntity.getStaff_id());
        } else {
            mStaffVo = imStaffService.selectById(id);
            mUserEntity = mUserMapper.selectById(mStaffVo.getUser_id());
        }
        AppUserBo appUserBo = new AppUserBo();
        /** 设置1：用户信息 */
        appUserBo.setApp_user_info(mUserEntity);
        /** 设置2：员工 信息 */
        appUserBo.setApp_staff_info(mStaffVo);

        // 设置basebean
        appUserBo.setUser_Id(mUserEntity.getId());
        appUserBo.setUsername(mUserEntity.getLogin_name());
        appUserBo.setWx_unionid(mUserEntity.getWx_unionid());
        appUserBo.setStaff_Id(mStaffVo != null ? mStaffVo.getStaff_id() : null);
        appUserBo.setStaff_code(mStaffVo != null ? mStaffVo.getCode() : null);

        AppMUserJwtTokenVo appMUserJwtTokenVo = appIMUserJwtTokenService.selectByUserId(mUserEntity.getId());
        if (appMUserJwtTokenVo != null) {
            appUserBo.setToken_expires_at(appMUserJwtTokenVo.getToken_expires_at());
        }

        return appUserBo;
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppInsertResultAo<Integer> insert(MUserEntity entity) {
        // 插入前check
        AppCheckResultAo cr = checkLogic(entity, AppCheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new AppBusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        entity.setIs_del(false);

        // 执行插入
        int rtn = mUserMapper.insert(entity);

        // 用户简单重构
        imUserLiteService.reBuildUserLiteData(entity.getId());

        return AppInsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppUpdateResultAo<Integer> update(MUserEntity entity) {
        // 更新前check
        AppCheckResultAo cr = checkLogic(entity, AppCheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new AppBusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);

        // 执行更新操作
        int rtn = mUserMapper.updateById(entity);

        // 用户简单重构
        imUserLiteService.reBuildUserLiteData(entity.getId());

        return AppUpdateResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppUpdateResultAo<Integer> updatePwd(AppPasswordVo vo) {
        MUserEntity mUserEntity = mUserMapper.selectById(SecurityUtil.getLoginUser_id());
        checkPwdLogic(vo, mUserEntity);

        mUserEntity.setPwd_his_pwd(vo.getEncode_pwd_his_pwd());
        mUserEntity.setPwd(vo.getEncode_pwd());
        mUserEntity.setIs_changed_pwd(Boolean.TRUE);
        int rtn = mUserMapper.updateById(mUserEntity);

        return AppUpdateResultUtil.OK(rtn);
    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public AppMUserVo selectByid(Long id){
        return mUserMapper.selectByid(id);
    }

    /**
     * 密码check逻辑
     */
    public AppCheckResultAo checkPwdLogic(AppPasswordVo vo, MUserEntity user){
        if (StringUtils.equals(vo.getPwd_his_pwd(), vo.getPwd())) {
            throw new AppBusinessException(AppResultEnum.PASSWORD_NEW_SAME_OLD);
        }

        user = mUserMapper.selectById(SecurityUtil.getLoginUser_id());
        if (StringUtils.equals(user.getPwd(), vo.getEncode_pwd_his_pwd())) {
            throw new AppBusinessException(AppResultEnum.PASSWORD_NOT_CORRECT);
        }

        return AppCheckResultUtil.OK();
    }

    /**
     * check逻辑
     * @return
     */
    public AppCheckResultAo checkLogic(MUserEntity entity, String moduleType){
        return AppCheckResultUtil.OK();
    }

    /**
     * 根据登录的username获取entity
     *
     * @param username
     * @return
     */
    @Override
    public AppMUserVo getDataByName(String username) {
        AppMUserVo vo = mUserMapper.getDataByName(username);
        return vo;
    }
}