package com.xinyirun.scm.core.app.serviceimpl.client.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.app.vo.master.user.AppMUserLiteVo;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserLiteEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MUserLiteVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.app.mapper.client.user.AppUserLiteMapper;
import com.xinyirun.scm.core.app.mapper.client.user.AppMUserMapper;
import com.xinyirun.scm.core.app.mapper.master.user.AppMStaffMapper;
import com.xinyirun.scm.core.app.service.cilent.user.AppIMUserLiteService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppMUserLiteServiceImpl extends AppBaseServiceImpl<AppUserLiteMapper, MUserLiteEntity> implements AppIMUserLiteService {
    @Autowired
    private AppUserLiteMapper mapper;

    @Autowired
    private AppMUserMapper userMapper;

    @Autowired
    private AppMStaffMapper staffMapper;

    /**
     * 重建用户简单
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public AppMUserLiteVo reBuildUserLiteData(Long user_id) {
        // 1： 删除m_user_lite的user_id = user_id
        mapper.delete(new QueryWrapper<MUserLiteEntity>()
                .eq("user_id",user_id)
        );

        // 2:搜索m_user
        MUserEntity mUserEntity = userMapper.selectById(user_id);

        // 3:搜索m_staff
        MStaffEntity mStaffEntity = staffMapper.selectById(mUserEntity.getStaff_id());

        // 4:设置数据
        MUserLiteEntity userLiteEntity = new MUserLiteEntity();
        userLiteEntity.setUser_id(mUserEntity.getId());
        userLiteEntity.setLogin_type(mUserEntity.getLogin_type());
        userLiteEntity.setLogin_name(mUserEntity.getLogin_name());
        userLiteEntity.setAvatar(mUserEntity.getAvatar());
        userLiteEntity.setType(mUserEntity.getType());
        if(mStaffEntity !=null) {
            userLiteEntity.setStaff_id(mStaffEntity.getId());
            userLiteEntity.setName(mStaffEntity.getName());
            userLiteEntity.setCompany_id(mStaffEntity.getCompany_id());
            userLiteEntity.setDept_id(mStaffEntity.getDept_id());
        }
        mapper.insert(userLiteEntity);
        AppMUserLiteVo rtnBean = new AppMUserLiteVo();
        BeanUtilsSupport.copyProperties(userLiteEntity, rtnBean);
        return rtnBean;
    }
}
