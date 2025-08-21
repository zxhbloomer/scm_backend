package com.xinyirun.scm.core.system.serviceimpl.sys.rbac.role;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.rbac.role.MRolePositionEntity;
import com.xinyirun.scm.bean.entity.sys.rbac.role.SRoleEntity;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateBo;
import com.xinyirun.scm.bean.system.bo.log.operate.CustomOperateDetailBo;
import com.xinyirun.scm.bean.system.vo.master.org.MRolePositionTransferVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRolePositionOperationVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.MRoleTransferVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleExportVo;
import com.xinyirun.scm.bean.system.vo.sys.rbac.role.SRoleVo;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.ArrayPfUtil;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.rbac.permission.MPermissionMapper;
import com.xinyirun.scm.core.system.mapper.sys.rbac.role.SRoleMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.rbac.role.ISRoleService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.SRoleAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.master.rbac.permission.role.MRolePositionServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-07-11
 */
@Service
public class SRoleServiceImpl extends BaseServiceImpl<SRoleMapper, SRoleEntity> implements ISRoleService {

    @Autowired
    private SRoleMapper sRoleMapper;

    @Autowired
    private MPermissionMapper mPermissionMapper;

    @Autowired
    private MRolePositionServiceImpl rolePositionService;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SRoleAutoCodeServiceImpl sRoleAutoCodeService;

