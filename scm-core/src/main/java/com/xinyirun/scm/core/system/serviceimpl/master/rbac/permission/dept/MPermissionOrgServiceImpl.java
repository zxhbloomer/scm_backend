package com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.dept;

import com.xinyirun.scm.bean.entity.master.rbac.permission.MPermissionEntity;
import com.xinyirun.scm.bean.system.utils.common.tree.TreeUtil;
import com.xinyirun.scm.bean.system.vo.master.rbac.permission.dept.MOrgDeptPermissionTreeVo;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgMapper;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.dept.MPermissionOrgMapper;
import com.xinyirun.scm.core.system.service.master.rbac.permission.dept.IMPermissionOrgService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 权限类页面左侧的树 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2020-07-27
 */
@Service
public class MPermissionOrgServiceImpl extends BaseServiceImpl<MPermissionOrgMapper, MPermissionEntity> implements
    IMPermissionOrgService {

    @Autowired
    private MOrgMapper mOrgMapper;

    @Autowired
    private MPermissionOrgMapper mapper;


    /**
     * 获取所有数据，左侧树数据
     */
    @Override
    public List<MOrgDeptPermissionTreeVo> getTreeList(MOrgDeptPermissionTreeVo searchCondition) {
        // 查询 数据
        List<MOrgDeptPermissionTreeVo> list = mapper.getTreeList(searchCondition);
        List<MOrgDeptPermissionTreeVo> rtnList = TreeUtil.getTreeList(list);
        return rtnList;
    }
}
