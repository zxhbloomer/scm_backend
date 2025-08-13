package com.xinyirun.scm.core.system.serviceimpl.master.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MStaffOrgEntity;
import com.xinyirun.scm.bean.entity.master.user.MStaffEntity;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionCountsVo;
import com.xinyirun.scm.bean.system.vo.master.org.MStaffPositionVo;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.user.MPositionInfoVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffExportVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.avatar.CreateAvatarByUserNameUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.client.user.MUserMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MPositionMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MStaffOrgMapper;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
import com.xinyirun.scm.core.system.service.client.user.IMUserLiteService;
import com.xinyirun.scm.core.system.service.master.user.IMStaffService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MStaffAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 员工 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-13
 */
@Service
public class MStaffServiceImpl extends BaseServiceImpl<MStaffMapper, MStaffEntity> implements IMStaffService {

    @Autowired
    private MStaffMapper mapper;

    @Autowired
    private MUserMapper mUserMapper;

    @Autowired
    private MStaffOrgMapper mStaffOrgMapper;

    @Autowired
    private IMUserLiteService imUserLiteService;

    @Autowired
    private MStaffAutoCodeServiceImpl mstaffAutoCodeService;

    @Autowired
    private MPositionMapper mPositionMapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private MWarehouseMapper mWarehouseMapper;

    @Autowired
    private IBWarehouseRelationService ibWarehouseRelationService;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MStaffVo> selectPage(MStaffVo searchCondition) {
        // 分页条件
        Page<MStaffVo> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MStaffVo> list = mapper.selectPage(pageCondition, searchCondition);
        for (MStaffVo vo : list.getRecords()) {
            MPositionInfoVo condition = new MPositionInfoVo();
            condition.setStaff_id(vo.getId());
            List<MPositionInfoVo> positions = mPositionMapper.getAllPositionList(condition);
            vo.setPositions(positions);
            setFile(vo);
        }
        return list;
    }

    @Override
    public MStaffVo getDetail(MStaffVo searchCondition) {
        MStaffVo vo = mapper.getDetail(searchCondition.getId());

        MPositionInfoVo condition = new MPositionInfoVo();
        condition.setStaff_id(vo.getId());
        List<MPositionInfoVo> positions = mPositionMapper.getAllPositionList(condition);
        vo.setPositions(positions);
        setFile(vo);

        vo.setWarehouseGroupStaffList(mWarehouseMapper.getWarehouseByGroupStaffId(vo.getId().intValue()));
        vo.setWarehouseGroupList(mWarehouseMapper.getWarehouseGroupByStaffId(vo.getId().intValue()));
        vo.setWarehouseStaffList(mWarehouseMapper.getWarehouseByStaffId(vo.getId().intValue()));
        vo.setWarehouseGroupIds(new ArrayList<>());

        MUserVo muservo = mUserMapper.selectUserById(vo.getUser_id());
        vo.setUser(muservo);

        TreeDataVo mStaffPermissionDataVo = new TreeDataVo();
        mStaffPermissionDataVo.setSerial_id(vo.getId());
        mStaffPermissionDataVo.setSerial_type("m_staff");
        mStaffPermissionDataVo.setLabel(vo.getName());

        // 岗位信息
        List<TreeDataVo> positionList = mUserMapper.selectStaffPositionList(vo.getId());
        for (TreeDataVo positionItem : positionList) {
            // 角色信息
            List<TreeDataVo> roleList = mUserMapper.selectPositionRoleList(positionItem.getSerial_id());
            for (TreeDataVo roleItem:roleList) {
                // 权限信息
                List<TreeDataVo> permissionList = mUserMapper.selectRolePermissionList(roleItem.getSerial_id());
                for (TreeDataVo permissionItem:permissionList) {
                    permissionItem.setChildren(new ArrayList<>());
                }

                roleItem.setChildren(permissionList);

            }

            positionItem.setChildren(roleList);
        }


        mStaffPermissionDataVo.setChildren(positionList);
        vo.setPermissionTreeData(mStaffPermissionDataVo);

        TreeDataVo warehouseTreeDataVo = new TreeDataVo();
        warehouseTreeDataVo.setSerial_id(vo.getId());
        warehouseTreeDataVo.setSerial_type("m_staff");
        warehouseTreeDataVo.setLabel(vo.getName());
        // 仓库组信息
        List<TreeDataVo> warehouseGroupList = mUserMapper.selectWarehouseGroupList(vo.getId());
        for(TreeDataVo warehouseGroupItem:warehouseGroupList) {
            // 仓库组下的仓库信息
            List<TreeDataVo> warehouseList = mUserMapper.selectWarehouseListByGroupId(warehouseGroupItem.getSerial_id());
            warehouseGroupItem.setChildren(warehouseList);
        }
        warehouseTreeDataVo.setChildren(warehouseGroupList);

        // 员工下的仓库信息
        List<TreeDataVo> warehouseStaffList = mUserMapper.selectWarehouseListByStaffId(vo.getId());
        warehouseTreeDataVo.getChildren().addAll(warehouseStaffList);
        vo.setWarehouseTreeData(warehouseTreeDataVo);

        return vo;
    }

