package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.entity.master.org.*;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateBo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateDetailBo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.utils.common.tree.TreeUtil;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.bean.system.vo.common.component.TreeNode;
import com.xinyirun.scm.bean.system.vo.master.org.*;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.LogByIdAnnotion;
import com.xinyirun.scm.common.annotations.LogByIdsAnnotion;
import com.xinyirun.scm.common.annotations.OperationLogAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.enums.ParameterEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.ArrayPfUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.common.utils.redis.RedisUtil;
import com.xinyirun.scm.core.system.mapper.master.org   .MOrgCompanyDeptMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgDeptPositionMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgGroupCompanyMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MStaffOrgMapper;
import com.xinyirun.scm.core.system.service.common.ICommonComponentService;
import com.xinyirun.scm.core.system.service.master.org.IMOrgService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.log.operate.SLogOperServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *  岗位主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Slf4j
@Service
public class MOrgServiceImpl extends BaseServiceImpl<MOrgMapper, MOrgEntity> implements IMOrgService {

    @Autowired
    private MOrgGroupCompanyMapper oGCMapper;

    @Autowired
    private MOrgDeptPositionMapper oDPMapper;

    @Autowired
    private MOrgCompanyDeptMapper oCDMapper;

    @Autowired
    private MOrgMapper mapper;

    @Autowired
    private MStaffOrgMapper mStaffOrgMapper;

    @Autowired
    private ICommonComponentService iCommonComponentService;

    private MOrgServiceImpl self;

    @Autowired
    private MStaffOrgServiceImpl mStaffOrgService;

    @Autowired
    private SLogOperServiceImpl sLogOperService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    public MOrgServiceImpl(@Lazy MOrgServiceImpl self) {
        this.self = self;
    }

    /** 组织entity数组 */
//    List<MOrgEntity> entities;

    /**
     * 获取所有数据，左侧树数据
     */
    @Override
    public List<MOrgTreeVo> getTreeList(MOrgTreeVo searchCondition) {
        switch (searchCondition.getType()) {
            case DictConstant.DICT_ORG_SETTING_TYPE_COMPANY:
                // 企业
                String[] company_codes = {
                    DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE,
                    DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE};
                searchCondition.setCodes(company_codes);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_DEPT:
                // 部门
                String[] dept_codes = {
                    DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE,
                    DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE,
                    DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE
                };
                searchCondition.setCodes(dept_codes);
                // 获取code
                MOrgEntity mOrgEntity = mapper.selectOne(new QueryWrapper<MOrgEntity>()
                    .eq("serial_id",searchCondition.getSerial_id())
                    .eq("serial_type", searchCondition.getSerial_type())
                );
                String code = mOrgEntity.getCode().substring(0,8);
                searchCondition.setCode(code);
                searchCondition.setCurrent_code(mOrgEntity.getCode());
                break;
        };

        // 查询 数据
        List<MOrgTreeVo> list = mapper.getTreeList(searchCondition);
        List<MOrgTreeVo> rtnList = TreeUtil.getTreeList(list);
        return rtnList;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MOrgTreeVo> select(MOrgVo searchCondition) {
        // 查询 数据
        List<MOrgTreeVo> list = mapper.select(searchCondition);
        List<MOrgTreeVo> rtnList = TreeUtil.getTreeList(list);
        return rtnList;
    }

    /**
     * 获取所有的组织数据
     * @param searchCondition
     * @return
     */
    @Override
    public MOrgCountsVo getAllOrgDataCount(MOrgVo searchCondition) {
        MOrgCountsVo mOrgCountsVo = mapper.getAllOrgDataCount(searchCondition);
        return mOrgCountsVo;
    }

    /**
     * 获取组织数据
     * @param searchCondition
     * @return
     */
    @Override
    public List<MOrgTreeVo> getOrgs(MOrgVo searchCondition) {
        List<MOrgTreeVo> listOrg = select(searchCondition);
        return listOrg;
    }

    /**
     * 获取集团数据
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MGroupVo> getGroups(MOrgTreeVo searchCondition) {
        // 分页条件
        Page<MGroupEntity> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MGroupVo> listGroup = mapper.getGroupList(pageCondition, searchCondition);
        return listGroup;
    }

    /**
     * 获取企业数据
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MCompanyVo> getCompanies(MOrgTreeVo searchCondition) {
        // 分页条件
        Page<MCompanyEntity> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MCompanyVo> listcompany = mapper.getCompanyList(pageCondition, searchCondition);
        return listcompany;
    }

    /**
     * 获取部门数据
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MDeptVo> getDepts(MOrgTreeVo searchCondition) {
        // 分页条件
        Page<MDeptVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MDeptVo> listDept =  mapper.getDeptList(pageCondition, searchCondition);
        return listDept;
    }

    /**
     * 获取岗位数据
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MPositionVo> getPositions(MOrgTreeVo searchCondition) {
        // 分页条件
        Page<MDeptVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MPositionVo> listPosition =  mapper.getPositionList(pageCondition, searchCondition);
        return listPosition;
    }

    /**
     * 获取员工数据
     * @param searchCondition
     * @return
     */
    @Override
    public List<MStaffVo> getStaffs(MOrgVo searchCondition) {
        return null;
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_ORG.OPER_INSERT,
        type = OperationEnum.ADD,
        logById = @LogByIdAnnotion(
            name = SystemConstants.OPERATION.M_ORG.OPER_INSERT,
            type = OperationEnum.ADD,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_ORG.TABLE_NAME,
            id = "#{entity.id}"
        )
    )
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MOrgEntity entity) {
        // 设置entity
        switch (entity.getType()) {
            case DictConstant.DICT_ORG_SETTING_TYPE_GROUP:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_COMPANY:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_DEPT:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_POSITION:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_STAFF:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_STAFF_SERIAL_TYPE);
                break;
        }

        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);

        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 获取父亲的entity
        MOrgEntity parentEntity = getById(entity.getParent_id());
        Integer son_count = parentEntity.getSon_count();
        son_count = (son_count == null ? 0 : son_count)  + 1;
        parentEntity.setSon_count(son_count);
        // 保存父亲的儿子的个数