    /**
     * 获取列表，页面查询
     * 
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<SRoleVo> selectPage(SRoleVo searchCondition) {
        // 分页条件
        Page<SRoleEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        // 查询数据，TypeHandler会自动处理permissionList的JSON转换
        IPage<SRoleVo> list = sRoleMapper.selectPage(pageCondition, searchCondition);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     * 
     * @param searchCondition
     * @return
     */
    @Override
    public List<SRoleExportVo> selectExportAll(SRoleVo searchCondition){
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = sRoleMapper.selectExportNum(searchCondition);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        // 查询 数据
        List<SRoleExportVo> list = sRoleMapper.selectExportAll(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     * 
     * @param searchCondition
     * @return
     */
    @Override
    public List<SRoleEntity> selectIdsIn(List<SRoleVo> searchCondition) {
        // 查询 数据
        List<SRoleEntity> list = sRoleMapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 批量导入逻辑
     * 
     * @param entityList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveBatches(List<SRoleEntity> entityList) {
        // 为批量保存中编码为空的记录自动生成编码
        for (SRoleEntity entity : entityList) {
            if (StringUtils.isEmpty(entity.getCode())) {
                entity.setCode(sRoleAutoCodeService.autoCode().getCode());
            }
        }
        return super.saveBatch(entityList, 500);
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(value = SystemConstants.CACHE_PC.CACHE_SYSTEM_MENU_SEARCH_TYPE, allEntries=true)
    public void deleteByIdsIn(List<SRoleVo> searchCondition) {
        List<SRoleEntity> list = sRoleMapper.selectIdsIn(searchCondition);
        list.forEach(
            bean -> {
                bean.setIs_del(!bean.getIs_del());
            }
        );
        saveOrUpdateBatch(list, 500);
    }


    @Override
    public MRolePositionTransferVo getRoleTransferList(MRoleTransferVo condition) {
        MRolePositionTransferVo rtn = new MRolePositionTransferVo();
        // 获取全部用户
        rtn.setRole_all(sRoleMapper.getAllRoleTransferList(new MRoleTransferVo()));
        // 获取该岗位已经设置过的用户
        List<Integer> rtnList = sRoleMapper.getUsedRoleTransferList(condition);
        rtn.setPosition_role(rtnList.toArray(new Integer[rtnList.size()]));
        return rtn;
    }

    @Override
    public MRolePositionTransferVo setRoleTransfer(MRoleTransferVo bean) {
        // 操作日志bean初始化
        CustomOperateBo cobo = new CustomOperateBo();
        cobo.setName(SystemConstants.OPERATION.M_ROLE_POSITION.OPER_POSITION_ROLE);
        cobo.setPlatform(SystemConstants.PLATFORM.PC);
        cobo.setType(OperationEnum.BATCH_UPDATE_INSERT_DELETE);


        // 查询出需要剔除的权限list
        List<MRolePositionOperationVo> deleteMemberList = sRoleMapper.selectDeleteMember(bean);
        // 查询出需要添加的权限list
        List<MRolePositionOperationVo> insertMemberList = sRoleMapper.selectInsertMember(bean);

        // 执行保存逻辑，并返回权限数量
        return this.saveMemberList(deleteMemberList, insertMemberList, cobo, bean);
    }

    /**
     * 部分导出
     *
     * @param searchConditionList 导出id
     * @return List<SRoleExportVo>
     */
    @Override
    public List<SRoleExportVo> selectExportList(List<SRoleVo> searchConditionList) {
        return sRoleMapper.selectExportList(searchConditionList);
    }

    /**
     * 保存员工关系，删除剔除的员工，增加选择的员工
     * @param deleteMemberList
     * @param insertMemberList
     * @param cobo
     * @param bean
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public MRolePositionTransferVo saveMemberList(List<MRolePositionOperationVo> deleteMemberList, List<MRolePositionOperationVo> insertMemberList,CustomOperateBo cobo, MRoleTransferVo bean) {

        List<CustomOperateDetailBo> detail = new ArrayList<>();

        // ---------------------------------操作日志 新增 start-----------------------------------------------------
        // 操作日志：记录删除前数据
        for(MRolePositionOperationVo vo : deleteMemberList) {
            CustomOperateDetailBo<MRolePositionOperationVo> bo = new CustomOperateDetailBo<>();
            bo.setName(cobo.getName());
            bo.setType(OperationEnum.DELETE);
            bo.setTable_name(SystemConstants.OPERATION.M_STAFF_ORG.TABLE_NAME);
            bo.setNewData(null);
            bo.setOldData(vo);
            setColumnsMap(bo);
            detail.add(bo);
        }
        // ---------------------------------操作日志 新增 end-----------------------------------------------------

        // 删除剔除的权限
        List<MRolePositionEntity> delete_list = BeanUtilsSupport.copyProperties(deleteMemberList, MRolePositionEntity.class, new String[] {"c_time", "u_time"});
        List<Integer> ids = Lists.newArrayList();
        delete_list.forEach(beans -> {
            ids.add(beans.getId());
        });
        if (ArrayPfUtil.isNotEmpty(ids)) {
            rolePositionService.removeByIds(ids);
        }

        // 增加选择的权限
        Integer[] position_roles = new Integer[insertMemberList.size()];
        int i = 0;
        List<MRolePositionEntity> mRolePositionEntities = new ArrayList<>();
        for( MRolePositionOperationVo vo : insertMemberList ) {
            MRolePositionEntity mRolePositionEntity = new MRolePositionEntity();
            mRolePositionEntity.setRole_id(vo.getId());
            mRolePositionEntity.setPosition_id(bean.getPosition_id());
            mRolePositionEntities.add(mRolePositionEntity);

            position_roles[i] = vo.getId();
            i = i + 1;
        }

        rolePositionService.saveBatch(mRolePositionEntities);

        // ---------------------------------操作日志 新增 start-----------------------------------------------------
        // 记录更新后数据
        MRoleTransferVo condition = new MRoleTransferVo();
        condition.setPosition_id(bean.getPosition_id());
        condition.setPosition_roles(position_roles);
        List<MRolePositionOperationVo> selectMemberList = sRoleMapper.selectMember(bean);
        for(MRolePositionOperationVo vo: selectMemberList) {
            // 操作日志：记录新增数据
            CustomOperateDetailBo<MRolePositionOperationVo> bo = new CustomOperateDetailBo<>();
            bo.setName(cobo.getName());
            bo.setType(OperationEnum.ADD);
            bo.setTable_name(SystemConstants.OPERATION.M_PERMISSION_ROLE.TABLE_NAME);
            bo.setNewData(vo);
            bo.setOldData(new MRolePositionOperationVo());
            setColumnsMap(bo);
            detail.add(bo);
        }
        cobo.setDetail(detail);
        // ---------------------------------操作日志 新增 end-----------------------------------------------------

        // 保存操作日志
//        sLogOperService.save(cobo);

        // 查询最新数据并返回
        // 获取该岗位已经设置过得用户
        List<Integer> rtnList = sRoleMapper.getUsedRoleTransferList(condition);
        MRolePositionTransferVo mRolePositionTransferVo = new MRolePositionTransferVo();
        mRolePositionTransferVo.setPosition_role_count(rtnList.size());
        return mRolePositionTransferVo;
    }

    /**
     * 设置列相对应的列名称
     */
    private void setColumnsMap(CustomOperateDetailBo<MRolePositionOperationVo> bean){
        Map<String, String> columns = new ConcurrentHashMap<>();
        columns.put("position_name", "岗位名称");
        columns.put("role_name", "角色名称");
        columns.put("c_id", "新增人id");
        columns.put("c_time", "新增时间");
        columns.put("u_id", "更新人id");
        columns.put("u_time", "更新时间");
        bean.setColumns(columns);
    }

    /**
     * 重写save方法，添加AutoCode逻辑
     * @param entity
     * @return
     */
    @Override
    public boolean save(SRoleEntity entity) {
        // 如果角色编码为空，则自动生成
        if (StringUtils.isEmpty(entity.getCode())) {
            entity.setCode(sRoleAutoCodeService.autoCode().getCode());
        }
        return super.save(entity);
    }

}