    public void setFile(MStaffVo vo) {
        SFileInfoVo fileInfoVo ;
        // 空车过磅附件
        if(vo.getOne_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getOne_file());
            // 如果文件不存在，设置空对象避免空指针异常
            vo.setOne_fileVo(fileInfoVo != null ? fileInfoVo : new SFileInfoVo());
        } else {
            vo.setOne_fileVo(new SFileInfoVo());
        }
        // 车头车尾带司机附件
        if(vo.getTwo_file() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getTwo_file());
            // 如果文件不存在，设置空对象避免空指针异常
            vo.setTwo_fileVo(fileInfoVo != null ? fileInfoVo : new SFileInfoVo());
        } else {
            vo.setTwo_fileVo(new SFileInfoVo());
        }
    }

    /**
     * 查询附件对象
     */
    public SFileInfoVo getFileInfo(Integer id) {
        if (id == null) {
            return null;
        }
        
        SFileEntity file = fileMapper.selectById(id);
        if (file == null) {
            // 文件记录不存在，返回null而不是抛出异常
            return null;
        }
        
        SFileInfoEntity fileInfo = fileInfoMapper.selectFIdEntity(file.getId());
        if (fileInfo == null) {
            // 文件详情不存在，返回null
            return null;
        }
        
        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
        fileInfoVo.setFileName(fileInfoVo.getFile_name());
        return fileInfoVo;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
//    @Override
//    public List<MStaffVo> select(MStaffVo searchCondition) {
//        // 查询 数据
//        List<MStaffVo> list = mapper.select(searchCondition);
//        return list;
//    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MStaffVo> selectIdsIn(List<MStaffVo> searchCondition) {
        // 查询 数据
        List<MStaffVo> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 获取所选id的数据
     */
    @Override
    public List<MStaffVo> exportBySelectIdsIn(List<MStaffVo> searchCondition) {
        // 查询 数据
        List<MStaffVo> list = mapper.exportSelectIdsIn(searchCondition);
        return list;
    }

    /**
     * 批量删除复原
     *
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public DeleteResultAo<Integer> realDeleteByIdsIn(List<MStaffVo> searchCondition) {
        List<Long> idList = new ArrayList<>();
        searchCondition.forEach(bean -> {
            idList.add(bean.getId());
        });
        int result=mapper.deleteBatchIds(idList);
        return DeleteResultUtil.OK(result);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MStaffVo vo, HttpSession session) {

        // 分拆entity
        MStaffEntity mStaffEntity = (MStaffEntity) BeanUtilsSupport.copyProperties(vo, MStaffEntity.class);
        MUserEntity mUserEntity = (MUserEntity) BeanUtilsSupport.copyProperties(vo.getUser(), MUserEntity.class);

        // 设置autocode
        mStaffEntity.setCode(mstaffAutoCodeService.autoCode().getCode());

        // 插入前check，员工表check
        CheckResultAo cr1 = checkStaffEntity(mStaffEntity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr1.isSuccess() == false) {
            throw new BusinessException(cr1.getMessage());
        }
        // 插入前check，账号表check
        CheckResultAo cr2 = checkUserEntity(mUserEntity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr2.isSuccess() == false) {
            throw new BusinessException(cr2.getMessage());
        }

        // 部分默认值设置
        mStaffEntity.setIs_del(mStaffEntity.getIs_del() == null ? false : mStaffEntity.getIs_del());

        mUserEntity.setIs_del(mUserEntity.getIs_del() == null ? false : mUserEntity.getIs_del());
        mUserEntity.setIs_lock(mUserEntity.getIs_lock() == null ? false : mUserEntity.getIs_lock());
        mUserEntity.setIs_enable(mUserEntity.getIs_enable() == null ? true : mUserEntity.getIs_enable());
        mUserEntity.setIs_biz_admin(mUserEntity.getIs_biz_admin() == null ? false : mUserEntity.getIs_biz_admin());
        mUserEntity.setIs_changed_pwd(mUserEntity.getIs_changed_pwd() == null ? false : mUserEntity.getIs_changed_pwd());
        mUserEntity.setPwd_u_time(LocalDateTime.now());
        try {
            CreateAvatarByUserNameUtil.generateImg(mStaffEntity.getName(), "/wms/avatar_temp", mStaffEntity.getName());
            String avatarUrl = uploadFile("/wms/avatar_temp/"+mStaffEntity.getName()+".jpg", mStaffEntity.getName() +".jpg", 0);
            mUserEntity.setAvatar(avatarUrl);
        } catch (Exception e) {
            log.error("insert error", e);
        }

        mapper.insert(mStaffEntity);

        SFileInfoVo file = vo.getOne_fileVo();
        // 附件从表
        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(mStaffEntity.getId().intValue());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_STAFF);
        insertFile(file,fileEntity,fileInfoEntity);
        mStaffEntity.setOne_file(fileEntity.getId());

        file = vo.getTwo_fileVo();
        // 附件从表
        fileInfoEntity = new SFileInfoEntity();
        // 附件主表
        fileEntity = new SFileEntity();
        fileEntity.setSerial_id(mStaffEntity.getId().intValue());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_STAFF);
        insertFile(file,fileEntity,fileInfoEntity);
        mStaffEntity.setTwo_file(fileEntity.getId());

        // 插入逻辑保存
        mUserMapper.insert(mUserEntity);
        mapper.updateById(mStaffEntity);

        // 增添关系
        mUserEntity.setStaff_id(mStaffEntity.getId());
        mStaffEntity.setUser_id(mUserEntity.getId());
        mStaffEntity.setIs_del(false);
        mUserEntity.setIs_del(false);

        // 判断session中的密码是否有设置，如果有设置则获取并保存
        Object sessionObj = session.getAttribute(SystemConstants.SESSION_KEY_USER_PASSWORD);
        if(sessionObj != null) {
            String encodePsd = (String) sessionObj;
            mUserEntity.setPwd(encodePsd);
            // 删除
            session.removeAttribute(SystemConstants.SESSION_KEY_USER_PASSWORD);
        }

        // 更新保存
//        mStaffEntity.setC_id(null);
//        mStaffEntity.setC_time(null);
        mapper.updateById(mStaffEntity);

//        mUserEntity.setU_id(null);
//        mUserEntity.setU_time(null);
        mUserMapper.updateById(mUserEntity);

        // 判断企业、部门字段，对用户组织关系表进行更新
        updateStaffOrg(mStaffEntity);

        // 用户简单重构
        imUserLiteService.reBulidUserLiteData(mUserEntity.getId());

        // 用户仓库分组权限
        for (BWarehouseGroupVo bWarehouseGroupVo: vo.getWarehouseGroupList() ) {
            BWarehouseRelationVo bWarehouseRelationVo = new BWarehouseRelationVo();
            bWarehouseRelationVo.setStaff_id(mStaffEntity.getId().intValue());
            bWarehouseRelationVo.setSerial_id(bWarehouseGroupVo.getId());
            bWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_WAREHOUSE_GROUP);

            ibWarehouseRelationService.insert(bWarehouseRelationVo);
        }

        // 用户仓库权限
        for (MWarehouseVo mWarehouseVo: vo.getWarehouseStaffList()) {
            BWarehouseRelationVo bWarehouseRelationVo = new BWarehouseRelationVo();
            bWarehouseRelationVo.setStaff_id(mStaffEntity.getId().intValue());
            bWarehouseRelationVo.setSerial_id(mWarehouseVo.getId());
            bWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_WAREHOUSE);

            ibWarehouseRelationService.insert(bWarehouseRelationVo);
        }

        // 返回值确定
        vo.setId(mStaffEntity.getId());
        return InsertResultUtil.OK(1);
    }

    /**
     * 新增监管任务附件
     */
    public void insertFile(SFileInfoVo file, SFileEntity fileEntity, SFileInfoEntity fileInfoEntity) {
        // 主表新增
        if (null != file) {
            fileEntity.setId(null);
            fileMapper.insert(fileEntity);
            file.setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(file,fileInfoEntity);
            fileInfoEntity.setFile_name(file.getFileName());
            fileInfoEntity.setId(null);
            fileInfoMapper.insert(fileInfoEntity);
        }
    }

    /**
     * 更新用户组织机构关系表
     */
    private void updateStaffOrg(MStaffEntity entity) {
        // 删除关系表：企业
        mStaffOrgMapper.delete(new QueryWrapper<MStaffOrgEntity>()
                .eq("staff_id",entity.getId())
                .eq("serial_type", DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE)
        );
        // 删除关系表：部门
        mStaffOrgMapper.delete(new QueryWrapper<MStaffOrgEntity>()
                .eq("staff_id",entity.getId())
                .eq("serial_type", DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE)
        );
        // 插入关系表：企业
        if(entity.getCompany_id() != null){
            MStaffOrgEntity companyStaffEntity = new MStaffOrgEntity();
            companyStaffEntity.setStaff_id(entity.getId());
            companyStaffEntity.setSerial_id(entity.getCompany_id());
            companyStaffEntity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE);
            mStaffOrgMapper.insert(companyStaffEntity);
        }
        // 插入关系表：部门
        if(entity.getDept_id() != null){
            MStaffOrgEntity deptStaffEntity = new MStaffOrgEntity();
            deptStaffEntity.setStaff_id(entity.getId());
            deptStaffEntity.setSerial_id(entity.getDept_id());
            deptStaffEntity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE);
            mStaffOrgMapper.insert(deptStaffEntity);
        }
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param vo 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MStaffVo vo, HttpSession session) {
        // 分拆entity
        MStaffEntity mStaffEntity = (MStaffEntity) BeanUtilsSupport.copyProperties(vo, MStaffEntity.class);
        MUserEntity mUserEntity = (MUserEntity) BeanUtilsSupport.copyProperties(vo.getUser(), MUserEntity.class);

        // 插入前check
        CheckResultAo cr1 = checkStaffEntity(mStaffEntity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr1.isSuccess() == false) {
            throw new BusinessException(cr1.getMessage());
        }
        CheckResultAo cr2 = checkUserEntity(mUserEntity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr2.isSuccess() == false) {
            throw new BusinessException(cr2.getMessage());
        }

        SFileInfoVo file = vo.getOne_fileVo();
        // 附件从表
        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(mStaffEntity.getId().intValue());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_STAFF);
        insertFile(file,fileEntity,fileInfoEntity);
        mStaffEntity.setOne_file(fileEntity.getId());

        file = vo.getTwo_fileVo();
        // 附件从表
        fileInfoEntity = new SFileInfoEntity();
        // 附件主表
        fileEntity = new SFileEntity();
        fileEntity.setSerial_id(mStaffEntity.getId().intValue());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_STAFF);
        insertFile(file,fileEntity,fileInfoEntity);
        mStaffEntity.setTwo_file(fileEntity.getId());

        // 判断session中的密码是否有设置，如果有设置则获取并保存
        Object sessionObj = session.getAttribute(SystemConstants.SESSION_KEY_USER_PASSWORD);
        if(sessionObj != null) {
            String encodePsd = (String) sessionObj;
            // 备份旧的密码
            mUserEntity.setPwd_his_pwd(mUserEntity.getPwd());
            // 保存新的密码
            mUserEntity.setPwd(encodePsd);
            // 更新时间
            mUserEntity.setPwd_u_time(LocalDateTime.now());
            // 删除
            session.removeAttribute(SystemConstants.SESSION_KEY_USER_PASSWORD);
        }

        try {
            if (StringUtils.isEmpty(mUserEntity.getAvatar())) {
                CreateAvatarByUserNameUtil.generateImg(mStaffEntity.getName(), "/wms/avatar_temp", mStaffEntity.getName());
                String avatarUrl = uploadFile("/wms/avatar_temp/"+mStaffEntity.getName()+".jpg", mStaffEntity.getName() +".jpg", 0);
                mUserEntity.setAvatar(avatarUrl);
            }
        } catch (Exception e) {
            log.error("update error", e);
        }

        mUserEntity.setStaff_id(mStaffEntity.getId());
        if(mStaffEntity.getUser_id() == null){
            mUserMapper.insert(mUserEntity);
        } else {
//            mUserEntity.setU_id(null);
//            mUserEntity.setU_time(null);
            mUserMapper.updateById(mUserEntity);
        }
        mStaffEntity.setUser_id(mUserEntity.getId());