//        parentEntity.setC_id(null);
//        parentEntity.setC_time(null);
        mapper.updateById(parentEntity);

        // 获取父亲的code
        String parentCode = parentEntity.getCode();
        // 计算当前编号
        // 获取当前son_count
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        String str = String.format("%04d", son_count);
        entity.setCode(parentCode + str);
        entity.setSon_count(0);

        // 执行插入操作
        int insert_result = mapper.insert(entity);

        // 设置组织关系表逻辑
        setOrgRelationData(entity,parentEntity);

        // 清理所有组织架构相关缓存
        clearAllOrgCaches();

        return InsertResultUtil.OK(insert_result);
    }

    /**
     * 设置集团关系，存在集团嵌套情况
     */
//    private void updateOTGRelation(MOrgEntity currentEntity, MOrgEntity parentEntity){
//        MOrgTenantGroupEntity oTGEntity = new MOrgTenantGroupEntity();
//        oTGEntity.setCurrent_id(currentEntity.getSerial_id());
//        oTGEntity.setOrg_id(currentEntity.getId());
//        oTGEntity.setOrg_parent_id(currentEntity.getParent_id());
//        if(DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE.equals(parentEntity.getSerial_type())) {
//            /** 查找上级结点如果是集团时，说明存在集团嵌套，m_org_tenant_group */
//            oTGEntity.setParent_id(parentEntity.getSerial_id());
//            oTGEntity.setParent_type(DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE);
//            // 查找上级结点获取，root信息
//            MOrgTenantGroupEntity parentOTGEntity = oTGMapper
//                    .getOTGEntityByCurrentId(parentEntity.getSerial_id());
//            oTGEntity.setRoot_id(parentOTGEntity.getRoot_id());
//        } else {
//            /** 查找上级结点如果是租户，则不存在嵌套 */
//            oTGEntity.setParent_id(parentEntity.getSerial_id());
//            oTGEntity.setParent_type(DictConstant.DICT_ORG_SETTING_TYPE_TENANT_SERIAL_TYPE);
//            oTGEntity.setRoot_id(currentEntity.getSerial_id());
//        }
//        /** 更新sort */
//        int count = oTGMapper.getOTGRelationCount(currentEntity);
//        count = count + 1;
//        oTGEntity.setSort(count);
//        /** 插入操作 */
//        oTGMapper.insert(oTGEntity);
//        /** 更新counts，和sorts */
//        oTGMapper.updateOTGCountAndSort(oTGEntity.getId());
//    }

    /**
     * 设置组织关系表逻辑
     */
    private void setOrgRelationData(MOrgEntity entity, MOrgEntity parentEntity) {
        try {
            // 判断当前结点
            switch (entity.getType()) {
                case DictConstant.DICT_ORG_SETTING_TYPE_GROUP:
    //                updateOTGRelation(entity,parentEntity);
                    break;
                case DictConstant.DICT_ORG_SETTING_TYPE_COMPANY:
                    updateOGCRelation(entity, parentEntity);
                    break;
                case DictConstant.DICT_ORG_SETTING_TYPE_DEPT:
                    updateOCDRelation(entity, parentEntity);
                    break;
                case DictConstant.DICT_ORG_SETTING_TYPE_POSITION:
                    updateODPRelation(entity, parentEntity);
                    break;
                case DictConstant.DICT_ORG_SETTING_TYPE_STAFF:
                    updateStaffPositionRelation(entity, parentEntity);
                    break;
                default:
                    log.warn("未知的组织实体类型：{}，跳过关系设置", entity.getType());
                    break;
            }
            
        } catch (Exception e) {
            log.error("设置组织关系表逻辑失败 - 实体ID：{}，类型：{}，错误信息：{}", 
                entity.getId(), entity.getType(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 设置集团->企业关系
     */
    private void updateOGCRelation(MOrgEntity currentEntity, MOrgEntity parentEntity){
        try {
            MOrgGroupCompanyEntity oGCEntity = new MOrgGroupCompanyEntity();
            oGCEntity.setOrg_id(currentEntity.getId());
            oGCEntity.setOrg_parent_id(currentEntity.getParent_id());
            oGCEntity.setCurrent_id(currentEntity.getSerial_id());
            oGCEntity.setParent_id(parentEntity.getSerial_id());
            oGCEntity.setParent_type(DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE);
            oGCEntity.setRoot_id(currentEntity.getSerial_id());
            oGCEntity.setRoot_group_id(parentEntity.getSerial_id());
            oGCEntity.setCounts(1);
            oGCEntity.setSort(1);
            
            /** 插入操作前先删除可能存在的旧记录，防止重复 */
            oGCMapper.delOGCRelation(oGCEntity.getCurrent_id());
            oGCMapper.insert(oGCEntity);
            
        } catch (Exception e) {
            log.error("设置集团->企业关系失败 - Current_ID：{}，Parent_ID：{}，错误：{}", 
                currentEntity.getSerial_id(), parentEntity.getSerial_id(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 设置企业->部门关系
     */
    private void updateOCDRelation(MOrgEntity currentEntity, MOrgEntity parentEntity){
        try {
            MOrgCompanyDeptEntity oCDEntity = new MOrgCompanyDeptEntity();
            oCDEntity.setOrg_id(currentEntity.getId());
            oCDEntity.setOrg_parent_id(currentEntity.getParent_id());
            oCDEntity.setCurrent_id(currentEntity.getSerial_id());
            
            if(DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE.equals(parentEntity.getSerial_type())) {
                /** 查找上级结点如果是部门时，说明存在部门嵌套， */
                oCDEntity.setParent_id(parentEntity.getSerial_id());
                oCDEntity.setParent_type(DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE);
                
                try {
                    MOrgCompanyDeptEntity parentOCDEntity = oCDMapper
                        .getOCDEntityByCurrentId(parentEntity.getSerial_id());
                    
                    if (parentOCDEntity == null) {
                        log.error("查询父级部门OCD实体失败，返回null - 父级Serial_ID：{}", parentEntity.getSerial_id());
                        throw new BusinessException("无法找到父级部门的组织关系信息，Serial_ID：" + parentEntity.getSerial_id());
                    }
                    
                    oCDEntity.setRoot_id(parentOCDEntity.getRoot_id());
                    
                } catch (org.apache.ibatis.exceptions.TooManyResultsException e) {
                    log.error("查询父级部门OCD实体时发生重复记录异常 - 父级Serial_ID：{}，期望返回1条记录但实际返回多条", 
                        parentEntity.getSerial_id(), e);
                    
                    // 查询重复记录的详细信息
                    log.error("开始查询该Serial_ID的所有相关记录，用于诊断数据重复问题");
                    throw new BusinessException("父级部门组织关系数据异常，存在重复记录，Serial_ID：" + parentEntity.getSerial_id(), e);
                }
                
            } else {
                /** 查找上级结点如果是企业，则不存在嵌套 */
                oCDEntity.setParent_id(parentEntity.getSerial_id());
                oCDEntity.setParent_type(DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE);
                oCDEntity.setRoot_id(currentEntity.getSerial_id());
            }
            
            /** 更新sort */
            int count = oCDMapper.getOCDRelationCount(currentEntity);
            count = count + 1;
            oCDEntity.setSort(count);
            
            /** 插入操作前先删除可能存在的旧记录，防止重复 */
            oCDMapper.delOCDRelation(oCDEntity.getCurrent_id());
            oCDMapper.insert(oCDEntity);
            
            /** 更新counts，和sorts */
            oCDMapper.updateOCDCountAndSort(oCDEntity.getId());
            oCDMapper.updateOCDParentData();
            
        } catch (Exception e) {
            log.error("设置企业->部门关系失败 - Current_ID：{}，Parent_ID：{}，错误：{}", 
                currentEntity.getSerial_id(), parentEntity.getSerial_id(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 设置部门->岗位关系，不存在嵌套
     */
    private void updateODPRelation(MOrgEntity currentEntity, MOrgEntity parentEntity){
        try {
            MOrgDeptPositionEntity oDPEntity = new MOrgDeptPositionEntity();
            oDPEntity.setOrg_id(currentEntity.getId());
            oDPEntity.setOrg_parent_id(currentEntity.getParent_id());
            oDPEntity.setCurrent_id(currentEntity.getSerial_id());
            oDPEntity.setParent_id(parentEntity.getSerial_id());
            oDPEntity.setParent_type(DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE);
            oDPEntity.setRoot_id(currentEntity.getSerial_id());
            oDPEntity.setCounts(1);
            oDPEntity.setSort(1);
            
            /** 插入操作前先删除可能存在的旧记录，防止重复 */
            oDPMapper.delODPRelation(oDPEntity.getCurrent_id());
            oDPMapper.insert(oDPEntity);
            
        } catch (Exception e) {
            log.error("设置部门->岗位关系失败 - Current_ID：{}，Parent_ID：{}，错误：{}", 
                currentEntity.getSerial_id(), parentEntity.getSerial_id(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 设置员工->岗位关系
     * 当在组织机构管理页面新增员工节点时，自动建立员工与父级岗位的关系
     */
    private void updateStaffPositionRelation(MOrgEntity currentEntity, MOrgEntity parentEntity) {
        try {
            // 验证父级实体必须是岗位类型
            if (!DictConstant.DICT_ORG_SETTING_TYPE_POSITION.equals(parentEntity.getType())) {
                log.warn("员工节点的父级不是岗位类型 - 员工Serial_ID：{}，父级Serial_ID：{}，父级类型：{}", 
                    currentEntity.getSerial_id(), parentEntity.getSerial_id(), parentEntity.getType());
                return;
            }

            // 创建员工-岗位关系实体
            MStaffOrgEntity staffOrgEntity = new MStaffOrgEntity();
            staffOrgEntity.setStaff_id(currentEntity.getSerial_id());  // 员工Serial_ID
            staffOrgEntity.setSerial_id(parentEntity.getSerial_id()); // 岗位Serial_ID
            staffOrgEntity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE);
            
            // 插入员工-岗位关系
            mStaffOrgService.save(staffOrgEntity);
            
            log.info("成功建立员工-岗位关系 - 员工Serial_ID：{}，岗位Serial_ID：{}", 
                currentEntity.getSerial_id(), parentEntity.getSerial_id());
                
        } catch (Exception e) {
            log.error("设置员工->岗位关系失败 - 员工Serial_ID：{}，岗位Serial_ID：{}，错误：{}", 
                currentEntity.getSerial_id(), parentEntity.getSerial_id(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_ORG.OPER_UPDATE,
        type = OperationEnum.UPDATE,
        logById = @LogByIdAnnotion(
            name = SystemConstants.OPERATION.M_ORG.OPER_UPDATE,
            type = OperationEnum.UPDATE,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_ORG.TABLE_NAME,
            id = "#{entity.id}"
        )
    )
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_ORG_SUB_COUNT, 
               key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::' + #entity.id + '::' + #entity.type")
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MOrgEntity entity) {
        // 设置entity
        switch (entity.getType()) {
//            case DictConstant.DICT_ORG_SETTING_TYPE_TENANT:
//                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_TENANT_SERIAL_TYPE);
//                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_GROUP:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_GROUP_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_COMPANY:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_COMPANY_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_DEPT:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_POSITION:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE);
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_STAFF:
                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_STAFF_SERIAL_TYPE);
                break;
        }

        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
        
        UpdateResultAo<Integer> result = UpdateResultUtil.OK(mapper.updateById(entity));
        
        // 清理所有组织架构相关缓存
        clearAllOrgCaches();
        
        return result;
    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MOrgVo selectByid(Long id){
        return mapper.selectByid(id);
    }

    /**
     * 查询添加的子结点是否合法
     *
     * @return
     */
    public Integer selectNodeInsertStatus(String code, String type) {
        // 查询 数据
        Integer count = mapper.selectNodeInsertStatus(code, type);
        return count;
    }

    /**
     * 查询添加的子结点是否合法，子结点被重复选择使用的情况
     *
     * @return
     */
    public Integer getCountBySerial(MOrgEntity entity, Long equal_id) {
        // 查询 数据
        Integer count = mapper.getCountBySerial(entity, equal_id);
        return count;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MOrgEntity entity, String moduleType){
        Integer count = 0;
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 查看子结点是否正确：租户->集团->企业->部门->岗位->员工
                Integer countInsert = this.selectNodeInsertStatus(entity.getCode(),entity.getType());
                if(countInsert > 0){
                    String nodeTypeName = iCommonComponentService.getDictName(DictConstant.DICT_ORG_SETTING_TYPE, entity.getType());
                    return CheckResultUtil.NG("新增保存出错：新增的子结点类型不能是" + "【" + nodeTypeName + "】", countInsert);
                }
                // 查看当前结点是否已经被选择使用
                count = getCountBySerial(entity, null);
                if(count > 0){
                    return CheckResultUtil.NG("新增保存出错：您选择的子结点已经在组织架构中，请选择尚未被使用的组织。", count);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 查看子结点是否正确：租户->集团->企业->部门->岗位->员工
                Integer countUpdate = this.selectNodeInsertStatus(entity.getCode(),entity.getType());
                if(countUpdate > 0){
                    String nodeTypeName = iCommonComponentService.getDictName(DictConstant.DICT_ORG_SETTING_TYPE, entity.getType());
                    return CheckResultUtil.NG("新增保存出错：更新的当前结点类型不能是" + "【" + nodeTypeName + "】", countUpdate);
                }
                // 查看当前结点是否已经被选择使用
                count = getCountBySerial(entity, entity.getId());
                if(count > 0){
                    return CheckResultUtil.NG("新增保存出错：您选择的子结点已经在组织架构中，请选择尚未被使用的组织。", count);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 新增模式下，可新增子结点得类型
     * @return
     */
    @Override
    public List<NameAndValueVo> getCorrectTypeByInsertStatus(MOrgVo vo) {
        // 查询 数据
        List<NameAndValueVo> rtn = mapper.getCorrectTypeByInsertStatus(vo);
        return rtn;
    }

    /**
     * 删除
     * @param entity
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_ORG.OPER_DELETE,
        type = OperationEnum.DELETE,
        logById = @LogByIdAnnotion(
            name = SystemConstants.OPERATION.M_ORG.OPER_DELETE,
            type = OperationEnum.DELETE,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_ORG.TABLE_NAME,
            id = "#{entity.id}"
        )
    )
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteById(MOrgEntity entity) {
        // 检索子组织数据
        List<MOrgEntity> rtnList = getDataByCode(entity);
        rtnList.forEach(bean -> {
            // 删除关联表数据
            deleteOrgRelation(bean);
            // 删除 数据
            mapper.deleteById(bean.getId());
        });

        // 清理所有组织架构相关缓存
        clearAllOrgCaches();

        return true;
    }

    /**
     * 删除关联表
     * @param entity
     */
    private void deleteOrgRelation(MOrgEntity entity) {
        // 防御性编程：检查实体数据完整性
        if (entity.getType() == null) {
            log.warn("跳过删除关系记录 - 实体类型为空，实体ID：{}，Serial_ID：{}", 
                entity.getId(), entity.getSerial_id());
            return;
        }
        
        if (entity.getSerial_id() == null) {
            log.warn("跳过删除关系记录 - Serial_ID为空，实体ID：{}，类型：{}", 
                entity.getId(), entity.getType());
            return;
        }
        
        switch (entity.getType()) {
//            case DictConstant.DICT_ORG_SETTING_TYPE_TENANT:
//                entity.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_TENANT_SERIAL_TYPE);
//                break;
//            case DictConstant.DICT_ORG_SETTING_TYPE_GROUP:
//                oTGMapper.delOTGRelation(entity.getSerial_id());
//                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_COMPANY:
                oGCMapper.delOGCRelation(entity.getSerial_id());
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_DEPT:
                oCDMapper.delOCDRelation(entity.getSerial_id());
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_POSITION:
                oDPMapper.delODPRelation(entity.getSerial_id());
                break;
            case DictConstant.DICT_ORG_SETTING_TYPE_STAFF:
                break;
        }
    }

    /**
     * 根据code，进行 like 'code%'，匹配当前结点以及子结点
     * @param entity
     * @return
     */
    @Override
    public List<MOrgEntity> getDataByCode(MOrgEntity entity) {
        List<MOrgEntity> rtnList = mapper.getDataByCode(entity);
        return rtnList;
    }

    /**
     * 拖拽保存
     * 未使用乐观锁，需要注意
     * @param beans
     * @return
     */
    @Override
    public Boolean dragsave(List<MOrgTreeVo> beans) {
        List<MOrgEntity> entities = new ArrayList<>();
        int code = 0;
        List<MOrgEntity> beanList = dragData2List(beans, null ,entities, code);
        /**
         * 注意调用方法，必须使用外部调用，激活aop，内部调用不能激活aop和注解
         */
        return self.dragsave2Db(beanList);
    }

    /**
     * 拖拽保存
     * @param list
     * @return
     */
    @OperationLogAnnotion(
        name = SystemConstants.OPERATION.M_ORG.OPER_DRAG_DROP,
        type = OperationEnum.DRAG_DROP,
        logByIds = @LogByIdsAnnotion(
            name = SystemConstants.OPERATION.M_ORG.OPER_DRAG_DROP,
            type = OperationEnum.DRAG_DROP,
            oper_info = "",
            table_name = SystemConstants.OPERATION.M_ORG.TABLE_NAME,
            id_position = ParameterEnum.FIRST,
            ids = "#{orgList.id}"
        )
    )
    @Transactional(rollbackFor = Exception.class)
    public Boolean dragsave2Db(List<MOrgEntity> list){
        log.info("开始执行拖拽保存操作，总计待处理实体数量：{}", list.size());
        
        // 验证修复效果 - 检查前3个实体的数据
        for (int i = 0; i < Math.min(3, list.size()); i++) {
            MOrgEntity entity = list.get(i);
            log.info("验证实体[{}]: id={}, type={}, serial_id={}, serial_type={}, parent_id={}", 
                    i, entity.getId(), entity.getType(), entity.getSerial_id(), 
                    entity.getSerial_type(), entity.getParent_id());
        }
        
        log.info("员工类型常量：{}", DictConstant.DICT_ORG_SETTING_TYPE_STAFF);
        
        // 分离员工节点和组织节点，操作日志只记录组织节点的变更
        List<MOrgEntity> orgList = list.stream()
            .filter(entity -> !DictConstant.DICT_ORG_SETTING_TYPE_STAFF.equals(entity.getType()))
            .collect(Collectors.toList());
        
        List<MOrgEntity> staffList = list.stream()
            .filter(entity -> DictConstant.DICT_ORG_SETTING_TYPE_STAFF.equals(entity.getType()))
            .collect(Collectors.toList());
        
        log.info("分离结果：组织节点{}个，员工节点{}个", orgList.size(), staffList.size());
        
        // 显示员工节点详情
        if (!staffList.isEmpty()) {
            for (int i = 0; i < Math.min(2, staffList.size()); i++) {
                MOrgEntity staff = staffList.get(i);
                log.info("员工节点[{}]: id={}, serial_id={}, parent_id={}", 
                        i, staff.getId(), staff.getSerial_id(), staff.getParent_id());
            }
        }
        
        try {
            // 首先处理员工拖拽到岗位（不记录到m_org操作日志）
            for (MOrgEntity staffEntity : staffList) {
                handleStaffDragToPosition(staffEntity);
            }
            
            // 如果没有组织节点需要处理，直接返回成功
            if (orgList.isEmpty()) {
                log.info("只有员工节点拖拽，组织结构无变化，操作完成");
                // 清理相关缓存
                clearAllOrgCaches();
                return true;
            }
            // 编号重置（只处理组织节点）
            for (MOrgEntity entity : orgList) {
                if(entity.getParent_id() != null){
                    setParentSonCount(orgList, entity.getParent_id());
                }
            }
            
            // 更新开始（只处理组织节点）
            for (MOrgEntity entity : orgList) {
                entity.setSon_count(entity.getSon_count() == null ? 0 : entity.getSon_count());
                entity.setU_id(SecurityUtil.getLoginUser_id());
                entity.setU_time(LocalDateTime.now());
                mapper.updateDragSave(entity);
            }

            // 设置组织关系表逻辑 - 先清理所有组织实体的旧关系记录，避免重复记录导致TooManyResultsException
            for (MOrgEntity entity : orgList) {
                // 数据完整性检查
                if (entity.getType() == null || entity.getSerial_id() == null) {
                    continue;
                }
                deleteOrgRelation(entity);
            }
            
            // 重新创建关系记录（只处理组织节点）
            for (MOrgEntity entity : orgList) {
                
                /** 获取当前实体 */
                MOrgEntity currentEntity = getById(entity.getId());
                if (currentEntity == null) {
                    log.warn("跳过不存在的实体，ID：{}，可能已被删除或前端缓存过期", entity.getId());
                    continue; // 跳过不存在的实体，继续处理其他实体
                }
                
                // 检查是否为根节点（租户级别节点，parent_id为null）
                if (entity.getParent_id() == null) {
                    log.info("检测到根节点（租户级别），实体ID：{}，跳过父级关系处理", entity.getId());
                    // 根节点不需要设置组织关系表，直接跳过
                    continue;
                }
                
                /** 获取父级实体 */
                MOrgEntity parentEntity = getById(entity.getParent_id());
                if (parentEntity == null) {
                    log.warn("跳过父级不存在的实体，实体ID：{}，父级ID：{}，可能已被删除或前端缓存过期", 
                            entity.getId(), entity.getParent_id());
                    continue; // 跳过父级不存在的实体，继续处理其他实体
                }
                
                /** 设置组织关系表逻辑 */
                setOrgRelationData(currentEntity, parentEntity);
            }

            // 清理所有组织架构相关缓存
            clearAllOrgCaches();

            log.info("拖拽保存操作成功完成，总计处理实体数量：{}（组织节点：{}，员工节点：{}）", 
                    list.size(), orgList.size(), staffList.size());
            return true;
            
        } catch (Exception e) {
            log.error("拖拽保存操作失败，事务将回滚，异常类型：{}，异常信息：{}", 
                e.getClass().getSimpleName(), e.getMessage(), e);
            
            // 特殊处理TooManyResultsException，提供更详细的诊断信息
            if (e instanceof org.apache.ibatis.exceptions.TooManyResultsException) {
                log.error("检测到TooManyResultsException异常，这通常表示组织关系表中存在重复记录");
                log.error("建议检查表：m_org_company_dept 中是否存在current_id重复的记录");
                log.error("可执行SQL诊断：SELECT current_id, COUNT(*) FROM m_org_company_dept GROUP BY current_id HAVING COUNT(*) > 1");
            }
            
            throw e;
        }
    }

    /**
     * 处理员工拖拽到岗位的业务逻辑
     * @param entity 员工实体（type="60"，id为员工ID，parent_id为目标岗位ID）
     */
    private void handleStaffDragToPosition(MOrgEntity entity) {
        try {
            // 获取实际的员工ID和目标岗位组织节点ID
            Long actualStaffId = entity.getSerial_id(); // 使用serial_id作为实际员工ID
            Long targetOrgNodeId = entity.getParent_id(); // 目标岗位的组织节点ID
            
            log.info("开始处理员工拖拽：员工Serial_ID={}，目标岗位组织节点ID={}", actualStaffId, targetOrgNodeId);
            
            // 验证目标岗位是否存在
            MOrgEntity targetPosition = getById(targetOrgNodeId);
            if (targetPosition == null) {
                log.warn("目标岗位不存在，岗位组织节点ID：{}，跳过员工拖拽处理", targetOrgNodeId);
                return;
            }
            
            // 验证目标是岗位类型
            if (!DictConstant.DICT_ORG_SETTING_TYPE_POSITION.equals(targetPosition.getType())) {
                log.warn("目标不是岗位节点，类型：{}，跳过员工拖拽处理", targetPosition.getType());
                return;
            }
            
            // 获取目标岗位的实际岗位ID
            Long actualPositionId = targetPosition.getSerial_id();
            log.info("目标岗位实际ID：{}", actualPositionId);
            
            // 更新员工-岗位关系 (使用实际的员工ID和岗位ID)
            updateStaffPositionRelation(actualStaffId, actualPositionId);
            
            log.info("员工拖拽处理成功：员工ID={} 已调整到岗位ID={}", actualStaffId, actualPositionId);
            
        } catch (Exception e) {
            log.error("处理员工拖拽失败，员工Serial_ID：{}，目标岗位组织节点ID：{}，错误：{}", 
                    entity.getSerial_id(), entity.getParent_id(), e.getMessage(), e);
            throw new BusinessException("员工岗位调整失败：" + e.getMessage());
        }
    }

    /**
     * 更新员工-岗位关系
     * @param staffId 员工ID
     * @param newPositionId 新岗位ID
     */
    private void updateStaffPositionRelation(Long staffId, Long newPositionId) {
        // 1. 删除员工原有的岗位关系
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MStaffOrgEntity> deleteWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        deleteWrapper.eq(MStaffOrgEntity::getStaff_id, staffId)
                    .eq(MStaffOrgEntity::getSerial_type, DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE);
        
        int deletedCount = mStaffOrgMapper.delete(deleteWrapper);
        log.info("删除员工原有岗位关系，员工ID：{}，删除记录数：{}", staffId, deletedCount);
        
        // 2. 建立员工与新岗位的关系
        MStaffOrgEntity newRelation = new MStaffOrgEntity();
        newRelation.setStaff_id(staffId);
        newRelation.setSerial_id(newPositionId);
        newRelation.setSerial_type(DictConstant.DICT_ORG_SETTING_TYPE_POSITION_SERIAL_TYPE);
        
        mStaffOrgMapper.insert(newRelation);
        log.info("建立员工新岗位关系，员工ID：{}，新岗位ID：{}", staffId, newPositionId);
    }

    /**
     * 设置儿子个数
     * @return
     */
    private List<MOrgEntity> setParentSonCount(List<MOrgEntity> entities, Long parent_id) {
        for(MOrgEntity entity : entities){
            if(entity.getId().equals(parent_id)){
                entity.setSon_count(entity.getSon_count() == null ? 1 : entity.getSon_count() + 1);
            }
        }
        return entities;
    }

    /**
     * 拖拽数据规整
     * @param beans         ：循环的beans
     * @param parent_bean   ：父亲bean
     * @param entities      ：最终返回的list bean
     * @param code          ：
     * @return
     */
    private List<MOrgEntity> dragData2List(List<? extends TreeNode> beans, MOrgEntity parent_bean, List<MOrgEntity> entities, int code) {
        for (TreeNode bean : beans) {
            code = code + 1;
            MOrgEntity entity = new MOrgEntity();
            entity.setId(bean.getId());
            entity.setParent_id(bean.getParent_id());
            
            if (bean instanceof MOrgTreeVo) {
                MOrgTreeVo treeVo = (MOrgTreeVo) bean;
                entity.setType(treeVo.getType());
                entity.setSerial_id(treeVo.getSerial_id());
                entity.setSerial_type(treeVo.getSerial_type());
            }
            
            if(parent_bean == null) {
                entity.setCode(String.format("%04d", code));
            } else {
                entity.setCode(parent_bean.getCode() + String.format("%04d", code));
            }
            entities.add(entity);
            if(bean.getChildren() !=null && bean.getChildren().size() !=0){
                dragData2List(bean.getChildren(), entity, entities, 0);
            }
        }
        return entities;
    }

    /**
     * 获取员工清单，为穿梭框服务
     * @return
     */
    @Override
    public MStaffPositionTransferVo getStaffTransferList(MStaffTransferVo condition) {

        MStaffPositionTransferVo rtn = new MStaffPositionTransferVo();
        // 获取全部用户
        rtn.setStaff_all(mapper.getAllStaffTransferList(condition));
        // 获取该岗位已经设置过得用户
        List<Long> rtnList = mapper.getUsedStaffTransferList(condition);
        rtn.setStaff_positions(rtnList.toArray(new Long[rtnList.size()]));
        return rtn;
    }

    /**
     * 设置员工关系，删除剔除的员工，增加选择的员工
     * @param bean 员工id list
     * @return
     */
    @Override
    public MStaffPositionTransferVo setStaffTransfer(MStaffTransferVo bean) {
        // 操作日志bean初始化
        CustomOperateBo cobo = new CustomOperateBo();
        cobo.setName(SystemConstants.OPERATION.M_STAFF_ORG.OPER_POSITION_STAFF);
        cobo.setPlatform(SystemConstants.PLATFORM.PC);
        cobo.setType(OperationEnum.BATCH_UPDATE_INSERT_DELETE);


        // 查询出需要剔除的员工list
        List<MStaffPositionOperationVo> deleteMemgerList = mapper.selete_delete_member(bean);
        // 查询出需要添加的员工list
        List<MStaffPositionOperationVo> insertMemgerList = new ArrayList<>();
        if (bean.getStaff_positions().length != 0) {
            insertMemgerList = mapper.selete_insert_member(bean);
        }

        // 执行保存逻辑，并返回员工数量
        return self.saveMemberList(deleteMemgerList, insertMemgerList, cobo, bean);
    }

    /**
     * 保存员工关系，删除剔除的员工，增加选择的员工
     * @param deleteMemberList
     * @param insertMemgerList
     * @param cobo
     * @param bean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MStaffPositionTransferVo saveMemberList(List<MStaffPositionOperationVo> deleteMemberList,
        List<MStaffPositionOperationVo> insertMemgerList,
        CustomOperateBo cobo,
        MStaffTransferVo bean) {

        List<CustomOperateDetailBo> detail = new ArrayList<>();

        // ---------------------------------操作日志 新增 start-----------------------------------------------------
        // 操作日志：记录删除前数据
        for(MStaffPositionOperationVo vo : deleteMemberList) {
            CustomOperateDetailBo<MStaffPositionOperationVo> bo = new CustomOperateDetailBo<>();
            bo.setName(cobo.getName());
            bo.setType(OperationEnum.DELETE);
            bo.setTable_name(SystemConstants.OPERATION.M_STAFF_ORG.TABLE_NAME);
            bo.setNewData(null);
            bo.setOldData(vo);
            setColumnsMap(bo);
            detail.add(bo);
        }
        // ---------------------------------操作日志 新增 end-----------------------------------------------------

        // 删除剔除的员工
        List<MStaffOrgEntity> delete_list =
            BeanUtilsSupport.copyProperties(deleteMemberList, MStaffOrgEntity.class, new String[] {"c_time", "u_time"});
        List<Long> ids = Lists.newArrayList();
        delete_list.forEach(beans -> {
            ids.add(beans.getId());
        });
        if (ArrayPfUtil.isNotEmpty(ids)) {
            mStaffOrgService.removeByIds(ids);
        }

        // 增加选择的员工
        Long[] staff_positions = new Long[insertMemgerList.size()];
        int i = 0;
        List<MStaffOrgEntity> mStaffOrgEntities = new ArrayList<>();
        for( MStaffPositionOperationVo vo : insertMemgerList ) {
            MStaffOrgEntity mStaffOrgEntity = new MStaffOrgEntity();
            mStaffOrgEntity.setStaff_id(vo.getId());
            mStaffOrgEntity.setSerial_id(bean.getPosition_id());
            mStaffOrgEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_POSITION);
            mStaffOrgEntities.add(mStaffOrgEntity);

            staff_positions[i] = vo.getId();
            i = i + 1;
        }

        mStaffOrgService.saveBatch(mStaffOrgEntities);

        // ---------------------------------操作日志 新增 start-----------------------------------------------------
        // 记录更新后数据
        MStaffTransferVo condition = new MStaffTransferVo();
        condition.setPosition_id(bean.getPosition_id());
        condition.setStaff_positions(staff_positions);
        List<MStaffPositionOperationVo> seleteMemgerList = mapper.selete_member(bean);
        for(MStaffPositionOperationVo vo: seleteMemgerList) {
            // 操作日志：记录新增数据
            CustomOperateDetailBo<MStaffPositionOperationVo> bo = new CustomOperateDetailBo<>();
            bo.setName(cobo.getName());
            bo.setType(OperationEnum.ADD);
            bo.setTable_name(SystemConstants.OPERATION.M_STAFF_ORG.TABLE_NAME);
            bo.setNewData(vo);
            bo.setOldData(null);
            setColumnsMap(bo);
            detail.add(bo);
        }
        cobo.setDetail(detail);
        // ---------------------------------操作日志 新增 end-----------------------------------------------------

        // 保存操作日志
        sLogOperService.save(cobo);

        // 清理所有组织架构相关缓存（员工关系变更影响统计）
        clearAllOrgCaches();

        // 查询最新数据并返回
        // 获取该岗位已经设置过得用户
        List<Long> rtnList = mapper.getUsedStaffTransferList(condition);
        MStaffPositionTransferVo mStaffPositionTransferVo = new MStaffPositionTransferVo();
        mStaffPositionTransferVo.setStaff_positions_count(rtnList.size());
        return mStaffPositionTransferVo;
    }

    /**
     * 设置列相对应的列名称
     */
    private void setColumnsMap(CustomOperateDetailBo<MStaffPositionOperationVo> bean){
        Map<String, String> columns = new ConcurrentHashMap<>();
        columns.put("staff_name", "员工名称");
        columns.put("position_name", "岗位名称");
        columns.put("c_id", "新增人id");
        columns.put("c_time", "新增时间");
        columns.put("u_id", "更新人id");
        columns.put("u_time", "更新时间");
        bean.setColumns(columns);
    }

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public MStaffTabVo selectStaff(MStaffTabDataVo searchCondition) {
        MStaffTabVo mStaffTabVo = new MStaffTabVo();
        // 这个需要提前运行
        mStaffTabVo.setCurrentOrgStaffCount(getCurrentOrgStaffCount(searchCondition));

        // 分页条件
        Page<MStaffTabDataVo> pageCondition = new Page(1, Integer.MAX_VALUE);
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        /**
         * 表数据
         * 判断是当前组织下还是全部员工
         * 0:当前组织
         * 1：全部员工
         * */
        if (searchCondition.getActive_tabs_index() == 1){
            // 1：全部员工
            IPage<MStaffTabDataVo> list = mapper.getAllOrgStaff(pageCondition,searchCondition);
            mStaffTabVo.setList(list.getRecords());
        } else {
            // 0:当前组织
            IPage<MStaffTabDataVo> list = mapper.selectStaff(pageCondition,searchCondition);
            mStaffTabVo.setList(list.getRecords());
        }

        // count 数据
        mStaffTabVo.setAllOrgStaffCount(getAllOrgStaffCount(searchCondition));
        return mStaffTabVo;
    }

    /**
     * 获取当组织下员工count
     */
    @Override
    public Integer getCurrentOrgStaffCount(MStaffTabDataVo searchCondition) {
        return mapper.getCurrentOrgStaffCount(searchCondition);
    }

    /**
     * 获取所有员工count
     */
    @Override
    public Integer getAllOrgStaffCount(MStaffTabDataVo searchCondition) {
        /**
         * 考虑所有员工的方法
         * 1:根据code的定义规则，0001xxxx|xxxx|，每4位为一个层，所以找到第一组的4个
         * 2：并设置回code中去
         *
         * 2020.04.26 updated：该方法不适合，只需要判断，用户表中租户=参数，即可
         */
//        String _code = searchCondition.getCode();
//        _code = StrUtil.sub(_code, 0, 4);
//        searchCondition.setCode(_code);
        return mapper.getAllOrgStaffCount(searchCondition);
    }

    /**
     * 获取组织子节点数量（原方法保留兼容性）
     */
    @Override
    public Integer getSubCount(Long orgId) {
        return mapper.getSubCount(orgId);
    }

    /**
     * 根据组织类型智能获取子节点统计
     * 集团类型返回详细分类统计，企业类型返回部门统计，部门类型返回子部门和岗位统计，其他类型返回简单计数
     */
    @Cacheable(value = SystemConstants.CACHE_PC.CACHE_ORG_SUB_COUNT, 
              key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::' + #orgId + '::' + #orgType")
    @Override
    public Object getSubCountByType(Long orgId, String orgType) {
        log.info("=== Service层处理组织统计 === orgId: {}, orgType: {}", orgId, orgType);
        
        // 集团类型常量：DICT_ORG_SETTING_TYPE_GROUP = "20"
        if (DictConstant.DICT_ORG_SETTING_TYPE_GROUP.equals(orgType)) {
            log.info("=== 处理集团类型 === orgId: {}", orgId);
            // 集团节点返回详细统计
            return mapper.getGroupSubCountDetail(orgId);
        } 
        // 企业类型常量：DICT_ORG_SETTING_TYPE_COMPANY = "30"  
        else if (DictConstant.DICT_ORG_SETTING_TYPE_COMPANY.equals(orgType)) {
            // 企业节点返回部门统计
            return mapper.getCompanySubCountDetail(orgId);
        } 
        // 部门类型常量：DICT_ORG_SETTING_TYPE_DEPT = "40"
        else if (DictConstant.DICT_ORG_SETTING_TYPE_DEPT.equals(orgType)) {
            // 部门节点返回子部门和岗位统计
            return mapper.getDeptSubCountDetail(orgId);
        }
        // 岗位类型常量：DICT_ORG_SETTING_TYPE_POSITION = "50"
        else if (DictConstant.DICT_ORG_SETTING_TYPE_POSITION.equals(orgType)) {
            // 岗位节点返回员工数量
            Long staffCount = mapper.countStaffByPositionId(orgId);
            return staffCount;
        }
        else {
            // 其他节点返回简单计数
            return mapper.getSubCount(orgId);
        }
    }

    /**
     * 重载方法：当未提供orgType时，根据orgId查询类型并统计
     * @param orgId 组织ID
     * @return 统计结果
     */
    public Object getSubCountByType(Long orgId) {
        // 查询组织信息获取类型
        MOrgEntity orgEntity = this.getById(orgId);
        if (orgEntity == null) {
            return 0;
        }
        
        String orgType = orgEntity.getType();
        
        // 岗位类型常量：DICT_ORG_SETTING_TYPE_POSITION = "50"
        if (DictConstant.DICT_ORG_SETTING_TYPE_POSITION.equals(orgType)) {
            // 岗位节点返回员工数量
            return mapper.countStaffByPositionId(orgId);
        }
        
        // 其他类型调用带orgType参数的重载方法
        return getSubCountByType(orgId, orgType);
    }

    /**
     * 获取根节点统计信息
     * 包含集团数、主体企业数、岗位数、员工数的综合统计
     * 
     * @return 根节点统计数据VO
     */
    @Cacheable(value = SystemConstants.CACHE_PC.CACHE_ORG_SUB_COUNT, 
              key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + '::root::statistics'")
    public MOrgCountsVo getRootStatistics() {
        try {
            MOrgCountsVo result = mapper.getRootStatistics();
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            log.error("获取根节点统计信息失败：{}", e.getMessage(), e);
        }
        
        // 异常情况返回默认值
        MOrgCountsVo defaultResult = new MOrgCountsVo();
        defaultResult.setGroup_count(0L);
        defaultResult.setCompany_count(0L);
        defaultResult.setPosition_count(0L);
        defaultResult.setStaff_count(0L);
        return defaultResult;
    }

    /**
     * 清理所有组织架构相关缓存
     * 模仿logout中的clearTenantSharedCaches方法
     * 在组织架构数据发生变化时调用，确保缓存一致性
     * 
     * 清理的缓存包括：
     * - CACHE_ORG_SUB_COUNT: 组织架构统计缓存（部门岗位数等）
     * - CACHE_DICT_TYPE: 数据字典缓存（可能包含组织类型等）
     * - CACHE_COLUMNS_TYPE: 表格列配置缓存（组织架构相关页面的列配置）
     * - CACHE_SYSTEM_ICON_TYPE: 系统图标缓存（组织架构图标）
     * - CACHE_CONFIG: 系统参数配置缓存（可能包含组织配置参数）
     */
    public void clearAllOrgCaches() {
        try {
            String tenantKey = DataSourceHelper.getCurrentDataSourceName();
            log.info("开始清理租户 [{}] 的组织架构相关缓存", tenantKey);
            
            // 要清理的组织架构相关缓存列表
            List<String> orgRelatedCaches = Arrays.asList(
                SystemConstants.CACHE_PC.CACHE_ORG_SUB_COUNT,        // 组织统计缓存
                SystemConstants.CACHE_PC.CACHE_DICT_TYPE,            // 数据字典缓存
                SystemConstants.CACHE_PC.CACHE_COLUMNS_TYPE,         // 表格列配置缓存
                SystemConstants.CACHE_PC.CACHE_SYSTEM_ICON_TYPE,     // 系统图标缓存
                SystemConstants.CACHE_PC.CACHE_CONFIG                // 系统参数配置缓存
            );
            
            int clearedCount = 0;
            int totalCaches = orgRelatedCaches.size();
            
            // 执行缓存清理
            for (String cacheName : orgRelatedCaches) {
                try {
                    Cache cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                        clearedCount++;
                        log.debug("已清理组织相关缓存: {} (租户: {})", cacheName, tenantKey);
                    } else {
                        log.warn("缓存不存在，跳过清理: {} (租户: {})", cacheName, tenantKey);
                    }
                } catch (Exception e) {
                    log.error("清理组织缓存失败: {} (租户: {}), 错误: {}", cacheName, tenantKey, e.getMessage());
                }
            }
            
            // 额外清理Redis中的组织统计缓存（兼容现有逻辑）
            if (tenantKey != null) {
                String cachePattern = SystemConstants.CACHE_PC.CACHE_ORG_SUB_COUNT + "::" + tenantKey + "::*";
                long deletedCount = redisUtil.deleteByPattern(cachePattern);
                if (deletedCount > 0) {
                    log.debug("额外清理Redis组织统计缓存成功，租户: {}，清除键数量: {}", tenantKey, deletedCount);
                }
            }
            
            log.info("租户 [{}] 组织架构缓存清理完成，共清理 {}/{} 个Spring缓存", tenantKey, clearedCount, totalCaches);
        } catch (Exception e) {
            log.error("组织架构缓存清理失败: {}", e.getMessage(), e);
            // 不影响主要业务流程，继续执行
        }
    }


}
