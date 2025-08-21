package com.xinyirun.scm.core.system.serviceimpl.client.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserLiteEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MUserLiteVo;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.client.user.MUserLiteMapper;
import com.xinyirun.scm.core.system.mapper.client.user.MUserMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.service.client.user.IMUserLiteService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户表 简单 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Service
public class MUserLiteServiceImpl extends BaseServiceImpl<MUserLiteMapper, MUserLiteEntity> implements IMUserLiteService {

    @Autowired
    private MUserLiteMapper mapper;
    @Autowired
    private MUserMapper userMapper;
    @Autowired
    private MStaffMapper staffMapper;

    /**
     * 重建用户简单
     *
     * @param user_id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MUserLiteVo reBulidUserLiteData(Long user_id) {
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
        MUserLiteVo rtnBean = new MUserLiteVo();
        BeanUtilsSupport.copyProperties(userLiteEntity, rtnBean);
        return rtnBean;
    }

    /**
     * 更细用户默认菜单
     *
     * @param user_id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public MUserLiteVo updDefaultMenu(Long user_id) {
        
        // 获取部门
        MUserLiteEntity mUserLiteEntity = mapper.selectOne(new QueryWrapper<MUserLiteEntity>()
                                                            .eq("user_id",user_id));

        return null;
    }
}
