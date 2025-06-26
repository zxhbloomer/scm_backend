package com.xinyirun.scm.core.app.serviceimpl.master.user;

import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.app.bo.jwt.user.AppJwtBaseBo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppUpdateResultUtil;
import com.xinyirun.scm.bean.app.vo.master.user.AppMStaffVo;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.app.mapper.client.user.AppMUserMapper;
import com.xinyirun.scm.core.app.mapper.master.user.AppMStaffMapper;
import com.xinyirun.scm.core.app.service.master.user.AppIMStaffService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppMStaffServiceImpl extends AppBaseServiceImpl<AppMStaffMapper, MStaffEntity> implements AppIMStaffService {

    @Autowired
    private AppMStaffMapper mapper;

    @Autowired
    private AppMUserMapper mUserMapper;

    /**
     * 获取个人信息
     */
    @Override
    public AppMStaffVo getDetail(){
        AppJwtBaseBo appJwtBaseBo = SecurityUtil.getAppJwtBaseBo();
        Long staffId = appJwtBaseBo.getStaff_Id();
        return mapper.getDetail(staffId);
    }

    /**
     * 更新头像
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveAvatar(String url) {
        AppJwtBaseBo appJwtBaseBo = SecurityUtil.getAppJwtBaseBo();
        Long staffId = appJwtBaseBo.getStaff_Id();
        MStaffEntity mStaffEntity = mapper.selectById(staffId.intValue());
        MUserEntity userEntity = mUserMapper.selectById(mStaffEntity.getUser_id());
        userEntity.setAvatar(url);
        mUserMapper.updateById(userEntity);
    }

    /**
     * 更新手机号（选择字段，策略更新）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppMStaffVo updatePhone(String mobile_phone) {
        AppJwtBaseBo appJwtBaseBo = SecurityUtil.getAppJwtBaseBo();
        Long staffId = appJwtBaseBo.getStaff_Id();
        MStaffEntity mStaffEntity = mapper.selectById(staffId.intValue());
        mStaffEntity.setMobile_phone(mobile_phone);
        int updCount = mapper.updateById(mStaffEntity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        AppMStaffVo vo = (AppMStaffVo) BeanUtilsSupport.copyProperties(mStaffEntity, AppMStaffVo.class);
        return vo;
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppUpdateResultAo<Integer> update(AppMStaffVo vo) {
        AppJwtBaseBo appJwtBaseBo = SecurityUtil.getAppJwtBaseBo();
        Long staffId = appJwtBaseBo.getStaff_Id();
        vo.setStaff_id(staffId);
        // 分拆entity
        MStaffEntity mStaffEntity = mapper.selectById(vo.getStaff_id());
        MUserEntity mUserEntity = mUserMapper.selectById(mStaffEntity.getUser_id());

        // 插入前check
//        AppCheckResultAo cr1 = checkStaffEntity(mStaffEntity, vo);
//        if (cr1.isSuccess() == false) {
//            throw new BusinessException(cr1.getMessage());
//        }
        if(vo.getAvatar() != null) {
            mUserEntity.setAvatar(vo.getAvatar());
        }
//        mStaffEntity.setName(vo.getStaff_name());
//        mUserEntity.setLogin_name(vo.getUser_login_name());
        mUserEntity.setStaff_id(mStaffEntity.getId());
        mUserMapper.updateById(mUserEntity);
        mapper.updateById(mStaffEntity);

        // 更新逻辑保存
        return AppUpdateResultUtil.OK(1);
    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public AppMStaffVo selectById(Long id){
        AppMStaffVo searchCondition = new AppMStaffVo();
        searchCondition.setStaff_id(id);
        AppMStaffVo vo = mapper.selectId(searchCondition);
        MUserEntity mUserEntity = mUserMapper.selectById(vo.getUser_id());
        vo.setAvatar(mUserEntity.getAvatar());
        return vo;
    }

    /**
     * 查询 by login_name
     * @param login_name
     * @param equal_id
     * @return
     */
    public List<MUserEntity> selectLoginName(String login_name, Long equal_id) {
        // 查询 数据
        List<MUserEntity> list = mUserMapper.selectLoginName(login_name, equal_id);
        return list;
    }

    /**
     * 获取审批节点使用的数据
     * @param vo
     * @return
     */
    @Override
    public AppStaffUserBpmInfoVo getBpmDataByStaffid(AppStaffUserBpmInfoVo vo) {
        return mapper.getBpmDataByStaffid(vo.getId());
    }
}
