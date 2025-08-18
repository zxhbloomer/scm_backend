package com.xinyirun.scm.core.system.serviceimpl.client.user;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppCheckResultUtil;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.system.bo.user.login.MUserBo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.user.*;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.app.AppResultEnum;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.client.user.MUserMapper;
import com.xinyirun.scm.core.system.service.client.user.IMUserLiteService;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jobob
 * @since 2019-05-17
 */
@Slf4j
@Service
public class MUserServiceImpl extends BaseServiceImpl<MUserMapper, MUserEntity> implements IMUserService {

    @Autowired
    private MUserMapper mUserMapper;

    @Autowired
    private IMStaffService imStaffService;

    @Autowired
    private IMUserLiteService imUserLiteService;

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private CacheManager cacheManager;

    /**
     * system登录入口
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    @DS("#header.X-Tenant-ID")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        /**
         * 切换数据源：
         * 1、从request head中获取到X-Tenant-ID的租户id
         * 2、根据不同的租户id，切换数据源
         */
        String tenantId = SecurityUtil.getTenantIdByRequest();
//        DataSourceHelper.use(tenantId);
        MUserVo user = getDataByName(username);

        if (user == null) {
            log.warn("您输入的用户名不存在！");
            throw new UsernameNotFoundException("您输入的用户名不存在！");
        }

        if (!user.getIs_enable()) {
            throw new DisabledException("用户已被禁用！");
        }

        //        Clerk clerk = clerkMapper.selectByPrimaryKey(user.getId());
        //        if (clerk == null) {
        //            throw new ClerkNotFoundException("Couldn't found clerk in system");
        //        }
        //
        //        List<Role> roles = userMapper.selectRoles(user.getId());
        //@
        List<String> permissions = new ArrayList<>();
//        permissions.addAll(CollectionUtils.arrayToList(new String[]{"ROLE_USER"}));
        permissions.addAll(Arrays.asList(new String[]{"ROLE_USER"}));
        //        for (Role role : roles) {
        //            permissions.addAll(CollectionUtils.arrayToList(role.getPermissions()));
        //        }

        // 更新登录时间
        //mUserMapper.updateLoginDate(user.getId(), LocalDateTime.now());

