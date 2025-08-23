package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.BWarehouseGroupVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.BWarehouseRelationVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.warhouse.MWarehouseVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.org.MPositionMapper;
import com.xinyirun.scm.core.system.mapper.master.warehouse.MWarehouseMapper;
import com.xinyirun.scm.core.system.mapper.sys.rbac.role.SRoleMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
import com.xinyirun.scm.core.system.service.master.org.IMPositionService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MPositionAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  岗位主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class MPositiionServiceImpl extends BaseServiceImpl<MPositionMapper, MPositionEntity> implements IMPositionService {

    @Autowired
    private MPositionMapper mapper;

    @Autowired
    private MPositionAutoCodeServiceImpl autoCode;

    @Autowired
    private SRoleMapper sRoleMapper;

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
    public IPage<MPositionVo> selectPage(MPositionVo searchCondition) {
        // 分页条件
        Page<MPositionEntity> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MPositionVo> list = mapper.selectPage(pageCondition, searchCondition);
        return  list;
    }

    /**
     * 获取列表，页面查询
     *
     * @param page_code
     * @return
     */
    @Override
    public List<MPositionVo> selectPositionByPageCode(String page_code) {
        // 分页条件
        return mapper.selectPositionByPageCode(page_code);
    }

    @Override
    public MPositionVo getDetail(MPositionVo searchCondition) {
        MPositionVo vo = mapper.getDetail(searchCondition);

        vo.setWarehouseGroupPositionList(mWarehouseMapper.getWarehouseByGroupPositionId(vo.getId().intValue()));
        vo.setWarehouseGroupList(mWarehouseMapper.getWarehouseGroupByPositionId(vo.getId().intValue()));
        vo.setWarehousePositionList(mWarehouseMapper.getWarehouseByPositionId(vo.getId().intValue()));

        // 查询仓库权限树信息
        TreeDataVo mStaffPermissionDataVo = new TreeDataVo();
        mStaffPermissionDataVo.setSerial_id(searchCondition.getId());
        mStaffPermissionDataVo.setSerial_type("m_position");
        mStaffPermissionDataVo.setLabel(searchCondition.getName());

        // 岗位仓库组列表
        List<TreeDataVo> warehouseGroupList = mapper.selectWarehouseGroupList(searchCondition.getId());
        for (TreeDataVo warehouseGroupItem : warehouseGroupList) {
            // 仓库组仓库列表
            List<TreeDataVo> warehouseList = mapper.selectWarehouseListByGroupId(warehouseGroupItem.getSerial_id());
            for (TreeDataVo warehouseItem:warehouseList) {
                // 仓库信息
                warehouseItem.setChildren(new ArrayList<>());
            }
        }

        // 岗位仓库列表
        List<TreeDataVo> warehouseList = mapper.selectWarehouseListByPositionId(searchCondition.getId());
        for (TreeDataVo warehouseItem: warehouseList) {
            // 仓库信息
            warehouseItem.setChildren(new ArrayList<>());
        }
        warehouseGroupList.addAll(warehouseList);

        mStaffPermissionDataVo.setChildren(warehouseGroupList);

        vo.setWarehouseTreeData(mStaffPermissionDataVo);

        return vo;
    }

    @Override
    public TreeDataVo getWarehouseTreeData(MPositionVo searchCondition) {
        TreeDataVo mStaffPermissionDataVo = new TreeDataVo();
        mStaffPermissionDataVo.setSerial_id(searchCondition.getId());
        mStaffPermissionDataVo.setSerial_type("m_position");
        mStaffPermissionDataVo.setLabel(searchCondition.getName());

        // 岗位仓库组列表
        List<TreeDataVo> warehouseGroupList = mapper.selectWarehouseGroupList(searchCondition.getId());
        for (TreeDataVo warehouseGroupItem : warehouseGroupList) {
            // 仓库组仓库列表
            List<TreeDataVo> warehouseList = mapper.selectWarehouseListByGroupId(warehouseGroupItem.getSerial_id());
            for (TreeDataVo warehouseItem:warehouseList) {
                // 仓库信息
                warehouseItem.setChildren(new ArrayList<>());
            }
        }

        // 岗位仓库列表
        List<TreeDataVo> warehouseList = mapper.selectWarehouseListByPositionId(searchCondition.getId());
        for (TreeDataVo warehouseItem: warehouseList) {
            // 仓库信息
            warehouseItem.setChildren(new ArrayList<>());
        }
        warehouseGroupList.addAll(warehouseList);

        mStaffPermissionDataVo.setChildren(warehouseGroupList);

        return mStaffPermissionDataVo;
    }

    @Override
    public List<MPositionVo> selectPositionByPerms(String perms) {
        return mapper.selectPositionByPerms(perms);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MPositionExportVo> select(MPositionVo searchCondition) {
        // 查询 数据
        List<MPositionExportVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MPositionEntity> selectIdsIn(List<MPositionVo> searchCondition) {
        // 查询 数据
        List<MPositionEntity> list = mapper.selectIdsIn(searchCondition);
        return list;
    }

    /**
     * 获取列表，根据id查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MPositionExportVo> selectIdsInForExport(List<MPositionVo> searchCondition) {
        // 查询 数据
        return mapper.selectIdsInForExport(searchCondition);
    }

    /**
     * 批量删除复原
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<MPositionVo> searchCondition) {
        List<MPositionEntity> list = mapper.selectIdsIn(searchCondition);
        list.forEach(
            bean -> {
                bean.setIs_del(!bean.getIs_del());
            }
        );
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MPositionEntity entity) {
        // 编号为空则自动生成编号
        if(StringUtils.isEmpty(entity.getCode())){
            entity.setCode(autoCode.autoCode().getCode());
        }
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        entity.setIs_del(false);
        return InsertResultUtil.OK(mapper.insert(entity));
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MPositionEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
        return UpdateResultUtil.OK(mapper.updateById(entity));
    }

    /**
     * 更新岗位仓库权限
     * @param vo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWarehousePermission(MPositionVo vo) {
        // 全删全插
        ibWarehouseRelationService.deleteByPositionId(vo.getId().intValue());
        // 岗位仓库分组权限
        for (BWarehouseGroupVo bWarehouseGroupVo: vo.getWarehouseGroupList() ) {
            BWarehouseRelationVo bWarehouseRelationVo = new BWarehouseRelationVo();
            bWarehouseRelationVo.setPosition_id(vo.getId().intValue());
            bWarehouseRelationVo.setSerial_id(bWarehouseGroupVo.getId());
            bWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_WAREHOUSE_GROUP);

            ibWarehouseRelationService.insert(bWarehouseRelationVo);
        }

        // 岗位仓库权限
        for (MWarehouseVo mWarehouseVo: vo.getWarehousePositionList()) {
            BWarehouseRelationVo bWarehouseRelationVo = new BWarehouseRelationVo();
            bWarehouseRelationVo.setPosition_id(vo.getId().intValue());
            bWarehouseRelationVo.setSerial_id(mWarehouseVo.getId());
            bWarehouseRelationVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_WAREHOUSE);

            ibWarehouseRelationService.insert(bWarehouseRelationVo);
        }

    }

    /**
     * 获取数据byid
     * @param id
     * @return
     */
    @Override
    public MPositionVo selectByid(Long id){
        MPositionVo searchCondition = new MPositionVo();
        searchCondition.setId(id);
        return mapper.selectByid(searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param code
     * @return
     */
    public List<MPositionEntity> selectByCode(String code, Long equal_id) {
        // 查询 数据
        List<MPositionEntity> list = mapper.selectByCode(code, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MPositionEntity> selectByName(String name, Long equal_id) {
        // 查询 数据
        return mapper.selectByName(name, equal_id);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MPositionEntity> selectBySimpleName(String name, Long equal_id) {
        // 查询 数据
        return mapper.selectBySimpleName(name, equal_id);
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MPositionEntity entity, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<MPositionEntity> codeList_insertCheck = selectByCode(entity.getCode(), entity.getId());
                List<MPositionEntity> nameList_insertCheck = selectByName(entity.getName(), entity.getId());
                List<MPositionEntity> simple_name_insertCheck = selectBySimpleName(entity.getSimple_name(), entity.getId());
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：岗位编号【"+ entity.getCode() +"】出现重复", entity.getCode());
                }
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：岗位名称【"+ entity.getName() +"】出现重复", entity.getName());
                }
                if (simple_name_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：岗位简称【"+ entity.getSimple_name() +"】出现重复", entity.getSimple_name());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                List<MPositionEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getId());
                List<MPositionEntity> nameList_updCheck = selectByName(entity.getName(), entity.getId());
                List<MPositionEntity> simple_name_updCheck = selectBySimpleName(entity.getSimple_name(), entity.getId());

                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：岗位编号【"+ entity.getCode() +"】出现重复", entity.getCode());
                }
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：岗位名称【"+ entity.getName() +"】出现重复", entity.getName());
                }
                if (simple_name_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：岗位简称【"+ entity.getSimple_name() +"】出现重复", entity.getSimple_name());
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
                    return CheckResultUtil.NG("删除出错：该岗位【"+ entity.getSimple_name() +"】在组织机构中正在被使用，不能删除！", count);
                }
                break;
            case CheckResultAo.UNDELETE_CHECK_TYPE:
//                /** 如果逻辑删除为true，表示为：页面点击了删除操作 */
//                if(!entity.getIs_del()) {
//                    return CheckResultUtil.OK();
//                }
//                List<MPositionEntity> codeList_undelete_Check = selectByCode(entity.getCode(), entity.getId());
////                List<MPositionEntity> nameList_undelete_Check = selectByName(entity.getName(), null, entity.getId());
////                List<MPositionEntity> simple_name_undelete_Check = selectBySimpleName(entity.getSimple_name(), null, entity.getId());
//
//                if (codeList_undelete_Check.size() >= 1) {
//                    return CheckResultUtil.NG("复原出错：复原岗位编号【"+ entity.getCode() +"】出现重复", entity.getCode());
//                }
////                if (nameList_undelete_Check.size() >= 1) {
////                    return CheckResultUtil.NG("复原出错：复原岗位全称【"+ entity.getName() +"】出现重复", entity.getName());
////                }
////                if (simple_name_undelete_Check.size() >= 1) {
////                    return CheckResultUtil.NG("复原出错：复原岗位简称【"+ entity.getSimple_name() +"】出现重复", entity.getSimple_name());
////                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