//        mStaffEntity.setU_id(null);
//        mStaffEntity.setU_time(null);
        mapper.updateById(mStaffEntity);

        // 判断企业、部门字段，对用户组织关系表进行更新
        updateStaffOrg(mStaffEntity);

        // 设置返回值
        vo.setId(mStaffEntity.getId());

        // 用户简单重构
        imUserLiteService.reBulidUserLiteData(mUserEntity.getId());

        // 全删全插
        ibWarehouseRelationService.deleteByStaffId(vo.getId().intValue());
        // 用户仓库分组权限
        for (BWarehouseGroupVo bWarehouseGroupVo: vo.getWarehouseGroupList() ) {
            BWarehouseRelationVo bWarehouseRelationVo = new BWarehouseRelationVo();
            bWarehouseRelationVo.setStaff_id(vo.getId().intValue());
            bWarehouseRelationVo.setSerial_id(bWarehouseGroupVo.getId());
            bWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_WAREHOUSE_GROUP);

            ibWarehouseRelationService.insert(bWarehouseRelationVo);
        }

        // 用户仓库权限
        for (MWarehouseVo mWarehouseVo: vo.getWarehouseStaffList()) {
            BWarehouseRelationVo bWarehouseRelationVo = new BWarehouseRelationVo();
            bWarehouseRelationVo.setStaff_id(vo.getId().intValue());
            bWarehouseRelationVo.setSerial_id(mWarehouseVo.getId());
            bWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_WAREHOUSE);

            ibWarehouseRelationService.insert(bWarehouseRelationVo);
        }

        // 更新逻辑保存
        return UpdateResultUtil.OK(1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> updateSelf(MStaffVo vo) {
        MStaffEntity entity = mapper.selectById(SecurityUtil.getStaff_id());
        entity.setEmail(vo.getEmail());
        entity.setName(vo.getName());
        entity.setMobile_phone(vo.getMobile_phone());
        entity.setSex(vo.getSex());
        mapper.updateById(entity);

        // 更新逻辑保存
        return UpdateResultUtil.OK(1);
    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MStaffVo selectByid(Long id){
        MStaffVo searchCondition = new MStaffVo();
        searchCondition.setId(id);
        MStaffVo vo = mapper.selectByid(searchCondition);

        MPositionInfoVo condition = new MPositionInfoVo();
        condition.setStaff_id(vo.getId());
        List<MPositionInfoVo> positions = mPositionMapper.getAllPositionList(condition);
        vo.setPositions(positions);
        return vo;
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIdsIn(List<MStaffVo> searchCondition){
        List<MStaffVo> list = mapper.selectIdsIn(searchCondition);
        list.forEach(bean -> {
            bean.setIs_del(!bean.getIs_del());
            MUserEntity user = mUserMapper.getDataByStaffId(bean.getId());
            user.setIs_enable(!user.getIs_enable());
            mUserMapper.updateById(user);
        });
        List<MStaffEntity> entityList = BeanUtilsSupport.copyProperties(list, MStaffEntity.class);
        super.saveOrUpdateBatch(entityList, 500);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MStaffEntity> selectBySimpleName(String name, Long equal_id) {
        // 查询 数据
        List<MStaffEntity> list = mapper.selectBySimpleName(name, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MStaffEntity> selectBySimpleNameNotEqualId(String name, Long not_equal_id) {
        // 查询 数据
        List<MStaffEntity> list = mapper.selectBySimpleNameNotEqualId(name, not_equal_id);
        return list;
    }

    /**
     * 查询by name，返回结果
     *
     * @return
     */
    public List<MStaffEntity> selectByNameNotEqualId(String val, Long not_equal_id) {
        // 查询 数据
        List<MStaffEntity> list = mapper.selectByNameNotEqualId(val, not_equal_id);
        return list;
    }

    /**
     * 查询by name，返回结果
     *
     * @return
     */
    public List<MStaffEntity> selectByName(String val, Long equal_id) {
        // 查询 数据
        List<MStaffEntity> list = mapper.selectByName(val, equal_id);
        return list;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkStaffEntity(MStaffEntity entity, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 员工姓名重复性check
                List<MStaffEntity> nameList_insertCheck = selectByName(entity.getName(), null);
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：员工姓名【"+ entity.getName() +"】出现重复", nameList_insertCheck);
                }

                // 员工简称重复性check
                List<MStaffEntity> _insertCheck = selectBySimpleName(entity.getSimple_name(), null);
                if (_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：员工姓名简称【"+ entity.getSimple_name() +"】出现重复", _insertCheck);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 员工姓名重复性check
                List<MStaffEntity> nameList_updCheck = selectByNameNotEqualId(entity.getName(),  entity.getId());
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：员工姓名【"+ entity.getName() +"】出现重复", nameList_updCheck);
                }

                // 员工简称重复性check
                List<MStaffEntity> simpleNameList_updCheck = selectBySimpleNameNotEqualId(entity.getSimple_name(), entity.getId());
                if (simpleNameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：员工姓名简称【"+ entity.getSimple_name() +"】出现重复", simpleNameList_updCheck);
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为false，表示为：页面点击了删除操作 */
                if(entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                // 是否被使用的check，如果被使用则不能删除
                int count = mapper.isExistsInOrg(entity);
                if(count > 0){
                    return CheckResultUtil.NG("删除出错：该员工【"+ entity.getSimple_name() +"】在组织机构中正在使用！", count);
                }
                break;
            case CheckResultAo.UNDELETE_CHECK_TYPE:
                // 员工姓名重复性check
//                List<MStaffEntity> nameList_undelete_Check = selectByName(entity.getName(),  entity.getId());
//                if (nameList_undelete_Check.size() >= 1) {
//                    CheckResultUtil.NG("复原出错：该员工【"+ entity.getName() +"】在组织机构数据中正在被使用，复原这条数据会造成数据重复！", entity.getName());
//                }
//
//                // 员工简称重复性check
//                List<MStaffEntity> simpleNameList_undelete_Check = selectBySimpleName(entity.getName(),  null);
//                if (simpleNameList_undelete_Check.size() >= 1) {
//                    return CheckResultUtil.NG("复原出错：该员工【"+ entity.getSimple_name() +"】在组织机构数据中正在被使用，复原这条数据会造成数据重复！", entity.getName());
//                }
                break;
        }
        return CheckResultUtil.OK();
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkUserEntity(MUserEntity entity, String moduleType){
        // 登录人名称不能重复
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                if(entity.getIs_enable()){
                    List<MUserEntity> listValue_insertCheck = selectLoginName(entity.getLogin_name(),  null);
                    // 新增场合，不能重复
                    if (listValue_insertCheck.size() >= 1) {
                        // 模块编号不能重复
                        return CheckResultUtil.NG("新增保存出错：登录用户名【"+ entity.getLogin_name() +"】出现重复！", listValue_insertCheck);
                    }
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                if(entity.getIs_enable() == null || entity.getIs_enable()){
                    List<MUserEntity> listValue_updCheck = selectLoginName(entity.getLogin_name(),  entity.getId());
                    // 更新场合，不能重复设置
                    if (listValue_updCheck.size() >= 1) {
                        // 模块编号不能重复
                        return CheckResultUtil.NG("更新保存出错：登录用户名【"+ entity.getLogin_name() +"】出现重复！", listValue_updCheck);
                    }
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                break;
            case CheckResultAo.UNDELETE_CHECK_TYPE:
                /** 如果逻辑删除为true，表示为：页面点击了删除操作 */
//                if(!entity.getIs_del()) {
//                    return CheckResultUtil.OK();
//                }
//                if(entity.getIs_enable()){
//                    List<MUserEntity> listValue_updCheck = selectLoginName(entity.getLogin_name(),  entity.getId());
//                    // 更新场合，不能重复设置
//                    if (listValue_updCheck.size() >= 1) {
//                        // 模块编号不能重复
//                        return CheckResultUtil.NG("复原出错：登录用户名【"+ entity.getLogin_name() +"】出现重复！", listValue_updCheck);
//                    }
//                }
                break;
            default:
        }
        return CheckResultUtil.OK();
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
     * 查询岗位员工
     * @param searchCondition
     * @return
     */
    @Override
    public MStaffPositionVo getPositionStaffData(MStaffPositionVo searchCondition) {
        // 分页条件
        Page<MStaffEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        MStaffPositionVo mStaffPositionVo = new MStaffPositionVo();
        mStaffPositionVo.setId(searchCondition.getId());
        IPage<MPositionVo> list = mapper.getPositionStaffData(pageCondition, searchCondition);
        List<MStaffPositionCountsVo> counts = mapper.getPositionStaffDataCount(searchCondition);
        mStaffPositionVo.setAll(counts.get(0).getCount());
        mStaffPositionVo.setSettled(counts.get(1).getCount());
        mStaffPositionVo.setUnsettled(counts.get(2).getCount());
        mStaffPositionVo.setList(list);
        return  mStaffPositionVo;
    }

    /**
     * 查询岗位员工
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setPositionStaff(MStaffPositionVo searchCondition) {
        /** 用户组织机构关系表:m_staff_org 插入数据 */
        if(searchCondition.getPosition_settled()){
            // 设置了岗位
            MStaffOrgEntity entity = new MStaffOrgEntity();
            entity.setStaff_id(searchCondition.getId());
            entity.setSerial_id(searchCondition.getPosition_id());
            entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE);
            mStaffOrgMapper.insert(entity);
        } else {
            // 取消了岗位
            mStaffOrgMapper.delete(new QueryWrapper<MStaffOrgEntity>()
                    .eq("staff_id",searchCondition.getId())
                    .eq("serial_id",searchCondition.getPosition_id())
                    .eq("serial_type", DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE)
            );
        }
    }

    /**
     * 更新头像
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAvatar(String url) {
        MStaffEntity mStaffEntity = mapper.selectById(SecurityUtil.getStaff_id());
        MUserEntity userEntity = mUserMapper.selectById(mStaffEntity.getUser_id().intValue());
        userEntity.setAvatar(url);
        mUserMapper.updateById(userEntity);
    }

    /**
     * 根据 用户code查询ID
     *
     * @param auditStaffCode
     * @return
     */
    @Override
    public Integer selectIdByStaffCode(String auditStaffCode) {
        MStaffEntity mStaffEntity = mapper.selectOne(new LambdaQueryWrapper<MStaffEntity>().eq(MStaffEntity::getCode, auditStaffCode));
        if (Objects.isNull(mStaffEntity)) {
            return null;
        }
        return mStaffEntity.getId().intValue();
    }

    /**
     * 查询所有数据导出
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MStaffExportVo> selectExportAllList(MStaffVo searchCondition) {
        return mapper.selectExportAllList(searchCondition);
    }

    /**
     * 查询部分数据数据导出
     *
     * @param searchConditionList
     * @return
     */
    @Override
    public List<MStaffExportVo> selectExportList(List<MStaffVo> searchConditionList) {
        return mapper.selectExportList(searchConditionList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initAvatar() {
        List<MStaffEntity> list = mapper.selectList(new LambdaQueryWrapper<MStaffEntity>());
        for (MStaffEntity mStaffEntity : list) {
            if (mStaffEntity.getUser_id() == null) {
                continue;
            }
            MUserEntity userEntity = mUserMapper.selectById(mStaffEntity.getUser_id().intValue());
            try {
                if (StringUtils.isEmpty(mStaffEntity.getName())) {
                    continue;
                }
                CreateAvatarByUserNameUtil.generateImg(mStaffEntity.getName(), "/wms/avatar_temp", mStaffEntity.getName());
                String avatarUrl = uploadFile("/wms/avatar_temp/"+mStaffEntity.getName()+".jpg", mStaffEntity.getName() +".jpg", 0);
                userEntity.setAvatar(avatarUrl);
                mUserMapper.updateById(userEntity);
            } catch (Exception e) {
                log.error("initAvatar error", e);
            }
        }
    }
}