        return new MUserBo(
                user.getId(),
                username,
                user.getPwd(),
                AuthorityUtils.createAuthorityList(permissions.toArray(new String[]{})))   // 加载权限的关键部分
                .setUser(user);
    }

    /**
     * 获取use的基本信息
     *
     * @param userName
     * @return
     */
    @Override
    public UserInfoVo getUserInfo(String userName) {
        // 获取session数据
        UserSessionBo bo = SecurityUtil.getUserSession();

        UserInfoVo ui = new UserInfoVo();
        ui.setAvatar(bo.getUser_info().getAvatar());
        ui.setIntroduction("我是" + bo.getStaff_info().getName());
        ui.setName(bo.getStaff_info().getName());
        ui.setRoles(new String[]{"admin"});
        return ui;
    }

    @Override
    public UserDetails loadUserByMd5Username(String md5UserName) {
        MUserVo user = mUserMapper.getDataByMd5Name(md5UserName);

        if (user == null) {
            log.warn("您输入的用户名不存在！");
            throw new UsernameNotFoundException("您输入的用户名不存在！");
        }

        if (!user.getIs_enable()) {
            throw new DisabledException("用户已被禁用！");
        }

        //        Clerk clerk = clerkMapper.selectByPrimaryKey(user.getId());
        //        if (clerk == null) {
        //            throw new ClerkNotFoundException("Couldn't found clerk in system");
        //        }
        //
        //        List<Role> roles = userMapper.selectRoles(user.getId());
        //@
        List<String> permissions = new ArrayList<>();
//        permissions.addAll(CollectionUtils.arrayToList(new String[]{"ROLE_USER"}));
        permissions.addAll(Arrays.asList(new String[]{"ROLE_USER"}));
        //        for (Role role : roles) {
        //            permissions.addAll(CollectionUtils.arrayToList(role.getPermissions()));
        //        }

        // 更新登录时间
        mUserMapper.updateLoginDate(user.getId(), LocalDateTime.now());

        return new MUserBo(
                user.getId(),
                user.getLogin_name(),
                user.getPwd(),
                AuthorityUtils.createAuthorityList(permissions.toArray(new String[]{})))   // 加载权限的关键部分
                .setUser(user);
    }

    @Override
    public SsoUserInfoVo ssoUserValidate(SsoUserInfoVo vo) {
        MUserVo user = mUserMapper.getDataByMd5Name(vo.getMd5_user_name());

        if (user == null) {
            log.warn("您输入的用户名不存在！");
            throw new UsernameNotFoundException("您输入的用户名不存在！");
        }

        if (!user.getIs_enable()) {
            throw new DisabledException("用户已被禁用！");
        }

        SsoUserInfoVo ssoUserInfoVo = new SsoUserInfoVo();
        ssoUserInfoVo.setUser_name(user.getLogin_name());
        ssoUserInfoVo.setMd5_user_name(vo.getMd5_user_name());
        if (StringUtils.isEmpty(vo.getDes_page())) {
            ssoUserInfoVo.setLogin_url(systemConfigProperies.getDomainName() + "#/sso?u=" + vo.getMd5_user_name());
        } else {
            String[] params_name = vo.getParams_name().split(",");
            String[] params_value = vo.getParams_value().split(",");
            String params = "";
            for (String param_name : params_name) {
                // 拼接参数
                params += param_name + "=" + params_value[Arrays.asList(params_name).indexOf(param_name)] + "&";
            }
            if (params.length() > 0) {
                params = params.substring(0, params.length() - 1);
            }

            ssoUserInfoVo.setLogin_url(systemConfigProperies.getDomainName() + "#/sso?u=" + vo.getMd5_user_name() + "&callback=" + vo.getDes_page() + "?" + params);
        }


        return ssoUserInfoVo;
    }

    @Override
    public UserInfoVo getUserInfo() {
        MStaffVo mStaffVo = imStaffService.selectByid(SecurityUtil.getStaff_id());
        MUserVo mUserVo = mUserMapper.getDataById(mStaffVo.getUser_id());

        UserInfoVo ui = new UserInfoVo();
        if (StringUtils.isEmpty(mUserVo.getAvatar())) {
            ui.setAvatar("https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        } else {
            ui.setAvatar(mUserVo.getAvatar());
        }
        ui.setIntroduction("我是" + mStaffVo.getName());
        ui.setName(mStaffVo.getName());
//        ui.setRoles(new String[]{"admin"});
        return ui;
    }

    /**
     * 获取userbean
     * <p>
     * 1：用户 信息
     * 2：员工 信息
     * 3：租户 信息
     *
     * @param loginOrStaffId
     * @return
     */
    @DS("#header.X-Tenant-ID")
    @Override
    public UserSessionBo getUserBean(Long id, String loginOrStaffId) {
        MUserEntity mUserEntity = null;
        MStaffVo mStaffVo = null;
        if (loginOrStaffId.equals(SystemConstants.LOGINUSER_OR_STAFF_ID.LOGIN_USER_ID)) {
            mUserEntity = mUserMapper.selectById(id.intValue());
            mStaffVo = imStaffService.selectByid(mUserEntity.getStaff_id());
        } else {
            mStaffVo = imStaffService.selectByid(id);
            mUserEntity = mUserMapper.selectById(mStaffVo.getUser_id().intValue());
        }
        UserSessionBo userSessionBo = new UserSessionBo();
        /** 设置1：用户信息 */
        userSessionBo.setUser_info(mUserEntity);
        /** 设置2：员工 信息 */
        userSessionBo.setStaff_info(mStaffVo);

        // 设置basebean
        userSessionBo.setAccountId(mUserEntity.getId());
        userSessionBo.setStaff_Id(mStaffVo != null ? mStaffVo.getId() : null);

        return userSessionBo;
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     *
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MUserEntity entity) {
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        entity.setIs_del(false);

        // 执行插入
        int rtn = mUserMapper.insert(entity);

        // 用户简单重构
        imUserLiteService.reBulidUserLiteData(entity.getId());

        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     *
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MUserEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);

        // 执行更新操作
        int rtn = mUserMapper.updateById(entity);

        // 用户简单重构
        imUserLiteService.reBulidUserLiteData(entity.getId());

        return UpdateResultUtil.OK(rtn);
    }

    /**
     * 获取数据byid
     *
     * @param id
     * @return
     */
    @Override
    public MUserVo selectUserById(Long id) {
        return mUserMapper.selectUserById(id);
    }

    /**
     * check逻辑
     *
     * @return
     */
    public CheckResultAo checkLogic(MUserEntity entity, String moduleType) {
        return CheckResultUtil.OK();
    }

    /**
     * 根据登录的username获取entity
     *
     * @param username
     * @return
     */
    @Override
    public MUserVo getDataByName(String username) {
        // 测试测试测试
        MUserVo vo = mUserMapper.getDataByName(username);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> updatePwd(PasswordVo vo) {
        MUserEntity mUserEntity = mUserMapper.selectById(SecurityUtil.getLoginUser_id().intValue());
        checkPwdLogic(vo, mUserEntity);

        mUserEntity.setPwd_his_pwd(vo.getEncode_pwd_his_pwd());
        mUserEntity.setPwd(vo.getEncode_pwd());
        mUserEntity.setIs_changed_pwd(Boolean.TRUE);
        mUserEntity.setPwd_u_time(LocalDateTime.now());
        int rtn = mUserMapper.updateById(mUserEntity);

        return UpdateResultUtil.OK(rtn);
    }

    /**
     * 更新最后登出时间
     */
    @Override
    public void updateLastLogoutDate() {
        try {
            Long loginUserId = SecurityUtil.getLoginUser_id();
            log.debug("登出人id: {}", loginUserId);
            mUserMapper.updateLastLogoutDate(loginUserId, LocalDateTime.now());
        } catch (Exception e) {
            log.error("获取登出人ID 失败");
        }
    }

    /**
     * 执行完整的用户登出流程
     * 包括：更新登出时间 + 清理租户共享缓存
     * 注意：保留用户个人缓存（菜单收藏、搜索历史等）
     */
    @Override
    public void performLogout() {
        try {
            log.info("开始执行用户登出流程");
            
            // 1. 更新用户最后登出时间
            updateLastLogoutDate();
            
            // 2. 清理租户级共享缓存（保留用户个人缓存）
            clearTenantSharedCaches();
            
            log.info("用户登出流程完成，租户缓存已清理");
        } catch (Exception e) {
            log.error("用户登出时缓存清理失败: {}", e.getMessage(), e);
            // 不影响登出流程，继续执行
        }
    }

    /**
     * 清理租户级共享缓存
     * 注意：不清理用户个人缓存（菜单收藏、搜索历史）
     * 
     * 清理的缓存包括：
     * - CACHE_AREAS_CASCADER: 地区级联数据缓存
     * - CACHE_DICT_TYPE: 数据字典缓存
     * - CACHE_COLUMNS_TYPE: 表格列配置缓存
     * - CACHE_SYSTEM_ICON_TYPE: 系统图标缓存
     * - CACHE_ORG_SUB_COUNT: 组织架构统计缓存
     * - CACHE_CONFIG: 系统参数配置缓存
     * 
     * 保留的用户个人缓存：
     * - CACHE_SYSTEM_MENU_SEARCH_TYPE: 菜单收藏缓存
     * - CACHE_SYSTEM_MENU_SEARCH_HISTORY: 菜单搜索历史缓存
     */
    private void clearTenantSharedCaches() {
        String tenantKey = DataSourceHelper.getCurrentDataSourceName();
        log.info("开始清理租户 [{}] 的共享缓存", tenantKey);
        
        // 要清理的租户共享缓存列表
        List<String> sharedCaches = Arrays.asList(
            SystemConstants.CACHE_PC.CACHE_AREAS_CASCADER,       // 地区级联数据
            SystemConstants.CACHE_PC.CACHE_DICT_TYPE,            // 数据字典
            SystemConstants.CACHE_PC.CACHE_COLUMNS_TYPE,         // 表格列配置  
            SystemConstants.CACHE_PC.CACHE_SYSTEM_ICON_TYPE,     // 系统图标
            SystemConstants.CACHE_PC.CACHE_ORG_SUB_COUNT,        // 组织统计
            SystemConstants.CACHE_PC.CACHE_CONFIG                // 系统参数
        );
        
        int clearedCount = 0;
        int totalCaches = sharedCaches.size();
        
        // 执行缓存清理
        for (String cacheName : sharedCaches) {
            try {
                Cache cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    cache.clear();
                    clearedCount++;
                    log.debug("已清理缓存: {} (租户: {})", cacheName, tenantKey);
                } else {
                    log.warn("缓存不存在，跳过清理: {} (租户: {})", cacheName, tenantKey);
                }
            } catch (Exception e) {
                log.error("清理缓存失败: {} (租户: {}), 错误: {}", cacheName, tenantKey, e.getMessage());
            }
        }
        
        log.info("租户 [{}] 共享缓存清理完成，共清理 {}/{} 个缓存", tenantKey, clearedCount, totalCaches);
    }

    /**
     * 密码check逻辑
     */
    public AppCheckResultAo checkPwdLogic(PasswordVo vo, MUserEntity user) {
        if (StringUtils.equals(vo.getPwd_his_pwd(), vo.getPwd())) {
            throw new AppBusinessException(AppResultEnum.PASSWORD_NEW_SAME_OLD);
        }

        user = mUserMapper.selectById(SecurityUtil.getLoginUser_id().intValue());
        if (!StringUtils.equals(user.getPwd(), vo.getEncode_pwd_his_pwd())) {
//            throw new AppBusinessException(AppResultEnum.PASSWORD_NOT_CORRECT);
        }

        return AppCheckResultUtil.OK();
    }

    /**
     * 更新用户登录时间
     */
    @Override
    @DSTransactional(rollbackFor = Exception.class)
    @DS("#header.X-Tenant-ID")
    public void updateLoginDate(Long id) {
        mUserMapper.updateLoginDate(id, LocalDateTime.now());
    }

    /**
     * 获取当前登录用户密码是否过期
     * @param id
     */
    @Override
    public MUserVo getUserPwdWarning(Long id) {
        MUserVo mUserVo = mUserMapper.selectUserById(id);
        mUserVo.setPwd_expired(Boolean.FALSE);
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.PWD_SWITCH);
        if (sConfigEntity != null && sConfigEntity.getValue().equals("1") && sConfigEntity.getExtra1() != null) {
            LocalDate dataTime = LocalDate.parse(sConfigEntity.getExtra1(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            if (mUserVo.getPwd_u_time() == null || mUserVo.getPwd_u_time().toLocalDate().isBefore(dataTime)){
                mUserVo.setPwd_expired(Boolean.TRUE);
            }
        }
        return mUserVo;
    }

    @Override
    public MUserEntity getDataByWxUnionid(String wxUnionid) {
        return mUserMapper.getDataByWxUnionid(wxUnionid);
    }

    @Override
    public MUserEntity getDataById(Integer id) {
        return mUserMapper.selectById(id);
    }
}